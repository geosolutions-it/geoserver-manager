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
package it.geosolutions.geoserver.rest.encoder;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GSWorkspaceEncoderTest extends TestCase {

    /**
     * Default logger
     */
    protected final static Logger LOGGER = LoggerFactory.getLogger(GSWorkspaceEncoderTest.class);

    @Test
    public void testAll() {
        final GSWorkspaceEncoder wsenc = new GSWorkspaceEncoder("WS1");
        LOGGER.info(wsenc.toString());

        wsenc.setName("test_name");
        LOGGER.info(wsenc.toString());
        wsenc.setName("new_name");
        LOGGER.info(wsenc.toString());

    }
}
