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
import io.novaordis.events.api.parser.Parser;
import io.novaordis.events.api.parser.ParsingException;
import io.novaordis.events.processing.EventProcessingException;
import io.novaordis.events.processing.Procedure;
import io.novaordis.events.processing.ProcedureFactory;
import io.novaordis.events.query.Query;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A generic event parser runtime, that can be configured on command line with a procedure, a query, output options
 * and files.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/7/17
 */
public class EventParserRuntime {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(EventParserRuntime.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String applicationName;

    private Configuration configuration;

    private AtomicLong parsingFailureCount;
    private volatile boolean failedOnClose;
    private AtomicLong processingFailureCount;
    private AtomicLong processedEventsCount;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param applicationName the application name to be used in help content. May be null.
     * @param localProcedureFactory the local procedure factory, to be used with priority over the default procedure
     *                              factory. May be null.
     */
    public EventParserRuntime(
            String[] commandLineArguments, String applicationName, ProcedureFactory localProcedureFactory)
            throws UserErrorException {

        this.configuration = new ConfigurationImpl(commandLineArguments, localProcedureFactory);
        this.applicationName = applicationName;
        this.parsingFailureCount = new AtomicLong(0L);
        this.processingFailureCount = new AtomicLong(0L);
        this.processedEventsCount = new AtomicLong(0L);

        log.debug(this + " constructed");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void run() throws UserErrorException {

        Query query = configuration.getQuery();
        Parser parser = configuration.getParser();
        InputStream is = configuration.getInputStream();
        Procedure procedure = configuration.getProcedure();

        BufferedReader br;

        try {

            br = new BufferedReader(new InputStreamReader(is));


            String line;

            while((line = br.readLine()) != null) {

                try {

                    processBatch(parser.parse(line), query, procedure, processedEventsCount, processingFailureCount);

                }
                catch(ParsingException e) {

                    //
                    // do not interrupt stream processing, log as error instead
                    //

                    parsingFailureCount.incrementAndGet();

                    log.error("" + e.getMessage());
                    log.debug("parsing failure", e);
                }
            }

            try {

                processBatch(parser.close(), query, procedure, processedEventsCount, processingFailureCount);

            }
            catch(ParsingException e) {

                //
                // do not interrupt stream processing, log as error instead
                //

                failedOnClose = true;

                log.error("" + e.getMessage());
                log.debug("parser close() failure", e);
            }
        }
        catch (IOException e) {

            //
            // this is not recoverable, it means most likely the script is broken
            //

            throw new UserErrorException("failed to process the input stream", e);

        }
        finally {

            if (is != null) {

                try {

                    is.close();
                }
                catch(Exception e) {

                    //
                    // we're wrapping up, so this is logged as a warning, and not pushed up
                    //

                    String msg = "failed to close the input stream";
                    log.warn(msg + ": " + e.getMessage());
                    log.debug(msg, e);
                }
            }
        }
    }

    public Configuration getConfiguration() {

        return configuration;
    }

    @Override
    public String toString() {

        return "EventParserRuntime[" + applicationName + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    long getParsingFailureCount() {

        return parsingFailureCount.get();
    }

    boolean isFailedOnClose() {

        return failedOnClose;
    }

    long getProcessingFailureCount() {

        return processingFailureCount.get();
    }

    long getProcessedEventsCount() {

        return processedEventsCount.get();
    }

    // Static package protected ----------------------------------------------------------------------------------------

    static void processBatch(List<Event> events, Query query, Procedure procedure, AtomicLong processedEventsCount, AtomicLong processingFailureCount) {

        if (query != null) {

            events = query.filter(events);
        }

        try {

            processedEventsCount.addAndGet(events.size());
            procedure.process(events);
        }
        catch(EventProcessingException e) {

            //
            // do not interrupt stream processing, log as error instead
            //

            processingFailureCount.incrementAndGet();
            log.error("" + e.getMessage());
            log.debug("event processing failure", e);
        }
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------


    // Inner classes ---------------------------------------------------------------------------------------------------

}
