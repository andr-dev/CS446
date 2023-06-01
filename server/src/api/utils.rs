use data_encoding::HEXLOWER;
use ring::digest::{Context, SHA256};

pub(super) fn hash_password(password: &String) -> String {
    let mut context = Context::new(&SHA256);

    context.update(password.as_bytes());

    HEXLOWER.encode(context.finish().as_ref())
}
