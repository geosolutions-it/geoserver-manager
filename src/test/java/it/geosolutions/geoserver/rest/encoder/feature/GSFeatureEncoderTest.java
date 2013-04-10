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
package it.geosolutions.geoserver.rest.encoder.feature;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTResource;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder.Presentation;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder.PresentationDiscrete;
import it.geosolutions.geoserver.rest.encoder.metadata.GSFeatureDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadatalink.GSMetadataLinkInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.publisher.GeoserverRESTPublisherTest;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GSFeatureEncoderTest extends GeoserverRESTPublisherTest {
    protected final static Logger LOGGER = LoggerFactory.getLogger(GSFeatureEncoderTest.class);

    @Test
    public void testIntegration() throws IOException {

        if (!enabled())
            return;
        deleteAll();

        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);

        String storeName = "resttestshp";
        String layerName = "cities";

        GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();
        fte.setName(layerName + "_NEW");
        fte.setTitle("title");
        // fte.addKeyword("TODO");
        fte.setNativeCRS("EPSG:4326");
        fte.setDescription("desc");
        fte.setEnabled(true);

        //metadataLink
        GSMetadataLinkInfoEncoder metadatalink = new GSMetadataLinkInfoEncoder();
        metadatalink.setup("text/xml", "ISO19115:2003","http://www.organization.org/metadata1");
        fte.addMetadataLinkInfo(metadatalink);
        
        GSLayerEncoder layerEncoder = new GSLayerEncoder();
        layerEncoder.setEnabled(true);
        layerEncoder.setQueryable(true);

        layerEncoder.setDefaultStyle("point");

        publisher.createWorkspace(DEFAULT_WS);

        File zipFile = new ClassPathResource("testdata/resttestshp.zip").getFile();

        // test insert
        boolean published = publisher.publishShp(DEFAULT_WS, storeName, layerName, zipFile);
        assertTrue("publish() failed", published);
        assertTrue(existsLayer(layerName));

        publisher.publishStyle(new File(new ClassPathResource("testdata").getFile(),
                "default_point.sld"));

        // optionally select the attributes to publish
        RESTLayer layer = reader.getLayer(layerName);
        RESTResource resource = reader.getResource(layer);
        List<GSAttributeEncoder> attrs = resource.getEncodedAttributeList();
        assertNotNull(attrs);
        for (GSAttributeEncoder enc : attrs) {
            fte.setAttribute(enc);
        }

        assertTrue(publisher.publishDBLayer(DEFAULT_WS, storeName, fte, layerEncoder));
    }

    @Test
    public void testFeatureTypeEncoder() {

        GSFeatureTypeEncoder encoder = new GSFeatureTypeEncoder();
        encoder.addKeyword("KEYWORD_1");
        encoder.addKeyword("KEYWORD_2");
        encoder.addKeyword("...");
        encoder.addKeyword("KEYWORD_N");
        
        encoder.setName("Layername");
        
        encoder.setTitle("title");
        encoder.addKeyword("TODO");
        encoder.setNativeCRS("EPSG:4326");
        encoder.setDescription("desc");
        encoder.setEnabled(true);

        GSAttributeEncoder attribute = new GSAttributeEncoder();
        attribute.setAttribute(FeatureTypeAttribute.name, "NAME");
        attribute.setAttribute(FeatureTypeAttribute.binding, "java.lang.String");
        attribute.setAttribute(FeatureTypeAttribute.maxOccurs, "1");
        attribute.setAttribute(FeatureTypeAttribute.minOccurs, "0");
        attribute.setAttribute(FeatureTypeAttribute.nillable, "true");
        
        encoder.setAttribute(attribute);
        
        encoder.delAttribute("NAME");
        
        attribute.setAttribute(FeatureTypeAttribute.name, "NEW_NAME");
        
        encoder.setAttribute(attribute);
        
        // TODO encoder.getAttribute("NAME");
        
        GSFeatureDimensionInfoEncoder dim2 = new GSFeatureDimensionInfoEncoder("ELE");
        
        encoder.addMetadata("elevation", dim2);
        dim2.setPresentation(PresentationDiscrete.DISCRETE_INTERVAL, BigDecimal.valueOf(10));
        Element el = ElementUtils.contains(encoder.getRoot(), GSDimensionInfoEncoder.PRESENTATION);
        Assert.assertNotNull(el);
        
        LOGGER.info("contains_key:" + el.toString());

        dim2.setPresentation(PresentationDiscrete.DISCRETE_INTERVAL, BigDecimal.valueOf(12));
        el = ElementUtils.contains(encoder.getRoot(), GSDimensionInfoEncoder.RESOLUTION);
        Assert.assertNotNull(el);
        Assert.assertEquals("12", el.getText());

        dim2.setPresentation(Presentation.CONTINUOUS_INTERVAL);
        
        encoder.setMetadata("time", new GSFeatureDimensionInfoEncoder("time"));
        el = ElementUtils.contains(encoder.getRoot(), GSDimensionInfoEncoder.PRESENTATION);
        Assert.assertNotNull(el);
        el = ElementUtils.contains(encoder.getRoot(), GSDimensionInfoEncoder.RESOLUTION);
        Assert.assertNull(el);

        el = ElementUtils.contains(encoder.getRoot(), GSResourceEncoder.METADATA);
        Assert.assertNotNull(el);
        LOGGER.info("contains_key:" + el.toString());

        final boolean removed = ElementUtils.remove(encoder.getRoot(), el);
        LOGGER.info("remove:" + removed);
        Assert.assertTrue(removed);

        el = ElementUtils.contains(encoder.getRoot(), "metadata");
        Assert.assertNull(el);
        if (el == null)
            LOGGER.info("REMOVED");
        
        if (LOGGER.isInfoEnabled())
            LOGGER.info(encoder.toString());

        assertEquals(encoder.getName(),"Layername");
    }

    @Test
    public void testModifyFeature() {
        GSFeatureTypeEncoder encoder = new GSFeatureTypeEncoder();
        encoder.addKeyword("KEYWORD_1");
        encoder.addKeyword("KEYWORD_2");
        encoder.addKeyword("...");
        encoder.addKeyword("KEYWORD_N");

        Assert.assertTrue(encoder.delKeyword("KEYWORD_2"));
        Assert.assertFalse(encoder.delKeyword("KEYWORD_M"));

        //metadataLinkInfo
        encoder.addMetadataLinkInfo("text/xml", "ISO19115:2003","http://www.organization.org/metadata1");
        encoder.addMetadataLinkInfo("text/html", "ISO19115:2003","http://www.organization.org/metadata2");
        
        Assert.assertTrue(encoder.delMetadataLinkInfo("http://www.organization.org/metadata2"));
        Assert.assertFalse(encoder.delMetadataLinkInfo("http://www.organization.org/metadata3"));
        
        //dimensions
        final GSFeatureDimensionInfoEncoder elevationDimension = new GSFeatureDimensionInfoEncoder(
                "elevation_field");

        // if (LOGGER.isInfoEnabled())
        // LOGGER.info(encoder.toString());

        final String metadata = "elevation";
        encoder.setMetadata(metadata, elevationDimension);

        elevationDimension.setPresentation(PresentationDiscrete.DISCRETE_INTERVAL,
                BigDecimal.valueOf(10));

        if (LOGGER.isInfoEnabled())
            LOGGER.info(encoder.toString());

        Assert.assertTrue(encoder.delMetadata(metadata));

        if (LOGGER.isInfoEnabled())
            LOGGER.info(encoder.toString());

        final Element el = ElementUtils.contains(encoder.getRoot(),
                GSDimensionInfoEncoder.DIMENSIONINFO);
        Assert.assertNull(el);
        if (el == null)
            LOGGER.info("REMOVED");

    }
}
