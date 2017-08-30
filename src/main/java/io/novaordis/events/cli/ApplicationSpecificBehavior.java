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

/**
 * A generic instance that carries application-specific behavior.
 *
 * In general, application-specific behavior, if present, takes precedence over corresponding, but more generic behavior
 * present in the generic runtime.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/30/17
 */
public class ApplicationSpecificBehavior {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Object[] instances;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param instances instances that carry application-specific behavior. They will be offered to the runtime.
     */
    public ApplicationSpecificBehavior(Object ... instances) {

        if (instances == null) {

            this.instances = new Object[0];
        }
        else {

            this.instances = instances;
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Looks up an application-specific behavior instance based on the type. The most specific instances that matches
     * it is returned. If two instances match in the same way, DuplicateInstanceException is thrown.
     *
     * @return the most specific instances that matches the type, of null if not found.
     *
     * @exception DuplicateInstanceException if more than one instance that matches the type if the same way is found.
     */
    public <T> T lookup(Class<T> type) {

        T found = null;

        for(Object o: instances) {

            if (o == null) {

                continue;
            }

            if (type.isAssignableFrom(o.getClass())) {

                if (found != null) {

                    throw new DuplicateInstanceException("" + found + ", " + o);
                }

                //noinspection unchecked
                found = (T)o;
            }
        }

        return found;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
