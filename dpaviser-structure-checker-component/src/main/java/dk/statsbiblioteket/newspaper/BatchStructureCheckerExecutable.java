package dk.statsbiblioteket.newspaper;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.CallResult;
import dk.statsbiblioteket.medieplatform.autonomous.NewspaperBatchAutonomousComponentUtils;
import dk.statsbiblioteket.medieplatform.autonomous.RunnableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

/**
 * This is a sample component to serve as a guide to developers
 */
public class BatchStructureCheckerExecutable {
    private static Logger log = LoggerFactory.getLogger(BatchStructureCheckerExecutable.class);

    /**
     * The class must have a main method, so it can be started as a command line tool
     *
     * @param args the arguments.
     * @throws Exception
     * @see NewspaperBatchAutonomousComponentUtils#parseArgs(String[])
     */
    public static void main(String... args) throws Exception {
        System.exit(doMain(args));
    }

    /**
     * The class must have a main method, so it can be started as a command line tool
     *
     * @param args the arguments.
     * @throws Exception
     * @see NewspaperBatchAutonomousComponentUtils#parseArgs(String[])
     */
    private static int doMain(String... args) throws Exception {
        log.info("Starting with args {}", Arrays.asList(args));

        //Parse the args to a properties construct
        Properties properties = NewspaperBatchAutonomousComponentUtils.parseArgs(args);

        RunnableComponent<Batch> component = new BatchStructureCheckerComponent(properties);
        CallResult result = NewspaperBatchAutonomousComponentUtils.startAutonomousComponent(properties, component);
        log.info(result.toString());
        return result.containsFailures();
    }
}
