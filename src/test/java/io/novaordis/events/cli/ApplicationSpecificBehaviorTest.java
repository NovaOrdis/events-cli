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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/30/17
 */
public class ApplicationSpecificBehaviorTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // constructors ----------------------------------------------------------------------------------------------------

    @Test
    public void constructor_NoArgs() throws Exception {

        ApplicationSpecificBehavior a = new ApplicationSpecificBehavior();

        assertNull(a.lookup(Object.class));
    }

    @Test
    public void constructor_Null() throws Exception {

        ApplicationSpecificBehavior a = new ApplicationSpecificBehavior(null);

        assertNull(a.lookup(Object.class));
    }

    // lookup() --------------------------------------------------------------------------------------------------------

    @Test
    public void lookup_OneInstance() throws Exception {

        MockBehaviorImpl b = new MockBehaviorImpl();

        ApplicationSpecificBehavior a = new ApplicationSpecificBehavior(b);

        MockBehavior b2 = a.lookup(MockBehavior.class);

        assertEquals(b, b2);

        assertNull(a.lookup(MoreSpecializedMockBehavior.class));
    }

    @Test
    public void lookup_DuplicateInstance() throws Exception {

        MockBehaviorImpl b = new MockBehaviorImpl();
        MockBehaviorImpl b2 = new MockBehaviorImpl();

        ApplicationSpecificBehavior a = new ApplicationSpecificBehavior(b, b2);

        try {

            a.lookup(MockBehavior.class);
            fail("should have thrown exception");
        }
        catch(DuplicateInstanceException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("MockBehaviorImpl"));
        }
    }

    @Test
    public void lookup_MoreSpecificMatch() throws Exception {

        MockBehavior b = new MockBehaviorImpl();
        MoreSpecializedMockBehavior b2 = new MoreSpecializedMockBehaviorImpl();

        ApplicationSpecificBehavior a = new ApplicationSpecificBehavior(b, b2);

        MoreSpecializedMockBehavior b3 = a.lookup(MoreSpecializedMockBehavior.class);
        assertEquals(b2, b3);

        //
        // if we search for MockBehavior, we'll get two matches, and an exception
        //

        try {

            a.lookup(MockBehavior.class);
            fail("should have thrown exception");
        }
        catch(DuplicateInstanceException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("MockBehaviorImpl"));
            assertTrue(msg.contains("MoreSpecializedMockBehaviorImpl"));
        }
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
