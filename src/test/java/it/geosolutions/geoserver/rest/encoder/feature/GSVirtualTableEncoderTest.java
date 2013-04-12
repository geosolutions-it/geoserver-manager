package it.geosolutions.geoserver.rest.encoder.feature;

import it.geosolutions.geoserver.rest.encoder.metadata.GSVirtualTableEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSVirtualTableGeomEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSVirtualTableParamEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.VirtualTableGeometry;
import it.geosolutions.geoserver.rest.encoder.metadata.VirtualTableParameter;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author eblondel
 *
 */
public class GSVirtualTableEncoderTest {
	
	@Test
	public void virtualTableTest(){
		
		// Set-up the vtGeom
		final GSVirtualTableGeomEncoder vtGeom = new GSVirtualTableGeomEncoder();
		vtGeom.setup("the_geom", "MultiPolygon", "4326");

		// Set-up 2 virtual table parameters
		final GSVirtualTableParamEncoder vtParam1 = new GSVirtualTableParamEncoder();
		vtParam1.setup("fieldname1", "default_value1", "^[\\w\\d\\s]+$");
		final GSVirtualTableParamEncoder vtParam2 = new GSVirtualTableParamEncoder();
		vtParam2.setup("fieldname2", "default_value2", "^[\\w\\d\\s]+$");

		// sql
		String sql = "select the_geom, id, field1, field2 from mytable where field1 = '%fieldname1%' and field2 = '%fieldname2%'";

		// Set-up the virtual table
		final GSVirtualTableEncoder vte = new GSVirtualTableEncoder();
		vte.setup("mysqlview", sql, Arrays.asList("id"), Arrays.asList(vtGeom), Arrays.asList(vtParam1, vtParam2));
		
		//TESTS
		
		Assert.assertEquals("mysqlview", vte.getName());
		Assert.assertEquals(sql, vte.getSql());
		Assert.assertEquals("id", vte.getKeyColumnList().get(0));
		
		final GSVirtualTableGeomEncoder ge = vte.getVirtualTableGeomEncoderList().get(0);
		Assert.assertEquals("the_geom", ge.getVirtualTableGeometryMember(VirtualTableGeometry.name));
		Assert.assertEquals("MultiPolygon", ge.getVirtualTableGeometryMember(VirtualTableGeometry.type));
		Assert.assertEquals("4326", ge.getVirtualTableGeometryMember(VirtualTableGeometry.srid));
		
		final GSVirtualTableParamEncoder p1 = vte.getVirtualTableParamEncoderList().get(0);
		final GSVirtualTableParamEncoder p2 = vte.getVirtualTableParamEncoderList().get(1);
		Assert.assertEquals("fieldname1", p1.getVirtualTableParamMember(VirtualTableParameter.name));
		Assert.assertEquals("default_value1", p1.getVirtualTableParamMember(VirtualTableParameter.defaultValue));
		Assert.assertEquals("^[\\w\\d\\s]+$", p1.getVirtualTableParamMember(VirtualTableParameter.regexpValidator));
		Assert.assertEquals("fieldname2", p2.getVirtualTableParamMember(VirtualTableParameter.name));
		Assert.assertEquals("default_value2", p2.getVirtualTableParamMember(VirtualTableParameter.defaultValue));
		Assert.assertEquals("^[\\w\\d\\s]+$", p2.getVirtualTableParamMember(VirtualTableParameter.regexpValidator));
		
		ge.setVirtualTableGeometryMember(VirtualTableGeometry.type, "Point");
		p1.setVirtualTableParamMember(VirtualTableParameter.name, "newfieldname");
		vte.setup("mysqlview", sql, null, Arrays.asList(ge), Arrays.asList(p1,p2));
		
		Assert.assertEquals("mysqlview", vte.getName());
		Assert.assertEquals(sql, vte.getSql());
		Assert.assertNull(vte.getKeyColumnList());
		Assert.assertEquals("Point", vte.getVirtualTableGeomEncoderList().get(0)
											.getVirtualTableGeometryMember(VirtualTableGeometry.type));
		Assert.assertEquals("newfieldname", vte.getVirtualTableParamEncoderList().get(0)
											.getVirtualTableParamMember(VirtualTableParameter.name));

		Assert.assertTrue(vtGeom.delVirtualTableGeometryMember(VirtualTableGeometry.srid));
		Assert.assertTrue(vtParam1.delVirtualTableParamMember(VirtualTableParameter.regexpValidator));
	}
	

}
