const std = @import("std");
const build_options = @import("BuildOptions");

pub fn getBanner() []const u8 {
    const version = build_options.version;

    return std.fmt.comptimePrint(
        "\n\x1b[38;5;208m" ++
            "______              _ _ _   _____                      _                   \n" ++
            "| ___ \\            | (_) | /  ___|                    | |                 \n" ++
            "| |_/ /__   ___  __| |_| |_\\ `--. _ __ ___   __ _ _ __| |_ _   _          \n" ++
            "|  __/ _ \\ / _ \\/ _` | | __|`--. \\ '_ ` _ \\ / _` | '__| __| | | |      \n" ++
            "| | | (_) |  __/ (_| | | |_/\\__/ / | | | | | (_| | |  | |_| |_| |         \n" ++
            "\\_|  \\___/ \\___|\\__,_|_|\\__\\____/|_| |_| |_|\\__,_|_|   \\__|\\__, | \n" ++
            " https://github.com/Garefild/PoeditSmarty                   __/ |          \n" ++
            "                                                           |___/           \n\x1b[39m" ++
            "Version: \x1b[38;5;197m{s}\x1b[39m\n" ++
            "\n",
        .{version},
    );
}
