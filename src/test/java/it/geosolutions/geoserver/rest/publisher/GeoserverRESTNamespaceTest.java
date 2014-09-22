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
package it.geosolutions.geoserver.rest.publisher;

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTNamespace;

import java.net.URI;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testcase for namespace management.
 * 
 * We need a running GeoServer to properly run the tests. 
 * If such geoserver instance cannot be contacted, tests will be skipped.
 *
 * @author Oscar Fonts
 */
public class GeoserverRESTNamespaceTest extends GeoserverRESTTest {
	
	/**
	 * Test Namespace create
	 */
	@Test
	public void testCreate() {
        if (!enabled()) return;

        deleteAll();
        assertEquals(0, reader.getNamespaces().size());
        assertEquals(0, reader.getWorkspaces().size());	
        
        // Test Namespace Creation
        assertTrue(publisher.createNamespace("NS1", URI.create("http://a.example.com")));
        assertTrue(publisher.createNamespace("NS2", URI.create("http://b.example.com")));
        assertEquals(2, reader.getNamespaces().size());
        
        // Test Namespace exists
        assertTrue(reader.existsNamespace("NS1"));
        assertTrue(reader.existsNamespace("NS2"));
        
        // When creating a namespace, its associated workspace will be automatically created:
        assertEquals(2, reader.getWorkspaces().size());

        // Existing prefix / existing URI
        assertFalse(publisher.createNamespace("NS1", URI.create("http://c.example.com")));
        assertFalse(publisher.createNamespace("NS3", URI.create("http://a.example.com")));
        assertEquals(2, reader.getWorkspaces().size());
	}
	
	/**
	 * Test Namespace read, update and delete
	 */
	@Test
	public void testReadUpdateDelete() {
        if (!enabled()) return;

        deleteAll();
        assertTrue(publisher.createNamespace("NS1", URI.create("http://a.example.com")));
        
        // Test read namespace list
		String nsName = reader.getNamespaceNames().get(0);
		assertEquals(nsName, "NS1");
		
		// Read a namespace
		RESTNamespace ns = reader.getNamespace(nsName);
		assertEquals(ns.getPrefix(), "NS1");
		assertEquals(ns.getURI(), URI.create("http://a.example.com"));
		
		// Update namespaces (change URI)
		assertTrue(publisher.updateNamespace("NS1", URI.create("http://b.example.com")));
		assertFalse(publisher.updateNamespace("NS2", URI.create("http://a.example.com"))); // Nonexistent
		
		// Delete namespaces
		assertTrue(publisher.removeNamespace("NS1", true));
		assertFalse(publisher.removeNamespace("NS3", true)); // Nonexistent
	
		assertEquals(0, reader.getNamespaces().size());
		assertEquals(0, reader.getWorkspaces().size());
		// Test non existens
		assertFalse(reader.existsNamespace("NS1"));
	}
}
