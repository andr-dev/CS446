// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: user/create.proto

package org.uwaterloo.subletr.proto.user;

public final class Create {
  private Create() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_user_CreateUserRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_user_CreateUserRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_user_CreateUserResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_user_CreateUserResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\021user/create.proto\022\004user\"k\n\021CreateUserR" +
      "equest\022\022\n\nfirst_name\030\001 \001(\t\022\021\n\tlast_name\030" +
      "\002 \001(\t\022\r\n\005email\030\003 \001(\t\022\020\n\010password\030\004 \001(\t\022\016" +
      "\n\006gender\030\005 \001(\t\"%\n\022CreateUserResponse\022\017\n\007" +
      "user_id\030\001 \001(\003B$\n org.uwaterloo.subletr.p" +
      "roto.userP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_user_CreateUserRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_user_CreateUserRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_user_CreateUserRequest_descriptor,
        new java.lang.String[] { "FirstName", "LastName", "Email", "Password", "Gender", });
    internal_static_user_CreateUserResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_user_CreateUserResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_user_CreateUserResponse_descriptor,
        new java.lang.String[] { "UserId", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
