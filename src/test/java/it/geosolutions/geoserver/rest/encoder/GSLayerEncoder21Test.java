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

import java.util.ArrayList;
import java.util.List;

import it.geosolutions.geoserver.rest.encoder.authorityurl.AuthorityURLInfo;
import it.geosolutions.geoserver.rest.encoder.authorityurl.GSAuthorityURLInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.identifier.GSIdentifierInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.identifier.IdentifierInfo;
import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

/**
 * GSLayerEncoder21Test
 * 
 * @author Emmanuel Blondel - emmanuel.blondel1@gmail.com |
 *         emmanuel.blondel@fao.org
 */
public class GSLayerEncoder21Test {

	GSLayerEncoder21 layerEncoder;

	@Before
	public void setup() {
		layerEncoder = new GSLayerEncoder21();
		layerEncoder.setAdvertised(true);
		layerEncoder.addAuthorityURL(new GSAuthorityURLInfoEncoder(
				"authority1", "http://www.authority1.org"));
		layerEncoder.addIdentifier(new GSIdentifierInfoEncoder("authority1",
				"identifier1"));
		layerEncoder.addAuthorityURL(new GSAuthorityURLInfoEncoder(
				"authority2", "http://www.authority2.org"));
		layerEncoder.addIdentifier(new GSIdentifierInfoEncoder("authority2",
				"identifier2"));
		layerEncoder.addIdentifier(new GSIdentifierInfoEncoder("authority2",
				"additionalId"));
	}

	
	@Test
	public void testMetadata(){
		List<Element> metaElements = layerEncoder.getRoot().getChild("metadata").getChildren();
		for(Element el : metaElements){
			String key = el.getAttributeValue("key");
			
			if(key.matches("advertised")){
				Assert.assertEquals(true,
						Boolean.parseBoolean(el.getValue()));
				
			}else if(key.matches("authorityURLs")){
				String jsonStr = el.getValue();
				jsonStr = jsonStr.substring(2);
				jsonStr = jsonStr.substring(0,
						jsonStr.length() - 3);

				String[] items = jsonStr.split("\\}(,)\\{");

				String[] props1 = items[0].split(",");
				String[] kvp1_1 = props1[0].split("\":");
				String[] kvp1_2 = props1[1].split("\":");
				Assert.assertEquals(AuthorityURLInfo.name.name(), kvp1_1[0].replace("\"", ""));
				Assert.assertEquals("authority1", kvp1_1[1].replace("\"", ""));
				Assert.assertEquals(AuthorityURLInfo.href.name(), kvp1_2[0].replace("\"", ""));
				Assert.assertEquals("http://www.authority1.org", kvp1_2[1].replace("\"", ""));
				
				String[] props2 = items[1].split(",");
				String[] kvp2_1 = props2[0].split("\":");
				String[] kvp2_2 = props2[1].split("\":");
				Assert.assertEquals(AuthorityURLInfo.name.name(), kvp2_1[0].replace("\"", ""));
				Assert.assertEquals("authority2", kvp2_1[1].replace("\"", ""));
				Assert.assertEquals(AuthorityURLInfo.href.name(), kvp2_2[0].replace("\"", ""));
				Assert.assertEquals("http://www.authority2.org", kvp2_2[1].replace("\"", ""));

				
			}else if(key.matches("identifiers")){
				String jsonStr = el.getValue();
				jsonStr = jsonStr.substring(2);
				jsonStr = jsonStr.substring(0,
						jsonStr.length() - 3);

				String[] items = jsonStr.split("\\}(,)\\{");

				String[] props1 = items[0].split(",");
				String[] kvp1_1 = props1[0].split("\":");
				String[] kvp1_2 = props1[1].split("\":");
				Assert.assertEquals(IdentifierInfo.authority.name(), kvp1_1[0].replace("\"", ""));
				Assert.assertEquals("authority2", kvp1_1[1].replace("\"", ""));
				Assert.assertEquals(IdentifierInfo.identifier.name(), kvp1_2[0].replace("\"", ""));
				Assert.assertEquals("identifier2", kvp1_2[1].replace("\"", ""));
				
				String[] props2 = items[1].split(",");
				String[] kvp2_1 = props2[0].split("\":");
				String[] kvp2_2 = props2[1].split("\":");
				Assert.assertEquals(IdentifierInfo.authority.name(), kvp2_1[0].replace("\"", ""));
				Assert.assertEquals("authority2", kvp2_1[1].replace("\"", ""));
				Assert.assertEquals(IdentifierInfo.identifier.name(), kvp2_2[0].replace("\"", ""));
				Assert.assertEquals("additionalId", kvp2_2[1].replace("\"", ""));
				
				String[] props3 = items[2].split(",");
				String[] kvp3_1 = props3[0].split("\":");
				String[] kvp3_2 = props3[1].split("\":");
				Assert.assertEquals(IdentifierInfo.authority.name(), kvp3_1[0].replace("\"", ""));
				Assert.assertEquals("authority1", kvp3_1[1].replace("\"", ""));
				Assert.assertEquals(IdentifierInfo.identifier.name(), kvp3_2[0].replace("\"", ""));
				Assert.assertEquals("identifier1", kvp3_2[1].replace("\"", ""));

			}
		}
	}
}
