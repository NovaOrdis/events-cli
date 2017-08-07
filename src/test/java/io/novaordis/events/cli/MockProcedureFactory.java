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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/7/17
 */
public class MockProcedureFactory implements ProcedureFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<Procedure> procedures;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockProcedureFactory() {

        this.procedures = new ArrayList<>();
    }

    // ProcedureFactory implementation ---------------------------------------------------------------------------------

    @Override
    public Procedure find(String commandLineLabel, int from, List<String> arguments) {

        for(Procedure p: procedures) {

            if (p.getCommandLineLabels().contains(commandLineLabel)) {

                return p;
            }
        }

        return null;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void addProcedure(Procedure p) {

        procedures.add(p);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
