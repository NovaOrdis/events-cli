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
 * Identifies the implementor as a top-level command-line argument processor that will be given priority in inspecting
 * and processing command-line arguments. Needed to allow application to extract application-specific (but not
 * procedure specific) arguments.
 *
 * See https://kb.novaordis.com/index.php/Events-cli#Parser-specific_Top-level_Argument_Processing
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 10/28/17
 */
public interface TopLevelArgumentProcessor {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

}
