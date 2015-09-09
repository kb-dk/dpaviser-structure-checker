package dk.statsbiblioteket.dpaviser.eventhandlers;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.NodeBeginsParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.NodeEndParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.DefaultTreeEventHandler;

import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This page sequence number checker keeps a count of pages (pdf files) encountered. When nodeEnd day event is reached,
 * it checks if the count is continous and starts at 1. It then resets the count.
 */
public class PageSequenceChecker extends DefaultTreeEventHandler {

    TreeSet<Integer> pageNumbers;
    private ResultCollector resultCollector;

    public PageSequenceChecker(ResultCollector resultCollector) {
        this.resultCollector = resultCollector;
        pageNumbers = new TreeSet<>();
    }

    @Override
    public void handleNodeBegin(NodeBeginsParsingEvent event) {
        if (event.getName().endsWith(".pdf")) {
            int pageNumber = getPageNumber(event.getName());
            pageNumbers.add(pageNumber);

        }
    }

    private final Pattern pageNumberPattern = Pattern.compile("\\#(\\d+)\\.pdf");

    protected int getPageNumber(String name) {
        // Name is like this infomed/JYP/2015/06/01/JYP20150601L11#0002.pdf
        Matcher matcher = pageNumberPattern.matcher(name);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            // throw same exception as previous implementation.
            throw new ArrayIndexOutOfBoundsException("pattern did not match: " + name);
        }
    }

    private final Pattern isDayPattern = Pattern.compile("^.*/[A-Z0-9]+/[0-9]{4}/(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$");

    protected boolean isDay(String name) {
        return isDayPattern.matcher(name).matches();
    }

    @Override
    public void handleNodeEnd(NodeEndParsingEvent event) {
        if (isDay(event.getName())) {
            if (pageNumbers.size() > 0) {  // any PDF files found in folder?
                if (pageNumbers.first() != 1 || pageNumbers.size() != pageNumbers.last()) {
                    resultCollector.addFailure(event.getName(), "Structure", "", "Wrong count of pages");
                }
            }
            pageNumbers.clear();
        }
    }
}
