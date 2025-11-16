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

pub const Context = struct {
    parser: xArgsParser,
    args: [][:0]u8,
    allocator: std.mem.Allocator,

    pub fn deinit(self: *Context) void {
        self.parser.deinit();
        std.process.argsFree(self.allocator, self.args);
    }
};

pub fn init(allocator: std.mem.Allocator) !Context {
    const args = try std.process.argsAlloc(allocator);
    errdefer std.process.argsFree(allocator, args);

    var parser = xArgsParser.init(allocator, &options);
    const argv = if (args.len > 1) args[1..] else &[_][]const u8{};

    for (argv) |arg| {
        if (std.mem.eql(u8, arg, "-h") or std.mem.eql(u8, arg, "--help")) {
            parser.printHelp();
            return error.HelpPrinted;
        }
    }

    parser.parse(argv) catch |parseError| {
        if (parseError == error.MissingRequiredOption) {
            parser.printMissingArguments();
        } else {
            std.debug.print("Error parsing arguments: {}\n", .{parseError});
        }
        parser.printHelp();
        parser.deinit();
        return parseError;
    };

    if (parser.get("log")) |logArg| {
        if (logArg.getSingle()) |logValue| {
            if (logValue.bool) {
                logger.enable();
            } else {
                logger.disable();
            }
        }
    }

    if (parser.get("logFile")) |logFileArg| {
        if (logFileArg.getSingle()) |logFileValue| {
            const logPath = logFileValue.string;
            try logger.initFile(logPath);
            std.log.info("Logging to file: {s}", .{logPath});
        }
    }

    return Context{
        .parser = parser,
        .args = args,
        .allocator = allocator,
    };
}

