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

import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.NestedElementEncoder;

import java.util.Iterator;

import org.jdom.Element;
import org.jdom.filter.Filter;

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 *
 */
public class GSImageMosaicEncoder extends GSCoverageEncoder {

	final private static String STRING="string";
	final private static String ENTRY="entry";
	
	final private NestedElementEncoder parameters=new NestedElementEncoder("parameters");
	
	public GSImageMosaicEncoder() {
        // Link members to the parent
		addContent(parameters.getRoot());
	}
	
	static class parametersFilter implements Filter {
		final String name;
		public parametersFilter(final String name){
			this.name=name;
		}
		
		private static final long serialVersionUID = 1L;
		public boolean matches(Object obj) {
			if (obj instanceof Element) {
				if (((Element)obj).getName().equals(ENTRY)){
					final Element el=((Element)obj).getChild(STRING);
					if (el.getText().equals(this.name)){
						return true;
					}
				}
			}
			return false;
		}
	};
	
	private final boolean removeParameter(final Filter filter){
		final Iterator<Element> it=ElementUtils.search(getRoot(), filter).iterator();
		if (it.hasNext()){
			final Element el=it.next();
//			if (it.hasNext())
//				return false;
//			else
			return ElementUtils.remove(el,el);
		}
		return false;
	}

	private final static String allowMultithreading="AllowMultithreading";
	/**
	 * @deprecated will be set to protected in the next release
	 * @param val
	 */
	public void addAllowMultithreading(final boolean val){
		final Element param=new Element(ENTRY);
		param.addContent(new Element(STRING).setText(allowMultithreading));
		param.addContent(new Element(STRING).setText((val)?"true":"false"));
		parameters.addContent(param);
	}
	
	private final static Filter allowMultithreadingFilter=new parametersFilter(allowMultithreading);
	public void setAllowMultithreading(final boolean val){
		removeParameter(allowMultithreadingFilter);
		addAllowMultithreading(val);
	}
	
	private final static String filter="Filter";
	/**
	 * @deprecated will be set to protected in the next release
	 * @param val
	 */
	public void addFilter(final String val){
		final Element param=new Element(ENTRY);
		param.addContent(new Element(STRING).setText(filter));
		param.addContent(new Element(STRING).setText(val));
		parameters.addContent(param);
	}
	
	private final static Filter filterFilter=new parametersFilter(filter);
	public void setFilter(final String val){
		removeParameter(filterFilter);
		addFilter(val);
	}

	private final static String maxAllowedTiles="MaxAllowedTiles";
	/**
	 * @deprecated will be set to protected in the next release
	 * @param val
	 */
	public void addMaxAllowedTiles(final int val){
		final Element param=new Element(ENTRY);
		param.addContent(new Element(STRING).setText(maxAllowedTiles));
		param.addContent(new Element(STRING).setText(String.valueOf(val)));
		parameters.addContent(param);
	}
	
	private final static Filter maxAllowedTilesFilter=new parametersFilter(maxAllowedTiles);
	public void setMaxAllowedTiles(final int val){
		removeParameter(maxAllowedTilesFilter);
		addMaxAllowedTiles(val);
	}
	
	private final static String inputTransparentColor="InputTransparentColor";
	/**
	 * @deprecated will be set to protected in the next release
	 * @param val
	 */
	public void addInputTransparentColor(final String val){
		final Element param=new Element(ENTRY);
		param.addContent(new Element(STRING).setText(inputTransparentColor));
		param.addContent(new Element(STRING).setText(val));
		parameters.addContent(param);
	}
	
	private final static Filter inputTransparentColorFilter=new parametersFilter(inputTransparentColor);
	public void setInputTransparentColor(final String val){
		removeParameter(inputTransparentColorFilter);
		addInputTransparentColor(val);
	}
	
	private final static String outputTransparentColor="OutputTransparentColor";
	/**
	 * @deprecated will be set to protected in the next release
	 * @param val
	 */
	public void addOutputTransparentColor(final String val){
		final Element param=new Element(ENTRY);
		param.addContent(new Element(STRING).setText(outputTransparentColor));
		param.addContent(new Element(STRING).setText(val));
		parameters.addContent(param);
	}
	
	private final static Filter outputTransparentColorFilter=new parametersFilter(outputTransparentColor);
	public void setOutputTransparentColor(final String val){
		removeParameter(outputTransparentColorFilter);
		addInputTransparentColor(val);
	}
	
	private final static String SUGGESTED_TILE_SIZE="SUGGESTED_TILE_SIZE";
	/**
	 * 
	 * @param val
	 * @deprecated will be set to protected in the next release
	 */
	public void addSUGGESTED_TILE_SIZE(final String val){
		final Element param=new Element(ENTRY);
		param.addContent(new Element(STRING).setText(SUGGESTED_TILE_SIZE));
		param.addContent(new Element(STRING).setText(val));
		parameters.addContent(param);
	}
	
	private final static Filter SUGGESTED_TILE_SIZEFilter=new parametersFilter(SUGGESTED_TILE_SIZE);
	public void setSUGGESTED_TILE_SIZE(final String val){
		removeParameter(SUGGESTED_TILE_SIZEFilter);
		addSUGGESTED_TILE_SIZE(val);
	}
	
	private final static String USE_JAI_IMAGEREAD="USE_JAI_IMAGEREAD";
	/**
	 * @deprecated will be set to protected in the next release
	 * @param val
	 */
	public void addUSE_JAI_IMAGEREAD(final boolean val){
		final Element param=new Element(ENTRY);
		param.addContent(new Element(STRING).setText(USE_JAI_IMAGEREAD));
		param.addContent(new Element(STRING).setText((val)?"true":"false"));
		parameters.addContent(param);
	}
	
	private final static Filter USE_JAI_IMAGEREADFilter=new parametersFilter(USE_JAI_IMAGEREAD);
	public void setUSE_JAI_IMAGEREAD(final boolean val){
		removeParameter(USE_JAI_IMAGEREADFilter);
		addUSE_JAI_IMAGEREAD(val);
	}
	
	private final static String backgroundValues="BackgroundValues";
	/**
	 * @deprecated will be set to protected in the next release
	 * @param val
	 */
	public void addBackgroundValues(final String val){
		final Element param=new Element(ENTRY);
		param.addContent(new Element(STRING).setText(backgroundValues));
		param.addContent(new Element(STRING).setText(val));
		parameters.addContent(param);
	}
	
	private final static Filter backgroundValuesFilter=new parametersFilter(backgroundValues);
	public void setBackgroundValues(final String val){
		removeParameter(backgroundValuesFilter);
		addBackgroundValues(val);
	}

}
