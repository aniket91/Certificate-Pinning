package com.osfg.certificatepinning.httpclient;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by athakur on 6/26/16.
 */
public class SecureSocketFactory extends SSLSocketFactory {

    private static final String TLS = "TLS";
    SSLContext sslContext = SSLContext.getInstance(TLS);
    private static final String TAG = SecureSocketFactory.class.getSimpleName();

    public  SecureSocketFactory(KeyStore truststore, List<X509Certificate> pinnedCerts, boolean pinCerts) throws NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, KeyStoreException {
        super(truststore);
        sslContext.init(null, new TrustManager[] {
                new SecureTrustManager(pinnedCerts, pinCerts)
        }, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }

}
