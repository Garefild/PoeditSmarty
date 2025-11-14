const std = @import("std");
const xArgsOption = @import("../types/option.type.zig").xArgsOption;

pub fn printHelp(options: []const xArgsOption) void {
    std.debug.print("Options:\n", .{});
    for (options) |option| {
        printOptionHeader(option);
        printOptionType(option);
        printOptionFlags(option);
        printDefaultValue(option);
        std.debug.print("\n      {s}\n", .{option.description});
    }
}

fn printOptionHeader(option: xArgsOption) void {
    if (option.alias) |optionAlias| {
        std.debug.print("  -{c}, --{s}", .{ optionAlias, option.name });
    } else {
        std.debug.print("      --{s}", .{option.name});
    }
}

fn printOptionType(option: xArgsOption) void {
    const typeStr = switch (option.type) {
        .string => "<string>",
        .int => "<int>",
        .float => "<float>",
        .bool => "",
    };
    std.debug.print(" {s}", .{typeStr});
}

fn printOptionFlags(option: xArgsOption) void {
    if (option.isArray) {
        std.debug.print(" (can be repeated)", .{});
    }
    if (option.required) {
        std.debug.print(" (required)", .{});
    }
}

fn printDefaultValue(option: xArgsOption) void {
    if (option.default) |defaultValue| {
        std.debug.print(" (default: ", .{});
        switch (defaultValue) {
            .string => |strVal| std.debug.print("{s}", .{strVal}),
            .int => |intVal| std.debug.print("{}", .{intVal}),
            .float => |floatVal| std.debug.print("{d}", .{floatVal}),
            .bool => |boolVal| std.debug.print("{}", .{boolVal}),
        }
        std.debug.print(")", .{});
    }
}
