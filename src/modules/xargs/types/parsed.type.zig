const std = @import("std");
const ArgValue = @import("xargs.type.zig").xArgValue;
const Allocator = std.mem.Allocator;

pub const xParsedArg = struct {
    name: []const u8,
    values: std.ArrayList(ArgValue),
    allocator: Allocator,

    pub fn deinit(self: *xParsedArg) void {
        for (self.values.items) |value| {
            switch (value) {
                .string => |str| {
                    self.allocator.free(str);
                },
                else => {},
            }
        }

        self.values.deinit(self.allocator);
    }

    pub fn getSingle(self: *const xParsedArg) ?ArgValue {
        if (self.values.items.len > 0) {
            return self.values.items[0];
        }

        return null;
    }

    pub fn getArray(self: *const xParsedArg) []ArgValue {
        return self.values.items;
    }
};
