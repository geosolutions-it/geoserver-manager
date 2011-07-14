/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2011 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package it.geosolutions.geoserver.rest.encoder.metadata;

import it.geosolutions.geoserver.rest.encoder.utils.XmlElement;

import java.math.BigDecimal;

import org.jdom.Element;

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 *
 */
public class GSDimensionInfoEncoder extends XmlElement{
	final boolean enabled;
	
	/**
	 * Enum for presentation mode
	 */
	public enum Presentation{
		LIST,
		CONTINUOUS_INTERVAL
	}
	/**
	 * Enum for presentation mode which needs arguments
	 */
	public enum PresentationDiscrete{
		DISCRETE_INTERVAL
	}
	
	/**
	 * Build a dimension
	 * @param enabled enable dimension if true
	 * @note a enabled dimension also need a presentation mode set.
	 */
	public GSDimensionInfoEncoder(final boolean enabled) {
		super("dimensionInfo");
		add("enabled", (enabled)?"true":"false");
		this.enabled=enabled;
	}
	
	/**
	 * build an not enabled dimension
	 */
	public GSDimensionInfoEncoder() {
		super("dimensionInfo");
		add("enabled", "false");
		this.enabled=Boolean.FALSE;
	}
	
	public void addPresentation(final Presentation pres){
		if (enabled){
			add("presentation",pres.toString());
		}
	}
	
	public void addPresentation(final PresentationDiscrete pres, final BigDecimal interval){
		if (enabled){
			add("presentation",pres.toString());
			add("resolution",String.valueOf(interval));	
		}
	}
	
    
    public void add(String nodename, String nodetext) {
    	final Element el=new Element(nodename);
    	el.setText(nodetext);
    	this.addContent(el);
    }
	
	
}
