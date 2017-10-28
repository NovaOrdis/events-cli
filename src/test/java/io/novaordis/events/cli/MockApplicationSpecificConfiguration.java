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
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 10/28/17
 */
public class MockApplicationSpecificConfiguration implements ApplicationSpecificConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<String> recognizedArguments;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockApplicationSpecificConfiguration() {

        this.recognizedArguments = new ArrayList<>();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the list of recognized argument, in the order in which they were recognized. The argument processor
     * must deposit them in this application configuration instance, in the order in which it recogizes the arguments
     */
    public List<String> getRecognizedArguments() {

        return recognizedArguments;
    }

    public void addRecognizedArgument(String a) {

        recognizedArguments.add(a);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
