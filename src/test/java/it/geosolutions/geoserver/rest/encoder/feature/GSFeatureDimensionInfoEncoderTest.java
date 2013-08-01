package it.geosolutions.geoserver.rest.encoder.feature;

import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder.Presentation;
import it.geosolutions.geoserver.rest.encoder.metadata.GSFeatureDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;

import java.math.BigDecimal;
import java.util.List;

import org.jdom.Element;
import org.jdom.filter.Filter;
import org.junit.Assert;
import org.junit.Test;

public class GSFeatureDimensionInfoEncoderTest {

	@Test
	public void dimensionTest() {
		final GSFeatureDimensionInfoEncoder elevationDimension = new GSFeatureDimensionInfoEncoder(
				"elevation_field");

		// if (LOGGER.isInfoEnabled())
		// LOGGER.info(encoder.toString());

		elevationDimension.setPresentation(
				Presentation.DISCRETE_INTERVAL, BigDecimal.valueOf(10));

		elevationDimension.setPresentation(
				Presentation.DISCRETE_INTERVAL, BigDecimal.valueOf(12));

		List<Element> elList = ElementUtils.search(
				elevationDimension.getRoot(), new Filter() {
					public boolean matches(Object obj) {
						if (obj instanceof Element) {
							final Element el = ((Element) obj);
							if (el.getName().equals(
									GSDimensionInfoEncoder.DIMENSIONINFO)) {
								return true;
							}
						}
						return false;
					}
				});
		// using set we get only one element called
		// Presentation.DISCRETE_INTERVAL
		Assert.assertEquals(elList.size(), 1);

		
		elevationDimension.setPresentation(Presentation.LIST);
		
		// this kind of presentation do not support a resolution parameter
		elList = ElementUtils.search(
				elevationDimension.getRoot(), new Filter() {
					public boolean matches(Object obj) {
						if (obj instanceof Element) {
							final Element el = ((Element) obj);
							if (el.getName().equals(
									GSDimensionInfoEncoder.RESOLUTION)) {
								return true;
							}
						}
						return false;
					}
				});
		
		Assert.assertEquals(elList.size(), 0);
		
	}

}
