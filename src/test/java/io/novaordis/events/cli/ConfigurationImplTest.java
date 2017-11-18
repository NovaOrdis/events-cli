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

import java.io.InputStream;

import org.junit.Test;

import io.novaordis.events.processing.help.Help;
import io.novaordis.utilities.appspec.ApplicationSpecificBehavior;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/31/17
 */
public class ConfigurationImplTest extends ConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void parser() throws Exception {

        ConfigurationImpl c = getConfigurationToTest(new String[0], null);

        assertNull(c.getParser());

        MockParser mp = new MockParser();
        c.setParser(mp);

        assertEquals(mp, c.getParser());
    }

    // constructor -----------------------------------------------------------------------------------------------------

    @Test
    public void constructor() throws Exception {

        MockProcedureFactory mf = new MockProcedureFactory();
        MockProcedure mproc = new MockProcedure("mock-procedure");
        mf.addProcedure(mproc);
        MockParser mp = new MockParser();
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf, mp);

        String[] args = new String[] {"mock-procedure"};

        ConfigurationImpl c = new ConfigurationImpl(args, asb);

        assertEquals(mp, c.getParser());

        assertEquals(mproc, c.getProcedure());

        assertNull(c.getQuery());
    }

    // help ------------------------------------------------------------------------------------------------------------

    @Test
    public void help_ConfigurationWithHelpProcedure() throws Exception {

        ConfigurationImpl c = new ConfigurationImpl(new String[] { "test" }, null);

        assertFalse(c.isHelp());

        c.setProcedure(new Help());

        assertTrue(c.isHelp());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected ConfigurationImpl getConfigurationToTest(String[] args, InputStream mockStdin) throws Exception {

        try {

            if (mockStdin != null) {

                ConfigurationImpl.STDIN = mockStdin;
            }

            return new ConfigurationImpl(args, null);
        }
        finally {

            ConfigurationImpl.STDIN = System.in;
        }
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
