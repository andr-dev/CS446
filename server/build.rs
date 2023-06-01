use itertools::Itertools;
use prost::Message;
use prost_build::protoc_from_env;
use prost_build::protoc_include_from_env;
use prost_build::Module;
use prost_types::FileDescriptorSet;
use std::collections::HashMap;
use std::io::ErrorKind;
use std::io::Write;
use std::path::PathBuf;
use std::process::Command;
use std::{fs, io::Result, path::Path};

use log::{debug, trace};

pub const PROTO_SRC_PATH: &'static str = "../protos";
pub const PROTO_OUT_PATH: &'static str = "src/proto";

fn main() -> Result<()> {
    println!("cargo:rerun-if-changed={}", PROTO_SRC_PATH);

    let proto_out_path = Path::new(PROTO_OUT_PATH);

    if proto_out_path.exists() && proto_out_path.is_dir() {
        fs::remove_dir_all(proto_out_path)?;
    }

    let protos = get_protos(PROTO_SRC_PATH)?;

    if !protos.is_empty() {
        fs::create_dir(proto_out_path)?;

        let mut prost_config = prost_build::Config::new();

        prost_config.type_attribute(".", "#[derive(Serialize, Deserialize)]");

        let out_dir = Path::new(PROTO_OUT_PATH);
        let include_file = "mod.rs";

        let includes = &[Path::new(PROTO_SRC_PATH).canonicalize()?];

        let target_is_env = false;
        let target: PathBuf = out_dir.to_path_buf();

        let tmp = tempfile::Builder::new().prefix("prost-build").tempdir()?;
        let file_descriptor_set_path = tmp.path().join("prost-descriptor-set");

        let protoc = protoc_from_env();

        let mut cmd = Command::new(protoc.clone());
        cmd.arg("--include_imports")
            .arg("--include_source_info")
            .arg("-o")
            .arg(&file_descriptor_set_path);

        for include in includes {
            if include.exists() {
                cmd.arg("-I").arg(include);
            } else {
                debug!("ignoring {} since it does not exist.", include.display())
            }
        }

        if let Some(protoc_include) = protoc_include_from_env() {
            cmd.arg("-I").arg(protoc_include);
        }

        for proto in protos {
            cmd.arg(proto);
        }

        debug!("Running: {:?}", cmd);

        let output = cmd.output().map_err(|error| {
                std::io::Error::new(
                    error.kind(),
                    format!("failed to invoke protoc (hint: https://docs.rs/prost-build/#sourcing-protoc): (path: {:?}): {}", &protoc, error),
                )
            })?;

        if !output.status.success() {
            return Err(std::io::Error::new(
                std::io::ErrorKind::Other,
                format!("protoc failed: {}", String::from_utf8_lossy(&output.stderr)),
            ));
        }

        let buf = fs::read(&file_descriptor_set_path).map_err(|e| {
            std::io::Error::new(
                e.kind(),
                format!(
                    "unable to open file_descriptor_set_path: {:?}, OS: {}",
                    &file_descriptor_set_path, e
                ),
            )
        })?;

        let file_descriptor_set = FileDescriptorSet::decode(&*buf).map_err(|error| {
            std::io::Error::new(
                ErrorKind::InvalidInput,
                format!("invalid FileDescriptorSet: {}", error),
            )
        })?;

        let requests = file_descriptor_set
            .file
            .into_iter()
            .map(|descriptor| {
                (
                    Module::from_protobuf_package_name(descriptor.package()),
                    descriptor,
                )
            })
            .collect::<Vec<_>>();

        let file_names = requests
            .iter()
            .map(|req| (req.0.clone(), req.0.to_file_name_or("_")))
            .collect::<HashMap<Module, String>>();

        let modules = prost_config.generate(requests)?;

        for (module, module_content) in &modules {
            let content = format!(
                "use serde::{{Serialize, Deserialize}};\n\n{}",
                module_content
            );

            let file_name = file_names
                .get(module)
                .expect("every module should have a filename");

            let output_path = target.join(file_name);

            let previous_content = fs::read(&output_path);

            if previous_content
                .map(|previous_content| previous_content == content.as_bytes())
                .unwrap_or(false)
            {
                trace!("unchanged: {:?}", file_name);
            } else {
                trace!("writing: {:?}", file_name);
                fs::write(output_path, content)?;
            }
        }

        trace!("Writing include file: {:?}", target.join(include_file));

        let mut file = fs::File::create(target.join(include_file))?;

        write_includes(
            modules.keys().collect(),
            &mut file,
            0,
            if target_is_env { None } else { Some(&target) },
        )?;

        file.flush()?;
    }

    Ok(())
}

fn get_protos(dir: &str) -> Result<Vec<String>> {
    let mut protos = Vec::new();

    for entry_result in fs::read_dir(dir)? {
        let entry = entry_result?;

        if entry.file_type()?.is_dir() {
            protos.append(&mut get_protos(
                entry.path().canonicalize()?.to_str().unwrap(),
            )?);
        } else {
            protos.push(entry.path().canonicalize()?.to_str().unwrap().to_string());
        }
    }

    Ok(protos)
}

fn write_includes(
    mut entries: Vec<&Module>,
    outfile: &mut fs::File,
    depth: usize,
    basepath: Option<&PathBuf>,
) -> Result<usize> {
    let mut written = 0;

    entries.sort();

    while !entries.is_empty() {
        let modident = entries[0].parts().nth(depth).unwrap();

        let matching: Vec<&Module> = entries
            .iter()
            .filter(|&v| v.parts().nth(depth).unwrap() == modident)
            .copied()
            .collect();
        {
            let _temp = entries
                .drain(..)
                .filter(|&v| v.parts().nth(depth).unwrap() != modident)
                .collect();
            entries = _temp;
        }

        write_line(outfile, depth, &format!("pub mod {} {{", modident))?;

        let subwritten = write_includes(
            matching
                .iter()
                .filter(|v| v.len() > depth + 1)
                .copied()
                .collect(),
            outfile,
            depth + 1,
            basepath,
        )?;

        written += subwritten;

        if subwritten != matching.len() {
            let modname = matching[0].parts().take(depth + 1).join(".");

            if basepath.is_some() {
                write_line(
                    outfile,
                    depth + 1,
                    &format!("include!(\"{}.rs\");", modname),
                )?;
            } else {
                write_line(
                    outfile,
                    depth + 1,
                    &format!("include!(concat!(env!(\"OUT_DIR\"), \"/{}.rs\"));", modname),
                )?;
            }
            written += 1;
        }

        write_line(outfile, depth, "}")?;
    }

    Ok(written)
}

fn write_line(outfile: &mut fs::File, depth: usize, line: &str) -> Result<()> {
    outfile.write_all(format!("{}{}\n", ("    ").to_owned().repeat(depth), line).as_bytes())
}
