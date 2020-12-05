/**
 * Package name
 */

package parse;

/**
 * Imports 3'd parts library
 */

import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.DisplayName;

/**
 * Imports library
 */

import core.Logger;
import core.PotFile;


/**
 * ParseData test
 */


class ParseDataTest {

    private Logger log;
    private PotFile spyFile;

    /**
     * Before each test
     */

    @BeforeEach
    void setupEach() {
        log = new Logger("log");
        spyFile = Mockito.spy(new PotFile("xxx.pot", log));

        ParseData.renderPatternKeywords();
    }

    /**
     * Validate {t}string data{/t}
     */

    @Test
    @DisplayName("Validate one line {t}string data{/t}")
    void parseOneLine() {

        ParseData parser = new ParseData("test.tpl", spyFile, log);

        parser.parse("{t}string data one line{/t}", 1);
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(1), eq("string data one line"));

        log.deleteAndClose();
    }

    @Test
    @DisplayName("Validate one line with space { t parameters }string data{/t}")
    void parseOneLineWithSpace() {
        ParseData parser = new ParseData("test.tpl", spyFile, log);

        parser.parse("{ t parameters }string data with space{/t}", 1);
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(1), eq("string data with space"));

        parser.parse("{ t (parameters) }string data with space{/t}", 2);
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(2), eq("string data with space"));

        parser.parse("{t nick=$userName day=$dayOfTheWeek}Hello %1, today is %2.7{/t}", 3);
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(3), eq("Hello %1, today is %2.7"));

        log.deleteAndClose();
    }


    @Test
    @DisplayName("Validate one line with space { t parameters }string data{/t} { t parameters }string data2{/t}")
    void parseOneLineWithSpaceMultiSameLine() {
        ParseData parser = new ParseData("test.tpl", spyFile, log);

        parser.parse("{ t parameters }string data{/t} { t parameters }string data2{/t}", 1);
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(1), eq("string data"));
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(1), eq("string data2"));

        log.deleteAndClose();
    }

    @Test
    @DisplayName("Validate multi lines {t parameters}string data\ndata2\ndata3{/t}")
    void parseMultilines() {
        ParseData parser = new ParseData("test.tpl", spyFile, log);

        parser.parse("{t parameters}string data", 1);
        parser.parse("data2", 2);
        parser.parse("data3{/t}", 3);
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(3), eq("string data data2 data3"));

        log.deleteAndClose();
    }

    @Test
    @DisplayName("Validate {_(\"Text to be (localized) 11\")}")
    void parseKeywordsRegex() {
        ParseData parser = new ParseData("test.tpl", spyFile, log);

        parser.parse("{ _ (\"Text to be (localized) 10\")  }", 1);
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(1), eq("Text to be (localized) 10"));

        parser.parse("{_(\"Text to be (localized) 11\")}", 2);
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(2), eq("Text to be (localized) 11"));

        log.deleteAndClose();
    }

    @Test
    @DisplayName("Validate <option value=\"{__('vegetarian')}\">{__('vegetarian')}</option>")
    void parseKeywordsRegexMultiSameLine() {
        ParseData parser = new ParseData("test.tpl", spyFile, log);

        parser.parse("<option value=\"{_('vegetarian')}\">{_('vegetarians')}</option>", 1);
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(1), eq("vegetarian"));
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(1), eq("vegetarians"));

        log.deleteAndClose();
    }

    @Test
    @DisplayName("Validate {Text to be localized10 | _}")
    void parseKeywordsRegexPipe() {
        ParseData parser = new ParseData("test.tpl", spyFile, log);

        parser.parse("{\"Text to be localized 10\"|_}", 1);
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(1), eq("Text to be localized 10"));

        parser.parse("{ \"Text to be localized 11\" | _ }", 2);
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(2), eq("Text to be localized 11"));

        parser.parse("{ \"Text to be (localized) 12\" | _ }", 3);
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(3), eq("Text to be (localized) 12"));

        log.deleteAndClose();
    }

    @Test
    @DisplayName("Validate { ngettext('product', \"products\", $products_count) }")
    void parseNgettext() {
        ParseData parser = new ParseData("test.tpl", spyFile, log);

        parser.parse("{ ngettext('product', \"products\", $products_count) }}", 1);
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(1), eq("products"));
        verify(spyFile, times(1)).addKey(eq("test.tpl"), eq(1), eq("product"));

        log.deleteAndClose();
    }
}