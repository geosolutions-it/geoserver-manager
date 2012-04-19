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

import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder.Presentation;
import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import junit.framework.TestCase;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GSCoverageEncoderTest extends TestCase {

    public GSCoverageEncoderTest() {
    }

    /**
     * Default logger
     */
    protected final static Logger LOGGER = LoggerFactory.getLogger(GSCoverageEncoderTest.class);
    
	/**
	 * test set or reset of reprojection
	 */
	@Test
	public void testReprojection(){
		GSResourceEncoder/*<GSDimensionInfoEncoder>*/ re=new GSCoverageEncoder();
		
		re.setProjectionPolicy(ProjectionPolicy.FORCE_DECLARED);
		Assert.assertNotNull(ElementUtils.contains(re.getRoot(),"projectionPolicy",ProjectionPolicy.FORCE_DECLARED.toString()));
		
		re.setProjectionPolicy(ProjectionPolicy.NONE);
		Assert.assertNull(ElementUtils.contains(re.getRoot(),"projectionPolicy",ProjectionPolicy.FORCE_DECLARED.toString()));
		Assert.assertNotNull(ElementUtils.contains(re.getRoot(),"projectionPolicy",ProjectionPolicy.NONE.toString()));
	}
	
	/**
	 * test set or reset of BB
	 */
	@Test
	public void testBB(){
		GSResourceEncoder/*<GSDimensionInfoEncoder>*/ re=new GSCoverageEncoder();
		
		re.setLatLonBoundingBox(-180d, 90d, 180d, -90d, null);
		Assert.assertNotNull(ElementUtils.contains(re.getRoot(),"minx","-180.0"));
		
		re.setLatLonBoundingBox(-90d, 45d, 180d, -90d, null);
		
		Assert.assertNull(ElementUtils.contains(re.getRoot(),"minx","-180.0"));
		Assert.assertNotNull(ElementUtils.contains(re.getRoot(),"minx","-90.0"));
	}
    
    @Test
    public void testAll() {
    	final GSCoverageEncoder encoder=new GSCoverageEncoder();
    	encoder.addKeyword("KEYWORD_1");
    	encoder.addKeyword("KEYWORD_2");
    	encoder.addKeyword("...");
    	encoder.addKeyword("KEYWORD_N");
    	
    	final GSDimensionInfoEncoder timeDimension=new GSDimensionInfoEncoder(true);
    	timeDimension.setPresentation(Presentation.CONTINUOUS_INTERVAL);
    	encoder.setMetadata("time", timeDimension);
    	
    	if (LOGGER.isInfoEnabled())
    		LOGGER.info(encoder.toString());
    	
    	final Element el=ElementUtils.contains(encoder.getRoot(),GSResourceEncoder.METADATA);
    	Assert.assertNotNull(el);
    	LOGGER.info("contains_key:"+el.toString());
    	
    	final GSDimensionInfoEncoder elevationDimension=new GSDimensionInfoEncoder(true);
    	elevationDimension.setPresentation(Presentation.LIST);
    	encoder.setMetadata("elevation", elevationDimension);

    	if (LOGGER.isInfoEnabled())
    		LOGGER.info(encoder.toString());
    	
    	final Element el2=ElementUtils.contains(encoder.getRoot(),GSDimensionInfoEncoder.PRESENTATION);
    	Assert.assertNotNull(el2);
    	LOGGER.info("contains_key:"+el2.toString());
    	
    	encoder.delMetadata("time");
    	if (LOGGER.isInfoEnabled())
    		LOGGER.info(encoder.toString());
    	
    	final Element el3=ElementUtils.contains(encoder.getRoot(),GSResourceEncoder.METADATA);
    	Assert.assertNotNull(el3);
    	LOGGER.info("contains_by_node:"+el3.toString());
    	
    	final boolean removed=ElementUtils.remove(encoder.getRoot(),el3);
    	LOGGER.info("remove:"+removed);
    	Assert.assertTrue(removed);
    	
    	final Element el4=ElementUtils.contains(encoder.getRoot(),GSResourceEncoder.METADATA);
    	Assert.assertNull(el4);
    	if (el4==null)
    		LOGGER.info("REMOVED");
    }
}
