package it.geosolutions.geoserver.rest.encoder.metadata;

import it.geosolutions.geoserver.rest.encoder.utils.EntryKeyListEncoder;

public class GSMetadataEncoder <T extends GSDimensionInfoEncoder> extends EntryKeyListEncoder<T>{

	public GSMetadataEncoder() {
		super("metadata");
	}
	 
	public void addMetadata(final String key, final T value) {
		this.add(key, value);
	}

}
