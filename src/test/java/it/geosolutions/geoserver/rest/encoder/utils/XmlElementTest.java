package it.geosolutions.geoserver.rest.encoder.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class XmlElementTest {

    public XmlElementTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private XmlElement makeElement(String docString){
        Document doc;
        SAXBuilder builder = new SAXBuilder();
        try {
            doc = builder.build(new StringReader(docString));
        } catch (JDOMException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        Element root = doc.getRootElement();
        
        return new XmlElement(root);
    }
    
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