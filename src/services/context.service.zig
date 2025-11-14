const std = @import("std");
const logger = @import("../components/logger.component.zig");
const xArgsParser = @import("../modules/xargs/xargs.zig").xArgsParser;
const xArgValue = @import("../modules/xargs/types/xargs.type.zig").xArgValue;
const xArgsOption = @import("../modules/xargs/types/option.type.zig").xArgsOption;

const options = [_]xArgsOption{
    .{
        .type = .string,
        .name = "charset",
        .alias = 'c',
        .description = "Specify the character encoding of input files (e.g., utf8, ascii).",
        .default = xArgValue{ .string = "utf8" },
    },
    .{
        .type = .string,
        .name = "keywords",
        .alias = 'k',
        .isArray = true,
        .required = true,
        .description = "List of keywords to search for (can specify multiple times).",
    },
    .{
        .type = .string,
        .name = "files",
        .alias = 'f',
        .isArray = true,
        .required = true,
        .description = "Input files to process (can specify multiple files).",
    },
    .{
        .type = .string,
        .name = "out",
        .alias = 'o',
        .description = "Path for the output POT file.",
    },
    .{
        .type = .bool,
        .name = "log",
        .alias = 'l',
        .description = "Enable logging to standard output.",
        .default = xArgValue{ .bool = false },
    },
    .{
        .type = .string,
        .name = "logFile",
        .description = "File path to save logs.",
    },
};

pub fn init(allocator: std.mem.Allocator) !void {
    const args = try std.process.argsAlloc(allocator);
    defer std.process.argsFree(allocator, args);

    var parser = xArgsParser.init(allocator, &options);
    const argv = if (args.len > 1) args[1..] else &[_][]const u8{};

    for (argv) |arg| {
        if (std.mem.eql(u8, arg, "-h") or std.mem.eql(u8, arg, "--help")) {
            parser.printHelp();
            return error.HelpPrinted;
        }
    }

    try parser.parse(argv);
}

