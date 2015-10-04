/* Copyright 2015 David Overeem (dovereem@cox.net)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.omg.space.xtce.toolkit;

import java.util.List;
import org.omg.space.xtce.database.CommandMetaDataType.MetaCommandSet.BlockMetaCommand;
import org.omg.space.xtce.database.CommandMetaDataType.MetaCommandSet.BlockMetaCommand.MetaCommandStepList.MetaCommandStep;
import org.omg.space.xtce.database.MetaCommandType;

/** The XTCESpaceSystemMetrics class is instantiated by the toolkit to capture
 * interesting information about the contents of an XTCE Space System.
 *
 * A "deep" or "recursive" count is performed when this object is
 * constructed with an XTCEDatabase object and is shallow reflecting only
 * one Space System when constructed using an XTCESpaceSystem object.
 *
 * @author David Overeem
 *
 */

public class XTCESpaceSystemMetrics {

    /** Constructor
     *
     * The constructor computes all the metrics for the provided Space System
     * object at construction time and cannot throw.  The accessors are trivial
     * and just return the computed value.  Values cannot be recomputed without
     * reconstructing the object because the reference to the XTCESpaceSystem
     * is not kept after construction.
     *
     * @param spaceSystem The XTCESpaceSystem object to count.
     *
     */

    public XTCESpaceSystemMetrics( XTCESpaceSystem spaceSystem ) {
        recursiveCount = false;
        computeMetrics( spaceSystem );
    }

    /** Constructor
     *
     * The constructor computes all the metrics for the provided Database
     * object at construction time and cannot throw.  The accessors are trivial
     * and just return the computed value.  Values cannot be recomputed without
     * reconstructing the object because the reference to the XTCEDatabase
     * is not kept after construction.
     *
     * @param database The XTCEDatabase object to count.
     *
     */

    public XTCESpaceSystemMetrics( XTCEDatabase database ) {
        recursiveCount = true;
        for ( XTCESpaceSystem spaceSystem : database.getSpaceSystemTree() ) {
            computeMetrics( spaceSystem );
        }
    }

    /** Retrieve the type of count that was performed to construct these
     * metrics.
     *
     * The "deep" or "recursive" count is performed when this object is
     * constructed with an XTCEDatabase object and is shallow reflecting only
     * one Space System when constructed using an XTCESpaceSystem object.
     *
     * @return Boolean indicating if the count is for an entire database or
     * false when it is just one Space System in a database object.
     *
     */

    public boolean isDeepCount() {
        return recursiveCount;
    }

    /** Retrieve the number of child Space Systems in the counted Space System.
     *
     * @return long indicating the number of Space Systems that are children of
     * this Space System or the total number of Space Systems when this object
     * is a "deep" count.
     *
     */

    public long getNumberOfChildSpaceSystems() {
        return numberOfChildSpaceSystems;
    }

    /** Retrieve the number of telemetry parameters counted.
     *
     * @return long containing the number of telemetry parameters in the
     * counted Space System or the total number in the database when this
     * object is a "deep" count.
     *
     */

    public long getNumberOfTelemetryParameters() {
        return numberOfTelemetryParameters;
    }

    /** Retrieve the number of telecommand parameters counted.
     *
     * @return long containing the number of telecommand parameters in the
     * counted Space System or the total number in the database when this
     * object is a "deep" count.
     *
     */

    public long getNumberOfTelecommandParameters() {
        return numberOfTelecommandParameters;
    }

    /** Retrieve the number of parameters counted.
     *
     * @return long containing the number of all parameters in the
     * counted Space System or the total number in the database when this
     * object is a "deep" count.
     *
     */

    public long getNumberOfParameters() {
        return numberOfTelemetryParameters +
               numberOfTelecommandParameters;
    }

    /** Retrieve the number of telemetry parameter types counted.
     *
     * @return long containing the number of telemetry parameter types in the
     * counted Space System or the total number in the database when this
     * object is a "deep" count.
     *
     */

    public long getNumberOfTelemetryParameterTypes() {
        return numberOfTelemetryParameterTypes;
    }

    /** Retrieve the number of telecommand parameter types counted.
     *
     * @return long containing the number of telecommand parameter types in the
     * counted Space System or the total number in the database when this
     * object is a "deep" count.
     *
     */

    public long getNumberOfTelecommandParameterTypes() {
        return numberOfTelecommandParameterTypes;
    }

    /** Retrieve the number of parameter types counted.
     *
     * @return long containing the number of all parameter types in the
     * counted Space System or the total number in the database when this
     * object is a "deep" count.
     *
     */

    public long getNumberOfParameterTypes() {
        return numberOfTelemetryParameterTypes +
               numberOfTelecommandParameterTypes;
    }

    /** Retrieve the number of telecommand arguments counted.
     *
     * @return long containing the number of telecommand arguments in the
     * counted Space System or the total number in the database when this
     * object is a "deep" count.
     *
     */

    public long getNumberOfTelecommandArguments() {
        return numberOfTelecommandArguments;
    }

    /** Retrieve the number of telecommand argument types counted.
     *
     * @return long containing the number of telecommand argument types in the
     * counted Space System or the total number in the database when this
     * object is a "deep" count.
     *
     */

    public long getNumberOfTelecommandArgumentTypes() {
        return numberOfTelecommandArgumentTypes;
    }

    /** Retrieve the number of telemetry containers counted.
     *
     * @return long containing the number of telemetry containers in the
     * counted Space System or the total number in the database when this
     * object is a "deep" count.
     *
     */

    public long getNumberOfTelemetryContainers() {
        return numberOfTelemetryContainers;
    }

    /** Retrieve the number of telecommand containers counted.
     *
     * @return long containing the number of telecommand containers in the
     * counted Space System or the total number in the database when this
     * object is a "deep" count.
     *
     */

    public long getNumberOfTelecommandContainers() {
        return numberOfTelecommandContainers;
    }

    /** Retrieve the number of containers counted.
     *
     * @return long containing the number of all containers in the
     * counted Space System or the total number in the database when this
     * object is a "deep" count.
     *
     */

    public long getNumberOfContainers() {
        return numberOfTelemetryContainers +
               numberOfTelecommandContainers;
    }

    /** Retrieve the number of telecommands counted.
     *
     * @return long containing the number of telecommands in the
     * counted Space System or the total number in the database when this
     * object is a "deep" count.  Both MetaCommand and BlockMetaCommand
     * elements are considered telecommands for this purpose.
     *
     */

    public long getNumberOfTelecommands() {
        return numberOfTelecommands;
    }

    /** Private method to perform the count on an individual Space System, with
     * cumulative Space Systems adding to the count.
     *
     * @param spaceSystem The XTCESpaceSystem object to be counted.
     *
     */

    private void computeMetrics( XTCESpaceSystem spaceSystem ) {

        numberOfChildSpaceSystems +=
            spaceSystem.getReference().getSpaceSystem().size();

        numberOfTelemetryParameters +=
            spaceSystem.getTelemetryParameters().size();
        
        numberOfTelecommandParameters +=
            spaceSystem.getTelecommandParameters().size();

        try {
            numberOfTelemetryParameterTypes +=
                spaceSystem.getReference()
                           .getTelemetryMetaData()
                           .getParameterTypeSet()
                           .getStringParameterTypeOrEnumeratedParameterTypeOrIntegerParameterType()
                           .size();
        } catch ( NullPointerException ex ) {
            // it is okay if there is nothing, that is 0
        }

        try {
            numberOfTelecommandParameterTypes +=
                spaceSystem.getReference()
                           .getCommandMetaData()
                           .getParameterTypeSet()
                           .getStringParameterTypeOrEnumeratedParameterTypeOrIntegerParameterType()
                           .size();
        } catch ( NullPointerException ex ) {
            // it is okay if there is nothing, that is 0
        }

        try {

            List<Object> list = spaceSystem.getReference()
                                           .getCommandMetaData()
                                           .getMetaCommandSet()
                                           .getMetaCommandOrMetaCommandRefOrBlockMetaCommand();
            for ( Object item : list ) {
                if ( item.getClass() == MetaCommandType.class ) {
                    try {
                        numberOfTelecommandArguments +=
                            ((MetaCommandType)item).getArgumentList().getArgument().size();
                    } catch ( NullPointerException ex ) {
                        // it is okay if there is nothing, that is 0
                    }
                } else if ( item.getClass() == BlockMetaCommand.class ) {
                    try {
                        List<MetaCommandStep> steps = 
                            ((BlockMetaCommand)item).getMetaCommandStepList().getMetaCommandStep();
                        for ( MetaCommandStep step : steps ) {
                            numberOfTelecommandArguments +=
                                step.getArgumentList().getArgument().size();
                        }
                    } catch ( NullPointerException ex ) {
                        // it is okay if there is nothing, that is 0
                    }
                }
            }

        } catch ( NullPointerException ex ) {
            // it is okay if there is nothing, that is 0
        }

        try {
            numberOfTelecommandArgumentTypes +=
                spaceSystem.getReference()
                           .getCommandMetaData()
                           .getArgumentTypeSet()
                           .getStringArgumentTypeOrEnumeratedArgumentTypeOrIntegerArgumentType()
                           .size();
        } catch ( NullPointerException ex ) {
            // it is okay if there is nothing, that is 0
        }

        try {
            numberOfTelemetryContainers +=
                spaceSystem.getReference()
                           .getTelemetryMetaData()
                           .getContainerSet()
                           .getSequenceContainer()
                           .size();
        } catch ( NullPointerException ex ) {
            // it is okay if there is nothing, that is 0
        }

        try {
            numberOfTelecommandContainers +=
                spaceSystem.getReference()
                           .getCommandMetaData()
                           .getCommandContainerSet()
                           .getCommandContainer()
                           .size();
        } catch ( NullPointerException ex ) {
            // it is okay if there is nothing, that is 0
        }

        try {
            numberOfTelecommands +=
                spaceSystem.getReference()
                           .getCommandMetaData()
                           .getMetaCommandSet()
                           .getMetaCommandOrMetaCommandRefOrBlockMetaCommand()
                           .size();
        } catch ( NullPointerException ex ) {
            // it is okay if there is nothing, that is 0
        }

    }

    private boolean recursiveCount = false;

    private long numberOfChildSpaceSystems         = 0;
    private long numberOfTelemetryParameters       = 0;
    private long numberOfTelecommandParameters     = 0;
    private long numberOfTelemetryParameterTypes   = 0;
    private long numberOfTelecommandParameterTypes = 0;
    private long numberOfTelecommandArguments      = 0;
    private long numberOfTelecommandArgumentTypes  = 0;
    private long numberOfTelemetryContainers       = 0;
    private long numberOfTelecommandContainers     = 0;
    private long numberOfTelecommands              = 0;

}
