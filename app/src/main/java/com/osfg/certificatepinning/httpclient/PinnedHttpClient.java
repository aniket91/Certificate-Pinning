package com.osfg.certificatepinning.httpclient;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Created by athakur on 6/26/16.
 */
public class PinnedHttpClient extends DefaultHttpClient {

    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";
    private static final int HTTP_PORT = 80;
    private static final int HTTPS_PORT = 443;

    private List<X509Certificate> pinnedCerts;
    private boolean pinCerts;

    private static final String TAG = PinnedHttpClient.class.getSimpleName();

    public PinnedHttpClient(List<X509Certificate> pinnedCerts, boolean pinCerts) {
        this.pinnedCerts = pinnedCerts;
        this.pinCerts = pinCerts;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme(HTTP_SCHEME, PlainSocketFactory.getSocketFactory(),HTTP_PORT));
        try {
            schemeRegistry.register(new Scheme(HTTPS_SCHEME, new SecureSocketFactory(null, pinnedCerts, pinCerts), HTTPS_PORT));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return new ThreadSafeClientConnManager(getParams(), schemeRegistry);
    }
}
