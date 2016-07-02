package com.osfg.certificatepinning.httpclient;

import com.osfg.certificatepinning.utils.CertpinningUtil;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

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

    public static final String HTTP_SCHEME = "http";
    public static final String HTTPS_SCHEME = "https";
    public static final int HTTP_PORT = 80;
    public static final int HTTPS_PORT = 443;

    private static final String TAG = PinnedHttpClient.class.getSimpleName();

    public PinnedHttpClient(HttpParams params, List<X509Certificate> pinnedCerts, boolean pinCerts, boolean isProxy) {
        super(CertpinningUtil.createClientConnectionManager(params, pinnedCerts, pinCerts, isProxy), (HttpParams)null);
    }
}
