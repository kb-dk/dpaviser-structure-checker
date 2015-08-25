package dk.statsbiblioteket.dpaviser.eventhandlers;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.NodeBeginsParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.NodeEndParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.DefaultTreeEventHandler;

import java.util.NoSuchElementException;
import java.util.TreeSet;

/**
 * This page sequence number checker keeps a count of pages (pdf files) encountered. When nodeEnd day event
 * is reached, it checks if the count is continous and starts at 1. It then resets the count.
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
        if (event.getName().endsWith(".pdf")){
            int pageNumber = getPageNumer(event.getName());
            pageNumbers.add(pageNumber);

        }
    }

    protected int getPageNumer(String name) {
        //Name is like this infomed/JYP/2015/06/01/JYP20150601L11#0002.pdf
        String[] pathElements = name.split("/");
        String filename = pathElements[pathElements.length - 1];
        String[] names = filename.split("\\.");
        String firstName = names[0];
        String lastName = names[1];
        String[] parts = firstName.split("#");
        String page = parts[1];
        return Integer.parseInt(page,10);
    }

    protected boolean isDay(String name) {
        return name.matches("^.*/[A-Z0-9]+/[0-9]{4}/(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$");
    }

    @Override
    public void handleNodeEnd(NodeEndParsingEvent event) {
        if (isDay(event.getName())) {
            try {
                if (pageNumbers.first() != 1 || pageNumbers.size() != pageNumbers.last()) {
                    resultCollector.addFailure(event.getName(), "Structure", "", "Wrong count of pages");
                }
                pageNumbers.clear();
            } catch (NoSuchElementException e) {
                // FIXME: Fails for TRA for Infomedia batch 1.
                throw new RuntimeException("pageNumbers=" + pageNumbers, e);
            }
        }
    }

}
