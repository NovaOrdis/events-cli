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

import io.novaordis.utilities.UserErrorException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

    @Test
    public void loop_InputStreamFailsWithIOExceptionOnReadingFirstLine() throws Exception {

        String args[] = new String[] {

                "mock-procedure",
        };

        MockProcedureFactory mf = new MockProcedureFactory();

        EventParserRuntime r = new EventParserRuntime(args, "test", mf);

        ConfigurationImpl c = (ConfigurationImpl)r.getConfiguration();

        MockInputStream mos = new MockInputStream();

        c.setInputStream(mos);

        //
        // the mock input stream will fail when the first line is read
        //

        mos.setFailWhileReadingFirstLine(true);

        try {

            r.run();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertEquals("failed to process the input stream", msg);
            IOException e2 = (IOException)e.getCause();
            assertNotNull(e2);
        }
    }

    @Test
    public void loop_InputStreamFailsWithIOExceptionOnReadingSecondLine() throws Exception {

        String args[] = new String[] {

                "mock-procedure",
                ""
        };

        MockProcedureFactory mf = new MockProcedureFactory();

        EventParserRuntime r = new EventParserRuntime(args, "test", mf);

        ConfigurationImpl c = (ConfigurationImpl)r.getConfiguration();

        MockInputStream mos = new MockInputStream();

        c.setInputStream(mos);

        //
        // the mock input stream will fail when the second line is read
        //

        r.run();

        throw new RuntimeException("RETURN HERE");
    }

    @Test
    public void loop_EventParsingFailsInParse() throws Exception {

        String args[] = new String[] {

                "mock-procedure",
                ""
        };

        MockProcedureFactory mf = new MockProcedureFactory();

        EventParserRuntime r = new EventParserRuntime(args, "test", mf);

        ConfigurationImpl c = (ConfigurationImpl)r.getConfiguration();

        MockInputStream mos = new MockInputStream();

        c.setInputStream(mos);

        //
        // the mock input stream will fail when the second line is read
        //

        r.run();

        throw new RuntimeException("RETURN HERE");
    }

    @Test
    public void loop_EventParsingFailsInClose() throws Exception {

        String args[] = new String[] {

                "mock-procedure",
                ""
        };

        MockProcedureFactory mf = new MockProcedureFactory();

        EventParserRuntime r = new EventParserRuntime(args, "test", mf);

        ConfigurationImpl c = (ConfigurationImpl)r.getConfiguration();

        MockInputStream mos = new MockInputStream();

        c.setInputStream(mos);

        //
        // the mock input stream will fail when the second line is read
        //

        r.run();

        throw new RuntimeException("RETURN HERE");
    }

    @Test
    public void loop_EventProcessingFails() throws Exception {

        String args[] = new String[] {

                "mock-procedure",
                ""
        };

        MockProcedureFactory mf = new MockProcedureFactory();

        EventParserRuntime r = new EventParserRuntime(args, "test", mf);

        ConfigurationImpl c = (ConfigurationImpl)r.getConfiguration();

        MockInputStream mos = new MockInputStream();

        c.setInputStream(mos);

        //
        // the mock input stream will fail when the second line is read
        //

        r.run();

        throw new RuntimeException("RETURN HERE");
    }

    @Test
    public void loop() throws Exception {

        String args[] = new String[] {

                "mock-procedure",
                ""
        };

        MockProcedureFactory mf = new MockProcedureFactory();

        EventParserRuntime r = new EventParserRuntime(args, "test", mf);

        r.run();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
