package com.osfg.certificatepinning.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.osfg.certificatepinning.R;
import com.osfg.certificatepinning.httpclient.PinnedHttpClient;
import com.osfg.certificatepinning.httpclient.ApacheSecureSocketFactory;
import com.osfg.certificatepinning.httpclient.SecureTrustManager;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by athakur on 6/26/16.
 */
public class CertpinningUtil {

    private static final String TLS = "TLS";

    private static final String TAG = CertpinningUtil.class.getSimpleName();

    private static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERT = "-----END CERTIFICATE-----";

    public static X509Certificate convertToX509Certificate(String certData) {
        try {
            byte[] certByteData = Base64.decode(certData.replaceAll(BEGIN_CERT, "").replaceAll(END_CERT, ""), Base64.DEFAULT);

            InputStream inStream = new ByteArrayInputStream(certByteData);
            CertificateFactory factory = CertificateFactory.getInstance("X.509");

            return (X509Certificate) factory.generateCertificate(inStream);
        } catch (CertificateException e) {
            Log.e(TAG, "Error in creating X509Certificate object", e);
        }
        return null;
    }

    public static ClientConnectionManager createClientConnectionManager(HttpParams params, List<X509Certificate> pinnedCerts, boolean pinCerts) {

        Log.d(TAG, "Creating client connection manager with pinning enabled : " + pinCerts);
        String noOfCertsToPin = pinnedCerts == null ? "0" : String.valueOf(pinnedCerts.size());
        Log.d(TAG, "Creating client connection manager with no of pinned certs : " + noOfCertsToPin);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme(PinnedHttpClient.HTTP_SCHEME, PlainSocketFactory.getSocketFactory(), PinnedHttpClient.HTTP_PORT));
        try {
            schemeRegistry.register(new Scheme(PinnedHttpClient.HTTPS_SCHEME, new ApacheSecureSocketFactory(null, pinnedCerts, pinCerts), PinnedHttpClient.HTTPS_PORT));
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

    public static void showDialog(String title, String[] instructions, Activity callingActivity) {
        LayoutInflater inflater = LayoutInflater.from(callingActivity);
        View informationView = inflater.inflate(R.layout.information_layout, null);

        LinearLayout informationLayout = (LinearLayout) informationView.findViewById(R.id.informationLayout);

        for (String instruction : instructions) {
            final TextView instructionTextView = new TextView(callingActivity);
            instructionTextView.setText(instruction);
            instructionTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bullet_point, 0, 0, 0);
            final float scale = callingActivity.getResources().getDisplayMetrics().density;
            instructionTextView.setCompoundDrawablePadding((int) (10.0f * scale));
            int padding = (int) (10.0f * scale);
            instructionTextView.setPadding(padding, padding, padding, padding);
            informationLayout.addView(instructionTextView);
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(callingActivity);
        alertDialog.setTitle(title);
        alertDialog.setView(informationView);
        alertDialog.setPositiveButton(callingActivity.getString(R.string.string_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public static String downloadUrl(String myurl, List<X509Certificate> pinnedCerts, boolean pinnCerts) throws IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            if(pinnCerts) {
                conn.setSSLSocketFactory(getPinnedSSLContext(null, pinnedCerts, pinnCerts).getSocketFactory());
            }
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return String.valueOf(response);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    // Reads an InputStream and converts it to a String.
    public static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    public static SSLContext getPinnedSSLContext(KeyStore truststore, List<X509Certificate> pinnedCerts, boolean pinCerts) throws NoSuchAlgorithmException, KeyManagementException {

        SSLContext sslContext = SSLContext.getInstance(TLS);
        TrustManager tm = new SecureTrustManager(pinnedCerts, pinCerts);
        sslContext.init(null, new TrustManager[] {tm}, null);
        return sslContext;
    }


}
