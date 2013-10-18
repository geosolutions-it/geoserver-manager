/*
 *  GeoBatch - Open Source geospatial batch processing system
 *  https://github.com/nfms4redd/nfms-geobatch
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.geoserver.decoder;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.about.GSVersionDecoder;
import it.geosolutions.geoserver.rest.decoder.about.GSVersionDecoder.VERSION;
import junit.framework.Assert;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

/**
 * @author Carlo Cancellieri {@code
 * <about>
 *      <resource name="GeoServer">
 *              <Build-Timestamp>10-Oct-2013 03:08</Build-Timestamp>
 *              <Git-Revision>32db076555e57cc5f826b0361d1af4efe6d3f01b</Git-Revision>
 *              <Version>2.2-ENTERPRISE-SNAPSHOT</Version>
 *      </resource>
 *      <resource name="GeoTools">
 *              <Build-Timestamp>10-Oct-2013 03:01</Build-Timestamp>
 *              <Git-Revision>da12effd42a9545628bd6e8ec20494607fbfe3a4</Git-Revision>
 *              <Version>8-ENTERPRISE-SNAPSHOT</Version>
 *      </resource>
 * </about>
 * }
 */
public class VersionDecoderTest extends GeoserverRESTTest {

    private final String version = "<about><resource name=\"GeoServer\"><Build-Timestamp>10-Oct-2013 03:08</Build-Timestamp>"
            + "<Git-Revision>32db076555e57cc5f826b0361d1af4efe6d3f01b</Git-Revision><Version>2.2-ENTERPRISE-SNAPSHOT</Version></resource></about>";

    @Test
    public void testVersionDecoder() {
        
        GSVersionDecoder dec=new GSVersionDecoder(version);
        Assert.assertEquals(GSVersionDecoder.VERSION.v22, dec.getVersion());
        Assert.assertEquals("GeoServer", dec.getGeoServer().getName());
        
        GSVersionDecoder.GSAboutResource geoserver=dec.getGeoServer();
        geoserver.setVersion("2.3-SNAPSHOT");
        geoserver.setName("_CustomGeoServerName_");
        Assert.assertEquals(GSVersionDecoder.VERSION.v23, dec.getVersion());
        Assert.assertEquals("_CustomGeoServerName_", dec.getGeoServer().getName());
        
        //print(dec.getRoot());
    }
    
    @Test
    public void testIntegrationVersionDecoder() {
        if (!enabled())
            return;
        GSVersionDecoder geoserver = reader.getGeoserverVersion();
        if (GSVersionDecoder.VERSION.v22.equals(GSVersionDecoder.VERSION.getVersion(VERSION))) {
            Assert.assertEquals(geoserver.getVersion(), GSVersionDecoder.VERSION.v22);
        } else if (GSVersionDecoder.VERSION.UNRECOGNIZED.equals(GSVersionDecoder.VERSION
                .getVersion(VERSION))) {
            Assert.assertEquals(geoserver.getVersion(), GSVersionDecoder.VERSION.UNRECOGNIZED);
        }
        // print(dec.getRoot());
    }
    
    public String print(Element e){
        return new XMLOutputter().outputString(e);
    }

}
