/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package it.geosolutions.geoserver.rest.encoder;

import static org.junit.Assert.*;
import java.net.URI;
import org.junit.Test;

/**
 * @author Oscar Fonts
 */
public class GSNamespaceEncoderTest {

	private static final String ROOT_NAME   = "namespace";
	private static final String TEST_PREFIX = "example";
	private static final URI    TEST_URI    = URI.create("http://example.com");
	
	/**
	 * Test method for {@link GSNamespaceEncoder#GSNamespaceEncoder()}.
	 */
	@Test
	public void testGSNamespaceEncoder() {
		
		// Test constructor and getters.
		GSNamespaceEncoder enc = new GSNamespaceEncoder(TEST_PREFIX, TEST_URI);
		assertEquals(enc.getRoot().getName(), ROOT_NAME);
		assertEquals(enc.getPrefix(), TEST_PREFIX);
		assertEquals(enc.getURI(), TEST_URI);
		assertEquals(enc.toString(),"<namespace><prefix>"+TEST_PREFIX+"</prefix>"+
				"<uri>"+TEST_URI+"</uri></namespace>");
		
		// Test constructor parameter extreme values.
		// Should throw IllegalArgumentException if null or empty.
		try {
			new GSNamespaceEncoder(null, TEST_URI);
			fail("Namespace encoder should not accept a null prefix");
		} catch (IllegalArgumentException e) {}
		try {
			new GSNamespaceEncoder("", TEST_URI);
			fail("Namespace encoder should not accept an empty prefix");
		} catch (IllegalArgumentException e) {}
		try {
			new GSNamespaceEncoder(TEST_PREFIX, null);
			fail("Namespace encoder should not accept a null uri");
		} catch (IllegalArgumentException e) {}
		try {		
			new GSNamespaceEncoder(TEST_PREFIX, URI.create(""));
			fail("Namespace encoder should not accept an empty uri");
		} catch (IllegalArgumentException e) {}
	}
}
