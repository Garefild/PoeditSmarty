/**
 * Package name
 */

package arguments;

/**
 * Imports 3'd parts library
 */

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Imports library
 */

import core.Logger;
import core.Graphic;

/**
 * Render arguments
 */

public class RenderArguments {

    /**
     * list of Option object
     */

    private final Map<String, Argument> _optsList = new HashMap<>();

    /**
     * valid list of Option object
     */

    private final Map<String, Argument> _validOptsList = new HashMap<>();

    /**
     * logger object
     */

    private static Logger _log;

    /**
     * Cli
     */

    public RenderArguments(Logger logClass) {
        _log = logClass;

        addOption("c", "code", true, false, "charset flag.");
        addOption("k", "key", true, false, "list of keywords.");
        addOption("f", "file", true, true, "list of input files.");
        addOption("o", "out", true, true, "expands to the name of output file.");


        addOption("l", "log", false, false, "Save log file.");
        addOption("d", "debug", false, false, "enable debug.");
        addOption("r", "result", false, false, "save po file in log folder");
        addOption("h", "help", false, false, "Display command line parameters for use.");
    }

    /**
     * Creates an Option using the specified parameters.
     *
     * @param opt         String: short representation of the option
     * @param longOpt     String: the long representation of the option
     * @param hasArg      boolean: specifies whether the Option takes an argument or not
     * @param required    boolean: flag indicating if an argument is required
     * @param description String: describes the function of the option
     */

    public void addOption(String opt, String longOpt, boolean hasArg, boolean required, String description) {
        this._optsList.put(opt, new Argument(opt, longOpt, hasArg, required, description));
    }

    /**
     * if has "key" args
     *
     * @param key String: key to search
     * @return boolean
     */

    public boolean has(String key) {
        return this._validOptsList.get(key) != null;
    }

    /**
     * get Option object
     *
     * @param key String: key to search
     * @return Option
     */

    public Argument getByKey(String key) {
        if (has(key) && !this._validOptsList.get(key).isArgsEmpty()) {
            return this._validOptsList.get(key);
        }

        return null;
    }

    /**
     * show args help
     */

    public void help() {
        Graphic.setColor(36);

        String args;
        String Required;

        for (Map.Entry<String, Argument> entry : _optsList.entrySet()) {
            if (entry.getValue().isRequired()) {
                Required = "<Required>";
            } else {
                Required = "";
            }

            if (entry.getValue().hasArgs()) {
                args = "<Args>";
            } else {
                args = "";
            }

            Graphic.format("-%-2s, --%-15s %-7s %-10s : %-30s" + System.getProperty("line.separator"), entry.getValue().opt, entry.getValue().longOpt, args, Required, entry.getValue().description);
        }
    }

    /**
     * Checking parameters
     *
     * @param args String[]: String array of args
     */

    public boolean parse(String[] args) {
        _log.info("Received arguments: " + Arrays.toString(args));

        boolean match = false;
        boolean error = false;

        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(args));

        for (Map.Entry<String, Argument> entry : _optsList.entrySet()) {
            for (String anArrayList : arrayList) {
                if (anArrayList.startsWith("-") || anArrayList.startsWith("--")) {
                    if (match) {
                        break;
                    } else if (entry.getValue().valid(anArrayList)) {
                        _validOptsList.put(entry.getKey(), entry.getValue());
                        match = true;
                    }
                } else if (match && entry.getValue().hasArgs()) {
                    this._validOptsList.get(entry.getKey()).addArgs(anArrayList);
                }
            }

            if (_validOptsList.get(entry.getKey()) == null && entry.getValue().isRequired()) {
                _log.warning("Missing argument (-" + entry.getKey() + ") or missing argument data");
                error = true;
            }

            match = false;
        }

        return error;
    }
}
