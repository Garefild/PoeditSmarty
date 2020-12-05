/**
 * Imports library
 */

import core.Logger;
import core.Graphic;
import core.PotFile;
import parse.ParseData;
import parse.ParseFiles;
import arguments.RenderArguments;

/**
 * Imports 3'd parts library
 */

import java.util.Arrays;

/**
 * Poedit parser
 */

public class PoeditSmarty {

    /**
     * static classes assign
     */

    private static final Logger _log = new Logger("log");

    /**
     * CLI press args
     */

    private static final RenderArguments _commandLine = new RenderArguments(_log);

    /**
     * Entry point
     * @param args String arrays of args
     */

    public static void main(String[] args) {
        PotFile file;
        Graphic.clearDisplay();
        Graphic.printHeader("3.0.0");

        Graphic.printText(36, Arrays.toString(args), "info");

        if(_commandLine.parse(args) || _commandLine.has("h")) {
            _commandLine.help();
            Graphic.println("");
            return;
        }

        if(_commandLine.getByKey("k") == null) {
            ParseData.renderPatternKeywords();
        } else {
            ParseData.renderPatternKeywords(_commandLine.getByKey("k").getArgs());
        }

        if (_commandLine.getByKey("c") == null) {
            file = new PotFile(_commandLine.getByKey("o").getFirsArg(), _log);
        } else {
            file = new PotFile(_commandLine.getByKey("o").getFirsArg(), _commandLine.getByKey("c").getFirsArg(), _log);
        }

        new ParseFiles(_commandLine.getByKey("f").getArgs(), file, _log);

        file.save();
        beforeClose();
    }

    /**
     * do before close
     */

    private static void beforeClose() {
        try {
            if(_commandLine.has("d")) {
                _log.getResult();
                Thread.sleep(500);
            }

            if(!_commandLine.has("l")) {
                _log.deleteAndClose();
            } else {
                _log.close();
            }

            Graphic.clearColor();
        } catch (InterruptedException e) {
            _log.warning(e.toString());
        }

        Graphic.println("");
    }
}
