package dk.statsbiblioteket.dpaviser.eventhandlers;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.AttributeParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.DefaultTreeEventHandler;
import dk.statsbiblioteket.util.Strings;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.function.Predicate;

public class InputStreamSizeChecker extends DefaultTreeEventHandler {
    private final ResultCollector resultCollector;
    private final long minimumSize;
    private Predicate<AttributeParsingEvent> checkThisAttribute;

    public InputStreamSizeChecker(ResultCollector resultCollector, Predicate<AttributeParsingEvent> checkThisAttribute, long minimumSize) {
        this.resultCollector = resultCollector;
        this.checkThisAttribute = checkThisAttribute;
        this.minimumSize = minimumSize;
    }

    @Override
    public void handleAttribute(AttributeParsingEvent event) {
        if (checkThisAttribute.test(event)) {
            try (InputStream is = new BufferedInputStream(event.getData())) {
                long i = 0;
                while (is.read() != -1) {
                    if (++i > minimumSize) {
                        return;
                    }
                }
                resultCollector.addFailure(
                        event.getName(),
                        "input stream too small: " + i + " <  " + minimumSize,
                        getClass().getSimpleName(),
                        "Error verifying PDF: ");

            } catch (Exception e) {
                resultCollector.addFailure(
                        event.getName(),
                        "exception",
                        getClass().getSimpleName(),
                        "Error verifying PDF: " + e.toString(),
                        Strings.getStackTrace(e)
                );
            }
        }
    }
}
