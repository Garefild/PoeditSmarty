/**
 * Package name
 */

package core;

/**
 * Graphic class
 */

public class Graphic {

    /**
     * Current line position
     */

    protected static int _linePosition = 0;

    /**
     * Clean line
     */

    protected static final String CleanLine = "\u001b[0K";

    /**
     * print header string
     */

    public static void printHeader(String version) {
        setColor(33);
        println("/**\n" +
                " * ______              _ _ _   _____                      _         \n" +
                " * | ___ \\            | (_) | /  ___|                    | |        \n" +
                " * | |_/ /__   ___  __| |_| |_\\ `--. _ __ ___   __ _ _ __| |_ _   _ \n" +
                " * |  __/ _ \\ / _ \\/ _` | | __|`--. \\ '_ ` _ \\ / _` | '__| __| | | |\n" +
                " * | | | (_) |  __/ (_| | | |_/\\__/ / | | | | | (_| | |  | |_| |_| |\n" +
                " * \\_|  \\___/ \\___|\\__,_|_|\\__\\____/|_| |_| |_|\\__,_|_|   \\__|\\__, |\n" +
                " *  https://github.com/Garefild/PoeditSmarty                   __/ |\n" +
                " *                                                            |___/ \n" +
                " * @Author Garefild\n" +
                " * @Copyright 2010-2035\n" +
                " * @version " + version + " \n" +
                " */\n" + (char) 27 + "[36m"
        );

        Graphic._linePosition += 14;
    }

    /**
     * Clean screen
     */

    public static void clearDisplay() {
        Graphic._linePosition = 0;
        System.out.print("\033[H\033[2J");
    }

    /**
     * Get correct line position
     *
     * @return int
     */

    public static int getCorrectLine() {
        return Graphic._linePosition;
    }

    /**
     * Prints a String and then terminate the line.
     *
     * @param text The <code>String</code> to be printed.
     */

    public static void println(String text) {
        System.out.println(CleanLine + text);
        ++Graphic._linePosition;
    }

    /**
     * Prints a String and then terminate the line.
     *
     * @param text The <code>String</code> to be printed.
     */

    public static void println(String text, int color) {
        Graphic.println(Color.setTextColor(color, text));
    }

    /**
     * Prints a string.  If the argument is <code>null</code> then the string
     * <code>"null"</code> is printed.  Otherwise, the string's characters are
     * converted into bytes according to the platform's default character
     * encoding, and these bytes are written in exactly the manner of the
     *
     * @param text The <code>String</code> to be printed
     */

    public static void print(String text) {
        System.out.print(text);

        if (text.contains("\n")) {
            ++Graphic._linePosition;
        }
    }

    /**
     * Prints a string.  If the argument is <code>null</code> then the string
     * <code>"null"</code> is printed.  Otherwise, the string's characters are
     * converted into bytes according to the platform's default character
     * encoding, and these bytes are written in exactly the manner of the
     *
     * @param text The <code>String</code> to be printed
     */

    public static void print(String text, int color) {
        Graphic.print(Color.setTextColor(color, text));
    }

    /**
     * Writes a formatted string to this output stream using the specified
     * format string and arguments.
     * previous invocations of other formatting methods on this object.
     *
     * @param format A format string as described in <a
     *               href="../util/Formatter.html#syntax">Format string syntax</a>
     * @param args   Arguments referenced by the format specifiers in the format
     *               string.  If there are more arguments than format specifiers, the
     *               extra arguments are ignored.  The number of arguments is
     *               variable and may be zero.  The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by
     *               <cite>The Java&trade; Virtual Machine Specification</cite>.
     *               The behaviour on a
     *               <tt>null</tt> argument depends on the <a
     *               href="../util/Formatter.html#syntax">conversion</a>.
     */

    public static void format(String format, Object... args) {
        System.out.format(format, args);
        ++Graphic._linePosition;
    }

    /**
     * Set terminal text color
     */

    public static void setColor(int color) {
        Graphic.print(Color.setColor(color));
    }

    /**
     * set text color
     *
     * @param color int
     * @param text  String
     * @return String
     */

    public static String setTextColor(int color, String text) {
        return Color.setTextColor(color, text);
    }

    /**
     * Clear color to default
     */

    public static void clearColor() {
        Graphic.print(Color.clearColor());
    }

    /**
     * print text
     *
     * @param color int
     * @param text  String
     */

    public static void printText(int color, String text, String type) {
        Graphic.print(Color.setTextColor(color, "[" + type + "] "));
        Graphic.println(Color.setTextColor(33, text));
    }

    /**
     * course position
     *
     * @param y int
     */

    public static void setCoursePosition(int y) {
        Graphic._linePosition = y;
        System.out.print("\033[" + y + ";0H");
    }

    /**
     * progress
     */

    public static void progress(int progressPercentage) {
        int i = 0;
        final int width = 30; // progress bar width in chars
        int tempPosition = Graphic._linePosition;

        Graphic.setCoursePosition(tempPosition + 1);
        Graphic.print(CleanLine + progressPercentage + "%", 33);

        if (progressPercentage < 10) {
            Graphic.print("   [");
        } else if (progressPercentage < 100) {
            Graphic.print("  [");
        } else {
            Graphic.print(" [");
        }

        for (; i < (progressPercentage * width / 100); i++) {
            Graphic.print("█");
        }

        for (; i < width; i++) {
            Graphic.print("▒", 36);
        }

        Graphic.print("] ", 33);
        Graphic.setCoursePosition(tempPosition);
    }
}
