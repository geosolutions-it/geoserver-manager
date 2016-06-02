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

package it.geosolutions.geoserver.rest.encoder;

import it.geosolutions.geoserver.rest.encoder.authorityurl.GSAuthorityURLInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.identifier.GSIdentifierInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

import org.jdom.Element;
import org.jdom.filter.Filter;

/**
 * Layer encoder for Geoserver >= 2.2
 *
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * @author Emmanuel Blondel - emmanuel.blondel1@gmail.com 
 * 
 * The layer encoder is enabled by default
 * 
 * {@code
 * <layer>
 *      <name>{LAYERNAME}</name>
 *      <type>RASTER</type>
 *      <defaultStyle>
 *              <name>{STYLE_NAME}</name>
 *              <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://{GSURL}/rest/styles/{STYLE}xml" type="application/xml"/>
 *      </defaultStyle>
 *      <resource class="coverage">
 *            <name>{RESOURCE_NAME}</name>
 *              <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" 
 *                      href="http://{GSURL}/rest/workspaces/{WS}/coveragestores/{STORE}/coverages/{LAYER}.xml" type="application/xml"/>
 *      </resource>
 *      <attribution>
 *              <title>test</title>
 *              <href>http://www.fao.org/fileadmin/templates/faoweb/images/FAO-logo.png</href>
 *              <logoURL>http://www.fao.org/fileadmin/templates/faoweb/images/FAO-logo.png</logoURL>
 *              <logoWidth>412</logoWidth>
 *              <logoHeight>77</logoHeight>
 *              <logoType>image/png</logoType>
 *      </attribution>
 *      ...
 *      
 * </layer>
 * }
 * 
 * 
 * @since gs-2.2.x
 */
public class GSLayerEncoder extends PropertyXMLEncoder {
	
	public final static String STYLES = "styles";
	public final static String AUTHORITY_URLS="authorityURLs";
	public final static String IDENTIFIERS="identifiers";
	public final static String DEFAULT_STYLE = "defaultStyle";
	
	final private Element stylesEncoder = new Element(STYLES);
	final private Element defaultStyleEncoder = new Element(DEFAULT_STYLE);
	final private Element authorityURLListEncoder = new Element(AUTHORITY_URLS);
	final private Element identifierListEncoder = new Element(IDENTIFIERS);
	
    public GSLayerEncoder() {
        super("layer");
        addEnabled();
        addContent(stylesEncoder);
        addContent(authorityURLListEncoder);
        addContent(identifierListEncoder); 
    }
    
    /**
     * enabled the layer
     */
    protected void addEnabled(){
        add("enabled","true");
    }
    
    /**
     * @param enable true if layer should be set to enabled 
     */
    public void setEnabled(boolean enable){
    	if (enable)
    		set("enabled","true");
    	else
    		set("enabled","false");
    }
        
//    private final static String DESCRIPTION = "description";
//    /**
//     * Add the 'description' node with a text value from 'description'
//     * 
//     */
//    protected void addDescription(final String description) {
//        add(DESCRIPTION, description);
//    }
//    /**
//     * Set or modify the 'description' node with a text value from 'description'
//     */
//    public void setDescription(final String description) {
//        set(DESCRIPTION, description);
//    }
    
//    queryable
    private final static String QUERYABLE = "queryable";
    /**
     * Add the 'queryable' node with a text value from 'queryable' (true as default)
     */
    protected void addQueryable(final String queryable) {
        add(QUERYABLE, queryable!=null?queryable.toString():"true");
    }
    /**
     * Set or modify the 'queryable' node with a text value from 'queryable' (true as default)
     */
    public void setQueryable(final Boolean queryable) {
        set(QUERYABLE, queryable!=null?queryable.toString():"true");
    }

    /**
     * @see {@link GSLayerEncoder#setWmsPath(String)}
     * 
     * @param path the wms path to set
     */
    protected void addWmsPath(final String path) {
        add("path", path);
    }
    
    /**
     * Default WMS Path The GeoServer WMS path is, by default, /geoserver/wms.
     * However you can change it to /geoserver/anythingyouwant
     * 
     * @param path the wms path to set
     * @throws IllegalArgumentException if path is null or empty
     */
    public void setWmsPath(final String path) throws IllegalArgumentException {
        if (path==null || path.isEmpty())
            throw new IllegalArgumentException("Unable to set an empty or null parameter"); 
        set("path",path);
    }

    /**
     * @see {@link GSLayerEncoder#setDefaultStyle(String)}
     * @param defaultStyle 
     */
    protected void addDefaultStyle(String defaultStyle) {
        add("defaultStyle", defaultStyle);
    }
    
    /**
     * @see {@link GSLayerEncoder#setDefaultStyle(String)}
     * @param defaultStyle 
     */
    protected void addDefaultStyle(String workspace, String defaultStyle) {
        addContent(defaultStyleEncoder);
        Element el = new Element("name");
        el.setText(defaultStyle);
        defaultStyleEncoder.addContent(el);
        el = new Element("workspace");
        el.setText(workspace);
        defaultStyleEncoder.addContent(el);
    }

    /**
     * @param defaultStyle The style that will be applied if no style is specified.
     * @throws IllegalArgumentException if defaultStyle is null or empty
     */
    public void setDefaultStyle(final String defaultStyle) throws IllegalArgumentException {
        if (defaultStyle==null || defaultStyle.isEmpty())
            throw new IllegalArgumentException("Unable to set an empty or null parameter");
        set("defaultStyle", defaultStyle);
    }
    
    /**
     * @see {@link GSLayerEncoder#setDefaultStyle(String)}
     * @param defaultStyle 
     */
    public void setDefaultStyle(String workspace, String defaultStyle) {
        remove("defaultStyle");
        addDefaultStyle(workspace, defaultStyle);
    }
    
	/**
	 * Add a style
	 * 
	 * @param style
	 */
	public void addStyle(String style) {
		final Element el = new Element("style");
		el.setText(style);
		stylesEncoder.addContent(el);
	}

	/**
	 *
	 * @param styleName
	 * @param workspaceName
	 */
	public void addStyle(String styleName, String workspaceName) {
		final Element el = new Element("style");

		Element name = new Element("name");
		Element workspace = new Element("workspace");

		name.setText(styleName);
		workspace.setText(workspaceName);

		el.addContent(name);
		el.addContent(workspace);

		stylesEncoder.addContent(el);
	}

	/**
	 * delete a style from the list of available styles
	 * 
	 * @param style
	 * @return true if something is removed, false otherwise
	 */
	public boolean delStyle(final String style) {
		final Element el = new Element("style");
		el.setText(style);
		return (stylesEncoder.removeContent(new Filter() {
			private static final long serialVersionUID = 1L;

			public boolean matches(Object obj) {
				if (((Element) obj).getText().equals(style)) {
					return true;
				}
				return false;
			}
		})).size() == 0 ? false : true;
	}
	 
	/**
	 * 
	 * @param advertised
	 *            true if the layer should be advertised
	 */
	public void setAdvertised(boolean advertised) {
		if (advertised)
			set("advertised", "true");
		else
			set("advertised", "false");
	}
    
	/**
	 * Add an authorityURLInfo to the GeoServer layer
	 * 
	 * @param authorityURLInfo
	 */
	public void addAuthorityURL(GSAuthorityURLInfoEncoder authorityURLInfo) {
		authorityURLListEncoder.addContent(authorityURLInfo.getRoot());
	}

	/**
	 * Deletes a AuthorityURLInfo from the list using the authorityURL
	 * (AuthorityURLInfo href)
	 * 
	 * @param authorityURL
	 * @return true if something is removed, false otherwise
	 */
	public boolean delAuthorityURL(final String authorityURL) {
		return (authorityURLListEncoder.removeContent(GSAuthorityURLInfoEncoder
				.getFilterByHref(authorityURL))).size() == 0 ? false : true;
	}

	/**
	 * Add an identifierInfo to the GeoServer layer
	 * 
	 * @param identifierInfo
	 */
	public void addIdentifier(GSIdentifierInfoEncoder identifierInfo) {
		identifierListEncoder.addContent(identifierInfo.getRoot());
	}

	/**
	 * Deletes a IdentifierInfo from the list using the authority name
	 * (IdentifierInfo authority)
	 * 
	 * @param authority
	 * @return true if something is removed, false otherwise
	 */
	public boolean delIdentifier(final String authority) {
		return (identifierListEncoder.removeContent(GSIdentifierInfoEncoder
				.getFilterByHref(authority))).size() == 0 ? false : true;
	}
}
