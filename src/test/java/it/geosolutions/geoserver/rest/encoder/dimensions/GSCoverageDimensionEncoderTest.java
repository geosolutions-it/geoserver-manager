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
package it.geosolutions.geoserver.rest.encoder.dimensions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author Henry Rotzoll (henry.rotzoll@dlr.de)
 * 
 */
public class GSCoverageDimensionEncoderTest {

    @Test
    public void coverageDimensionTest() {
        GSCoverageDimensionEncoder encoder = new GSCoverageDimensionEncoder("GRAY_INDEX",
                "GridSampleDimension[-Infinity,Infinity]", "-inf", "inf", "dobson units³",
                "REAL_32BITS");

        Assert.assertEquals("GRAY_INDEX", encoder.getName());
        Assert.assertEquals("GridSampleDimension[-Infinity,Infinity]", encoder.getDescription());
        Assert.assertEquals("-inf", encoder.getRangeMin());
        Assert.assertEquals("inf", encoder.getRangeMax());
        Assert.assertEquals("dobson units³", encoder.getUnit());
        Assert.assertEquals("REAL_32BITS", encoder.getDimensionTypeName());

        Assert.assertTrue(encoder.delName());
        Assert.assertTrue(encoder.delDescription());
        Assert.assertTrue(encoder.delRange());
        Assert.assertTrue(encoder.delUnit());
        Assert.assertTrue(encoder.delDimensionType());

        Assert.assertNull(encoder.getName());
        Assert.assertNull(encoder.getDescription());
        Assert.assertNull(encoder.getRangeMin());
        Assert.assertNull(encoder.getRangeMax());
        Assert.assertNull(encoder.getUnit());
        Assert.assertNull(encoder.getDimensionTypeName());

        encoder.setName("GRAY_INDEX");
        encoder.setDescription("GridSampleDimension[-Infinity,Infinity]");
        encoder.setRange("-inf", "inf");
        encoder.setUnit("dobson units³");
        encoder.setDimensionType("REAL_32BITS");

        Assert.assertEquals("GRAY_INDEX", encoder.getName());
        Assert.assertEquals("GridSampleDimension[-Infinity,Infinity]", encoder.getDescription());
        Assert.assertEquals("-inf", encoder.getRangeMin());
        Assert.assertEquals("inf", encoder.getRangeMax());
        Assert.assertEquals("dobson units³", encoder.getUnit());
        Assert.assertEquals("REAL_32BITS", encoder.getDimensionTypeName());
    }
    
}