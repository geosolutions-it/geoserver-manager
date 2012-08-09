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
package it.geosolutions.geoserver.rest.encoder.utils;

import junit.framework.TestCase;

import org.jdom.Element;
import org.junit.Test;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class EntryKeyListEncoderTest extends TestCase {

    public EntryKeyListEncoderTest() {
    }

    @Test
    public void testAll() {
        NestedElementEncoder ekle = new NestedElementEncoder("EKL");
        ekle.add("k1", "v1");
        ekle.add("k2", "v2");
        ekle.add("k3", "v3");

        Element root = new Element("root");
        root.addContent(ekle.getRoot());

        assertEquals(1, root.getChildren().size());
        assertNotNull(root.getChild("EKL"));
        assertEquals(3, root.getChild("EKL").getChildren().size());
        assertEquals(3, root.getChild("EKL").getChildren("entry").size());

        assertEquals("k1", ((Element)root.getChild("EKL").getChildren("entry").get(0)).getAttributeValue("key"));
        assertEquals("k2", ((Element)root.getChild("EKL").getChildren("entry").get(1)).getAttributeValue("key"));
        assertEquals("k3", ((Element)root.getChild("EKL").getChildren("entry").get(2)).getAttributeValue("key"));

        assertEquals("v1", ((Element)root.getChild("EKL").getChildren("entry").get(0)).getText());
        assertEquals("v2", ((Element)root.getChild("EKL").getChildren("entry").get(1)).getText());
        assertEquals("v3", ((Element)root.getChild("EKL").getChildren("entry").get(2)).getText());
    }
}
