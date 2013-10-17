package it.geosolutions.geoserver.decoder;

import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTLayer21;
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
 * Apply Layer decoder tests on a GS 2.1 layer REST config
 * 
 * @author eblondel
 *
 */
public class LayerDecoder21Test{
	
	RESTLayer21 layer;
	
	@Before
	public void setUp() throws IOException{
		File layerFile = new ClassPathResource("testdata/layerExample21.xml").getFile();
	    String layerString = FileUtils.readFileToString(layerFile);
		layer = (RESTLayer21) RESTLayer21.build(layerString);
	}
	
	@Test
	public void testAdvertised(){
		Assert.assertEquals(true, layer.getAdvertised());
	}
	
	@Test
	public void testAuthorityURLs() {
		List<GSAuthorityURLInfoEncoder> authorityURLs = layer
				.getEncodedAuthorityURLInfoList();
		System.out.println(authorityURLs.size());
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