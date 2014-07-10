package it.geosolutions.geoserver.decoder;

import it.geosolutions.geoserver.rest.decoder.RESTBoundingBox;
import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import it.geosolutions.geoserver.rest.encoder.dimensions.GSCoverageDimensionEncoder;
import it.geosolutions.geoserver.rest.encoder.metadatalink.GSMetadataLinkInfoEncoder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * ResourceDecoderTest
 * 
 * @author eblondel
 * @author Henry Rotzoll (henry.rotzoll@dlr.de)
 * 
 */
public class ResourceDecoderTest {

    RESTCoverage coverage;

    @Before
    public void setup() throws IOException {
        File coverageFile = new ClassPathResource("testdata/coverageExample.xml").getFile();
        String coverageString = FileUtils.readFileToString(coverageFile);
        coverage = RESTCoverage.build(coverageString);
    }

    @Test
    public void testName() {
        Assert.assertEquals(coverage.getName(), "granuleTestMosaic");
    }

    @Test
    public void testNativeName() {
        Assert.assertEquals(coverage.getNativeName(), "granuleTestMosaic");
    }

    @Test
    public void testTitle() {
        Assert.assertEquals(coverage.getNativeName(), "granuleTestMosaic");
    }

    @Test
    public void testAbstract() {
        Assert.assertEquals(coverage.getAbstract(), "this is an abstract");
    }

    @Test
    public void testKeywords() {
        List<String> keywords = coverage.getKeywords();
        Assert.assertEquals(keywords.get(0), "keyword1");
        Assert.assertEquals(keywords.get(1), "keyword2");
        Assert.assertEquals(keywords.get(2), "keyword3");
    }

    @Test
    public void testNameSpace() {
        Assert.assertEquals(coverage.getNameSpace(), "topp");
    }

    @Test
    public void testStoreName() {
        Assert.assertEquals(coverage.getStoreName(), "granuleTestMosaic");
    }

    @Test
    public void testStoreType() {
        Assert.assertEquals(coverage.getStoreType(), "coverageStore");
    }

    @Test
    public void testStoreUrl() {
        Assert.assertEquals(coverage.getStoreUrl(),
                "http://localhost:8080/geoserver/rest/workspaces/topp/coveragestores/granuleTestMosaic.xml");
    }

    @Test
    public void testCRS() {
        Assert.assertEquals(coverage.getCRS(), "EPSG:4326");
    }

    @Test
    public void testBoundingBox() {
        Assert.assertEquals(coverage.getMinX(), -180.0, 0);
        Assert.assertEquals(coverage.getMaxX(), 180.0, 0);
        Assert.assertEquals(coverage.getMinY(), -90, 0);
        Assert.assertEquals(coverage.getMaxY(), 90, 0);
    }

    @Test
    public void testLatLonBoundingBox() {
    	RESTBoundingBox bbox = coverage.getLatLonBoundingBox();
    	Assert.assertTrue(bbox != null);
    	Assert.assertEquals("EPSG:4326", bbox.getCRS());
        Assert.assertEquals(-180.0, bbox.getMinX(), 0);
        Assert.assertEquals(180.0, bbox.getMaxX(), 0);
        Assert.assertEquals(-90, bbox.getMinY(), 0);
        Assert.assertEquals(90, bbox.getMaxY(), 0); 	
    }
    
    @Test
    public void testNativeBoundingBox() {
    	RESTBoundingBox bbox = coverage.getNativeBoundingBox();
    	Assert.assertTrue(bbox != null);
    	Assert.assertEquals("EPSG:4326", bbox.getCRS());
        Assert.assertEquals(-180.0, bbox.getMinX(), 0);
        Assert.assertEquals(180.0, bbox.getMaxX(), 0);
        Assert.assertEquals(-90, bbox.getMinY(), 0);
        Assert.assertEquals(90, bbox.getMaxY(), 0); 	
    }
    
    @Test
    public void testMetadataLinkInfo() throws IOException {

        List<GSMetadataLinkInfoEncoder> list = coverage.getEncodedMetadataLinkInfoList();

        GSMetadataLinkInfoEncoder metadataLinkInfo1 = list.get(0);
        Assert.assertEquals("text/xml", metadataLinkInfo1.getType());
        Assert.assertEquals("ISO19115:2003", metadataLinkInfo1.getMetadataType());
        Assert.assertEquals("http://www.organization.org/metadata1", metadataLinkInfo1.getContent());

        GSMetadataLinkInfoEncoder metadataLinkInfo2 = list.get(1);
        Assert.assertEquals("text/html", metadataLinkInfo2.getType());
        Assert.assertEquals("ISO19115:2003", metadataLinkInfo2.getMetadataType());
        Assert.assertEquals("http://www.organization.org/metadata2", metadataLinkInfo2.getContent());

    }

    @Test
    public void testCoverageDimension() throws IOException {

        List<GSCoverageDimensionEncoder> list = coverage.getEncodedDimensionsInfoList();

        GSCoverageDimensionEncoder coverageDimension1 = list.get(0);
        Assert.assertEquals("GRAY_INDEX", coverageDimension1.getName());
        Assert.assertEquals("GridSampleDimension[-Infinity,Infinity]",
                coverageDimension1.getDescription());
        Assert.assertEquals("-inf", coverageDimension1.getRangeMin());
        Assert.assertEquals("inf", coverageDimension1.getRangeMax());
        Assert.assertEquals("dobson units³", coverageDimension1.getUnit());
        Assert.assertEquals("REAL_32BITS", coverageDimension1.getDimensionTypeName());
    }

}
