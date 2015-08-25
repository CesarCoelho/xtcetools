/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import org.omg.space.xtce.database.AliasSetType;
import org.omg.space.xtce.database.DescriptionType.AncillaryDataSet;
import org.omg.space.xtce.database.FixedFrameStreamType;
import org.omg.space.xtce.database.PCMStreamType;
import org.omg.space.xtce.database.VariableFrameStreamType;

/** Class the represent a telemetry stream from the XTCE data model.
 *
 * @author dovereem
 *
 */

public class XTCETMStream extends XTCENamedObject {

    /** Constructor
     *
     * The constructor for this class is designed to be used from within the
     * toolkit package.
     *
     * @param stream PCMStreamType from the JAXB generated XTCE schema.
     *
     * @param spaceSystem XTCESpaceSystem object that represents the Space
     * System in XTCE that contains the definition of this stream.
     *
     * @param aliasSet AliasSetType object, which can be null, that contains
     * the AliasSet element from the XTCE data model that is associated with
     * this stream.
     *
     * @throws XTCEDatabaseException in the event that the element defining
     * the stream is not supported.
     *
     */

    XTCETMStream( PCMStreamType    stream,
                  XTCESpaceSystem  spaceSystem,
                  AliasSetType     aliasSet,
                  AncillaryDataSet ancDataSet ) throws XTCEDatabaseException {

        super( stream.getName(),
               spaceSystem.getFullPath(),
               aliasSet,
               ancDataSet );

        stream_ = stream;

        String ref = null;

        if ( stream_ instanceof FixedFrameStreamType ) {
            FixedFrameStreamType fstream = (FixedFrameStreamType)stream_;
            ref = fstream.getContainerRef().getContainerRef();
        } else if ( stream_ instanceof VariableFrameStreamType ) {
            VariableFrameStreamType vstream = (VariableFrameStreamType)stream_;
            ref = vstream.getContainerRef().getContainerRef();
        } else {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "error_stream_unsupported" ) +
                                             ": '" + getName() +
                                             "'" );
        }

        sPath_ = XTCEFunctions.resolvePathReference( spaceSystem.getFullPath(),
                                                     ref );

    }

    /** Retrieve the path to the XTCE SequenceContainer element that is the
     * base container for this stream definition.
     *
     * @return String containing the full path in the XTCE data model for the
     * container that is the base or root of this stream.
     *
     */

    public String getStreamContainerPath() {
        return sPath_;
    }

    /** Retrieve a reference to the native JAXB object that is generated from
     * the XTCE schema.
     *
     * @return PCMStreamType that underlies this stream object.
     *
     */

    public PCMStreamType getReference() {
        return stream_;
    }

    // Private Data Members

    private PCMStreamType   stream_    = null;
    private String          sPath_     = null;

}
