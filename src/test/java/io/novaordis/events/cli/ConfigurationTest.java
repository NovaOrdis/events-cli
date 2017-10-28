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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import io.novaordis.events.api.event.GenericEvent;
import io.novaordis.events.api.event.StringProperty;
import io.novaordis.events.processing.Procedure;
import io.novaordis.events.processing.count.Count;
import io.novaordis.events.processing.describe.Describe;
import io.novaordis.events.processing.exclude.Exclude;
import io.novaordis.events.processing.output.DefaultOutputFormat;
import io.novaordis.events.processing.output.Output;
import io.novaordis.events.processing.output.OutputFormat;
import io.novaordis.events.query.FieldQuery;
import io.novaordis.events.query.KeywordQuery;
import io.novaordis.events.query.MixedQuery;
import io.novaordis.events.query.Query;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.appspec.ApplicationSpecificBehavior;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/31/17
 */
public abstract class ConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // file and query handling -----------------------------------------------------------------------------------------

    @Test
    public void constructor_NoArguments() throws Exception {

        String[] args = new String[0];

        Configuration c = getConfigurationToTest(args);

        assertTrue(c.isHelp());
    }

    @Test
    public void constructor_OneFile() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        String[] args = {

                f.getPath()
        };

        Configuration c = getConfigurationToTest(args);

        InputStream is = c.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {

            baos.write(b);
        }

        assertEquals("SYNTHETIC", new String(baos.toByteArray()));

        is.close();

        Output output = (Output)c.getProcedure();
        assertNotNull(output);

        assertNull(c.getQuery());
        assertNull(c.getApplicationSpecificConfiguration());
    }

    @Test
    public void constructor_MultipleFiles() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");
        assertTrue(f.isFile());

        File f2 = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file-2.txt");
        assertTrue(f2.isFile());


        String[] args = {

                f.getPath(),
                f2.getPath()
        };

        try {

            getConfigurationToTest(args);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("multiple files"));
        }
    }

    @Test
    public void constructor_Count_Query_File() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        String[] args = {

                "count",
                "log-level:ERROR",
                f.getPath()
        };

        Configuration c = getConfigurationToTest(args);

        InputStream is = c.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {

            baos.write(b);
        }

        assertEquals("SYNTHETIC", new String(baos.toByteArray()));

        is.close();

        Procedure p = c.getProcedure();
        assertTrue(p instanceof Count);

        FieldQuery fq = (FieldQuery)c.getQuery();
        assertEquals("log-level", fq.getFieldName());
        assertEquals("ERROR", fq.getValue());

        assertNull(c.getApplicationSpecificConfiguration());
    }

    @Test
    public void constructor_AbbreviatedCount_Query_File() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        String[] args = {

                "-c",
                "log-level:ERROR",
                f.getPath()
        };

        Configuration c = getConfigurationToTest(args);

        InputStream is = c.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {

            baos.write(b);
        }

        assertEquals("SYNTHETIC", new String(baos.toByteArray()));

        is.close();

        Procedure p = c.getProcedure();
        assertTrue(p instanceof Count);

        FieldQuery fq = (FieldQuery)c.getQuery();
        assertEquals("log-level", fq.getFieldName());
        assertEquals("ERROR", fq.getValue());

        assertNull(c.getApplicationSpecificConfiguration());
    }

    @Test
    public void constructor_NoCommand_MixedQuery() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        String[] args = {

                "red",
                "blue",
                f.getPath(),
        };

        Configuration c = getConfigurationToTest(args);

        InputStream is = c.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {

            baos.write(b);
        }

        assertEquals("SYNTHETIC", new String(baos.toByteArray()));

        is.close();

        Query q = c.getQuery();

        MixedQuery mq = (MixedQuery)q;

        List<KeywordQuery> keywords = mq.getKeywordQueries();

        assertEquals(2, keywords.size());

        assertEquals("red", keywords.get(0).getKeyword());
        assertEquals("blue", keywords.get(1).getKeyword());

        Output output = (Output)c.getProcedure();
        assertNotNull(output);

        assertNull(c.getApplicationSpecificConfiguration());
    }

    @Test
    public void constructor_Command() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        String[] args = {

                "describe",
                "red",
                "blue",
                f.getPath(),
        };

        Configuration c = getConfigurationToTest(args);

        InputStream is = c.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {

            baos.write(b);
        }

        assertEquals("SYNTHETIC", new String(baos.toByteArray()));

        is.close();

        Describe p = (Describe)c.getProcedure();
        assertNotNull(p);

        Query q = c.getQuery();

        MixedQuery mq = (MixedQuery)q;

        List<KeywordQuery> keywords = mq.getKeywordQueries();

        assertEquals(2, keywords.size());

        assertEquals("red", keywords.get(0).getKeyword());
        assertEquals("blue", keywords.get(1).getKeyword());

        assertNull(c.getApplicationSpecificConfiguration());
    }

    @Test
    public void constructor_QueryAndFile() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        String[] args = {

                "log-level:ERROR",
                f.getPath(),
        };

        Configuration c = getConfigurationToTest(args);

        InputStream is = c.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {

            baos.write(b);
        }

        assertEquals("SYNTHETIC", new String(baos.toByteArray()));

        is.close();

        FieldQuery fq = (FieldQuery)c.getQuery();
        assertEquals("log-level", fq.getFieldName());
        assertEquals("ERROR", fq.getValue());

        Output output = (Output)c.getProcedure();
        assertNotNull(output);

        assertNull(c.getApplicationSpecificConfiguration());
    }

    @Test
    public void constructor_MissingFile_UseSystemIn() throws Exception {

        String[] args = {

                "-c",
                "something",
        };

        ByteArrayInputStream bais = new ByteArrayInputStream("STDIN SYNTHETIC".getBytes());

        Configuration c = getConfigurationToTest(args, bais);

        InputStream is = c.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {

            baos.write(b);
        }

        assertEquals("STDIN SYNTHETIC", new String(baos.toByteArray()));

        Count count = (Count)c.getProcedure();
        assertNotNull(count);

        assertNull(c.getApplicationSpecificConfiguration());
    }

    // procedure specification handling --------------------------------------------------------------------------------

    @Test
    public void constructor_ProcedureIsAvailableLocally_SameProcedureAvailableInProcessing() throws Exception {

        String[] args = {

                "describe"
        };

        MockProcedureFactory mf = new MockProcedureFactory();
        MockProcedure mp = new MockProcedure("describe");
        mf.addProcedure(mp);
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf);

        ConfigurationImpl c = new ConfigurationImpl(args, asb);

        Procedure p = c.getProcedure();

        assertEquals(mp, p);

        assertNull(c.getApplicationSpecificConfiguration());
    }

    @Test
    public void constructor_ProcedureIsNotAvailableLocally_LocalProcedureFactoryIsSet() throws Exception {

        String[] args = {

                "describe"
        };

        MockProcedureFactory mf = new MockProcedureFactory();
        MockProcedure mp = new MockProcedure("some-procedure");
        mf.addProcedure(mp);
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf);

        ConfigurationImpl c = new ConfigurationImpl(args, asb);

        Procedure p = c.getProcedure();

        assertTrue(p instanceof Describe);
        assertNotNull(p);

        assertNull(c.getApplicationSpecificConfiguration());
    }

    @Test
    public void constructor_ProcedureLeavesUnrecognizedArgumentsOnCommandLine_NoQueryArguments() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        MockProcedureFactory mf = new MockProcedureFactory();
        mf.registerType("mock-procedure", MockProcedureThatConsumesOnlyTheFirstArgument.class);
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf);

        String[] args = {

                "mock-procedure",
                "thisArgWillBeConsumed",
                "something",
                f.getPath()
        };

        try {

            new ConfigurationImpl(args, asb);

            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("unrecognized 'mock-procedure' argument:"));
            assertTrue(msg.contains("'something'"));
        }
    }

    @Test
    public void constructor_ProcedureConsumesOneArgument_NoQueryArguments() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        MockProcedureFactory mf = new MockProcedureFactory();
        mf.registerType("mock-procedure", MockProcedureThatConsumesOnlyTheFirstArgument.class);
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf);

        String[] args = {

                "mock-procedure",
                "thisArgWillBeConsumed",
                f.getPath()
        };

        ConfigurationImpl c = new ConfigurationImpl(args, asb);

        Procedure p = c.getProcedure();
        assertTrue(p instanceof MockProcedureThatConsumesOnlyTheFirstArgument);
        MockProcedureThatConsumesOnlyTheFirstArgument mp = (MockProcedureThatConsumesOnlyTheFirstArgument)p;
        assertEquals("thisArgWillBeConsumed", mp.getConsumedArgument());

        assertNull(c.getQuery());
        assertNull(c.getApplicationSpecificConfiguration());
    }

    @Test
    public void constructor_ProcedureLeavesUnrecognizedArgumentsOnCommandLine_QueryArgumentsPresent() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        MockProcedureFactory mf = new MockProcedureFactory();
        mf.registerType("mock-procedure", MockProcedureThatConsumesOnlyTheFirstArgument.class);
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf);

        String[] args = {

                "all",
                "these",
                "are",
                "query",
                "arguments",
                "mock-procedure",
                "thisArgWillBeConsumed",
                "red",
                "blue",
                f.getPath()
        };

        try {

            new ConfigurationImpl(args, asb);

            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("unrecognized 'mock-procedure' arguments:"));
            assertTrue(msg.contains("'red'"));
            assertTrue(msg.contains("'blue'"));
        }
    }

    @Test
    public void constructor_ProcedureConsumesOneArgument_QueryArgumentsExist() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        MockProcedureFactory mf = new MockProcedureFactory();
        mf.registerType("mock-procedure", MockProcedureThatConsumesOnlyTheFirstArgument.class);
        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mf);

        String[] args = {

                "all",
                "these",
                "are",
                "query",
                "arguments",
                "mock-procedure",
                "thisArgWillBeConsumed",
                f.getPath()
        };

        ConfigurationImpl c = new ConfigurationImpl(args, asb);

        Procedure p = c.getProcedure();
        assertTrue(p instanceof MockProcedureThatConsumesOnlyTheFirstArgument);
        MockProcedureThatConsumesOnlyTheFirstArgument mp = (MockProcedureThatConsumesOnlyTheFirstArgument)p;
        assertEquals("thisArgWillBeConsumed", mp.getConsumedArgument());

        MixedQuery q = (MixedQuery)c.getQuery();
        assertNotNull(q);

        List<KeywordQuery> kcs = q.getKeywordQueries();
        assertEquals(5, kcs.size());

        assertEquals("all", kcs.get(0).getKeyword());
        assertEquals("these", kcs.get(1).getKeyword());
        assertEquals("are", kcs.get(2).getKeyword());
        assertEquals("query", kcs.get(3).getKeyword());
        assertEquals("arguments", kcs.get(4).getKeyword());

        assertNull(c.getApplicationSpecificConfiguration());
    }

    @Test
    public void constructor_LocalProcedureFactoryIsNotSet() throws Exception {

        String[] args = {

                "describe"
        };

        ConfigurationImpl c = new ConfigurationImpl(args, null);

        Procedure p = c.getProcedure();

        assertTrue(p instanceof Describe);
        assertNotNull(p);

        assertNull(c.getApplicationSpecificConfiguration());
    }

    // output/output format --------------------------------------------------------------------------------------------

    @Test
    public void constructor_OutputFormat() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        String[] args = {

                "-o",
                "event-type",
                f.getPath(),
        };

        Configuration c = getConfigurationToTest(args);

        Output output = (Output) c.getProcedure();
        assertNotNull(output);

        assertEquals(System.out, output.getOutputStream());

        OutputFormat format = output.getFormat();
        assertNotNull(format);

        //
        // the OutputFormat is NOT the default, there's an event property to be displayed
        //

        assertFalse(format instanceof DefaultOutputFormat);

        assertNull(c.getApplicationSpecificConfiguration());
    }

    @Test
    public void constructor_ExplicitOutput_NoArguments() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        String[] args = {

                "output",
                f.getPath(),
        };

        Configuration c = getConfigurationToTest(args);

        InputStream is = c.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {

            baos.write(b);
        }

        assertEquals("SYNTHETIC", new String(baos.toByteArray()));

        is.close();

        Output output = (Output)c.getProcedure();
        assertEquals(System.out, output.getOutputStream());

        DefaultOutputFormat of = (DefaultOutputFormat)output.getFormat();
        assertNotNull(of);

        assertNull(c.getApplicationSpecificConfiguration());
    }

    @Test
    public void constructor_ExplicitOutput_Arguments() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        String[] args = {

                "output",
                "-o",
                "something",
                f.getPath(),
        };

        Configuration c = getConfigurationToTest(args);

        InputStream is = c.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {

            baos.write(b);
        }

        assertEquals("SYNTHETIC", new String(baos.toByteArray()));

        is.close();

        Output output = (Output)c.getProcedure();
        assertEquals(System.out, output.getOutputStream());

        OutputFormat of = output.getFormat();
        assertNotNull(of);

        String s = of.format(new GenericEvent(Collections.singletonList(new StringProperty("something", "else"))));
        assertEquals("else", s);

        assertNull(c.getApplicationSpecificConfiguration());
    }

    @Test
    public void constructor_heuristics_AnExcludeProcedureIsInitializedWithTheQuery() throws Exception {

        File f = new File(System.getProperty("basedir"), "src/test/resources/data/generic-file.txt");

        assertTrue(f.isFile());

        String[] args = {

                Exclude.COMMAND_LINE_LABEL,
                "log-level:ERROR",
                f.getPath(),
        };

        Configuration c = getConfigurationToTest(args);

        //
        // make sure the procedure is initialized with the query
        //

        Exclude exclude = (Exclude)c.getProcedure();

        FieldQuery q = (FieldQuery)exclude.getQuery();
        assertEquals("log-level", q.getFieldName());

        assertNull(c.getApplicationSpecificConfiguration());
    }

    // application-specific top-level argument handling ----------------------------------------------------------------

    @Test
    public void constructor_topLevelArgumentProcessorPresent_RecognizedArgumentInFront() throws Exception {

        String[] args = {

                "A",
                "B",
                "C",
                "D"

        };

        MockTopLevelArgumentProcessor mp = new MockTopLevelArgumentProcessor(Collections.singletonList("A"));

        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mp);

        ConfigurationImpl c = new ConfigurationImpl(args, asb);

        MockApplicationSpecificConfiguration mc =
                (MockApplicationSpecificConfiguration)c.getApplicationSpecificConfiguration();

        assertNotNull(mc);

        List<String> ra = mc.getRecognizedArguments();

        assertEquals(1, ra.size());
        assertEquals("A", ra.get(0));

        //
        // the rest of the arguments are interpreted as query keywords
        //

        Query q = c.getQuery();

        assertTrue(q instanceof MixedQuery);
        MixedQuery mq = (MixedQuery)q;

        List<KeywordQuery> kqs = mq.getKeywordQueries();
        assertEquals(3, kqs.size());
        assertEquals("B", kqs.get(0).getKeyword());
        assertEquals("C", kqs.get(1).getKeyword());
        assertEquals("D", kqs.get(2).getKeyword());
    }

    @Test
    public void constructor_topLevelArgumentProcessorPresent_RecognizedArgumentInTheMiddle() throws Exception {

        String[] args = {

                "A",
                "B",
                "C",
                "D"

        };

        MockTopLevelArgumentProcessor mp = new MockTopLevelArgumentProcessor(Collections.singletonList("C"));

        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mp);

        ConfigurationImpl c = new ConfigurationImpl(args, asb);

        MockApplicationSpecificConfiguration mc =
                (MockApplicationSpecificConfiguration)c.getApplicationSpecificConfiguration();

        assertNotNull(mc);

        List<String> ra = mc.getRecognizedArguments();

        assertEquals(1, ra.size());
        assertEquals("C", ra.get(0));

        //
        // the rest of the arguments are interpreted as query keywords
        //

        Query q = c.getQuery();

        assertTrue(q instanceof MixedQuery);
        MixedQuery mq = (MixedQuery)q;

        List<KeywordQuery> kqs = mq.getKeywordQueries();
        assertEquals(3, kqs.size());
        assertEquals("A", kqs.get(0).getKeyword());
        assertEquals("B", kqs.get(1).getKeyword());
        assertEquals("D", kqs.get(2).getKeyword());
    }

    @Test
    public void constructor_topLevelArgumentProcessorPresent_RecognizedArgumentAtTheEnd() throws Exception {

        String[] args = {

                "A",
                "B",
                "C",
                "D"

        };

        MockTopLevelArgumentProcessor mp = new MockTopLevelArgumentProcessor(Collections.singletonList("D"));

        ApplicationSpecificBehavior asb = new ApplicationSpecificBehavior(mp);

        ConfigurationImpl c = new ConfigurationImpl(args, asb);

        MockApplicationSpecificConfiguration mc =
                (MockApplicationSpecificConfiguration)c.getApplicationSpecificConfiguration();

        assertNotNull(mc);

        List<String> ra = mc.getRecognizedArguments();

        assertEquals(1, ra.size());
        assertEquals("D", ra.get(0));

        //
        // the rest of the arguments are interpreted as query keywords
        //

        Query q = c.getQuery();

        assertTrue(q instanceof MixedQuery);
        MixedQuery mq = (MixedQuery)q;

        List<KeywordQuery> kqs = mq.getKeywordQueries();
        assertEquals(3, kqs.size());
        assertEquals("A", kqs.get(0).getKeyword());
        assertEquals("B", kqs.get(1).getKeyword());
        assertEquals("C", kqs.get(2).getKeyword());
    }

    // setApplicationSpecificConfiguration() ---------------------------------------------------------------------------

    @Test
    public void setApplicationSpecificConfiguration() throws Exception {

        Configuration c = getConfigurationToTest(new String[0], null);

        assertNull(c.getApplicationSpecificConfiguration());

        MockApplicationSpecificConfiguration mc = new MockApplicationSpecificConfiguration();

        c.setApplicationSpecificConfiguration(mc);

        assertEquals(mc, c.getApplicationSpecificConfiguration());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    /**
     * @param mockStdin if non-null, the Configuration instance must return it with getInputStream() when no files
     *                  are identified among the command line arguments. If null, we don't interfere with stdin.
     */
    protected abstract Configuration getConfigurationToTest(String[] args, InputStream mockStdin) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    private Configuration getConfigurationToTest(String[] args) throws Exception {

        // leave stdin alone
        return getConfigurationToTest(args, null);
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
