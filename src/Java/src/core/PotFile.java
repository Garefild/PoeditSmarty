/**
 * Package name
 */

package core;

/**
 * Imports 3'd parts library
 */

import java.io.*;
import java.util.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

/**
 * Imports library
 */

import core.Graphic;

/**
 * Pot file object
 */

public class PotFile {

    /**
     * File encoding
     */

    private String _encoding = "UTF-8";

    /**
     * PoeditLogger log object
     */

    private final Logger _log;


    /**
     * stream object
     */

    private OutputStreamWriter _fileStream;

    /**
     * hold keys data
     */

    private final Map<String, List<String>> _data = new HashMap<>();

    /**
     * Constrictor
     *
     * @param filename filename with path
     * @param log      log object
     */

    public PotFile(String filename, Logger log) {
        this._log = log;
        this.createPotFile(filename);
    }

    /**
     * Constrictor
     *
     * @param filename filename with path
     * @param encode   file encoding
     * @param log      log object
     */

    public PotFile(String filename, String encode, Logger log) {
        this._log = log;
        this._encoding = encode;
        this.createPotFile(filename);
    }

    /**
     * add result translate to file
     *
     * @param sourceFile String : file to scanner
     * @param lineNumber int    : line number in file
     * @param text       String : the text to localize
     */

    public void addKey(String sourceFile, int lineNumber, String text) {
        if (text != null && !text.isEmpty()) {
            if (this._data.containsKey(text)) {
                this._data.get(text).add(0, "#: " + sourceFile + ":" + lineNumber + System.getProperty("line.separator"));
            } else {
                List<String> tempList = new ArrayList<>();
                tempList.add("#: " + sourceFile + ":" + lineNumber + System.getProperty("line.separator"));
                tempList.add("msgid \"" + text + "\"" + System.getProperty("line.separator"));
                tempList.add("msgstr \"\"" + System.getProperty("line.separator") + System.getProperty("line.separator"));

                this._data.put(text, tempList);
            }

            String string = "[file:line]: " + sourceFile + ":" + lineNumber + ", [text detected]: " + text;

            this._log.info(string);
//            Graphic.printText(36, string, "info");
        }
    }

    /**
     * write file header
     */

    private void writeHeader() {
        try {
            String output;
            SimpleDateFormat formatter;

            formatter = new SimpleDateFormat("yyyy-MM-dd H:mmZ");
            output = formatter.format(new Date());

            this._fileStream.write(
                    "# " + System.getProperty("line.separator") +
                            "# Copyright (C) Garefild" + System.getProperty("line.separator") +
                            "# Garefild https://github.com/Garefild/PoeditSmarty, 2015-2035." + System.getProperty("line.separator") +
                            "#" + System.getProperty("line.separator") + System.getProperty("line.separator") +
                            "msgid \"\"" + System.getProperty("line.separator") +
                            "msgstr \"\"" + System.getProperty("line.separator") +
                            "\"Project-Id-Version: PACKAGE VERSION\\n\"" + System.getProperty("line.separator") +
                            "\"Report-Msgid-Bugs-To: \\n\"" + System.getProperty("line.separator") +
                            "\"POT-Creation-Date: " + output + "\\n\"" + System.getProperty("line.separator") +
                            "\"MIME-Version: 1.0\\n\"" + System.getProperty("line.separator") +
                            "\"Content-Type: text/plain; charset=UTF-8\\n\"" + System.getProperty("line.separator") +
                            "\"Content-Transfer-Encoding: 8bit\\n\"" + System.getProperty("line.separator") + System.getProperty("line.separator")
            );

            this._fileStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            this._log.warning(e.getMessage());
        }
    }

    /**
     * Save file and close
     */

    public void save() {
        try {
            for (Map.Entry<String, List<String>> entry : this._data.entrySet()) {
                for (int i = 0; i < entry.getValue().size(); i++) {
                    this._fileStream.write(entry.getValue().get(i));
                }
            }
            this._log.info("save and close pot file");
            this._fileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            this._log.warning(e.getMessage());
        }
    }

    /**
     * Create the file stream
     *
     * @param filename filename with path
     */

    private void createPotFile(String filename) {
        try {
            File file = new File(filename);
            String pathWithoutFileName = file.getParent();

            if (pathWithoutFileName != null) {
                this._log.info("create dir: " + pathWithoutFileName);
                file.getParentFile().mkdirs();
            }

            this._fileStream = new OutputStreamWriter(new FileOutputStream(filename), Charset.forName(this._encoding).newEncoder());
            this.writeHeader();
            this._log.info("Generate pot file: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            this._log.warning("Error Couldn't create file: " + e);
        }
    }
}
