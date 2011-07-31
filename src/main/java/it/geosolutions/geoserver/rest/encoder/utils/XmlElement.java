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

package it.geosolutions.geoserver.rest.encoder.utils;


import org.jdom.Content;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 *
 */
public class XmlElement{
	
	private final Element root;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final static XMLOutputter OUTPUTTER = new XMLOutputter(Format.getCompactFormat());
	
	public XmlElement(final String name){
		root=new Element(name);
	}
	
	public Element getRoot(){
		return root;
	}
	
	private XmlElement(){root=null;};
	

	public Element addContent(Content child){
		return root.addContent(child);
	}
	
	public boolean isEmpty() {
        return root.getChildren().isEmpty();
    }
	
	public boolean remove(final Element el){
		return ElementUtils.remove(root,el);
	}
	
	public Element contains(final Element el){
		return ElementUtils.contains(root,el);
	}
	
	public Element contains(final String key, final String val){
		return ElementUtils.contains(root,key,val);
	}
	
	public Element contains(final String key){
		return ElementUtils.contains(root,key);
	}
	
	/**
     * @return an xml String 
     */
    @Override
    public String toString() {
		return OUTPUTTER.outputString(root);
    }
}
