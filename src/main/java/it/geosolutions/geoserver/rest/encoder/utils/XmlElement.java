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
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 *
 */
public class XmlElement{
	
	private Element root;
	

    private static final long serialVersionUID = 1L;
	
	private final static XMLOutputter OUTPUTTER = new XMLOutputter(Format.getCompactFormat());
	
	public XmlElement(final String name){
		root=new Element(name);
	}

    public XmlElement(final Element e) {
        root = e;
    }
    
    /**
     * Empty constructor:<br/>
     * Use {@link #setRoot()} to initialize the root
     */
    protected XmlElement() {
    }

    /**
     * update the root of this node
     * @param root
     */
    protected void setRoot(final Element root) {
        this.root = root;
    }
    
    /**
     * update the root of this node
     * @param name is the name of the root node
     */
    protected void setRoot(final String name){
        root=new Element(name);
    }

	public Element getRoot(){
		return root;
	}
	
	protected void add(final String nodename, final String nodetext) {
    	add(nodename,new Text(nodetext));
    }
	
	protected void add(final String nodename, final Content nodetext) {
    	final Element el=new Element(nodename);
    	el.setContent(nodetext);
    	this.addContent(el);
    }
    
	protected void set(final String nodename, final String nodetext) {
    	set(nodename,new Text(nodetext));
    }
	
	protected void set(final String nodename, final Content nodeContent) {
    	final Element el=ElementUtils.contains(getRoot(),nodename);
    	if (el==null){
    		add(nodename,nodeContent);
    	}
    	else {
    		el.setContent(nodeContent);
    	}
    }
    
	protected Element addContent(Content child){
		return root.addContent(child);
	}
	
	public boolean isEmpty() {
        return root.getChildren().isEmpty();
    }
	
	public boolean remove(final String key){
		final Element el=ElementUtils.contains(root,key);
		if (el!=null){
			return ElementUtils.remove(root,el);
		}
		else
			return false;
	}
	
	/**
     * @return an xml String 
     */
    @Override
    public String toString() {
		return OUTPUTTER.outputString(root);
    }
}
