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
package it.geosolutions.geoserver.rest.encoder.identifier;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author eblondel
 * 
 */
public class GSIdentifierInfoEncoderTest {

	@Test
	public void identifierInfoTest() {
		GSIdentifierInfoEncoder encoder = new GSIdentifierInfoEncoder();
		encoder.setup("authority1", "identifier1");

		Assert.assertEquals("authority1", encoder.getAuthority());
		Assert.assertEquals("identifier1", encoder.getIdentifier());

		Assert.assertTrue(encoder.delIdentifier());
		Assert.assertNull(encoder.getIdentifier());

		encoder.setAuthority("authority2");
		encoder.setIdentifier("identifier2");
		Assert.assertEquals("authority2", encoder.getAuthority());
		Assert.assertEquals("identifier2", encoder.getIdentifier());

	}
}