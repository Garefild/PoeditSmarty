/**
 * Package name
 */

package core;

/**
 * Imports 3'd parts library
 */

import java.awt.*;
import java.io.File;
import java.util.Date;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.logging.Level;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.MissingResourceException;
import java.util.logging.SimpleFormatter;

/**
 * A Logger object is used to log messages for a specific
 * system or application component.  Loggers are normally named,
 * using a hierarchical dot-separated namespace.  Logger names
 * can be arbitrary strings, but they should normally be based on
 * the package name or class name of the logged component, such
 * as java.net or javax.swing.  In addition it is possible to create
 * "anonymous" Loggers that are not stored in the Logger namespace.
 */

public class Logger extends java.util.logging.Logger {
    /**
     * Log file path
     * String filePath
     */

    private String _filePath;

    /**
     * Simple file logging
     * FileHandler fileHandler
     */

    public FileHandler _fileHandler;

    /**
     * Protected method to construct a logger for a named subsystem.
     * <p>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers set to true.
     *
     * @param name A name for the logger.  This should
     *             be a dot-separated name and should normally
     *             be based on the package name or class name
     *             of the subsystem, such as java.net
     *             of the messages require localization.
     */

    public Logger(String name) {
        super(name, null);

        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tb %1$td, %1$tY %1$tH:%1$tM:%1$tS] %4$s: %5$s%6$s%n");
        setLevel(Level.ALL);
        createLogFile();
    }

    /**
     * close lock file
     */

    public void close() {
        this._fileHandler.close();
    }

    /**
     * delete and close file
     */

    public void deleteAndClose() {
        try {
            this._fileHandler.close();
            Files.delete(Paths.get(_filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Log a SEVERE message.
     * <p>
     * If the logger is currently enabled for the SEVERE message
     * level then the given message is forwarded to all the
     * registered output Handler objects.
     *
     * @param msg The string message (or a key in the message catalog)
     */

    public void severe(String msg) {
        log(Level.SEVERE, msg);
        this._fileHandler.flush();
    }

    /**
     * Log a WARNING message.
     * <p>
     * If the logger is currently enabled for the WARNING message
     * level then the given message is forwarded to all the
     * registered output Handler objects.
     *
     * @param msg The string message (or a key in the message catalog)
     */

    public void warning(String msg) {
        log(Level.WARNING, msg);
        this._fileHandler.flush();
    }

    /**
     * Log an INFO message.
     * <p>
     * If the logger is currently enabled for the INFO message
     * level then the given message is forwarded to all the
     * registered output Handler objects.
     *
     * @param msg The string message (or a key in the message catalog)
     */

    public void info(String msg) {
        log(Level.INFO, msg);
        this._fileHandler.flush();
    }

    /**
     * Log a CONFIG message.
     * <p>
     * If the logger is currently enabled for the CONFIG message
     * level then the given message is forwarded to all the
     * registered output Handler objects.
     *
     * @param msg The string message (or a key in the message catalog)
     */

    public void config(String msg) {
        log(Level.CONFIG, msg);
        this._fileHandler.flush();
    }

    /**
     * Log a FINE message.
     * <p>
     * If the logger is currently enabled for the FINE message
     * level then the given message is forwarded to all the
     * registered output Handler objects.
     *
     * @param msg The string message (or a key in the message catalog)
     */

    public void fine(String msg) {
        log(Level.FINE, msg);
        this._fileHandler.flush();
    }

    /**
     * Log a FINER message.
     * <p>
     * If the logger is currently enabled for the FINER message
     * level then the given message is forwarded to all the
     * registered output Handler objects.
     *
     * @param msg The string message (or a key in the message catalog)
     */

    public void finer(String msg) {
        log(Level.FINER, msg);
        this._fileHandler.flush();
    }

    /**
     * Log a FINEST message.
     * <p>
     * If the logger is currently enabled for the FINEST message
     * level then the given message is forwarded to all the
     * registered output Handler objects.
     *
     * @param msg The string message (or a key in the message catalog)
     */

    public void finest(String msg) {
        log(Level.FINEST, msg);
        this._fileHandler.flush();
    }

    /**
     * get log file result
     */

    public void getResult() {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().edit(new File(_filePath));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a file name
     *
     * @return String
     */

    private String getFileName() {
        DateFormat dateFormat = new SimpleDateFormat("ddMMYHHmmss");
        Date date = new Date();

        return dateFormat.format(date) + ".txt";
    }

    /**
     * Create log file
     */

    private void createLogFile() {
        /**
         * new source log folder
         */

        String root     = System.getProperty("java.io.tmpdir") + "Smarty/log/";
        this._filePath   = root + this.getFileName();

        if (!Files.isDirectory(Paths.get(root))) {
            new File(root).mkdirs();
        }

        try {

            /**
             * This block configure the logger with handler and formatter
             */

            this._fileHandler = new FileHandler(this._filePath);
            this.addHandler(this._fileHandler);

            /**
             * logger.setLevel(Level.ALL);
             */

            this._fileHandler.setFormatter(new SimpleFormatter());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
