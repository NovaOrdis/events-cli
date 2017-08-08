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
import io.novaordis.events.api.event.StringProperty;
import io.novaordis.events.processing.EventProcessingException;
import io.novaordis.events.processing.Procedure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The procedure keep a record of all received events.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/7/17
 */
public class MockProcedure implements Procedure {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String commandLineLabel;

    // maintains received events in order
    private List<Event> receivedEvents;

    private String genericEventPayloadContentToFailOn;
    private String genericEventPayloadToExitLoop;

    private boolean exitLoop;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockProcedure(String commandLineLabel) {

        this.commandLineLabel = commandLineLabel;
        this.receivedEvents = new ArrayList<>();
        this.genericEventPayloadContentToFailOn = null;
        this.exitLoop = false;
    }

    // Procedure implementation ----------------------------------------------------------------------------------------

    @Override
    public List<String> getCommandLineLabels() {

        return Collections.singletonList(commandLineLabel);
    }

    @Override
    public void process(Event in) throws EventProcessingException {

        receivedEvents.add(in);

        StringProperty payload = in.getStringProperty(MockParser.PAYLOAD_PROPERTY_NAME);

        if (payload == null) {

            return;
        }

        String sPayload = payload.getString();

        if (sPayload == null) {

            return;
        }

        if (genericEventPayloadContentToFailOn != null && genericEventPayloadContentToFailOn.equals(sPayload)) {

            throw new EventProcessingException("SYNTHETIC PROCESSING EXCEPTION");
        }

        if (genericEventPayloadToExitLoop != null && genericEventPayloadToExitLoop.equals(sPayload)) {

            exitLoop = true;
        }
    }

    @Override
    public void process(List<Event> in) throws EventProcessingException {

        for(Event e: in) {

            process(e);
        }
    }

    @Override
    public long getInvocationCount() {
        throw new RuntimeException("getInvocationCount() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isExitLoop() {

        return exitLoop;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public List<Event> getReceivedEvents() {

        return receivedEvents;
    }

    public void failOnPayload(String genericEventPayloadContentToFailOn) {

        this.genericEventPayloadContentToFailOn = genericEventPayloadContentToFailOn;
    }

    public void setExitLoopOnPayload(String genericEventPayloadToExitLoop) {

        this.genericEventPayloadToExitLoop = genericEventPayloadToExitLoop;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
