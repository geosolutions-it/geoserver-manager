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
package it.geosolutions.geoserver.rest.encoder.metadatalink;

import junit.framework.Assert;

import org.junit.Test;

/**
*
* @author eblondel
*
*/
public class GSMetadataLinkInfoEncoderTest {

	@Test
	public void metadataLinkInfoTest(){
		GSMetadataLinkInfoEncoder encoder = new GSMetadataLinkInfoEncoder();
		encoder.setup("text/xml", "ISO19115:2003","http://www.organization.org/metadata1");

		Assert.assertEquals("text/xml", encoder.getType());
		Assert.assertEquals("ISO19115:2003", encoder.getMetadataType());
		Assert.assertEquals("http://www.organization.org/metadata1", encoder.getContent());

		Assert.assertTrue(encoder.delContent());
		Assert.assertNull(encoder.getContent());

		encoder.setType("text/html");
		encoder.setMetadataType("FGDC");
		encoder.setContent("http://www.organization.org/metadata2");
		Assert.assertEquals("text/html", encoder.getType());
		Assert.assertEquals("FGDC", encoder.getMetadataType());
		Assert.assertEquals("http://www.organization.org/metadata2", encoder.getContent());

	}
}