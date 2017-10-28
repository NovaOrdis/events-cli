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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 10/28/17
 */
public abstract class TopLevelArgumentProcessorTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void process_NoArgumentIsRecognized() throws Exception {

        List<String> validArgs = getValidArgumentSequence();

        //
        // build an argument list containing only unrecognizable arguments; also make it immutable, so an attempt
        // to remove will fail the test
        //

        List<String> args = new ArrayList<>();

        for(String s: validArgs) {

            args.add("blah" + s);
            args.add(s + "blah");
        }

        args = Collections.unmodifiableList(args);

        ConfigurationImpl c = new ConfigurationImpl(new String[0], null);
        assertNull(c.getApplicationSpecificConfiguration());

        TopLevelArgumentProcessor p = getTopLevelArgumentProcessorToTest();

        p.process(args, c);

        for(int i = 0; i < validArgs.size(); i ++) {

            String s = validArgs.get(i);

            String s2 = args.get(2 * i);
            String s3 = args.get(2 * i + 1);

            assertEquals("blah" + s, s2);
            assertEquals(s + "blah", s3);
        }

        ApplicationSpecificConfiguration mc = c.getApplicationSpecificConfiguration();
        assertNull(mc);
    }

    @Test
    public void process_ArgumentsAreRecognized() throws Exception {

        List<String> validArgs = getValidArgumentSequence();

        //
        // build an argument list containing a mixture of recognizable and unrecognizable arguments
        //

        List<String> args = new ArrayList<>();


        args.add("I am pretty sure this is not a recognizable argument");

        //noinspection Convert2streamapi
        for(String s: validArgs) {

            args.add(s);
        }

        args.add("I am pretty sure this is not a recognizable argument either");

        ConfigurationImpl c = new ConfigurationImpl(new String[0], null);
        assertNull(c.getApplicationSpecificConfiguration());

        TopLevelArgumentProcessor p = getTopLevelArgumentProcessorToTest();

        p.process(args, c);

        assertEquals(2, args.size());
        assertEquals("I am pretty sure this is not a recognizable argument", args.get(0));
        assertEquals("I am pretty sure this is not a recognizable argument either", args.get(1));

        ApplicationSpecificConfiguration mc = c.getApplicationSpecificConfiguration();
        assertNotNull(mc);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract TopLevelArgumentProcessor getTopLevelArgumentProcessorToTest() throws Exception;

    protected abstract List<String> getValidArgumentSequence();

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
