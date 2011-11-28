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
package it.geosolutions.geoserver.rest.encoder.coverage;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 *
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GSImageMosaicEncoderTest extends TestCase {

    public GSImageMosaicEncoderTest() {
    }

    /**
     * Default logger
     */
    protected final static Logger LOGGER = Logger.getLogger(GSImageMosaicEncoderTest.class);
    
    /**
     * TODO implement this test
     */
    @Test
    public void testAll() {
    	final GSImageMosaicEncoder encoder=new GSImageMosaicEncoder();
    
    	encoder.addAllowMultithreading(true);
    	LOGGER.info(encoder.toString());
    	encoder.setAllowMultithreading(false);
    	LOGGER.info(encoder.toString());
    	
    	
    	encoder.addSUGGESTED_TILE_SIZE("512,512");

    	
    }
}
