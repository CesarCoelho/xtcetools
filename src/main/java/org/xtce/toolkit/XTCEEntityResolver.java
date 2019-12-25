/* Copyright 2015-2019 David Overeem (dovereem@startmail.com)
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
package org.xtce.toolkit;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** Helper class to resolve resources stored in the JAR file
 *
 * @author stefanosperetta
 */
public class XTCEEntityResolver implements EntityResolver
{
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException 
    {
        try 
        {
            String filename = Paths.get(new URI(systemId)).getFileName().toString();
            InputStream is = getClass().getClassLoader().getResourceAsStream("org/omg/space/xtce/schema/" + filename);
            if (is == null)
            {
                // resource not found inside the Jar file, try to load it from the local path
                return new InputSource(systemId);
            }
            return new InputSource(is);
        } catch (URISyntaxException | NullPointerException ex) 
        {
            // if something goes wrong, try to load the file from the local path
            return new InputSource(systemId);
        }
    }    
}
