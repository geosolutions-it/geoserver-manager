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

package it.geosolutions.geoserver.rest.encoder.coverage;

import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;

import org.jdom.Element;


/**
 * Creates an XML 
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * 
 */
public class GSCoverageEncoder extends GSResourceEncoder {
    
        final private static String NATIVE_FORMAT="nativeFormat";
        final private static String SUPPORTED_FORMATS="supportedFormats";
        
        final private static String REQUEST_SRS="requestSRS";
        final private static String RESPONSE_SRS="responseSRS";
        
        final private Element supportedFormatsListEncoder = new Element(SUPPORTED_FORMATS);
    
	public GSCoverageEncoder() {
            super("coverage");
            addContent(supportedFormatsListEncoder);
	}
	
    /**
     * @param key
     * @param dimensionInfo
     * @deprecated Use {@link GSResourceEncoder#addMetadataDimension(String, GSDimensionInfoEncoder)} this method will be removed soon
     */
	protected void addMetadata(String key, GSDimensionInfoEncoder dimensionInfo) {
		super.addMetadata(key, dimensionInfo);
	}

    /**
     * @deprecated Use {@link GSResourceEncoder#setMetadataDimension(String, GSDimensionInfoEncoder)} this method will be removed soon
     * @param key
     * @param dimensionInfo
     */
	public void setMetadata(String key, GSDimensionInfoEncoder dimensionInfo) {
		super.setMetadata(key, dimensionInfo);
	}
	
	public void setNativeFormat(String format) {
            set(NATIVE_FORMAT, format);
        }
	
	public void addSupportedFormats(String format) {
            final Element el = new Element("string");
            el.setText(format);
            supportedFormatsListEncoder.addContent(el);
        }
	
	public void setRequestSRS(String srs) {
	    final Element el = new Element("string");
            el.setText(srs);
            set(REQUEST_SRS, el);
        }
	
	public void setResponseSRS(String srs) {
	    final Element el = new Element("string");
            el.setText(srs);
            set(RESPONSE_SRS, el);
	}
	
}
