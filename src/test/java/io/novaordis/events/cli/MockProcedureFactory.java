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
import io.novaordis.utilities.UserErrorException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 8/7/17
 */
public class MockProcedureFactory implements ProcedureFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<Procedure> procedures;

    // procedure name - procedure type to be instantiated by reflection
    private Map<String, Class<? extends MockProcedure>> nameToProcedureType;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockProcedureFactory() {

        this.procedures = new ArrayList<>();
        this.nameToProcedureType = new HashMap<>();
    }

    // ProcedureFactory implementation ---------------------------------------------------------------------------------

    @Override
    public Procedure find(String commandLineLabel, int from, List<String> arguments) throws UserErrorException {

        //
        // first try registered procedures
        //

        for(Procedure p: procedures) {

            if (p.getCommandLineLabels().contains(commandLineLabel)) {

                return p;
            }
        }

        //
        // then try to instantiate it by passing 'from' and 'arguments', as, presumably, the real life implementations
        // want to be instantiated
        //

        Class<? extends MockProcedure> type = nameToProcedureType.get(commandLineLabel);

        if (type != null) {

            try {

                Constructor c = type.getDeclaredConstructor(String.class, int.class, List.class);
                Object o = c.newInstance(commandLineLabel, from, arguments);
                return (Procedure)o;
            }
            catch(Exception e) {

                throw new UserErrorException(e);
            }
        }

        return null;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void addProcedure(Procedure p) {

        procedures.add(p);
    }

    public void registerType(String procedureName, Class<? extends MockProcedure> mockProcedureType) {

        nameToProcedureType.put(procedureName, mockProcedureType);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
