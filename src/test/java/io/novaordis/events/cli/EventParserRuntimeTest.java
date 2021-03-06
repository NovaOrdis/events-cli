/*
 * Copyright (c) 2017 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.events.cli;

import io.novaordis.events.api.event.Event;
import io.novaordis.events.api.event.GenericEvent;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.appspec.ApplicationSpecificBehavior;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/7/17
 */
public class EventParserRuntimeTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // help() ----------------------------------------------------------------------------------------------------------

    @Test
    public void help() throws Exception {

        EventParserRuntime r = new EventParserRuntime(new String[0], "test", null);

        //
        // no arguments is interpreted as a help rendering request, run() should be a noop.
        //

        r.run();

        assertTrue(r.getConfiguration().isHelp());
    }

    // loop() ----------------------------------------------------------------------------------------------------------

    @Test
    public void loop_InputStreamFailsWithIOExceptionOnReadingFirstLine() throws Exception {

        String args[] = new String[] {

                "mock-procedure",
        };

        MockProcedureFactory mf = new MockProcedureFactory();
        MockProcedure mp = new MockProcedure("mock-procedure");
        mf.addProcedure(mp);
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf);

        EventParserRuntime r = new EventParserRuntime(args, "test", asb);

        ConfigurationImpl c = (ConfigurationImpl)r.getConfiguration();

        MockInputStream mos = new MockInputStream();

        c.setInputStream(mos);

        //
        // the mock input stream will fail when the first line is read
        //

        mos.setFailWhileReadingFirstLine(true);

        MockParser mpa = new MockParser();

        c.setParser(mpa);

        try {

            r.run();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.startsWith("failed to process the input stream"));
            assertTrue(msg.contains("SYNTHETIC FAILURE WHILE READING THE FIRST LINE"));
            IOException e2 = (IOException)e.getCause();
            assertNotNull(e2);
        }

        assertEquals(0, r.getParsingFailureCount());
        assertFalse(r.isFailedOnClose());
    }

    @Test
    public void loop_InputStreamFailsWithIOExceptionOnReadingSecondLine() throws Exception {

        String args[] = new String[] {

                "mock-procedure",
        };

        MockProcedureFactory mf = new MockProcedureFactory();
        MockProcedure mp = new MockProcedure("mock-procedure");
        mf.addProcedure(mp);
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf);

        EventParserRuntime r = new EventParserRuntime(args, "test",asb);

        ConfigurationImpl c = (ConfigurationImpl)r.getConfiguration();

        MockInputStream mos = new MockInputStream("A\nB".getBytes());

        c.setInputStream(mos);

        //
        // the mock input stream will fail when the second line is read
        //

        mos.setFailWhileReadingSecondLine(true);

        MockParser mpa = new MockParser();

        c.setParser(mpa);

        try {

            r.run();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.startsWith("failed to process the input stream"));
            assertTrue(msg.contains("SYNTHETIC FAILURE WHILE READING THE SECOND LINE"));
            IOException e2 = (IOException)e.getCause();
            assertNotNull(e2);
        }

        assertEquals(0, r.getParsingFailureCount());
        assertFalse(r.isFailedOnClose());
    }

    @Test
    public void loop_EventParsingFailsInParse() throws Exception {

        String args[] = new String[] {

                "mock-procedure",
        };

        MockProcedureFactory mf = new MockProcedureFactory();
        MockProcedure mp = new MockProcedure("mock-procedure");
        mf.addProcedure(mp);
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf);

        EventParserRuntime r = new EventParserRuntime(args, "test", asb);

        ConfigurationImpl c = (ConfigurationImpl)r.getConfiguration();

        MockInputStream mos = new MockInputStream("mock-event-1\nmock-event-2\nmock-event-3\n");

        c.setInputStream(mos);

        MockParser mpar = new MockParser();

        c.setParser(mpar);

        //
        // the event fails in parse(), which won't interrupt processing
        //

        mpar.setFailWhenParsing(true);

        assertEquals(0, r.getParsingFailureCount());

        r.run();

        assertEquals(3, r.getParsingFailureCount());
        assertFalse(r.isFailedOnClose());

    }

    @Test
    public void loop_ParserCloseFails() throws Exception {

        String args[] = new String[] {

                "mock-procedure",
        };

        MockProcedureFactory mf = new MockProcedureFactory();
        MockProcedure mp = new MockProcedure("mock-procedure");
        mf.addProcedure(mp);
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf);

        EventParserRuntime r = new EventParserRuntime(args, "test", asb);

        ConfigurationImpl c = (ConfigurationImpl)r.getConfiguration();

        MockInputStream mos = new MockInputStream("mock-event-1\nmock-event-2\nmock-event-3\n");

        c.setInputStream(mos);

        MockParser mpar = new MockParser();

        c.setParser(mpar);

        //
        // the event fails in parse(), which won't interrupt processing
        //

        mpar.setFailWhenClosing(true);

        assertEquals(0, r.getParsingFailureCount());

        r.run();

        assertEquals(0, r.getParsingFailureCount());
        assertTrue(r.isFailedOnClose());
    }

    @Test
    public void loop_EventProcessingFailsForSomeEvents() throws Exception {

        String args[] = new String[] {

                "mock-procedure",
        };

        MockProcedureFactory mf = new MockProcedureFactory();
        MockProcedure mp = new MockProcedure("mock-procedure");
        mf.addProcedure(mp);
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf);

        EventParserRuntime r = new EventParserRuntime(args, "test", asb);

        ConfigurationImpl c = (ConfigurationImpl)r.getConfiguration();

        MockInputStream mos = new MockInputStream("mock-event-1\nmock-event-2\nmock-event-3\n");

        c.setInputStream(mos);

        //
        // configure the procedure to fail when it gets payload "mock-event-2"
        //

        mp.failOnPayload("mock-event-2");

        MockParser mpar = new MockParser();

        c.setParser(mpar);

        r.run();

        assertEquals(0, r.getParsingFailureCount());
        assertFalse(r.isFailedOnClose());

        //
        // all events should be received by the procedure
        //

        List<Event> receivedEvents = mp.getReceivedEvents();
        assertEquals(3, receivedEvents.size());

        //
        // we should have one processing failure and two successes
        //

        assertEquals(1, r.getProcessingFailureCount());
        assertEquals(3, r.getProcessedEventsCount());
    }

    @Test
    public void loop() throws Exception {

        String args[] = new String[] {

                "mock-procedure",
        };

        MockProcedureFactory mf = new MockProcedureFactory();
        MockProcedure mp = new MockProcedure("mock-procedure");
        mf.addProcedure(mp);
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf);

        EventParserRuntime r = new EventParserRuntime(args, "test", asb);

        ConfigurationImpl c = (ConfigurationImpl)r.getConfiguration();

        MockInputStream mos = new MockInputStream("mock-event-1\nmock-event-2\nmock-event-3\n");

        c.setInputStream(mos);

        MockParser mpar = new MockParser();

        c.setParser(mpar);

        r.run();

        assertEquals(0, r.getParsingFailureCount());
        assertFalse(r.isFailedOnClose());

        //
        // all events should be received by the procedure
        //

        List<Event> receivedEvents = mp.getReceivedEvents();
        assertEquals(3, receivedEvents.size());
        assertEquals(0, r.getProcessingFailureCount());
        assertEquals(3, r.getProcessedEventsCount());
    }

    @Test
    public void loop_ProcedureWantsToExitTheEventLoop() throws Exception {

        String args[] = new String[] {

                "mock-procedure",
        };

        MockProcedureFactory mf = new MockProcedureFactory();
        MockProcedure mp = new MockProcedure("mock-procedure");
        mf.addProcedure(mp);
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf);

        EventParserRuntime r = new EventParserRuntime(args, "test", asb);

        ConfigurationImpl c = (ConfigurationImpl)r.getConfiguration();

        MockInputStream mos = new MockInputStream("mock-event-1\nmock-event-2\nmock-event-3\n");

        c.setInputStream(mos);

        MockParser mpar = new MockParser();

        c.setParser(mpar);

        //
        // configure the procedure to "want" to exit after processing "mock-event-2"
        //

        mp.setExitLoopOnPayload("mock-event-2");

        r.run();

        assertEquals(0, r.getParsingFailureCount());
        assertFalse(r.isFailedOnClose());

        //
        // all events should be received by the procedure
        //

        List<Event> receivedEvents = mp.getReceivedEvents();

        //
        // because we cut the loop short, the last event is not presented to the procedure
        //
        assertEquals(2, receivedEvents.size());

        assertEquals(0, r.getProcessingFailureCount());

        //
        // the procedure only processes the first two events
        //
        assertEquals(2, r.getProcessedEventsCount());
    }

    // processBatch() --------------------------------------------------------------------------------------------------

    @Test
    public void processBatch_NullQuery() throws Exception {

        List<Event> events = Arrays.asList(new GenericEvent(), new GenericEvent(), new GenericEvent());

        MockProcedure mp = new MockProcedure("mock");

        EventParserRuntime r = new EventParserRuntime(new String[0], null, null);

        r.processBatch(events, null, mp);

        //
        // we just make sure that *all* events were transferred to the procedure
        //

        List<Event> receivedEvents = mp.getReceivedEvents();

        assertEquals(events.size(), receivedEvents.size());
        assertEquals(events.get(0), receivedEvents.get(0));
        assertEquals(events.get(1), receivedEvents.get(1));
        assertEquals(events.get(2), receivedEvents.get(2));
    }

    // displayHelp() ---------------------------------------------------------------------------------------------------

    @Test
    public void displayHelp_FileNotFound() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        EventParserRuntime r = new EventParserRuntime(new String[0], null, null);

        String original = r.getHelpFileName();

        try {

            r.setHelpFileName("no-such-file.txt");

            r.displayHelp("something", baos);

        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("no-such-file.txt"));
        }
        finally {

            r.setHelpFileName(original);
        }

        assertEquals(0, baos.toByteArray().length);
    }

    @Test
    public void displayHelp() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        EventParserRuntime r = new EventParserRuntime(new String[0], null, null);

        r.displayHelp("something", baos);

        assertEquals("synthetic help\n", new String(baos.toByteArray()));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
