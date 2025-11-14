const std = @import("std");
const build_options = @import("BuildOptions");

pub const Color = enum {
    red,
    cyan,
    blue,
    white,
    reset,
    green,
    yellow,
    magenta,
    brightRed,
    brightBlue,
    brightCyan,
    brightGreen,
    brightWhite,
    brightYellow,
    brightMagenta,
};

pub fn colorCode(c: Color) u8 {
    return switch (c) {
        .red => 31,
        .blue => 34,
        .cyan => 36,
        .reset => 0,
        .white => 37,
        .green => 32,
        .yellow => 33,
        .magenta => 35,
        .brightRed => 91,
        .brightCyan => 96,
        .brightBlue => 94,
        .brightWhite => 97,
        .brightGreen => 92,
        .brightYellow => 93,
        .brightMagenta => 95,
    };
}

pub fn colorize(allocator: std.mem.Allocator, text: []const u8, color: Color) ![]u8 {
    return std.fmt.allocPrint(allocator, "\x1b[{d}m{s}\x1b[39m", .{ colorCode(color), text });
}

pub fn reset() []const u8 {
    return "\x1b[0m";
}
