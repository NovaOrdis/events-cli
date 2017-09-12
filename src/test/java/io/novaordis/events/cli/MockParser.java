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
import io.novaordis.events.api.event.StringProperty;
import io.novaordis.events.api.parser.Parser;
import io.novaordis.utilities.parsing.ParsingException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/7/17
 */
public class MockParser implements Parser {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String PAYLOAD_PROPERTY_NAME = "payload";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private long lineNumber;

    private boolean failWhenParsing;
    private boolean failWhenClosing;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockParser() {

        this.lineNumber = 0;
        this.failWhenParsing = false;
        this.failWhenClosing = false;
    }

    // Parser implementation -------------------------------------------------------------------------------------------

    @Override
    public List<Event> parse(String line) throws ParsingException {

        lineNumber ++;

        if (failWhenParsing) {

            throw new ParsingException("SYNTHETIC PARSING EXCEPTION");
        }

        //
        // wrap each line in a generic event, with a "payload" property.
        //

        //noinspection ArraysAsListWithZeroOrOneArgument
        return Arrays.asList(new GenericEvent(Arrays.asList(new StringProperty(PAYLOAD_PROPERTY_NAME, line))));
    }

    @Override
    public List<Event> close() throws ParsingException {

        if (failWhenClosing) {

            throw new ParsingException("SYNTHETIC CLOSING EXCEPTION");
        }

        return Collections.emptyList();
    }

    @Override
    public long getLineNumber() {

        return lineNumber;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setFailWhenParsing(boolean b) {

        this.failWhenParsing = b;
    }

    public void setFailWhenClosing(boolean b) {

        this.failWhenClosing = b;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
