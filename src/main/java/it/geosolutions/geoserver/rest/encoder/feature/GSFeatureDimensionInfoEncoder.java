package it.geosolutions.geoserver.rest.encoder.feature;

import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;

public class GSFeatureDimensionInfoEncoder extends GSDimensionInfoEncoder {
	
	/**
	 * if this dimension is enabled this constructor should be called.
	 * @param attribute the attribute to use as dimension
	 */
	public GSFeatureDimensionInfoEncoder(final String attribute){
		super(true);
		add("attribute", attribute);
	}
	

}
