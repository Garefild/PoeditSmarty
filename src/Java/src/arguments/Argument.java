/**
 * Package name
 */

package arguments;

/**
 * Imports 3'd parts library
 */

import java.util.List;
import java.util.ArrayList;

/**
 * Argument Objects
 */

public class Argument {

    /**
     * the name of the option
     */

    public String opt;

    /**
     * the long representation of the option
     */

    public String longOpt;

    /**
     * description of the option
     */

    public String description;

    /**
     * specifies whether the argument value of this Option is optional
     */

    public boolean optionalArg;

    /**
     * specifies whether this option is required to be present
     */

    public boolean required;

    /**
     * hold arguments of the object
     */

    private final List<String> args = new ArrayList<>();

    /**
     * Creates an Option using the specified parameters.
     *
     * @param opt         String: short representation of the option
     * @param longOpt     String: the long representation of the option
     * @param hasArg      boolean: specifies whether the Option takes an argument or not
     * @param required    boolean: flag indicating if an argument is required
     * @param description String: describes the function of the option
     */

    public Argument(String opt, String longOpt, boolean hasArg, boolean required, String description) {
        this.opt = opt;
        this.longOpt = longOpt;
        this.required = required;
        this.optionalArg = hasArg;
        this.description = description;
    }

    /**
     * if 'opt' is null, then it is a 'long' option
     *
     * @return String: Returns the 'unique' Option identifier.
     */

    public String getKey() {
        return (opt == null) ? this.longOpt : this.opt;
    }

    /**
     * Query to see if this Option is mandatory
     *
     * @return boolean flag indicating whether this Option is mandatory
     */

    public boolean isRequired() {
        return this.required;
    }

    /**
     * is args empty
     *
     * @return boolean
     */

    public boolean isArgsEmpty() {
        return this.hasArgs() && this.args.isEmpty();
    }

    /**
     * Sets whether this Option is mandatory.
     *
     * @param required specifies whether this Option is mandatory
     */

    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Reviewed for compliance of the string to the object
     *
     * @param arg String: String review
     * @return boolean
     */

    public boolean valid(String arg) {
        return arg.equals("-" + this.opt) || arg.equals("--" + this.longOpt);
    }

    /**
     * Check to see if an object can hold arguments
     *
     * @return boolean
     */

    public boolean hasArgs() {
        return this.optionalArg;
    }

    /**
     * add arguments to the object
     *
     * @param arg String: add arguments to the object
     */

    public void addArgs(String arg) {
        this.args.add(arg);
    }

    /**
     * get first arg
     *
     * @return String
     */

    public String getFirsArg() {
        return this.args.get(0);
    }

    /**
     * get args
     *
     * @return List<String>
     */

    public List<String> getArgs() {
        return this.args;
    }
}
