/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *
 *  Copyright (C) 2007,2015 GeoSolutions S.A.S.
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

package it.geosolutions.geoserver.rest.decoder.about;

import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.XmlElement;

import org.jdom.Attribute;
import org.jdom.Element;

/**
 * Encode an XML for about/version.xml
 * 
 * 
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * 
 */
public class GSVersionDecoder extends XmlElement {
    public final static String ABOUT = "about";

    private GSAboutResource geoserver;

    public class GSAboutResource extends XmlElement {
        public final static String RESOURCE = "resource";

        public final static String NAME = "name";

        public final static String VERSION = "Version";

        private Element version;

        public GSAboutResource() {
            create();
        }
        
        private void create(){
            setRoot(RESOURCE);
            version = new Element(VERSION);
            addContent(version);
        }

        public void setName(String name) {
            final Attribute _name = this.getRoot().getAttribute(GSAboutResource.NAME);
            if (name!=null)
                _name.setValue(name);
            else
                this.getRoot().setAttribute(GSAboutResource.NAME, name);
            
        }
        
        public String getName() {
            final Attribute name = this.getRoot().getAttribute(GSAboutResource.NAME);
            if (name!=null)
                return name.getValue();
            else
                return null;
        }

        public GSAboutResource(Element el) {
            super();
            if (el!=null){
                setRoot(el);
                version = ElementUtils.contains(el, GSAboutResource.VERSION);
            } else {
                create();
                setVersion(GSVersionDecoder.VERSION.UNRECOGNIZED.toString());
            }
        }
        
        public void setVersion(String v){
            version.setText(v);
        }
    }
    

    /**
     * Load the string representation into this encoder
     * 
     * @param document
     */
    public GSVersionDecoder(String document) {
        Element root=JDOMBuilder.buildElement(document);
        if (root!=null){
            setRoot(root);
            geoserver = new GSAboutResource(ElementUtils.contains(this.getRoot(),
                    GSAboutResource.RESOURCE));
        }else {
            create();
        }
    }

    public GSVersionDecoder() {
        create();
    }
    
    private void create(){
        setRoot("about");
        geoserver = new GSAboutResource();
        addContent(geoserver.getRoot());
    }
    
    public GSAboutResource getGeoServer(){
        return geoserver;
    }

    public VERSION getVersion() {
        Element e = ElementUtils.contains(geoserver.version, GSAboutResource.VERSION);
        return VERSION.getVersion(e.getTextTrim());
    }
    
    /**
     * @see {@link Enum#compareTo(Enum)}
     * @param v
     * @return
     */
    public int compareTo(VERSION v) {
        return getVersion().compareTo(v);
    }

    public static GSVersionDecoder build(String response) {
        return new GSVersionDecoder(response);
    }

    public enum VERSION {
        v22(22, "2\\.2([^0-9]|$).*"),
        v23(23, "2\\.3([^0-9]|$).*"),
        v24(24, "2\\.4([^0-9]|$).*"),
        v25(25, "2\\.5([^0-9]|$).*"),
        v26(26, "2\\.6([^0-9]|$).*"),
        v27(27, "2\\.7([^0-9]|$).*"),
        v28(28, "2\\.8([^0-9]|$).*"),
        ABOVE(9999, "2\\..+"),
        UNRECOGNIZED(-1, null);

        final private int version;
        final private String pattern;

        private VERSION(int val, String pattern) {
            version = val;
            this.pattern = pattern;
        }

        public int getVersion() {
            return version;
        }
        
        public String toString(){
            return Integer.toString(version);
        }

        public static VERSION getVersion(String v) {
            if (v == null) {
                return UNRECOGNIZED;
            }
            
            for (VERSION version : VERSION.values()) {
                if(version.pattern != null && v.matches(version.pattern)) {
                    return version;
                }
            }

            return UNRECOGNIZED;
        }
        
        public static String print(){

            StringBuilder sb = new StringBuilder("[");
            for (VERSION v : VERSION.values()) {
                sb.append(v.toString()).append(' ');
            }
            sb.append("]");
            return sb.toString();
        }
    }

}
