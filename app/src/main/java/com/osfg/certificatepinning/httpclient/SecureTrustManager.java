package com.osfg.certificatepinning.httpclient;

import android.util.Log;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by athakur on 6/26/16.
 */
public class SecureTrustManager implements X509TrustManager {

    List<X509Certificate> pinnedCerts;
    boolean pinCerts;
    private static final String TAG = SecureTrustManager.class.getSimpleName();

    public SecureTrustManager(List<X509Certificate> pinnedCerts, boolean pinCerts) {
        this.pinnedCerts = pinnedCerts;
        this.pinCerts = pinCerts;
    }


    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    private TrustManagerFactory getTrustManagerFactory(KeyStore keyStore) {
        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
        } catch (Exception e) {
            Log.e(TAG, "Trustmanager instantiation failed",e);
        }
        return tmf;
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        Log.d(TAG, "Cheking if client is trusted. Pinning enabled : " + pinCerts);


        if(pinCerts) {

            if(pinnedCerts == null || pinnedCerts.size() == 0) {
                Log.e(TAG, "Pinning enabled but no pinned certs provided");
                throw new CertificateException("No certificates provided for pinning");
            }

            if(chain == null || chain.length < 1) {
                if(pinnedCerts == null || pinnedCerts.size() == 0) {
                    Log.e(TAG, "No certificates received in chain");
                    throw new CertificateException("No certificates received in chain");
                }
            }

            if(pinnedCerts.contains(chain[0])) {
                return;
            }
            else {
                throw new CertificateException("Pinned certificate not matching with pinned X509Certificate cert");
            }
        }
        else {
            Log.d(TAG,"Certificate pinning disable. Doing normal checks.");
            try {
                for(TrustManager trustManager : getTrustManagerFactory(null).getTrustManagers()) {
                    ((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
                }
            } catch (Exception e) {
                Log.e(TAG, "Normal SSL check failed for site",e);
            }

        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
