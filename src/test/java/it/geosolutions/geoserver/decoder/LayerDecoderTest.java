package it.geosolutions.geoserver.decoder;

import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTStyleList;
import it.geosolutions.geoserver.rest.encoder.authorityurl.GSAuthorityURLInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.identifier.GSIdentifierInfoEncoder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * 
 * @author eblondel
 *
 */
public class LayerDecoderTest {
	
	RESTLayer layer;
	
	@Before
	public void setUp() throws IOException{
		File layerFile = new ClassPathResource("testdata/layerExample.xml").getFile();
	    String layerString = FileUtils.readFileToString(layerFile);
		layer = RESTLayer.build(layerString);
	}

	@Test
	public void testEnabled(){
		Assert.assertEquals(true, layer.getEnabled());
	}
	
	@Test
	public void testQueryable(){
		Assert.assertEquals(true, layer.getQueryable());
	}
	
	@Test
	public void testAdvertised(){
		Assert.assertEquals(true, layer.getAdvertised());
	}
	
	@Test
	public void testName() {
		Assert.assertEquals("tasmania_cities", layer.getName());
	}

	@Test
	public void testTypeString() {
		Assert.assertEquals("VECTOR", layer.getTypeString());
	}

	@Test
	public void testType(){
		Assert.assertEquals(RESTLayer.Type.VECTOR, layer.getType());
	}
	
	@Test
	public void testDefaultStyle() {
		Assert.assertEquals("capitals", layer.getDefaultStyle());
  	}
	
	@Test
	public void testStyles() {
		RESTStyleList styles = layer.getStyles();
		Assert.assertTrue(styles.size() == 2);
		Assert.assertEquals("green", styles.get(0).getName());
		Assert.assertEquals("blue", styles.get(1).getName());
	}
	
	@Test
	public void testResourceUrl() {
		Assert.assertEquals(
				"http://localhost:8080/geoserver/rest/workspaces/topp/datastores/taz_shapes/featuretypes/tasmania_cities.xml",
				layer.getResourceUrl());
	}
	
	@Test
	public void testAuthorityURLs() {
		List<GSAuthorityURLInfoEncoder> authorityURLs = layer
				.getEncodedAuthorityURLInfoList();
		Assert.assertEquals("authority1", authorityURLs.get(0).getName());
		Assert.assertEquals("http://www.authority1.org", authorityURLs.get(0)
				.getHref());
		Assert.assertEquals("authority2", authorityURLs.get(1).getName());
		Assert.assertEquals("http://www.authority2.org", authorityURLs.get(1)
				.getHref());
	}

	@Test
	public void testIdentifiers() {
		List<GSIdentifierInfoEncoder> authorityURLs = layer
				.getEncodedIdentifierInfoList();
		Assert.assertEquals("authority1", authorityURLs.get(0).getAuthority());
		Assert.assertEquals("identifier1", authorityURLs.get(0).getIdentifier());
		Assert.assertEquals("authority2", authorityURLs.get(1).getAuthority());
		Assert.assertEquals("identifier2", authorityURLs.get(1).getIdentifier());
	}

}
