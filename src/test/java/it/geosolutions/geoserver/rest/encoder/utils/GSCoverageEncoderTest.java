/*
 *  Copyright (C) 2007 - 2011 GeoSolutions S.A.S.
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
package it.geosolutions.geoserver.rest.encoder.utils;

import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder.Presentation;
import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GSCoverageEncoderTest extends TestCase {

    public GSCoverageEncoderTest() {
    }

    /**
     * Default logger
     */
    protected final static Logger LOGGER = Logger.getLogger(GSCoverageEncoderTest.class);
    
    @Test
    public void testAll() {
    	GSCoverageEncoder encoder=new GSCoverageEncoder();
    	encoder.addKeyword("KEYWORD_1");
    	encoder.addKeyword("KEYWORD_2");
    	encoder.addKeyword("...");
    	encoder.addKeyword("KEYWORD_N");
    	GSDimensionInfoEncoder dim=new GSDimensionInfoEncoder(true);
    	dim.setPresentation(Presentation.CONTINUOUS_INTERVAL);
    	encoder.addMetadata("time", dim);
    	GSDimensionInfoEncoder dim2=new GSDimensionInfoEncoder(true);
    	dim2.setPresentation(Presentation.LIST);
    	encoder.addMetadata("elev", dim2);
    	encoder.setAllowMultithreading(true);
        
    	LOGGER.info(encoder.toString());
// TODO TESTS
    	
    	
    }
}
