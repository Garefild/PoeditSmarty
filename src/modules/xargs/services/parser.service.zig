const std = @import("std");
const xArgType = @import("../types/xargs.type.zig").xArgType;
const xArgValue = @import("../types/xargs.type.zig").xArgValue;
const xParsedArg = @import("../types/parsed.type.zig").xParsedArg;
const xArgsOption = @import("../types/option.type.zig").xArgsOption;

const mem = std.mem;
const Allocator = std.mem.Allocator;

pub fn parseValue(argType: xArgType, valueStr: []const u8) !xArgValue {
    return switch (argType) {
        .int => xArgValue{
            .int = try std.fmt.parseInt(i64, valueStr, 10),
        },
        .bool => xArgValue{
            .bool = mem.eql(u8, valueStr, "true") or mem.eql(u8, valueStr, "1"),
        },
        .float => xArgValue{
            .float = try std.fmt.parseFloat(f64, valueStr),
        },
        .string => xArgValue{
            .string = valueStr,
        },
    };
}

pub fn findOption(options: []const xArgsOption, optionName: ?[]const u8, optionAlias: ?u8) ?xArgsOption {
    for (options) |option| {
        if (optionName) |name| {
            if (mem.eql(u8, option.name, name)) {
                return option;
            }
        }
        if (optionAlias) |alias| {
            if (option.alias) |optAlias| {
                if (optAlias == alias) {
                    return option;
                }
            }
        }
    }
    return null;
}

pub fn addValue(
    parsedMap: *std.StringHashMap(xParsedArg),
    allocator: Allocator,
    option: xArgsOption,
    valueStr: []const u8,
) !void {
    const argValue = try parseValue(option.type, valueStr);

    const result = try parsedMap.getOrPut(option.name);
    if (!result.found_existing) {
        result.value_ptr.* = xParsedArg{
            .name = option.name,
            .values = .{},
            .allocator = allocator,
        };
    }

    if (!option.isArray and result.value_ptr.values.items.len > 0) {
        return error.DuplicateOption;
    }

    try result.value_ptr.values.append(allocator, argValue);
}

pub fn addBoolValue(
    parsedMap: *std.StringHashMap(xParsedArg),
    allocator: Allocator,
    option: xArgsOption,
    boolValue: bool,
) !void {
    const argValue = xArgValue{ .bool = boolValue };

    const result = try parsedMap.getOrPut(option.name);
    if (!result.found_existing) {
        result.value_ptr.* = xParsedArg{
            .name = option.name,
            .values = .{},
            .allocator = allocator,
        };
    }

    try result.value_ptr.values.append(allocator, argValue);
}

pub fn applyDefault(
    parsedMap: *std.StringHashMap(xParsedArg),
    allocator: Allocator,
    option: xArgsOption,
    defaultValue: xArgValue,
) !void {
    const result = try parsedMap.getOrPut(option.name);
    if (!result.found_existing) {
        result.value_ptr.* = xParsedArg{
            .name = option.name,
            .values = .{},
            .allocator = allocator,
        };
    }
    try result.value_ptr.values.append(allocator, defaultValue);
}

pub fn processLongOption(
    parsedMap: *std.StringHashMap(xParsedArg),
    allocator: Allocator,
    options: []const xArgsOption,
    argv: []const []const u8,
    currentIndex: *usize,
) !void {
    const arg = argv[currentIndex.*];
    const optionName = arg[2..];

    if (findOption(options, optionName, null)) |option| {
        if (option.type == .bool) {
            try addBoolValue(parsedMap, allocator, option, true);
        } else if (option.isArray) {
            currentIndex.* += 1;
            while (currentIndex.* < argv.len and !mem.startsWith(u8, argv[currentIndex.*], "-")) : (currentIndex.* += 1) {
                try addValue(parsedMap, allocator, option, argv[currentIndex.*]);
            }
            currentIndex.* -= 1;
        } else {
            currentIndex.* += 1;
            if (currentIndex.* >= argv.len) {
                return error.MissingValue;
            }
            try addValue(parsedMap, allocator, option, argv[currentIndex.*]);
        }
    } else {
        return error.UnknownOption;
    }
}

pub fn processShortOption(
    parsedMap: *std.StringHashMap(xParsedArg),
    allocator: Allocator,
    options: []const xArgsOption,
    argv: []const []const u8,
    currentIndex: *usize,
) !void {
    const arg = argv[currentIndex.*];
    const optionAlias = arg[1];

    if (findOption(options, null, optionAlias)) |option| {
        if (option.type == .bool) {
            try addBoolValue(parsedMap, allocator, option, true);
        } else if (option.isArray) {
            currentIndex.* += 1;
            while (currentIndex.* < argv.len and !mem.startsWith(u8, argv[currentIndex.*], "-")) : (currentIndex.* += 1) {
                try addValue(parsedMap, allocator, option, argv[currentIndex.*]);
            }
            currentIndex.* -= 1;
        } else {
            currentIndex.* += 1;
            if (currentIndex.* >= argv.len) {
                return error.MissingValue;
            }
            try addValue(parsedMap, allocator, option, argv[currentIndex.*]);
        }
    } else {
        return error.UnknownOption;
    }
}
