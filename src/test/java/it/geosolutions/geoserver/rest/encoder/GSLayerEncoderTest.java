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
package it.geosolutions.geoserver.rest.encoder;

import it.geosolutions.geoserver.rest.encoder.authorityurl.GSAuthorityURLInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.identifier.GSIdentifierInfoEncoder;
import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

/**
 * GSLayerEncoderTest
 * 
 * @author Emmanuel Blondel - emmanuel.blondel1@gmail.com |
 *         emmanuel.blondel@fao.org
 */
public class GSLayerEncoderTest {

	GSLayerEncoder layerEncoder;

	@Before
	public void setup() {
		layerEncoder = new GSLayerEncoder();
		layerEncoder.setEnabled(true);
		layerEncoder.setQueryable(true);
		layerEncoder.setAdvertised(true);
		
		layerEncoder.setDefaultStyle("point");
		layerEncoder.addStyle("additional_style1");
		layerEncoder.addStyle("additional_style2");
		
		layerEncoder.addAuthorityURL(new GSAuthorityURLInfoEncoder(
				"authority1", "http://www.authority1.org"));
		layerEncoder.addIdentifier(new GSIdentifierInfoEncoder("authority1",
				"identifier1"));
	}

	@Test
	public void testProperties() {
		Assert.assertEquals(
				true,
				Boolean.parseBoolean(layerEncoder.getRoot().getChild("enabled")
						.getValue()));
		Assert.assertEquals(
				true,
				Boolean.parseBoolean(layerEncoder.getRoot()
						.getChild("queryable").getValue()));
		Assert.assertEquals(
				true,
				Boolean.parseBoolean(layerEncoder.getRoot()
						.getChild("advertised").getValue()));
	}

	@Test
	public void testDefaultStyle() {
		Assert.assertEquals("point",
				layerEncoder.getRoot().getChild("defaultStyle").getValue());
	}

	@Test
	public void testMultipleStyles() {
		Assert.assertEquals(2, layerEncoder.getRoot().getChild("styles")
				.getChildren().size());
		Assert.assertEquals("additional_style1", ((Element) layerEncoder
				.getRoot().getChild("styles").getChildren().get(0)).getText());
		Assert.assertEquals("additional_style2", ((Element) layerEncoder
				.getRoot().getChild("styles").getChildren().get(1)).getText());
	}

	@Test
	public void testRemoveStyle() {
		Assert.assertTrue(layerEncoder.delStyle("additional_style1"));
		Assert.assertEquals(1, layerEncoder.getRoot().getChild("styles")
				.getChildren().size());
		Assert.assertEquals("additional_style2", ((Element) layerEncoder
				.getRoot().getChild("styles").getChildren().get(0)).getText());
	}
	
	@Test
	public void testAuthorityURL(){
		Element el = (Element) layerEncoder.getRoot().getChild("authorityURLs")
				.getChildren().get(0);
		Assert.assertEquals("authority1", el.getChild("name").getValue());
		Assert.assertEquals("http://www.authority1.org", el.getChild("href")
				.getValue());
	}
	
	@Test
	public void testIdentifier(){
		Element el = (Element) layerEncoder.getRoot().getChild("identifiers")
				.getChildren().get(0);
		Assert.assertEquals("authority1", el.getChild("authority").getValue());
		Assert.assertEquals("identifier1", el.getChild("identifier").getValue());
	}
}
