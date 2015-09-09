package dk.statsbiblioteket.dpaviser;

import dk.statsbiblioteket.dpaviser.eventhandlers.PageSequenceChecker;
import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.TreeProcessorAbstractRunnableComponent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.EventRunner;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.TreeEventHandler;
import dk.statsbiblioteket.newspaper.Validator;
import dk.statsbiblioteket.newspaper.schematron.StructureValidator;
import dk.statsbiblioteket.newspaper.schematron.XmlBuilderEventHandler;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

/**
 * Checks the directory structure of a batch. This should run both at Ninestars and at SB.
 */
public class BatchStructureCheckerComponent extends TreeProcessorAbstractRunnableComponent {

    public static final String DEMANDS_SCH = "dpaviser_structure_demands.sch.xml";
    public static final String DID_NOT_GENERATE_XML_ = "Did not generate xml representation of directory structure. Could not complete tests.";

    public BatchStructureCheckerComponent(Properties properties) {
        super(properties);
    }

    @Override
    public String getEventID() {
        return "Structure_Checked";
    }

    @Override
    /**
     * Check the batch-structure tree received for errors. (I.e. we are going to check the received tree for
     * errors. The tree received represents a batch structure, which is the structure of a batch).
     *
     * @throws IOException
     */

    public void doWorkOnItem(Batch batch, ResultCollector resultCollector) throws Exception {

        final List<TreeEventHandler> eventHandlers = asList(
                new XmlBuilderEventHandler(),
                new PageSequenceChecker(resultCollector)
        );

        EventRunner eventRunner = new EventRunner(createIterator(batch), eventHandlers, resultCollector);

        eventRunner.run();

        // Afterwards locate and ask an XmlBuilderEventHandler for an XML "serialization" of the events seen.

        TreeEventHandler xmlEventHandler = eventHandlers.stream()
                .filter(handler -> handler instanceof XmlBuilderEventHandler)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(DID_NOT_GENERATE_XML_));

        String xml = ((XmlBuilderEventHandler) xmlEventHandler).getXml();

        // --

        storeBatchStructure(batch, new ByteArrayInputStream(xml.getBytes(UTF_8)));

        final List<Validator> validators = asList(
                new StructureValidator(DEMANDS_SCH)
        );
        long validationCount = validators.stream()
                .map(v -> v.validate(batch, new ByteArrayInputStream(xml.getBytes(UTF_8)), resultCollector))
                .count();

    }
}
