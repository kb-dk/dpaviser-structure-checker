package dk.statsbiblioteket.dpaviser.eventhandlers;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.AttributeParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.TreeEventHandler;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Test InputStreamSizeChecker.  Unfortunately the failures inside a ResultCollector is almost impossible to get at
 * without using reflection, so for now I just test for a success or not (which maps directly to "are there any failures
 * or not"), and use the report (which list the failures) as the failure text.
 */

@SuppressWarnings("deprecation")
public class InputStreamSizeCheckerTest {

    @Test
    public void testEmptyInputStreamMinimumOne() {
        ResultCollector resultCollector = new ResultCollector(getClass().getName(), "?");
        TreeEventHandler h = new InputStreamSizeChecker(resultCollector, (e) -> true, 1);
        h.handleAttribute(new InputStreamParsingEventWrapper("testSingleCharInputStream", "".getBytes()));
        Assert.assertSame(resultCollector.isSuccess(), false, resultCollector.toReport());
    }

    @Test
    public void testTwoCharInputStreamMinimumOne() {
        ResultCollector resultCollector = new ResultCollector(getClass().getName(), "?");
        TreeEventHandler h = new InputStreamSizeChecker(resultCollector, (e) -> true, 1);
        h.handleAttribute(new InputStreamParsingEventWrapper("testSingleCharInputStream", "XX".getBytes()));
        Assert.assertSame(resultCollector.isSuccess(), true, resultCollector.toReport());
    }

    private static class InputStreamParsingEventWrapper extends AttributeParsingEvent {
        private byte[] input;

        public InputStreamParsingEventWrapper(String name, byte[] input) {
            super(name);
            this.input = input;
        }

        @Override
        public InputStream getData() throws IOException {
            return new ByteArrayInputStream(input);
        }

        @Override
        public String getChecksum() throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}
