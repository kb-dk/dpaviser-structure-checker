package dk.statsbiblioteket.dpaviser;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.newspaper.schematron.StructureValidator;
import org.testng.annotations.Test;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class StructureDemandsSchTest {
    private final int MAX_RESULTS = 100;
    private static final String TEST_BATCH_ID = "1";
    private static final String GOOD_BATCH = "infomedia-batch-good.xml";

    /**
     * Test that a good (error-free) batch runs through without problems
     */
    @Test
    public void testBatchNodeCheckerSuccess() throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(GOOD_BATCH));

        ResultCollector resultCollector = new ResultCollector("Schematron Demands Checker", "v0.1", MAX_RESULTS);

        Batch batch = new Batch();
        batch.setBatchID(TEST_BATCH_ID);
        batch.setRoundTripNumber(1);

        StructureValidator structureValidator = new StructureValidator(BatchStructureCheckerComponent.DEMANDS_SCH);
        structureValidator.validate(batch, document, resultCollector);

        if (!resultCollector.isSuccess()) {
            System.out.println(resultCollector.toReport());
        }
        assertTrue(resultCollector.isSuccess(), "Found failure during run on good batch");
    }


    /**
     * Test that a batch with a bad folder name (source) inside the batch folder gives error
     */
    @Test
    public void testBatchNodeCheckerBadSource() throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(GOOD_BATCH));

        // Get all nodes (in our sense of the word) in document order
        NodeList nodeList = document.getElementsByTagName("node");

        Node sourceNode = getNodeWithName(nodeList, "infomedia/DRS");

        replaceShortName(sourceNode, "drS");
        //System.out.println(DOM.domToString(document));

        ResultCollector resultCollector = new ResultCollector("Schematron Demands Checker", "v0.1", MAX_RESULTS);

        Batch batch = new Batch();
        batch.setBatchID(TEST_BATCH_ID);
        batch.setRoundTripNumber(1);

        StructureValidator structureValidator = new StructureValidator(BatchStructureCheckerComponent.DEMANDS_SCH);
        structureValidator.validate(batch, document, resultCollector);

        assertFalse(resultCollector.isSuccess(), "Bad source dir name unexpectedly passed structure demands checker");
    }


    /**
     * Return the first node in the given NodeList with the given name (as value of attribute "name")
     */
    private Node getNodeWithName(NodeList nodeList, String name) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            NamedNodeMap attributes = node.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Node attribute = attributes.item(j);
                String localName = attribute.getNodeName();
                if (localName.equals("name")) {
                    if (attribute.getNodeValue().equals(name)) {
                        return node;
                    }
                }
            }
        }
        throw new RuntimeException("Could not find node with name: '" + name + "'");
    }

    /**
     * Replace the value of the shortName attribute of given node with given replacement.
     */
    private Node replaceShortName(Node node, String replacement) {
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            if (attribute.getNodeName().equals("shortName")) {
                attribute.setNodeValue(replacement);
                //node.set
                return node;
            }
        }
        return node;
    }
}

