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

import java.util.HashSet;
import java.util.Set;

import org.jdom.Element;

/**
 * LayerGroup encoder for GeoServer >= 2.3
 * 
 * @author Davide Savazzi (geo-solutions.it)
 */
public class GSLayerGroupEncoder23 extends GSLayerGroupEncoder {

    public static final String MODE_SINGLE = "SINGLE";
    public static final String MODE_NAMED = "NAMED";
    public static final String MODE_CONTAINER = "CONTAINER";
    public static final String MODE_EO = "EO";
    private static final Set<String> modes;
    static {
        modes = new HashSet<String>();
        modes.add(MODE_SINGLE);
        modes.add(MODE_NAMED);
        modes.add(MODE_CONTAINER);
        modes.add(MODE_EO);
    }
    
    private Element titleElem;
    private Element abstractElem;
    private Element modeElem;
    private Element rootLayerElem;
    private Element rootLayerStyleElem;
    
    public void setTitle(String title) {
        titleElem = elem("title", title);
    }
    
    public void setAbstract(String abstractTxt) {
        abstractElem = elem("abstractTxt", abstractTxt);
    }
    
    public void setMode(String mode) {
        if (!modes.contains(mode)) {
            throw new IllegalArgumentException("Invalid mode: " + mode);
        }
        
        modeElem = elem("mode", mode);
    }

    public void setRootLayer(String layer, String style) {
        rootLayerElem = elem("rootLayer", elem("name", layer));
        rootLayerStyleElem = elem("rootLayerStyle", elem("name", style));
    }
    
    @Override
    public void addLayer(String layer, String styleName) {
        initPublishables("publishables");
                
        publishablesElem.addContent(
                new Element("published").setAttribute("type", "layer").addContent(
                        elem("name", layer)));
        
        Element style = new Element("style");
        stylesElem.addContent(style);
        if (styleName != null) {
            style.addContent(elem("name", styleName));         
        }
    }

    public void addLayer(String layer, String styleName, String styleWorkspace) {
        initPublishables("publishables");

        publishablesElem.addContent(
                new Element("published").setAttribute("type", "layer").addContent(
                        elem("name", layer)));

        Element style = new Element("style");
        stylesElem.addContent(style);
        if (styleName != null) {
            style.addContent(elem("name", styleName));
            style.addContent(elem("workspace", styleWorkspace));
        }
    }
    
    public void addLayerGroup(String group) {
        initPublishables("publishables");
        
        publishablesElem.addContent(
                new Element("published").setAttribute("type", "layerGroup").addContent(
                        elem("name", group)));
        
        stylesElem.addContent(new Element("style"));        
    }
    
    @Override
    public String toString() {
        addToRoot(titleElem, abstractElem, modeElem, rootLayerElem, rootLayerStyleElem);                
        return super.toString();
    } 
}