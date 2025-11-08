const std = @import("std");

// The main build entry point.
//
// This script configures both development and production builds for the `PoeditSmarty` project.
// It supports cross-compilation for Windows, macOS, and Linux, including ARM64 targets.
//
// Usage:
// - `zig build` — standard local build (host platform)
// - `zig build run` — build and run
// - `zig build test` — run unit tests (automatically discovers all *.test.zig files)
// - `zig build prod` — build production binaries for all supported platforms/architectures

pub fn build(b: *std.Build) void {
    // ------------------------------------------------------------
    // Build configuration options
    // ------------------------------------------------------------

    // Read the `--version` command-line option (optional).
    // Defaults to "1.0.0-local" if not specified.
    const version = b.option([]const u8, "version", "Application version")
        orelse "1.0.0-local";

    // Add global build options that can be imported into Zig modules.
    const options = b.addOptions();
    options.addOption([]const u8, "version", version);

    const options_module = options.createModule();

    // ------------------------------------------------------------
    // Default (host) build target and optimization
    // ------------------------------------------------------------

    // Automatically detects the host target (e.g., your dev machine).
    const target = b.standardTargetOptions(.{});

    // Allow the user to choose the optimization level (Debug, ReleaseSafe, etc.).
    const optimize = b.standardOptimizeOption(.{});

    // ------------------------------------------------------------
    // Main executable definition
    // ------------------------------------------------------------

    // Define the main executable to build locally.
    const exe = b.addExecutable(.{
        .name = "PoeditSmarty",
        .root_module = b.createModule(.{
            .root_source_file = b.path("src/main.zig"),
            .target = target,
            .optimize = optimize,
            .imports = &.{},
        }),
    });

    exe.root_module.addImport("BuildOptions", options_module);
    b.installArtifact(exe);

    // ------------------------------------------------------------
    // Run step (`zig build run`)
    // ------------------------------------------------------------

    // Create a custom build step for running the app.
    const run_step = b.step("run", "Run the app");
    const run_cmd = b.addRunArtifact(exe);
    run_step.dependOn(&run_cmd.step);

    // Ensure the binary is built and installed before running.
    run_cmd.step.dependOn(b.getInstallStep());

    // Forward any user-provided CLI arguments.
    if (b.args) |args| {
        run_cmd.addArgs(args);
    }

    // ------------------------------------------------------------
    // Test step (`zig build test`)
    // ------------------------------------------------------------

    // Define and run unit tests for the project.
    const exe_tests = b.addTest(.{
        .root_module = exe.root_module,
    });

    const run_exe_tests = b.addRunArtifact(exe_tests);
    const test_step = b.step("test", "Run tests");
    test_step.dependOn(&run_exe_tests.step);

    // Automatically discover and add all *.test.zig files
    addAllTestFiles(b, test_step, "src", target, optimize, options_module);

    // ------------------------------------------------------------
    // Production builds step (`zig build prod`)
    // ------------------------------------------------------------

    // A build step that compiles optimized production binaries for all supported targets.
    const prod_step = b.step("prod", "Build production versions for all platforms");

    // Define the list of supported production targets.
    // Each includes a platform (Windows/macOS/Linux) and both x86_64 + ARM64 architectures.
    const targets = [_]struct {
        name: []const u8,
        query: std.Target.Query,
    }{
        // Windows targets
        .{
            .name = "windows-x86_64",
            .query = .{
                .cpu_arch = .x86_64,
                .os_tag = .windows,
                .abi = .gnu,
            },
        },
        .{
            .name = "windows-aarch64",
            .query = .{
                .cpu_arch = .aarch64,
                .os_tag = .windows,
                .abi = .gnu,
            },
        },

        // Linux targets (using musl for static compatibility)
        .{
            .name = "linux-x86_64",
            .query = .{
                .cpu_arch = .x86_64,
                .os_tag = .linux,
                .abi = .musl,
            },
        },
        .{
            .name = "linux-aarch64",
            .query = .{
                .cpu_arch = .aarch64,
                .os_tag = .linux,
                .abi = .musl,
            },
        },

        // macOS targets
        .{
            .name = "macos-x86_64",
            .query = .{
                .cpu_arch = .x86_64,
                .os_tag = .macos,
                .abi = .none,
            },
        },
        .{
            .name = "macos-aarch64",
            .query = .{
                .cpu_arch = .aarch64,
                .os_tag = .macos,
                .abi = .none,
            },
        },
    };

    // ------------------------------------------------------------
    // Cross-compilation loop for production targets
    // ------------------------------------------------------------

    // Loop over each platform/architecture target and create an optimized build.
    for (targets) |t| {
        // Resolve the specific cross-compilation target.
        const prod_target = b.resolveTargetQuery(t.query);

        // Create the production executable (optimized for binary size).
        const prod_exe = b.addExecutable(.{
            .name = "PoeditSmarty",
            .root_module = b.createModule(.{
                .root_source_file = b.path("src/main.zig"),
                .target = prod_target,
                .optimize = .ReleaseSmall, // Optimize for binary size in production
                .imports = &.{},
            }),
        });

        prod_exe.root_module.addImport("BuildOptions", options_module);

        // Install each target's output in a `dist/<platform>` subdirectory.
        const install_dir = b.fmt("dist/{s}", .{t.name});
        const prod_install = b.addInstallArtifact(prod_exe, .{
            .dest_dir = .{
                .override = .{
                    .custom = install_dir,
                },
            },
        });

        // Add this build to the `prod` step so all are built together.
        prod_step.dependOn(&prod_install.step);
    }
}

// ------------------------------------------------------------
// Helper function to recursively discover *.test.zig files
// ------------------------------------------------------------

/// Recursively walks through the specified directory and adds all files
/// ending with `.test.zig` to the test step.
fn addAllTestFiles(
    b: *std.Build,
    test_step: *std.Build.Step,
    dir_path: []const u8,
    target: std.Build.ResolvedTarget,
    optimize: std.builtin.OptimizeMode,
    options_module: *std.Build.Module,
) void {
    var dir = std.fs.cwd().openDir(dir_path, .{ .iterate = true }) catch |err| {
        std.debug.print("Warning: Failed to open directory '{s}': {}\n", .{ dir_path, err });
        return;
    };
    defer dir.close();

    var walker = dir.walk(b.allocator) catch |err| {
        std.debug.print("Warning: Failed to walk directory '{s}': {}\n", .{ dir_path, err });
        return;
    };
    defer walker.deinit();

    while (walker.next() catch null) |entry| {
        // Only process regular files ending with .test.zig
        if (entry.kind == .file and std.mem.endsWith(u8, entry.basename, ".test.zig")) {
            const full_path = b.fmt("{s}/{s}", .{ dir_path, entry.path });

            // Create a test executable for this file
            const test_exe = b.addTest(.{
                .root_module = b.createModule(.{
                    .root_source_file = b.path(full_path),
                    .target = target,
                    .optimize = optimize,
                    .imports = &.{},
                }),
            });

            // Add BuildOptions to test modules so they can access build configuration
            test_exe.root_module.addImport("BuildOptions", options_module);

            // Run the test
            const run_test = b.addRunArtifact(test_exe);
            test_step.dependOn(&run_test.step);

            // Optional: Print discovered test files during build
            // std.debug.print("Discovered test: {s}\n", .{full_path});
        }
    }
}
