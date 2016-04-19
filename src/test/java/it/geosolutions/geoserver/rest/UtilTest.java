/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *
 *  Copyright (C) 2007 - 2016 GeoSolutions S.A.S.
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
package it.geosolutions.geoserver.rest;

import it.geosolutions.geoserver.rest.decoder.RESTStyle;

import java.io.File;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author etj
 */
public class UtilTest extends GeoserverRESTTest {

    @Test
    public void testSearchStyle() throws IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        final String WORKSPACE = "testWorkspace";
        final String STYLENAME = "restteststyle";

        File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();

        publisher.createWorkspace(WORKSPACE);

        assertEquals(0, reader.getStyles().size());
        assertEquals(0, reader.getStyles(WORKSPACE).size());
        assertEquals(0, Util.searchStyles(reader, STYLENAME).size());

        // insert style in workspace
        assertTrue(publisher.publishStyleInWorkspace(WORKSPACE, sldFile, STYLENAME));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));

        // GeoServer returns workspace specific names if hte name is not found as global
        assertTrue(reader.existsStyle(STYLENAME));

        assertEquals(0, reader.getStyles().size());
        assertEquals(1, reader.getStyles(WORKSPACE).size());
        assertEquals(1, Util.searchStyles(reader, STYLENAME).size());

        // insert global style
        assertTrue(publisher.publishStyle(sldFile, STYLENAME));

        assertTrue(reader.existsStyle(STYLENAME));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));

        for(RESTStyle style : Util.searchStyles(reader, STYLENAME))
        {
            System.out.println(style.getWorkspace() + " :: " + style.getName());
        }

        // there's a bug in geoserver here: the global style will include workspace info
        // https://osgeo-org.atlassian.net/browse/GEOS-7498
        // Commenting out all the concerned test code

//         assertEquals(2, Util.searchStyles(reader, STYLENAME).size());
//
//        assertEquals(1, reader.getStyles().size());
//        assertEquals(1, reader.getStyles(WORKSPACE).size());
//
//        List<RESTStyle> styles = Util.searchStyles(reader, STYLENAME);
//
//        assertEquals(STYLENAME, styles.get(0).getName());
//
//
//        // assertEquals(null, styles.get(0).getWorkspace());  // first one is the global one, if any
//
//        assertEquals(STYLENAME, styles.get(1).getName());
//        assertEquals(WORKSPACE, styles.get(1).getWorkspace());
    }

}
