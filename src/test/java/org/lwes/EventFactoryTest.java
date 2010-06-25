package org.lwes;
/**
 * @author fmaritato
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertNotNull;

public class EventFactoryTest {

    private static transient Log log = LogFactory.getLog(EventFactoryTest.class);

    @Test
    public void testEventFactoryInitializeFile() throws EventSystemException {
        EventFactory fact = new EventFactory();
        fact.setESFFile(new File("src/test/java/org/lwes/EventFactoryTest.esf"));
        fact.initialize();
    }

    @Test
    public void testEventFactoryInitializePath() throws EventSystemException {
        EventFactory fact = new EventFactory();
        fact.setESFFilePath("src/test/java/org/lwes/EventFactoryTest.esf");
        fact.initialize();
    }

    @Test
    public void testEventFactoryInitializeStream()
            throws EventSystemException, FileNotFoundException {
        EventFactory fact = new EventFactory();
        fact.setESFInputStream(new FileInputStream("src/test/java/org/lwes/EventFactoryTest.esf"));
        fact.initialize();
    }

    @Test
    public void testEventFactoryCreateEvent() throws EventSystemException {
        EventFactory fact = new EventFactory();
        fact.setESFFile(new File("src/test/java/org/lwes/EventFactoryTest.esf"));
        fact.initialize();

        Event evt = fact.createEvent("TestEvent");
        assertNotNull(evt);

        evt = fact.createEvent("TestEvent", (short) 1);
        assertNotNull(evt);

        evt = fact.createEvent("TestEvent", true);
        assertNotNull(evt);

        evt = fact.createEvent("TestEvent", false, (short) 0);
        assertNotNull(evt);
    }
}
