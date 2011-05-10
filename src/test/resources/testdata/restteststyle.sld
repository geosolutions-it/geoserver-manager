<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>country</sld:Name>
    <sld:UserStyle>
      <sld:Name>restteststyle</sld:Name>
      <sld:Title>STYLE FOR TESTING PURPOSES</sld:Title>
      <sld:Abstract>A sample style that just draws out a solid gray interior with a black 1px outline</sld:Abstract>
      <sld:FeatureTypeStyle>
        <sld:Name>name</sld:Name>
        <sld:FeatureTypeName>Feature</sld:FeatureTypeName>
        <sld:SemanticTypeIdentifier>SemanticType[ANY]</sld:SemanticTypeIdentifier>
        <sld:Rule>
          <sld:Title>Polygon</sld:Title>
          <sld:PolygonSymbolizer>
            <sld:Fill>
              <sld:CssParameter name="fill">
                <ogc:Literal>#C8B679</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="fill-opacity">
                <ogc:Literal>1.0</ogc:Literal>
              </sld:CssParameter>
            </sld:Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">
                <ogc:Literal>#000000</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="stroke-linecap">
                <ogc:Literal>butt</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="stroke-linejoin">
                <ogc:Literal>miter</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="stroke-opacity">
                <ogc:Literal>1</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="stroke-width">
                <ogc:Literal>1</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="stroke-dashoffset">
                <ogc:Literal>0</ogc:Literal>
              </sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
          <sld:TextSymbolizer>
            <sld:Label>
              <ogc:PropertyName>CNTRYNAME</ogc:PropertyName>
            </sld:Label>
            <sld:Font>
              <sld:CssParameter name="font-family">
                <ogc:Literal>Times New Roman</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="font-size">
                <ogc:Literal>14</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="font-style">
                <ogc:Literal>Normal</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="font-weight">
                <ogc:Literal>400</ogc:Literal>
              </sld:CssParameter>
            </sld:Font>
            <sld:LabelPlacement>
              <sld:LinePlacement>
                <sld:PerpendicularOffset>
                  <ogc:Literal>10</ogc:Literal>
                </sld:PerpendicularOffset>
              </sld:LinePlacement>
            </sld:LabelPlacement>
            <sld:Fill>
              <sld:CssParameter name="fill">
                <ogc:Literal>#000000</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="fill-opacity">
                <ogc:Literal>1.0</ogc:Literal>
              </sld:CssParameter>
            </sld:Fill>
          </sld:TextSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>

