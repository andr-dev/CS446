use std::{fs, io::Result, path::Path, thread, time::Duration};

fn main() -> Result<()> {
    println!("cargo:rerun-if-changed=../protos");

    let protos = get_protos()?;

    if !protos.is_empty() {
        prost_build::compile_protos(
            &protos,
            &[Path::new("../protos")
                .canonicalize()?
                .to_str()
                .unwrap()
                .to_string()],
        )?;
    }

    thread::sleep(Duration::from_secs(10));

    Ok(())
}

fn get_protos() -> Result<Vec<String>> {
    let mut protos = Vec::new();

    for proto_path in fs::read_dir("../protos")? {
        protos.push(
            proto_path?
                .path()
                .canonicalize()?
                .to_str()
                .unwrap()
                .to_string(),
        );
    }

    Ok(protos)
}
