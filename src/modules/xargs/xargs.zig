const std = @import("std");
const xArgsOption = @import("types/option.type.zig").xArgsOption;
const xParsedArg = @import("types/parsed.type.zig").xParsedArg;
const helpPrinter = @import("services/help.service.zig");
const parserService = @import("services/parser.service.zig");

const mem = std.mem;
const Allocator = std.mem.Allocator;

pub const xArgsParser = struct {
    allocator: Allocator,
    options: []const xArgsOption,
    parsed: std.StringHashMap(xParsedArg),
    positional: std.ArrayList([]const u8),

    pub fn init(allocator: Allocator, options: []const xArgsOption) xArgsParser {
        return .{
            .allocator = allocator,
            .options = options,
            .parsed = std.StringHashMap(xParsedArg).init(allocator),
            .positional = .{},
        };
    }

    pub fn deinit(self: *xArgsParser) void {
        var valueIterator = self.parsed.valueIterator();
        while (valueIterator.next()) |parsedValue| {
            parsedValue.deinit();
        }
        self.parsed.deinit();
        self.positional.deinit(self.allocator);
    }

    pub fn parse(self: *xArgsParser, argv: []const []const u8) !void {
        try self.parseArguments(argv);
        try self.validateAndApplyDefaults();
    }

    fn parseArguments(self: *xArgsParser, argv: []const []const u8) !void {
        var currentIndex: usize = 0;
        while (currentIndex < argv.len) : (currentIndex += 1) {
            const currentArg = argv[currentIndex];

            if (mem.startsWith(u8, currentArg, "--")) {
                try parserService.processLongOption(&self.parsed, self.allocator, self.options, argv, &currentIndex);
            } else if (mem.startsWith(u8, currentArg, "-") and currentArg.len > 1) {
                try parserService.processShortOption(&self.parsed, self.allocator, self.options, argv, &currentIndex);
            } else {
                try self.positional.append(self.allocator, currentArg);
            }
        }
    }

    fn validateAndApplyDefaults(self: *xArgsParser) !void {
        for (self.options) |option| {
            if (!self.parsed.contains(option.name)) {
                if (option.default) |defaultValue| {
                    try parserService.applyDefault(&self.parsed, self.allocator, option, defaultValue);
                } else if (option.required) {
                    return error.MissingRequiredOption;
                }
            }
        }
    }

    pub fn get(self: *const xArgsParser, optionName: []const u8) ?*const xParsedArg {
        return self.parsed.getPtr(optionName);
    }

    pub fn printHelp(self: *const xArgsParser) void {
        helpPrinter.printHelp(self.options);
    }
};
