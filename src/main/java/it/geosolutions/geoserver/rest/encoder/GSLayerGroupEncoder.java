/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2013 GeoSolutions S.A.S.
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

import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

import org.jdom.Element;

/**
 * LayerGroup encoder for GeoServer < 2.3
 * 
 * @author Davide Savazzi (geo-solutions.it)
 */
public class GSLayerGroupEncoder extends PropertyXMLEncoder {

    protected Element nameElem;
    protected Element workspaceElem;
    protected Element boundsElem;
    protected Element publishablesElem;
    protected Element stylesElem;
    
    
    public GSLayerGroupEncoder() {
        super("layerGroup");
    }

    
    public void setWorkspace(String workspace) {
        workspaceElem = elem("workspace", elem("name", workspace));
    }
    
    public void setName(String name) {
        nameElem = elem("name", name);
    }
        
    public void addLayer(String layer) {
        addLayer(layer, null);
    }
    
    public void addLayer(String layer, String styleName) {
        initPublishables("layers");
        
        publishablesElem.addContent(elem("layer", elem("name", layer)));
        
        Element style = new Element("style");
        stylesElem.addContent(style);
        if (styleName != null) {
            style.addContent(elem("name", styleName));         
        }
    }
    
    public void setBounds(String crs, double minx, double maxx, double miny, double maxy) {
        boundsElem = elem("bounds", 
                elem("minx", Double.toString(minx)),
                elem("maxx", Double.toString(maxx)),
                elem("miny", Double.toString(miny)),
                elem("maxy", Double.toString(maxy)),        
                elem("crs", "class", "projected").setText(crs));
    }
    
    protected void initPublishables(String publishablesTag) {
        if (publishablesElem == null) {
            publishablesElem = new Element(publishablesTag);
        }
        
        if (stylesElem == null) {
            stylesElem = new Element("styles");            
        }
    }    
    
    protected void addToRoot(Element ... elements) {
        for (Element e : elements) {
            if (e != null) {
                getRoot().addContent(e);
            }
        }
    }
    
    protected Element elem(String tag, String attributeName, String attributeValue) {
        return new Element(tag).setAttribute(attributeName, attributeValue);
    }    
    
    protected Element elem(String tag, String text) {
        return new Element(tag).setText(text);
    }
    
    protected Element elem(String tag, Element ... children) {
        Element parent = new Element(tag);
        for (Element child : children) {
            parent.addContent(child);
        }
        return parent;
    }    
    
    @Override
    public String toString() {
        addToRoot(nameElem, workspaceElem, boundsElem, publishablesElem, stylesElem);        
        return super.toString();
    }    
}