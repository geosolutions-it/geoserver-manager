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
package it.geosolutions.geoserver.rest.encoder.coverage;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GSImageMosaicEncoderTest extends TestCase {

    public GSImageMosaicEncoderTest() {
    }

    /**
     * Default logger
     */
    protected final static Logger LOGGER = LoggerFactory.getLogger(GSImageMosaicEncoderTest.class);

    /**
     * TODO implement this test
     */
    @Test
    public void testAll() {
        final GSImageMosaicEncoder encoder = new GSImageMosaicEncoder();

        Assert.isNull(encoder.getUseJaiImageRead());

        encoder.setAllowMultithreading(true);

        Assert.isTrue(encoder.getAllowMultithreading());

        encoder.setAllowMultithreading(false);

        Assert.isTrue(!encoder.getAllowMultithreading());

        encoder.setSUGGESTED_TILE_SIZE("512,512");

        Assert.isTrue(encoder.getSuggestedTileSize().equalsIgnoreCase("512,512"));

        LOGGER.info(encoder.toString());

        encoder.addSUGGESTED_TILE_SIZE("512,512");
        
        encoder.setInputTransparentColor("#FFFFFF");
        Assert.isTrue(encoder.getInputTransparentColor().equalsIgnoreCase("#FFFFFF"));
        
        encoder.setOutputTransparentColor("#000000");
        Assert.isTrue(encoder.getOutputTransparentColor().equalsIgnoreCase("#000000"));
        
        encoder.setAccurateResolutionComputation(true);
        Assert.isTrue(encoder.getAccurateResolutionComputation());
        
        encoder.setFootprintBehavior("None");
        Assert.isTrue(encoder.getFootprintBehavior().equalsIgnoreCase("None"));
    }
}
