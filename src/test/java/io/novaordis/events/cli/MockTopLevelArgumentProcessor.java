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
import java.util.Iterator;
import java.util.List;

import io.novaordis.utilities.UserErrorException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 10/28/17
 */
public class MockTopLevelArgumentProcessor implements TopLevelArgumentProcessor {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<String> validArgumentSequence;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockTopLevelArgumentProcessor(List<String> validArgumentSequence) {

        this.validArgumentSequence = new ArrayList<>(validArgumentSequence);
    }

    // TopLevelArgumentProcessor implementation ------------------------------------------------------------------------

    @Override
    public void process(List<String> mutableArgumentList, Configuration c) throws UserErrorException {

        if (validArgumentSequence.isEmpty()) {

            return;
        }

        Iterator<String> validArgumentIterator = validArgumentSequence.iterator();
        Iterator<String> argumentIterator = mutableArgumentList.iterator();
        String validArgument = validArgumentIterator.next();
        boolean inSequence = false;

        for(; argumentIterator.hasNext(); ) {

            String arg = argumentIterator.next();

            if (inSequence) {

                if (arg.equals(validArgument)) {

                    argumentIterator.remove();
                    addToConfiguration(arg, c);
                    if (!validArgumentIterator.hasNext()) {

                        break;
                    }
                    validArgument = validArgumentIterator.next();
                }
                else {

                    throw new UserErrorException("input arguments do not come in the expected sequence");
                }
            }
            else if (arg.equals(validArgument)) {

                //
                // sequence starts
                //

                inSequence = true;
                argumentIterator.remove();
                addToConfiguration(arg, c);
                if (!validArgumentIterator.hasNext()) {

                    break;
                }
                validArgument = validArgumentIterator.next();
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void addToConfiguration(String arg, Configuration c) {

        MockApplicationSpecificConfiguration mc =
                (MockApplicationSpecificConfiguration)c.getApplicationSpecificConfiguration();

        if (mc == null) {

            mc = new MockApplicationSpecificConfiguration();
            c.setApplicationSpecificConfiguration(mc);
        }

        mc.addRecognizedArgument(arg);
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
