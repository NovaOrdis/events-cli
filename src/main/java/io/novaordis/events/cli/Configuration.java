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

import io.novaordis.events.processing.Procedure;
import io.novaordis.events.query.Query;

import java.io.InputStream;

/**
 * The command line configuration.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/31/17
 */
public interface Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return true if in-line help was requested. The runtime is free to interpret this request as it may, but it
     * usually means the user wants to see in-line help at stdout.
     *
     * If the accessor returns true, the output of all other accessors is undefined.
     */
    boolean isHelp();

    /**
     * @return the input stream events will come from. It may be file-based or pipe-based, as it is the case when
     * the content is piped into the process. Unless isHelp() returns true, this method should never return null.
     * If the caller needs a buffered stream, it should wrap the returned stream in a BufferedStream.
     *
     * Important! It is the caller's responsibility to close the InputStream when it is not needed anymore.
     */
    InputStream getInputStream();

    /**
     * @return the procedure that was requested at command line. May return null, which has a "default procedure"
     * semantics. It usually means to display the incoming (and possibly filtered) events.
     */
    Procedure getProcedure();

    /**
     * @return the query to filter the incoming events. May return null, in which case all incoming events will
     * be allowed to "pass".
     */
    Query getQuery();

}
