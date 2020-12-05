/**
 * Package name
 */

package parse;

/**
 * Imports 3'd parts library
 */

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Imports library
 */

import core.Logger;
import core.PotFile;

/**
 * ParseData
 */

public class ParseData {

    /**
     * PoeditLogger log object
     */

    private final Logger _log;

    /**
     * Parse file path
     */

    private final String _filePath;

    /**
     * pot file object
     */

    private final PotFile _potFile;

    /**
     * If data is multilines
     */

    private boolean _multiline = false;

    /**
     * multiline string
     */

    private String _multilineString;

    /**
     * regex of pattern keywords
     */

    private static Pattern _patternKeywords;

    /**
     * Constrictor
     *
     * @param filepath file path
     * @param log      log object
     */

    public ParseData(String filepath, PotFile potfile, Logger log) {
        this._log = log;
        this._potFile = potfile;
        this._filePath = filepath;
    }

    /**
     * Parse data string
     *
     * @param data string to parse
     */

    public void parse(String data, int lineNumber) {
        if (this._multiline) {
            this._multiline = this.parseTagMultiLines(data, lineNumber);
            return;
        }

        if (!this.parsePatterns(data, lineNumber)) {
            this._multiline = this.parseTag(data, lineNumber);
        }
    }

    /**
     * render pattern keywords
     */

    public static void renderPatternKeywords() {
        ParseData._patternKeywords = Pattern.compile(ParseData.createPatterns("_"), Pattern.MULTILINE);
    }

    /**
     * render pattern keywords
     *
     * @param patternKeys List of pattern keywords
     */

    public static void renderPatternKeywords(List<String> patternKeys) {
        String patternKeywords = "";

        for (String key : patternKeys) {
            if (ParseData._patternKeywords == null) {
                patternKeywords = createPatterns(key);
            } else {
                patternKeywords = patternKeywords.concat("|" + createPatterns(key));
            }
        }

        ParseData._patternKeywords = Pattern.compile(patternKeywords, Pattern.MULTILINE);
    }

    /**
     * Parse tags
     *
     * @param data       string to parse
     * @param lineNumber correct file line
     * @return boolean
     */

    private boolean parseTag(String data, int lineNumber) {
        boolean tTag = this.contains(data, "\\{\\s*t.*?\\}");

        if (tTag && data.contains("{/t}")) {
            this.parseTagOneLine(data, lineNumber);
            return false;
        }

        if (tTag) {
            this.parseTagInitMultiLines(data, lineNumber);
            return true;
        }

        if (data.contains("ngettext")) {
            this.parseTagNgettext(data, lineNumber);
        }

        return false;
    }

    /**
     * Parse t tag one line
     *
     * @param data       string to parse
     * @param lineNumber correct file line
     */

    private void parseTagOneLine(String data, int lineNumber) {
        Pattern patternTag = Pattern.compile("\\{\\s*t.*?\\}([^\\{]+)\\{/t\\}");
        Matcher matcherTag = patternTag.matcher(data);

        while (matcherTag.find()) {
            if (matcherTag.group(1) != null) {
                this._potFile.addKey(this._filePath, lineNumber, matcherTag.group(1));
            }
        }
    }

    /**
     * Init multi line tag
     *
     * @param data       string to parse
     * @param lineNumber correct file line
     */

    private void parseTagInitMultiLines(String data, int lineNumber) {
        this._log.info("Start {t} in line " + lineNumber);

        Pattern patternTag = Pattern.compile("\\{\\s*t.*?\\}([^\\{]+)");
        Matcher matcherTag = patternTag.matcher(data);

        if (matcherTag.find()) {
            if (matcherTag.group(1) != null) {
                this._multilineString = matcherTag.group(1) + " ";
            }
        }
    }

    /**
     * Multi line tag parse
     *
     * @param data       string to parse
     * @param lineNumber correct file line
     * @return boolean
     */

    private boolean parseTagMultiLines(String data, int lineNumber) {
        if(!data.contains("{/t}")) {
            this._multilineString += data.trim() + " ";
            return true;
        }

        Pattern patternTag  = Pattern.compile("([^\\{]+)\\{/t\\}");
        Matcher matcherTag  = patternTag.matcher(data);

        matcherTag.find();
        this._multilineString += matcherTag.group(1);
        this._potFile.addKey(this._filePath, lineNumber,  this._multilineString);
        this._log.info("Ent {/t} in line " + lineNumber);

        return false;
    }

    /**
     * Parse ngettext
     *
     * @param data       string to parse
     * @param lineNumber correct file line
     */

    private void parseTagNgettext(String data, int lineNumber) {
        Pattern patternTag = Pattern.compile("\\{(\\s*)ngettext.*[\\w\\s$]*(\\((.*[\\w\\s,$]*)\\))(\\s*)\\}");
        Matcher matcherTag = patternTag.matcher(data);

        while (matcherTag.find()) {
            if (matcherTag.group(3) == null) {
                continue;
            }

            String[] splits = matcherTag.group(3).split(", ");

            this._potFile.addKey(this._filePath, lineNumber,  splits[0].replaceAll("[\"']", ""));
            this._potFile.addKey(this._filePath, lineNumber,  splits[1].replaceAll("[\"']", ""));
        }
    }

    /**
     * if string contains pattern
     *
     * @param data    string data to validate
     * @param pattern string pattern validator
     * @return boolean
     */

    private boolean contains(String data, String pattern) {
        return Pattern.compile(pattern).matcher(data).find();
    }

    /**
     * Parse patterns
     *
     * @param data       string to parse
     * @param lineNumber correct file line
     * @return boolean
     */

    private boolean parsePatterns(String data, int lineNumber) {
        Matcher matcher = ParseData._patternKeywords.matcher(data);

        if (!matcher.find()) {
            return false;
        }

        int start = 0;

        while (matcher.find(start)) {
            String result = (matcher.group(3) == null) ? matcher.group(6) : matcher.group(3);
            this._potFile.addKey(this._filePath, lineNumber, result);

            start = matcher.start() + 1;
        }

        return true;
    }

    /**
     * key to regex patterns
     *
     * @param keys String
     * @return String
     */

    private static String createPatterns(String keys) {
        // {_("Text to be localized")}
        String pattern1 = "(?<=\\{)(\\s*)" + keys + "(\\s*)\\((?:\"|')(.+?)(?:\"|')\\)(\\s*)(?=\\})";

        // {("Text to be localized")|_}
        String pattern2 = "(?<=\\{)(\\s*)(?:\"|')(.+?)(\"|')(\\s*)\\|(\\s*)" + keys + "(\\s*)\\}";

        return pattern1 + "|" + pattern2;
    }
}
