package com.osfg.certificatepinning.utils;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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

}
