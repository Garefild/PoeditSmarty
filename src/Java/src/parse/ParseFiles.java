/**
 * Package name
 */

package parse;

/**
 * Imports 3'd parts library
 */

import java.io.File;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;

/**
 * Imports library
 */

import core.Logger;
import core.Graphic;
import core.PotFile;

/**
 * Parse files
 */

public class ParseFiles {

    /**
     * PoeditLogger log object
     */

    private final Logger _log;

    /**
     * pot file result
     */

    private final PotFile _potFile;

    /**
     * Files array length
     */

    private final int _filesSize;

    /**
     * Files array length
     */

    private int _filesCounter;

    /**
     * Constrictor
     *
     * @param files   list of file to parse
     * @param potFile PoeditTempFile: pot file object
     * @param log     log object
     */

    public ParseFiles(List<String> files, PotFile potFile, Logger log) {
        File file;
        this._log = log;
        this._potFile = potFile;
        this._filesSize = files.size();

        for (String fileString : files) {
            file = new File(fileString);
            this.parse(file, fileString);

            this._filesCounter++;
        }

        Graphic.progress(this._filesCounter * 100 / this._filesSize);
    }

    /**
     * Parse file
     *
     * @param file single file to parse
     */

    private void parse(File file, String filename) {
        ParseData parser = new ParseData(filename, this._potFile, this._log);

        if (file.exists() && !file.isDirectory()) {
            this.readFromFile(filename, parser);
        } else {
            this._log.warning("File does not exist: " + filename);
        }
    }

    /**
     * read from tpl file
     *
     * @param file template file
     */

    private void readFromFile(String file, ParseData parser) {
        try (
                BufferedReader reader = Files.newBufferedReader(Paths.get(file), StandardCharsets.UTF_8);
                LineNumberReader lineReader = new LineNumberReader(reader)
        ) {
            String lineString;
            Graphic.println("render file: " + Graphic.setTextColor(32, file));
            Graphic.progress(this._filesCounter * 100 / this._filesSize);

            while ((lineString = lineReader.readLine()) != null) {
                parser.parse(lineString, lineReader.getLineNumber());
            }
        } catch (IOException ex) {
            this._log.warning("File does not exist: " + ex);
        }
    }
}
