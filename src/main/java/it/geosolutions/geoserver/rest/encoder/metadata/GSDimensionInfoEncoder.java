package it.geosolutions.geoserver.rest.encoder.metadata;

import java.math.BigDecimal;

import it.geosolutions.geoserver.rest.encoder.utils.TextNodeListEncoder;

public class GSDimensionInfoEncoder extends TextNodeListEncoder {
	final boolean enabled;
	
	public enum Presentation{
		LIST,
		CONTINUOUS_INTERVAL
	}
	
	public enum DiscretePresentation{
		DISCRETE_INTERVAL
	}
	
	public GSDimensionInfoEncoder(final boolean enabled) {
		super("dimensionInfo");
		add("enabled", (enabled)?"true":"false");
		this.enabled=enabled;
	}
	
	public GSDimensionInfoEncoder() {
		super("dimensionInfo");
		add("enabled", "false");
		this.enabled=Boolean.FALSE;
	}
	
	public void setPresentation(final Presentation pres){
		if (enabled){
			add("presentation",pres.toString());
		}
	}
	
	public void setPresentation(final DiscretePresentation pres, final BigDecimal interval){
		if (enabled){
			add("presentation",pres.toString());
			add("resolution",String.valueOf(interval));	
		}
	}
	
	
}
