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
public class GSCoverageEncoder extends GSResourceEncoder<GSDimensionInfoEncoder> {

	
	final private Element parameters=new Element("parameters");
    
	public GSCoverageEncoder() {
        super("coverage");
        // Link members to the parent
		addContent(parameters);
	}

	
	/**
	 * @param val
	 */
	public void setAllowMultithreading(final boolean val){
		final Element param=new Element("entry");
		param.addContent(new Element("string").setText("AllowMultithreading"));
		param.addContent(new Element("string").setText((val)?"true":"false"));
		parameters.addContent(param);
	}
	
	public void setFilter(final String val){
		final Element param=new Element("entry");
		param.addContent(new Element("string").setText("Filter"));
		param.addContent(new Element("string").setText(val));
		parameters.addContent(param);
	}
	
	public void setMaxAllowedTiles(final int val){
		final Element param=new Element("entry");
		param.addContent(new Element("string").setText("MaxAllowedTiles"));
		param.addContent(new Element("string").setText(String.valueOf(val)));
		parameters.addContent(param);
	}
	
	public void setInputTransparentColor(final String val){
		final Element param=new Element("entry");
		param.addContent(new Element("string").setText("InputTransparentColor"));
		param.addContent(new Element("string").setText(val));
		parameters.addContent(param);
	}
	
	public void setOutputTransparentColor(final String val){
		final Element param=new Element("entry");
		param.addContent(new Element("string").setText("OutputTransparentColor"));
		param.addContent(new Element("string").setText(val));
		parameters.addContent(param);
	}
	
	public void setSUGGESTED_TILE_SIZE(final String val){
		final Element param=new Element("entry");
		param.addContent(new Element("string").setText("SUGGESTED_TILE_SIZE"));
		param.addContent(new Element("string").setText(val));
		parameters.addContent(param);
	}
	
	public void setUSE_JAI_IMAGEREAD(final boolean val){
		final Element param=new Element("entry");
		param.addContent(new Element("string").setText("USE_JAI_IMAGEREAD"));
		param.addContent(new Element("string").setText((val)?"true":"false"));
		parameters.addContent(param);
	}
	
	public void setBackgroundValues(final String val){
		final Element param=new Element("entry");
		param.addContent(new Element("string").setText("BackgroundValues"));
		param.addContent(new Element("string").setText(val));
		parameters.addContent(param);
	}
}
