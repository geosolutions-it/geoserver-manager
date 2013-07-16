package it.geosolutions.geoserver.rest.encoder.feature;

import it.geosolutions.geoserver.rest.encoder.metadata.virtualtable.GSVirtualTableEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.virtualtable.VTGeometry;
import it.geosolutions.geoserver.rest.encoder.metadata.virtualtable.VTGeometryEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.virtualtable.VTParameter;
import it.geosolutions.geoserver.rest.encoder.metadata.virtualtable.VTParameterEncoder;
import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Test;

/**
 * 
 * @author eblondel
 * 
 */
public class GSVirtualTableEncoderTest {

	@Test
	public void virtualTableTest() {

		// Set-up the vtGeom
		final VTGeometryEncoder vtGeom = new VTGeometryEncoder("the_geom",
				"MultiPolygon", "4326");

		// Set-up 2 virtual table parameters
		final VTParameterEncoder vtParam1 = new VTParameterEncoder(
				"fieldname1", "default_value1", "^[\\w\\d\\s]+$");
		final VTParameterEncoder vtParam2 = new VTParameterEncoder(
				"fieldname2", "default_value2", "^[\\w\\d\\s]+$");

		// sql
		String sql = "select the_geom, id, field1, field2 from mytable where field1 = '%fieldname1%' and field2 = '%fieldname2%'";

		// Set-up the virtual table
		final GSVirtualTableEncoder vte = new GSVirtualTableEncoder();
		vte.setName("mysqlview");
		vte.setSql(sql);
		vte.addKeyColumn("id");
		vte.addVirtualTableGeometry(vtGeom);
		vte.addVirtualTableParameter(vtParam1);
		vte.addVirtualTableParameter(vtParam2);

		// TESTS

		Assert.assertEquals("mysqlview", vte.getName());
		Assert.assertEquals(sql, vte.getSql());
		Assert.assertEquals("id", vte.getRoot().getChildText("keyColumn"));

		final Element ge = (Element) vte.getRoot().getChild("geometry");
		Assert.assertEquals("the_geom",
				ge.getChildText(VTGeometry.name.toString()));
		Assert.assertEquals("MultiPolygon",
				ge.getChildText(VTGeometry.type.toString()));
		Assert.assertEquals("4326", ge.getChildText(VTGeometry.srid.toString()));

		final Element p1 = (Element) vte.getRoot().getChildren("parameter")
				.get(0);
		final Element p2 = (Element) vte.getRoot().getChildren("parameter")
				.get(1);
		Assert.assertEquals("fieldname1",
				p1.getChildText(VTParameter.name.toString()));
		Assert.assertEquals("default_value1",
				p1.getChildText(VTParameter.defaultValue.toString()));
		Assert.assertEquals("^[\\w\\d\\s]+$",
				p1.getChildText(VTParameter.regexpValidator.toString()));
		Assert.assertEquals("fieldname2",
				p2.getChildText(VTParameter.name.toString()));
		Assert.assertEquals("default_value2",
				p2.getChildText(VTParameter.defaultValue.toString()));
		Assert.assertEquals("^[\\w\\d\\s]+$",
				p2.getChildText(VTParameter.regexpValidator.toString()));

		vte.delKeyColumn("id");
		Assert.assertNull(vte.getRoot().getChildText("keyColumn"));
		Assert.assertEquals("mysqlview", vte.getName());
		Assert.assertEquals(sql, vte.getSql());

		vtGeom.setType("Point");
		vtParam1.setName("newfieldname");

		Assert.assertEquals("Point",
				((Element) vte.getRoot().getChildren("geometry").get(0))
						.getChildText(VTGeometry.type.toString()));
		Assert.assertEquals("newfieldname", ((Element) vte.getRoot()
				.getChildren("parameter").get(0)).getChildText(VTParameter.name
				.toString()));

		Assert.assertTrue(vtGeom.delSrid());
		Assert.assertTrue(vtParam1.delRegexpValidator());
	}

}
