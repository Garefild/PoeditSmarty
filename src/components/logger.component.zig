const std = @import("std");
const color = @import("color.component.zig");

var logFile: ?std.fs.File = null;
var logMutex: std.Thread.Mutex = .{};
var logEnabled: std.atomic.Value(bool) = std.atomic.Value(bool).init(true);
var logMinLevel: std.atomic.Value(u8) = std.atomic.Value(u8).init(@intFromEnum(std.log.Level.debug));

threadlocal var fileBuffer: [8192]u8 = undefined;
threadlocal var colorBuffer: [256]u8 = undefined;

pub fn logFn(
    comptime level: std.log.Level,
    comptime scope: @Type(.enum_literal),
    comptime format: []const u8,
    args: anytype,
) void {
    if (!logEnabled.load(.acquire)) return;
    if (@intFromEnum(level) > logMinLevel.load(.acquire)) return;

    logMutex.lock();
    defer logMutex.unlock();

    writeConsoleLog(level, scope, format, args);

    if (logFile) |file| {
        writeFileLog(file, level, scope, format, args);
    }
}

fn writeConsoleLog(
    comptime level: std.log.Level,
    comptime scope: @Type(.enum_literal),
    comptime format: []const u8,
    args: anytype,
) void {
    var lockBuffer: [64]u8 = undefined;
    const stderr, _ = std.debug.lockStderrWriter(&lockBuffer);
    defer std.debug.unlockStderrWriter();

    // Map log level to color
    const levelColor: color.Color = switch (level) {
        .debug => .brightCyan,
        .info => .brightGreen,
        .warn => .brightYellow,
        .err => .brightRed,
    };

    // Use thread-local buffer for colorization
    var fba = std.heap.FixedBufferAllocator.init(&colorBuffer);
    const allocator = fba.allocator();

    const coloredLevel = color.colorize(allocator, level.asText(), levelColor) catch level.asText();

    stderr.writeAll("[ ") catch return;
    stderr.writeAll(coloredLevel) catch return;
    stderr.writeAll(" ]") catch return;

    if (scope != .default) {
        stderr.writeAll("[ ") catch return;
        stderr.writeAll(@tagName(scope)) catch return;
        stderr.writeAll(" ]") catch return;
    }

    stderr.writeAll(" ") catch return;
    stderr.print(format ++ "\n", args) catch return;
}

fn writeFileLog(
    file: std.fs.File,
    comptime level: std.log.Level,
    comptime scope: @Type(.enum_literal),
    comptime format: []const u8,
    args: anytype,
) void {
    const prefix = std.fmt.bufPrint(&fileBuffer, "[ {s} ]", .{level.asText()}) catch return;
    file.writeAll(prefix) catch return;

    if (scope != .default) {
        const scopePrefix = std.fmt.bufPrint(&fileBuffer, "[ {s} ]", .{@tagName(scope)}) catch return;
        file.writeAll(scopePrefix) catch return;
    }

    const message = std.fmt.bufPrint(&fileBuffer, " " ++ format ++ "\n", args) catch return;
    file.writeAll(message) catch return;
}

pub fn enable() void {
    logEnabled.store(true, .release);
}

pub fn disable() void {
    logEnabled.store(false, .release);
}

pub fn setLevel(level: std.log.Level) void {
    logMinLevel.store(@intFromEnum(level), .release);
}

pub fn isEnabled() bool {
    return logEnabled.load(.acquire);
}

pub fn initFile(path: []const u8) !void {
    const file = try std.fs.cwd().createFile(path, .{
        .read = true,
    });

    try file.seekFromEnd(0);
    logFile = file;

    std.log.info("Log file initialized: {s}", .{path});
}

pub fn clearFileWriter() void {
    logMutex.lock();
    defer logMutex.unlock();

    if (logFile) |file| {
        defer file.close();
        file.sync() catch return;
    }
}
