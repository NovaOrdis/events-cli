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
import io.novaordis.events.processing.ProcedureFactory;
import io.novaordis.events.processing.exclude.Exclude;
import io.novaordis.events.processing.output.Output;
import io.novaordis.events.query.NullQuery;
import io.novaordis.events.query.Query;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Serves as a simple implementation and also as a base that can be extended.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/31/17
 */
public class ConfigurationImpl implements Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ConfigurationImpl.class);

    // Static ----------------------------------------------------------------------------------------------------------

    //
    // package exposed for testing
    //
    static InputStream STDIN = System.in;

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean help;
    private InputStream inputStream;
    private Query query;
    private Procedure procedure;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param argsa "log4jp [command] [command options] [query] <file1> [file2 ...]
     */
    public ConfigurationImpl(String[] argsa) throws UserErrorException {

        if (argsa.length == 0) {

            //
            // no arguments, display help
            //

            this.help = true;
        }

        List<String> args = new ArrayList<>(Arrays.asList(argsa));

        //
        // start from the back and identify the files
        //

        File file = null;

        int i;

        for(i = args.size() - 1; i >= 0; i --) {

            String arg = args.get(i);

            File candidate = new File(arg);

            if (candidate.isFile()) {

                if (file != null) {

                    throw new UserErrorException("multiple files cannot be processed at the same time");
                }

                file = candidate;
            }

            else {

                break;
            }
        }

        args = args.subList(0, i + 1);

        //
        // scan the argument list and identify the procedure
        //

        for(i = 0; i < args.size(); i ++) {

            String arg = args.get(i);
            this.procedure = ProcedureFactory.find(arg, i + 1, args);

            if (this.procedure != null) {

                //
                // we identified the procedure, which also consumed all its arguments from the list, remove the
                // argument and exit
                //

                args.remove(i);

                break;
            }
        }

        if (this.procedure == null) {

            //
            // no explicit procedure, default to Output
            //

            this.procedure = new Output(System.out, args);
        }

        //
        // query
        //

        if (!args.isEmpty()) {

            try {

                this.query = Query.fromArguments(args, 0);
            }
            catch (Exception e) {

                throw new UserErrorException(e);
            }

        }

        if (file != null) {

            try {

                this.inputStream = new FileInputStream(file);
            }
            catch(IOException e) {

                throw new UserErrorException(e);
            }
        }
        else {

            //
            // use the stdin
            //

            log.debug("no input file specified, using System.in (" + STDIN + ")");

            this.inputStream = STDIN;
        }

        //
        // configuration heuristics
        //

        if (procedure instanceof Exclude) {

            if (query == null) {

                query = new NullQuery();
            }

            ((Exclude) procedure).setQuery(query);
        }


        if (log.isDebugEnabled()) {

            String s =
                    "configuration\n" +
                    "  help:        " + help + "\n" +
                    "  query:       " + query + "" +
                    "  procedure:   " + procedure + "\n" +
                    "  inputStream: " + inputStream + "\n";

            log.debug(s);
        }
    }

    // Configuration implementation ------------------------------------------------------------------------------------

    @Override
    public boolean isHelp() {

        return help;
    }

    @Override
    public InputStream getInputStream() {

        return inputStream;
    }

    @Override
    public Procedure getProcedure() {

        return procedure;
    }

    @Override
    public Query getQuery() {

        return query;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
