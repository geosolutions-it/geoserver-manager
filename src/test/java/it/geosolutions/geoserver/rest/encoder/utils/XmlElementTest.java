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

import java.io.IOException;
import java.io.StringReader;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests XmlElements
 * @author Carl Schroedl - cschroedl@usgs.gov
 */
public class XmlElementTest {

    /**
     * Creates an XmlElement from a String
     * @param xmlString
     * @return the specified String as an XmlElement
     */
    private XmlElement makeElement(String xmlString){
        Document doc;
        SAXBuilder builder = new SAXBuilder();
        try {
            doc = builder.build(new StringReader(xmlString));
        } catch (JDOMException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        Element root = doc.getRootElement();
        
        return new XmlElement(root);
    }
    
    /**
     * Asserts that the serializations of two XmlElements are the same.
     * @param message
     * @param expected
     * @param actual 
     */
    private void assertEqualXml(String message, XmlElement expected, XmlElement actual){
        XMLOutputter out = new XMLOutputter();
        String expectedElementString = out.outputString(expected.getRoot());
        String actualElementString = out.outputString(actual.getRoot());
        assertEquals(message, expectedElementString, actualElementString);
    }
    
    @Test
    public void testRecursiveRemovalOnChildlessParent(){
        XmlElement root = makeElement("<parent/>");
        root.recursivelyRemoveEmptyChildren();
        assertEqualXml("no child elements expected", makeElement("<parent/>"), root);
    }
    
    @Test
    public void testRecursiveRemovalOfOneChild(){
        XmlElement root = makeElement("<parent><child/></parent>");
        root.recursivelyRemoveEmptyChildren();
        assertEqualXml("no child elements expected", makeElement("<parent/>"), root);
    }
    
    @Test
    public void testRecursiveRemovalOfOneChildWithOneKeeper(){
        XmlElement root = makeElement("<parent><child/><keep>keep</keep></parent>");
        root.recursivelyRemoveEmptyChildren();
        assertEqualXml("one child element expected", makeElement("<parent><keep>keep</keep></parent>"), root);
    }
    
    @Test
    public void testRecursiveRemovalOfOneParentAndOneChild() {
        XmlElement root = makeElement("<grandparent><parent><child/></parent></grandparent>");
        root.recursivelyRemoveEmptyChildren();
        assertEqualXml("no child elements expected", makeElement("<grandparent/>"), root);
    }
    
    @Test
    public void testRecursiveRemovalOfOneParentAndManyChildren() {
        XmlElement root = makeElement("<grandparent><parent><child/><child/><child/></parent></grandparent>");
        root.recursivelyRemoveEmptyChildren();
        assertEqualXml("no child elements expected", makeElement("<grandparent/>"), root);
    }
    
    @Test
    public void testRecursiveRemovalOfManyParentsWithOneChild() {
        XmlElement root = makeElement("<grandparent><parent><child/></parent><parent><child/></parent><parent><child/></parent></grandparent>");
        root.recursivelyRemoveEmptyChildren();
        assertEqualXml("no child elements expected", makeElement("<grandparent/>"), root);
    }

    @Test
    public void testRecursiveRemovalOfManyParentsAndManyChildren() {
        XmlElement root = makeElement("<grandparent><parent><child/><child/><child/></parent><parent><child/><child/><child/></parent><parent><child/><child/><child/></parent></grandparent>");
        root.recursivelyRemoveEmptyChildren();
        assertEqualXml("no child elements expected", makeElement("<grandparent/>"), root);
    }

    @Test
    public void testRecursiveRemovalOfManyParentsAndManyChildrenWithSomeKeepers() {
        XmlElement root = makeElement("<grandparent><keep>keep</keep><parent><keep>keep</keep><child/><child><keep>keep</keep></child><child/></parent><parent><child/><child/><child/></parent><parent><child/><child/><child><keep>keep</keep></child></parent></grandparent>");
        root.recursivelyRemoveEmptyChildren();
        assertEqualXml("only non-empty child elements should remain", makeElement("<grandparent><keep>keep</keep><parent><keep>keep</keep><child><keep>keep</keep></child></parent><parent><child><keep>keep</keep></child></parent></grandparent>"), root);
    }
    

}