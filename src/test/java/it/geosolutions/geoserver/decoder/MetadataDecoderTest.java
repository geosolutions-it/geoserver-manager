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

import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import it.geosolutions.geoserver.rest.decoder.RESTDimensionInfo;
import it.geosolutions.geoserver.rest.encoder.metadatalink.GSMetadataLinkInfoEncoder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * @author DamianoG
 *
 */
public class MetadataDecoderTest {

    
    @Test
    public void testMetadataDimensionInfo() throws IOException {
        
        File coverageFile = new ClassPathResource("testdata/coverageExample.xml").getFile();
        String coverageString = FileUtils.readFileToString(coverageFile);
        RESTCoverage coverage = RESTCoverage.build(coverageString);
        List<RESTDimensionInfo> list = coverage.getDimensionInfo();
        
        Assert.assertEquals(list.size(),2);
        
        
        for (RESTDimensionInfo el : list){
            if(el.getKey().equals("time")){
                Assert.assertEquals(el.getResolution(),null);
                Assert.assertEquals(el.getPresentation(),"LIST");
                Assert.assertEquals(el.getKey(),"time");
                Assert.assertEquals(el.isEnabled(),true);
            }
            if(el.getKey().equals("elevation")){
                Assert.assertEquals(el.getResolution(),"2");
                Assert.assertEquals(el.getPresentation(),"DISCRETE_INTERVAL");
                Assert.assertEquals(el.getKey(),"elevation");
                Assert.assertEquals(el.isEnabled(),true);
            }
        } 
    }
    
}
