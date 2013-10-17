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
				String content = el.getValue();
				content = content.substring(2);
				content = content.substring(0, content.length()-2);
				String[] props = content.split(",");
				for(String prop : props){
					String[] kvp = prop.split(":");
					if(kvp[0].matches(AuthorityURLInfo.name.name())){
						Assert.assertEquals("authority1", kvp[1]);
					}else if(kvp[0].matches(AuthorityURLInfo.href.name())){
						Assert.assertEquals("http://www.authority1.org", kvp[1]);
					}
				}
				
			}else if(key.matches("identifiers")){
				String content = el.getValue();
				content = content.substring(2);
				content = content.substring(0, content.length()-2);
				String[] props = content.split(",");
				for(String prop : props){
					String[] kvp = prop.split(":");
					if(kvp[0].matches(IdentifierInfo.authority.name())){
						Assert.assertEquals("authority1", kvp[1]);
					}else if(kvp[0].matches(IdentifierInfo.identifier.name())){
						Assert.assertEquals("identifier1", kvp[1]);
					}
				}
			}
		}
	}
}
