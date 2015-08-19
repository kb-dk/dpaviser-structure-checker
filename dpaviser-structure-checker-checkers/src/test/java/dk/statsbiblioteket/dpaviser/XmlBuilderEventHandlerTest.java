package dk.statsbiblioteket.dpaviser;

import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.TreeIterator;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.EventRunner;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.TreeEventHandler;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.filesystem.transforming.TransformingIteratorForFileSystems;
import dk.statsbiblioteket.newspaper.schematron.XmlBuilderEventHandler;
import dk.statsbiblioteket.util.xml.DOM;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 *
 */
public class XmlBuilderEventHandlerTest {

    @Test(groups = "testDataTest")
    public void testGetXml() throws Exception {
        TreeIterator iterator = new TransformingIteratorForFileSystems(new File("/Users/jrg/tmp/infomedia"),
                TransformingIteratorForFileSystems.GROUPING_PATTERN_DEFAULT_VALUE,
                ".*\\.pdf$",
                TransformingIteratorForFileSystems.CHECKSUM_POSTFIX_DEFAULT_VALUE,
                Arrays.asList(
                        TransformingIteratorForFileSystems.IGNORED_FILES_DEFAULT_VALUE
                                .split(",")));

        List<TreeEventHandler> handlers = new ArrayList<TreeEventHandler>();
        XmlBuilderEventHandler xmlBuilderEventHandler = new XmlBuilderEventHandler();
        handlers.add(xmlBuilderEventHandler);
        EventRunner eventRunner = new EventRunner(iterator, handlers, null);
        eventRunner.run();

        String xml = xmlBuilderEventHandler.getXml();
        assertTrue(xml.split("<node").length > 10, "Should be at least 10 nodes in document.");
        assertTrue(xml.split("<attribute").length > 10, "Should be at least 10 nodes in document.");
        assertNotNull(DOM.stringToDOM(xml), "Should have gotten parseable xml, not " + xml);
        System.out.println(xml);
    }
}
