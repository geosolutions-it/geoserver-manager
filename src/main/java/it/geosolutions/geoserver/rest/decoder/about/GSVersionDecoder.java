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
        UNRECOGNIZED(-1), v22(22), v23(23), v24(24), v25(25), ABOVE(9999);

        final private int version;

        private VERSION(int val) {
            version = val;
        }

        public int getVersion() {
            return version;
        }
        
        public String toString(){
            return Integer.toString(version);
        }

        public static VERSION getVersion(String v) {
            if (v==null) {
                return UNRECOGNIZED;
            } else if (v.matches("2\\.2.*")) {
                return v22;
            } else if (v.matches("2\\.3.*")) {
                return v23;
            } else if (v.matches("2\\.4.*")) {
                return v24;
            } else if (v.matches("2\\.5.*")) {
                return v25;
            } else if (v.matches("2\\..+")) {
                return ABOVE;
            } else {
                return UNRECOGNIZED;
            }
        }
        
        public static String print(){
            return "["+v22+", "+v23+", "+v24+", "+v25+", "+ABOVE+", "+UNRECOGNIZED+"]";
        }
    }

}
