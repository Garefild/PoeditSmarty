const std = @import("std");
const context = @import("services/context.service.zig");
const banner = @import("components/banner.component.zig");
const logger  = @import("components/logger.component.zig");

pub const std_options: std.Options = .{
    .log_level = .debug,
    .logFn = logger.logFn,
};

pub fn main() !void {
    std.debug.print(banner.getBanner(), .{});

    logger.enable();
    logger.setLevel(std.log.Level.debug);
    defer logger.clearFileWriter();

    var gpa = std.heap.GeneralPurposeAllocator(.{}){};
    const allocator = gpa.allocator();

    var ctx = context.init(allocator) catch |err| {
        if (err == error.HelpPrinted) {
            return; // Exit cleanly when help is printed
        }
        return err;
    };

    defer ctx.deinit();
    if (ctx.parser.get("charset")) |charsetArg| {
        if (charsetArg.getSingle()) |charsetValue| {
            std.log.debug("Charset: {s}", .{charsetValue.string});
        }
    }

    if (ctx.parser.get("keywords")) |keywordsArg| {
        const keywords = keywordsArg.getArray();
        std.log.debug("Keywords count: {d}", .{keywords.len});
        for (keywords) |keyword| {
            std.log.debug("Keyword: {s}", .{keyword.string});
        }
    }
    //
    // if (parser.get("files")) |filesArg| {
    //     const files = filesArg.getArray();
    //     std.debug.print("Files count: {d}", .{files.len});
    //     for (files) |file| {
    //         std.log.info("File: {s}", .{file.string});
    //     }
    // }
    //
    // if (parser.get("out")) |outArg| {
    //     if (outArg.getSingle()) |outValue| {
    //         std.debug.print("Output file: {s}", .{outValue.string});
    //     }
    // }

    std.log.info("Application initialization complete", .{});
}
