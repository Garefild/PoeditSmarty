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

    context.init(allocator) catch return;
}
