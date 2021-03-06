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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.novaordis.events.api.parser.Parser;
import io.novaordis.events.processing.DefaultProcedureFactory;
import io.novaordis.events.processing.Procedure;
import io.novaordis.events.processing.ProcedureFactory;
import io.novaordis.events.processing.exclude.Exclude;
import io.novaordis.events.processing.help.Help;
import io.novaordis.events.processing.output.Output;
import io.novaordis.events.query.NullQuery;
import io.novaordis.events.query.Query;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.appspec.ApplicationSpecificBehavior;

/**
 * Serves as the default Configuration implementation. Even if the class is not declared final, it should be no need
 * to extend it, as it allows for delegation-based ApplicationSpecificConfiguration mechanism. If that mechanism is
 * not flexible enough for the application needs, this class can presumably be extended.
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
    private Query query;
    private Parser parser;
    private Procedure procedure;
    private InputStream inputStream;
    private ApplicationSpecificConfiguration applicationSpecificConfiguration;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param argsa "log4jp [command] [command options] [query] <file1> [file2 ...]
     * @param applicationSpecificBehavior everything application-specific, which application built and it may be
     *                                    needed to plug-in into the generic runtime. In general, application-specific
     *                                    behavior, if present, takes precedence over corresponding, but more generic
     *                                    behavior present in the generic runtime.
     */
    public ConfigurationImpl(String[] argsa, ApplicationSpecificBehavior applicationSpecificBehavior)
            throws UserErrorException {

        log.debug("parsing argument: " + Arrays.asList(argsa));

        if (argsa.length == 0) {

            //
            // no arguments, display help
            //

            this.help = true;
            return;
        }

        setParser(applicationSpecificBehavior);

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
        // If there is an application-specific top-level argument processor installed, give it the chance, with
        // priority, to identify, parse and remove application-specific top-level arguments
        //

        TopLevelArgumentProcessor argumentProcessor;

        if (applicationSpecificBehavior != null) {

            argumentProcessor = applicationSpecificBehavior.lookup(TopLevelArgumentProcessor.class);

            if (argumentProcessor != null) {

                log.debug("found application specific top-level argument processor: " + argumentProcessor);

                //
                // process the arguments and remove the recognized ones
                //

                argumentProcessor.process(args, this);
            }
        }

        //
        // scan the argument list and identify the procedure; first try the local procedure factory, if it exists,
        // then try the default procedure factory
        //

        ProcedureFactory applicationSpecificProcedureFactory = null;

        if (applicationSpecificBehavior != null) {

            applicationSpecificProcedureFactory = applicationSpecificBehavior.lookup(ProcedureFactory.class);

            if (applicationSpecificProcedureFactory != null) {

                log.debug("found application specific procedure factory: " + applicationSpecificProcedureFactory);
            }
        }

        if (applicationSpecificProcedureFactory != null) {

            for (i = 0; i < args.size(); i++) {

                String arg = args.get(i);

                this.procedure = applicationSpecificProcedureFactory.find(arg, i + 1, args);

                if (this.procedure != null) {

                    log.debug("found local procedure " + procedure);

                    //
                    // we identified the procedure, which also consumed all its arguments from the list
                    //

                    //
                    // if there are unrecognized arguments left at the end of the list, fail fast
                    //

                    if (i < args.size() - 1) {

                        String msg = "unrecognized '" + arg + "' argument";

                        msg += (args.size() - i > 2 ? "s: " : ": ");

                        for(int j = i + 1; j < args.size(); j ++) {

                            msg += "'" + args.get(j) + "'";

                            if (j < args.size() - 1) {

                                msg += ", ";
                            }
                        }

                        throw new UserErrorException(msg);
                    }

                    //
                    // otherwise, remove the argument and exit
                    //

                    args.remove(i);

                    break;
                }
            }
        }

        if (this.procedure == null) {

            //
            // try the default procedure factory, which will build procedures shipped as part of the "events-processing"
            // project.
            //

            ProcedureFactory defaultProcedureFactory = new DefaultProcedureFactory(applicationSpecificBehavior);

            for (i = 0; i < args.size(); i++) {

                String arg = args.get(i);

                this.procedure = defaultProcedureFactory.find(arg, i + 1, args);

                if (this.procedure != null) {

                    log.debug("found default procedure " + procedure);

                    //
                    // we identified the procedure, which also consumed all its arguments from the list, remove the
                    // argument and exit
                    //

                    args.remove(i);

                    break;
                }
            }
        }

        log.debug("procedure factories were not able to identify any procedure (application-specific or default), building default procedure ...");

        if (this.procedure == null) {

            //
            // no explicit procedure, default to Output
            //

            this.procedure = new Output(System.out, applicationSpecificBehavior, 0, args);
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

                InputStream is  = new FileInputStream(file);
                setInputStream(is);
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

            setInputStream(STDIN);
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
                    "      help:        " + help + "\n" +
                    "      query:       " + query + "\n" +
                    "      procedure:   " + procedure + "\n" +
                    "      inputStream: " + inputStream;

            log.debug(s);
        }
    }

    // Configuration implementation ------------------------------------------------------------------------------------

    @Override
    public boolean isHelp() {

        return help || procedure instanceof Help;
    }

    @Override
    public InputStream getInputStream() {

        return inputStream;
    }

    @Override
    public Parser getParser() {

        return parser;
    }

    @Override
    public Procedure getProcedure() {

        return procedure;
    }

    @Override
    public Query getQuery() {

        return query;
    }

    @Override
    public ApplicationSpecificConfiguration getApplicationSpecificConfiguration() {

        return applicationSpecificConfiguration;
    }

    @Override
    public void setApplicationSpecificConfiguration(ApplicationSpecificConfiguration c) {

        this.applicationSpecificConfiguration = c;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * This method is public because ConfigurationImpl instances are used for testing outside the package.
     */
    public void setParser(Parser p) {

        this.parser = p;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    void setInputStream(InputStream is) {

        this.inputStream = is;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    void setProcedure(Procedure procedure) {

        this.procedure = procedure;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    private void setParser(ApplicationSpecificBehavior asb) {

        if (asb == null) {

            return;
        }

        Parser p = asb.lookup(Parser.class);

        log.debug("identified application specific parser: " + p);

        if (p != null) {

            setParser(p);
        }
    }


    // Inner classes ---------------------------------------------------------------------------------------------------

}
