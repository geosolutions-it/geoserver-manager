/*
* Copyright (C) 2007 - 2011 GeoSolutions S.A.S.
* http://www.geo-solutions.it
*
* GPLv3 + Classpath exception
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package it.geosolutions.geoserver.rest.encoder.authorityurl;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author eblondel
 * 
 */
public class GSAuthorityURLInfoEncoderTest {

	@Test
	public void authorityURLInfoTest() {
		GSAuthorityURLInfoEncoder encoder = new GSAuthorityURLInfoEncoder();
		encoder.setup("authority1", "http://www.authority1.org");

		Assert.assertEquals("authority1", encoder.getName());
		Assert.assertEquals("http://www.authority1.org", encoder.getHref());

		Assert.assertTrue(encoder.delHref());
		Assert.assertNull(encoder.getHref());

		encoder.setName("authority2");
		encoder.setHref("http://www.authority2.org");
		Assert.assertEquals("authority2", encoder.getName());
		Assert.assertEquals("http://www.authority2.org", encoder.getHref());
	}
}