package com.osfg.certificatepinning.test;

import com.osfg.certificatepinning.httpclient.PinnedHttpClient;
import com.osfg.certificatepinning.utils.CertpinningUtil;

import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by athakur on 6/26/16.
 */
public class CertPinningTest {

    public static void main(String args[]) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("//Users//athakur//Documents//git//CertificatePinning//app//src//main//res//raw//ssllabs_com_crt")));
        StringBuilder sb = new StringBuilder();
        String readLine = null;
        while((readLine  = br.readLine())!=null) {
            sb.append(readLine);
        }
        System.out.println("Cert Data : " + sb.toString());
        List<X509Certificate> pinnedCerts = new ArrayList<>();
        pinnedCerts.add(CertpinningUtil.convertToX509Certificate(sb.toString()));
        PinnedHttpClient pinnedHttpClient = new PinnedHttpClient(null,pinnedCerts,true);
        pinnedHttpClient.execute(new HttpGet("https://www.ssllabs.com/"));
    }


}
