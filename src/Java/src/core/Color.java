/**
 * Package name
 */

package core;

/**
 * Color class
 *
 * 30 black
 * 31 red
 * 32 green
 * 33 yellow
 * 34 blue
 * 35 magenta
 * 36 cyan
 * 37 white
 * 40 black background
 * 41 red background
 * 42 green background
 * 43 yellow background
 * 44 blue background
 * 45 magenta background
 * 46 cyan background
 * 47 white background
 * 1 make bright (usually just bold)
 * 21 stop bright (normalizes boldness)
 * 4 underline
 * 24 stop underline
 * 0 clear all formatting
 */

public class Color {

    /**
     * set general color
     * @param color int
     */

    public static String setColor(int color) {
        return (char) 27 + "[" + color + "m";
    }

    /**
     * clear all set color
     */

    public static String clearColor() {
        return (char) 27 + "[0m";
    }

    /**
     * set text color
     *
     * @param color int
     * @param text String
     * @return String
     */

    public static String setTextColor(int color, String text) {
        return (char)27 + "[" + color + "m" + text + (char)27 + "[0m";
    }
}
