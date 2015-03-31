/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *
 *  Copyright (C) 2015 GeoSolutions S.A.S.
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

package it.geosolutions.geoserver.rest.decoder.about;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author etj <etj at geo-solutions dot it>
 */
public class GSVersionDecoderTest {

    @Test
    public void testGetGeoServer() {

        assertEquals(GSVersionDecoder.VERSION.v22, GSVersionDecoder.VERSION.getVersion("2.2"));
        assertEquals(GSVersionDecoder.VERSION.v22, GSVersionDecoder.VERSION.getVersion("2.2-SNAPSHOT"));

        assertEquals(GSVersionDecoder.VERSION.v26, GSVersionDecoder.VERSION.getVersion("2.6"));
        assertEquals(GSVersionDecoder.VERSION.v26, GSVersionDecoder.VERSION.getVersion("2.6-SNAPSHOT"));
        assertEquals(GSVersionDecoder.VERSION.v26, GSVersionDecoder.VERSION.getVersion("2.6-ENTERPRISE"));
        assertEquals(GSVersionDecoder.VERSION.v26, GSVersionDecoder.VERSION.getVersion("2.6-ENTERPRISE-SNAPSHOT"));

        assertEquals(GSVersionDecoder.VERSION.ABOVE, GSVersionDecoder.VERSION.getVersion("2.21"));
        assertEquals(GSVersionDecoder.VERSION.ABOVE, GSVersionDecoder.VERSION.getVersion("2.21-SNAPSHOT"));
        assertEquals(GSVersionDecoder.VERSION.ABOVE, GSVersionDecoder.VERSION.getVersion("2.42"));
        assertEquals(GSVersionDecoder.VERSION.ABOVE, GSVersionDecoder.VERSION.getVersion("2.42-SNAPSHOT"));

        assertEquals(GSVersionDecoder.VERSION.UNRECOGNIZED, GSVersionDecoder.VERSION.getVersion("3.0"));
        assertEquals(GSVersionDecoder.VERSION.UNRECOGNIZED, GSVersionDecoder.VERSION.getVersion("anystring"));
        assertEquals(GSVersionDecoder.VERSION.UNRECOGNIZED, GSVersionDecoder.VERSION.getVersion(null));
    }

}
