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

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/7/17
 */
public class MockInputStream extends InputStream {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean failWhileReadingFirstLine;
    private boolean failWhileReadingSecondLine;
    private int crt;
    private byte[] content;

    private int lineCount;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockInputStream() {

        this((byte[])null);
    }

    public MockInputStream(String content) {

        this(content.getBytes());
    }

    public MockInputStream(byte[] content) {

        this.crt = 0;
        this.content = content;
        this.lineCount = 0;
    }

    // InputStream overrides -------------------------------------------------------------------------------------------

    @Override
    public int read() throws IOException {

        if (failWhileReadingFirstLine) {

            //
            // we fail immediately
            //

            throw new IOException("SYNTHETIC FAILURE WHILE READING THE FIRST LINE");
        }

        if (failWhileReadingSecondLine && lineCount == 1) {

            throw new IOException("SYNTHETIC FAILURE WHILE READING THE SECOND LINE");
        }

        if (crt >= content.length) {

            return -1;
        }

        int current = (int)content[crt ++];

        if (current == '\n') {

            lineCount ++;
        }

        return current;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setFailWhileReadingFirstLine(boolean b) {

        this.failWhileReadingFirstLine = b;
    }

    public void setFailWhileReadingSecondLine(boolean b) {

        this.failWhileReadingSecondLine = b;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
