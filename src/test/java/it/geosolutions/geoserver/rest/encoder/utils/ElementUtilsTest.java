package it.geosolutions.geoserver.rest.encoder.utils;


import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.filter.Filter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElementUtilsTest {
	

    /**
     * Default logger
     */
    protected final static Logger LOGGER = LoggerFactory.getLogger(ElementUtilsTest.class);
    
	
	final static String NAME="TEST";
	final Element root=new Element(NAME);
	final int maxDepth=5;
	final Filter filter=new Filter(){
		public boolean matches(Object obj) {
			if (obj instanceof Element){
				if (((Element)obj).getName().equals(NAME)){
					return true;
				}
			}
			return false;
		}
	};
	
	@Before
	public void setUp() throws Exception {
		root.addContent("1");
		final Element child1=new Element(NAME);
		child1.addContent("2");
		final Element child2=new Element(NAME);
		child2.addContent("3");
		final Element child3=new Element(NAME);
		child3.addContent("4");
		final Element child4=new Element(NAME);
		child4.addContent("5");
		
		root.addContent(child1);
		child1.addContent(child2);
		child2.addContent(child3);
		child3.addContent(child4);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void containsFilterDepthTest(){
		LOGGER.info("STARTING-> containsFilterDepthTest");
		
		final List<Element> list=ElementUtils.search(this.root, filter, 0);
		Assert.assertEquals(1,list.size());
		
		final List<Element> list2=ElementUtils.search(this.root, filter, 6);
		Assert.assertEquals(maxDepth,list2.size());
		
		final Filter myFilter=new Filter() {
			public boolean matches(Object obj) {
				if (obj instanceof Element){
					final Element el=((Element)obj);
					if (el.getName().equals(NAME)){
						if (el.getText().equals("1") || el.getText().equals("3")) 
							return true;
					}
				}
				return false;
			}
		};
		final List<Element> list3=ElementUtils.search(this.root, myFilter, 3);
		Assert.assertEquals(2,list3.size());
		final Iterator<?> it=list3.iterator();
		while (it.hasNext()){
			final Object obj=it.next();
			if (obj instanceof Element){
				final Element el=(Element)obj;
				LOGGER.info("LOCATED-> "+el.getName()+" level "+el.getText());
			}
		}
	}
	
	@Test
	public void containsFilterTest(){
		LOGGER.info("STARTING-> containsFilterTest");
		
		final Iterator<?> it=ElementUtils.search(this.root, filter).iterator();
		Assert.assertTrue("Elements found", it.hasNext());
		int nFound=0;
		while (it.hasNext()){
			final Object obj=it.next();
			if (obj instanceof Element){
				nFound++;
				Element el=(Element)obj;
				LOGGER.info("LOCATED-> "+el.getName()+" level "+el.getText());
			}
		}
		Assert.assertEquals(maxDepth,nFound);
	}
	
	
	@Test
	public void containsWrappersTest(){
		LOGGER.info("STARTING-> containsWrapperTest");
		
		Element el=ElementUtils.contains(this.root, root);
		Assert.assertNotNull(el);
		
		el=ElementUtils.contains(this.root, new Element(NAME));
		Assert.assertNull(el);
		
		el=ElementUtils.contains(this.root, NAME);
		Assert.assertNotNull(el);
		
		el=ElementUtils.contains(this.root, "NOTFOUND");
		Assert.assertNull(el);
		
		el=ElementUtils.contains(this.root, NAME, "3");
		Assert.assertNotNull(el);
		
		el=ElementUtils.contains(this.root, NAME, "NOTFOUND");
		Assert.assertNull(el);
	}
	
	@Test
	public void removeTest(){
		LOGGER.info("STARTING-> removeTest");
		
		// contains
		Element el=ElementUtils.contains(this.root, NAME, "4");
		Assert.assertNotNull(el);
		// remove		
		Assert.assertTrue(ElementUtils.remove(this.root, el));
		// do not contains
		el=ElementUtils.contains(this.root, el);
		Assert.assertNull(el);

//		XMLOutputter o=new XMLOutputter();
//		try {
//			o.output(root, System.out);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Assert.assertTrue(ElementUtils.remove(this.root, root));
		
	}
}
