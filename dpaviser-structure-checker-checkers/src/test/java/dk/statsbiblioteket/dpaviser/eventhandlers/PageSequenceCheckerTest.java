package dk.statsbiblioteket.dpaviser.eventhandlers;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.NodeBeginsParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.NodeEndParsingEvent;
import org.testng.annotations.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;


public class PageSequenceCheckerTest {


    @Test
    public void testIsDay1(){
        PageSequenceChecker sequenceChecker = new PageSequenceChecker(null);
        assertThat(sequenceChecker.isDay("infomed/JYP/2015/06/01"), is(true));
    }

    @Test
    public void testIsDay2(){
        PageSequenceChecker sequenceChecker = new PageSequenceChecker(null);
        assertThat(sequenceChecker.isDay("infomed/JYP/2015/01/31"), is(true));
    }


    @Test
    public void testIsDay3(){
        PageSequenceChecker sequenceChecker = new PageSequenceChecker(null);
        assertThat(sequenceChecker.isDay("infomed/JYP/215/06/01"), is(false));
    }

    @Test
    public void testGetPageNumber(){
        PageSequenceChecker sequenceChecker = new PageSequenceChecker(null);
        assertThat(sequenceChecker.getPageNumer("infomed/JYP/2015/06/01/JYP20150601L11#0002.pdf"), is(2));
    }

    @Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
    public void testGetPageNumber2(){
        PageSequenceChecker sequenceChecker = new PageSequenceChecker(null);
        assertThat(sequenceChecker.getPageNumer("infomed/JYP/2015/06/01/JYP20150601L11#0002,pdf"), is(2));
    }


    @Test
    public void testGetPageNumber3(){
        PageSequenceChecker sequenceChecker = new PageSequenceChecker(null);
        assertThat(sequenceChecker.getPageNumer("infomed/JYP/2015/06/01/JYP20150601L11#9999.pdf"), is(9999));
    }


    @Test
    public void testSimpleCount(){
        ResultCollector resultCollector = mock(ResultCollector.class);
        PageSequenceChecker sequenceChecker = new PageSequenceChecker(resultCollector);
        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/01"));
        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0002.pdf"));
        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0002.pdf"));
        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/01"));
        verify(resultCollector).addFailure(anyString(), anyString(), anyString(), anyString());//TODO test with correct values
    }

    @Test
    public void testTwoDaysOneError(){
        ResultCollector resultCollector = mock(ResultCollector.class);
        PageSequenceChecker sequenceChecker = new PageSequenceChecker(resultCollector);

        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/01"));
        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0002.pdf"));
        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0002.pdf"));
        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/01"));
        verify(resultCollector,times(1)).addFailure(anyString(), anyString(), anyString(), anyString());//TODO test with correct values

        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/02"));
        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/02/JYP20150602L11#0001.pdf"));
        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/02/JYP20150602L11#0001.pdf"));
        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/02"));

        verify(resultCollector,times(1)).addFailure(anyString(),anyString(),anyString(),anyString());//TODO test with correct values
    }

    @Test
    public void testWithBadOrdering(){
        ResultCollector resultCollector = mock(ResultCollector.class);
        PageSequenceChecker sequenceChecker = new PageSequenceChecker(resultCollector);
        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/01"));

        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0003.pdf"));
        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0003.pdf"));

        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0002.pdf"));
        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0002.pdf"));

        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0001.pdf"));
        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0001.pdf"));

        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/01"));
        verifyZeroInteractions(resultCollector);
    }


    @Test
    public void testWithDuplicates(){
        ResultCollector resultCollector = mock(ResultCollector.class);
        PageSequenceChecker sequenceChecker = new PageSequenceChecker(resultCollector);
        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/01"));

        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/01/JYP20150601L12#0002.pdf"));
        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/01/JYP20150601L12#0002.pdf"));

        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0002.pdf"));
        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0002.pdf"));

        sequenceChecker.handleNodeBegin(new NodeBeginsParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0001.pdf"));
        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/01/JYP20150601L11#0001.pdf"));

        sequenceChecker.handleNodeEnd(new NodeEndParsingEvent("infomed/JYP/2015/06/01"));
        verifyZeroInteractions(resultCollector);
    }


}