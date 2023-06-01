#####################
### Build Backend ###
#####################

FROM rust:1.64.0-alpine3.16 as builder_backend

# Install musl libc

RUN apk --no-cache add musl-dev

# Create backend rust package

WORKDIR /prod

RUN USER=root cargo new server

COPY Cargo.toml Cargo.lock /prod/server/

WORKDIR /prod/server

# Dummy build to cache dependencies

RUN cargo build --target x86_64-unknown-linux-musl

# Copy in sources

COPY src /prod/server/src/

## Touch main.rs to prevent cached build

RUN touch /prod/server/src/main.rs

# Build application

RUN cargo build --target x86_64-unknown-linux-musl


###############
### Runtime ###
###############

FROM alpine:3.16 AS runtime

RUN apk --no-cache add sqlite

# Copy binary and built frontend code
COPY --from=builder_backend /prod/server/target/x86_64-unknown-linux-musl/debug/server /prod/

# Copy DB and Rocket.toml
COPY db/store.db Rocket.toml /prod/

EXPOSE 3030

WORKDIR /prod

CMD ["/prod/server"]

