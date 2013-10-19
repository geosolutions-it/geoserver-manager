package it.geosolutions.geoserver.rest.decoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author toben
 */
public class RESTFeatureTypeListTest {

	public RESTFeatureTypeListTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testBuild() throws IOException {
		InputStream is = RESTFeatureTypeListTest.class.getResourceAsStream("/testdata/featureTypeListExample.xml");
		String response = IOUtils.toString(is);
		is.close();
		RESTFeatureTypeList result = RESTFeatureTypeList.build(response);
		List<String> list = result.getNames();

		assertArrayEquals(new String[]{"states", "tasmania_cities", "tasmania_roads", "tasmania_state_boundaries", "tasmania_water_bodies"}
				, list.toArray());
	}
}