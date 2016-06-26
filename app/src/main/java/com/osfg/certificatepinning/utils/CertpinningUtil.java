package com.osfg.certificatepinning.utils;

import android.util.Base64;
import android.util.Log;

import com.osfg.certificatepinning.httpclient.PinnedHttpClient;
import com.osfg.certificatepinning.httpclient.SecureSocketFactory;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Created by athakur on 6/26/16.
 */
public class CertpinningUtil {

    private static final String TAG = CertpinningUtil.class.getSimpleName();

    private static final String BEGIN_CERT= "-----BEGIN CERTIFICATE-----";
    private static final String END_CERT=  "-----END CERTIFICATE-----";

    public static X509Certificate convertToX509Certificate(String certData)
    {
        try {
            byte[] certByteData = Base64.decode(certData.replaceAll(BEGIN_CERT, "").replaceAll(END_CERT, ""), Base64.DEFAULT);

            InputStream inStream = new ByteArrayInputStream(certByteData);
            CertificateFactory factory = CertificateFactory.getInstance("X.509");

            return (X509Certificate) factory.generateCertificate(inStream);
        }
        catch (CertificateException e) {
            Log.e(TAG, "Error in creating X509Certificate object",e);
        }
        return null;
    }

    public static ClientConnectionManager createClientConnectionManager(HttpParams params, List<X509Certificate> pinnedCerts, boolean pinCerts) {

        Log.d(TAG, "Creating client connection manager with pinning enabled : " + pinCerts);
        String noOfCertsToPin = pinnedCerts==null?"0":String.valueOf(pinnedCerts.size());
        Log.d(TAG, "Creating client connection manager with no of pinned certs : " + noOfCertsToPin);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme(PinnedHttpClient.HTTP_SCHEME, PlainSocketFactory.getSocketFactory(),PinnedHttpClient.HTTP_PORT));
        try {
            schemeRegistry.register(new Scheme(PinnedHttpClient.HTTPS_SCHEME, new SecureSocketFactory(null, pinnedCerts, pinCerts), PinnedHttpClient.HTTPS_PORT));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return new ThreadSafeClientConnManager(params, schemeRegistry);
    }

}
