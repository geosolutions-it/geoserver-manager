package it.geosolutions.geoserver.rest.encoder.datastore;

import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.encoder.utils.NestedElementEncoder;

import java.net.URL;

/**
 * Created by r3sist3nt on 21.12.2016.
 */
public class GSWMSDatastoreEncoder extends GSAbstractStoreEncoder {
    static final String TYPE = "wmsStore";

    static final int DEFAULT_MAX_CONNECTIONS = 10;
    static final int DEFAULT_READ_TIMEOUT = 16;
    static final int DEFAULT_CONNECTION_TIMEOUT = 16;
    static final boolean DEFAULT_USE_CONNECTION_POOLING = true;

    /**
     * Encoder for the WMS-Datastore
     * @author r3sist3nt
     *
     * Can be used to create a WMS Datastore on the Server.
     */
    public GSWMSDatastoreEncoder(String workspace,String name) {
        // Set fixed values
        super(GeoServerRESTPublisher.StoreType.WMSSTORES,"wmsStores");
        setType(TYPE);
        setRoot("wmsStore");
        set("name" , name);
        set("type","WMS");


        //Set Metadata
        NestedElementEncoder e = new NestedElementEncoder("metadata");
        e.add("useConnectionPooling", Boolean.toString(DEFAULT_USE_CONNECTION_POOLING));
        addContent(e.getRoot());

        //Set Default Values
        setMaxConnections(DEFAULT_MAX_CONNECTIONS);
        setReadTimeout(DEFAULT_READ_TIMEOUT);
        setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);


    }


    /**
     * Set the URL for the the remote Capabilities Document.
     * @param capabilitiesURL Service Endpoint.
     */
    public void setCapabilitiesURL(String capabilitiesURL){
        set("capabilitiesURL", capabilitiesURL);
    }

    public void setReadTimeout(int readTimeout){
        set("readTimeout", ""+readTimeout);
    }

    public void setConnectTimeout(int connectTimeout){
        set("connectTimeout",""+connectTimeout);
    }

    public void setEnabled(boolean e){
        set("enabled",Boolean.toString(e));
    }

    public void setMaxConnections(int maxConnections){
        set("maxConnections", ""+maxConnections);

    }



    /**
     * Check url validity.
     *
     * @param url the url
     * @throws IllegalArgumentException if url is null or empty
     */
    private static void ensureValidURL(URL url) {
        if (url == null || url.toString().equals("")) {
            throw new IllegalArgumentException(
                    "Shapefile store URL cannot be null or empty");
        }
    }

    /**
     * @return {@value #TYPE}
     */
    protected String getValidType() {
        return TYPE;
    }
}
