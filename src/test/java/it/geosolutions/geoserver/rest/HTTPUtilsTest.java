package it.geosolutions.geoserver.rest;

import junit.framework.TestCase;

public class HTTPUtilsTest extends TestCase {
    public void testEncodeUrl() throws Exception {
        assertEquals("http://with%20spaces", HTTPUtils.encodeUrl("http://with spaces"));
        assertEquals("http://with%20spaces?p1=v1", HTTPUtils.encodeUrl("http://with spaces?p1=v1"));
        assertEquals("http://without/spaces?p1=v1", HTTPUtils.encodeUrl("http://without/spaces?p1=v1"));
        assertEquals("http://without/spaces", HTTPUtils.encodeUrl("http://without/spaces"));
        assertEquals("http://without/spaces#fragment", HTTPUtils.encodeUrl("http://without/spaces#fragment"));
        assertEquals("http://without/spaces?p1=v1#fragment", HTTPUtils.encodeUrl("http://without/spaces?p1=v1#fragment"));
        assertEquals("http://with%20spaces#fragment", HTTPUtils.encodeUrl("http://with spaces#fragment"));
        assertEquals("brokenurl?p1=v1", HTTPUtils.encodeUrl("brokenurl?p1=v1"));
    }
}
