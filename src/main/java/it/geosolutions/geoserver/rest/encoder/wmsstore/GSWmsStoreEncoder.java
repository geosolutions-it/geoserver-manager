package it.geosolutions.geoserver.rest.encoder.wmsstore;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSAbstractDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import org.jdom.Element;

import java.net.URL;

/**
 * Created by drakiko on 15/09/2015.
 */
public class GSWmsStoreEncoder extends GSAbstractStoreEncoder {

    static final String TYPE = "WMS";

    final static String ROOT = "wmsStore";

    static final String DEFAULT_MIN_CONNECTIONS = "1";
    static final String DEFAULT_MAX_CONNECTIONS = "6";
    static final String DEFAULT_CONNECTION_TIMEOUT = "30";
    static final String DEFAULT_READ_TIMEOUT = "60";


    public GSWmsStoreEncoder(String storeName, URL url) {
        super(GeoServerRESTPublisher.StoreType.WMSSTORES, ROOT);
        setType(TYPE);
        ensureValidName(storeName);
        setName(storeName);

        ensureValidURL(url);
        setCapabilitiesURL(url.toString());

        setMaxConnections(DEFAULT_MAX_CONNECTIONS);
        setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
        setReadTimeout(DEFAULT_READ_TIMEOUT);
    }

   public void setCapabilitiesURL(String capabilitiesURL){
       set("capabilitiesURL", capabilitiesURL);
   }

    public String getCapabilitiesURL(){
        Element e = ElementUtils.contains(getRoot(), "capabilitiesURL");
        return e!=null?e.getTextTrim():null;
    }

    public void setUser(String user) {
        set("user", user);
    }

    public String getUser() {
        Element e = ElementUtils.contains(getRoot(), "user");
        return e!=null?e.getTextTrim():null;
    }

    public void setPassword(String password) {
        set("password", password);
    }

    public String getPassword() {
        Element e = ElementUtils.contains(getRoot(), "password");
        return e!=null?e.getTextTrim():null;
    }

    public void setMaxConnections(String maxConnections) {
        set("maxConnections", maxConnections);
    }

    public String getMaxConnections() {
        Element e = ElementUtils.contains(getRoot(), "maxConnections");
        return e!=null?e.getTextTrim():null;
    }

    public void setReadTimeout(String readTimeout) {
        set("readTimeout", readTimeout);
    }

    public String getReadTimeout() {
        Element e = ElementUtils.contains(getRoot(), "readTimeout");
        return e!=null?e.getTextTrim():null;
    }

    public void setConnectTimeout(String connectTimeout) {
        set("connectTimeout", connectTimeout);
    }

    public String getConnectTimeout() {
        Element e = ElementUtils.contains(getRoot(), "connectTimeout");
        return e!=null?e.getTextTrim():null;
    }
    /**
     * @return {@value #TYPE}
     */
    protected String getValidType() {
        return TYPE;
    }

    /**
     * Check url validity.
     *
     * @param url the url
     * @throws IllegalArgumentException if url is null or empty
     */
    private static void ensureValidURL(URL url) {
        if (url == null || url.toString().isEmpty()) {
            throw new IllegalArgumentException(
                    "Wms store URL cannot be null or empty");
        }
    }

}
