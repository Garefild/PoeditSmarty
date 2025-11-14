const std = @import("std");

pub const xArgType = enum {
    int,
    bool,
    float,
    string,
};

pub const xArgValue = union(xArgType) {
    int: i64,
    bool: bool,
    float: f64,
    string: []const u8,
};
