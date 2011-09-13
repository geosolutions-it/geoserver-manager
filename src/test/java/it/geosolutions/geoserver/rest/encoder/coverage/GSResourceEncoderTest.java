package it.geosolutions.geoserver.rest.encoder.coverage;

import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;

import org.junit.Assert;
import org.junit.Test;

public class GSResourceEncoderTest {

	/**
	 * test set or reset of reprojection
	 */
	@Test
	public void testReprojection(){
		GSResourceEncoder<GSDimensionInfoEncoder> re=new GSCoverageEncoder();
		
		re.setProjectionPolicy(ProjectionPolicy.FORCE_DECLARED);
		Assert.assertNotNull(re.contains("projectionPolicy",ProjectionPolicy.FORCE_DECLARED.toString()));
		
		re.setProjectionPolicy(ProjectionPolicy.NONE);
		Assert.assertNull(re.contains("projectionPolicy",ProjectionPolicy.FORCE_DECLARED.toString()));
		Assert.assertNotNull(re.contains("projectionPolicy",ProjectionPolicy.NONE.toString()));
	}
}
