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
package it.geosolutions.geoserver.rest.encoder.feature;

import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder.Presentation;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder.PresentationDiscrete;
import it.geosolutions.geoserver.rest.encoder.metadata.GSFeatureDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GSFeatureEncoderTest extends TestCase {
	protected final static Logger LOGGER = LoggerFactory.getLogger(GSFeatureEncoderTest.class);
	
	public GSFeatureEncoderTest() {
	}

	@Test
	public void testAll() {
		
		GSFeatureTypeEncoder encoder = new GSFeatureTypeEncoder();
		encoder.addKeyword("KEYWORD_1");
		encoder.addKeyword("KEYWORD_2");
		encoder.addKeyword("...");
		encoder.addKeyword("KEYWORD_N");
		
		GSFeatureDimensionInfoEncoder dim2 = new GSFeatureDimensionInfoEncoder("ELE");

    	if (LOGGER.isInfoEnabled())
    		LOGGER.info(encoder.toString());

		encoder.addMetadata("elevation", dim2);
    	
		dim2.addPresentation(PresentationDiscrete.DISCRETE_INTERVAL,
				BigDecimal.valueOf(10));
		
		Element el=ElementUtils.contains(encoder.getRoot(),GSDimensionInfoEncoder.PRESENTATION);
    	Assert.assertNotNull(el);
    	
    	
    	LOGGER.info("contains_key:"+el.toString());
    	
		dim2.setPresentation(PresentationDiscrete.DISCRETE_INTERVAL,
				BigDecimal.valueOf(12));
		
		el=ElementUtils.contains(encoder.getRoot(),GSDimensionInfoEncoder.RESOLUTION);
    	Assert.assertNotNull(el);
    	Assert.assertEquals("12", el.getText());
    	
		dim2.setPresentation(Presentation.CONTINUOUS_INTERVAL);
		
		
		
		encoder.setMetadata("time", new GSFeatureDimensionInfoEncoder("time"));
		
		el=ElementUtils.contains(encoder.getRoot(),GSDimensionInfoEncoder.PRESENTATION);
    	Assert.assertNotNull(el);
    	el=ElementUtils.contains(encoder.getRoot(),GSDimensionInfoEncoder.RESOLUTION);
    	Assert.assertNull(el);

    	el=ElementUtils.contains(encoder.getRoot(),GSResourceEncoder.METADATA);
    	Assert.assertNotNull(el);
    	LOGGER.info("contains_key:"+el.toString());
    	
    	final boolean removed=ElementUtils.remove(encoder.getRoot(),el);
    	LOGGER.info("remove:"+removed);
    	Assert.assertTrue(removed);
    	
    	el=ElementUtils.contains(encoder.getRoot(),"metadata");
    	Assert.assertNull(el);
    	if (el==null)
    		LOGGER.info("REMOVED");

	}
	
	@Test
	public void testModifyFeature() {
		GSFeatureTypeEncoder encoder = new GSFeatureTypeEncoder();
		encoder.addKeyword("KEYWORD_1");
		encoder.addKeyword("KEYWORD_2");
		encoder.addKeyword("...");
		encoder.addKeyword("KEYWORD_N");
		
		Assert.assertTrue(encoder.delKeyword("KEYWORD_2"));
		Assert.assertFalse(encoder.delKeyword("KEYWORD_M"));
		
		final GSFeatureDimensionInfoEncoder elevationDimension = new GSFeatureDimensionInfoEncoder("elevation_field");

//    	if (LOGGER.isInfoEnabled())
//    		LOGGER.info(encoder.toString());

		final String metadata="elevation";
		encoder.setMetadata(metadata, elevationDimension);
    	
		elevationDimension.setPresentation(PresentationDiscrete.DISCRETE_INTERVAL,
				BigDecimal.valueOf(10));
		
		if (LOGGER.isInfoEnabled())
    		LOGGER.info(encoder.toString());
		
		Assert.assertTrue(encoder.delMetadata(metadata));
		
		if (LOGGER.isInfoEnabled())
    		LOGGER.info(encoder.toString());
		
    	final Element el=ElementUtils.contains(encoder.getRoot(),GSDimensionInfoEncoder.DIMENSIONINFO);
    	Assert.assertNull(el);
    	if (el==null)
    		LOGGER.info("REMOVED");

	}
}
