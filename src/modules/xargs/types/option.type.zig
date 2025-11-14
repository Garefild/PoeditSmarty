const xArgType = @import("xargs.type.zig").xArgType;
const xArgValue = @import("xargs.type.zig").xArgValue;

pub const xArgsOption = struct {
    type: xArgType,
    name: []const u8,
    alias: ?u8 = null,
    isArray: bool = false,
    default: ?xArgValue = null,
    required: bool = false,
    description: []const u8,
};
