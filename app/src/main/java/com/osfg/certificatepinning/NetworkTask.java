package com.osfg.certificatepinning;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.osfg.certificatepinning.httpclient.PinnedHttpClient;
import com.osfg.certificatepinning.utils.CertpinningUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by athakur on 6/26/16.
 */
public class NetworkTask extends AsyncTask<String,Object,String> {

    private static final String TAG = NetworkTask.class.getSimpleName();

    private MainActivity ctx;
    private boolean pinnCerts;
    private String url;

    public NetworkTask(MainActivity ctx, boolean pinnCerts, String url) {
        this.ctx = ctx;
        this.pinnCerts = pinnCerts;
        this.url = url;
    }

    @Override
    protected String doInBackground(String[] params) {

        List<X509Certificate> pinnedCerts = new ArrayList<>();

        if(pinnCerts) {
            //pinning ssllabs cert file
            BufferedReader br = new BufferedReader(new InputStreamReader(ctx.getResources().openRawResource(R.raw.ssllabs_com_crt)));
            //BufferedReader br = new BufferedReader(new InputStreamReader(ctx.getResources().openRawResource(R.raw.github_com_crt)));
            StringBuilder sb = new StringBuilder();
            String readLine = null;
            try {
                while((readLine  = br.readLine())!=null) {
                    sb.append(readLine);
                }
            } catch (IOException e) {
                Log.e(TAG, "Could not read cert file", e);
            }
            Log.d(TAG, "Cert : " + sb.toString());

            pinnedCerts.add(CertpinningUtil.convertToX509Certificate(sb.toString()));
        }

        HttpParams httpParams = new BasicHttpParams();
        PinnedHttpClient pinnedHttpClient = new PinnedHttpClient(httpParams,pinnedCerts,pinnCerts);
        try {
            HttpResponse response = pinnedHttpClient.execute(new HttpGet(url));
            return response.getStatusLine().toString();
        } catch (Exception e) {
            Log.e(TAG, "Error in making network call",e);
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        ((TextView)ctx.findViewById(R.id.resultView)).setText(result);
        ((Button)ctx.findViewById(R.id.submit_id)).setText(ctx.getString(R.string.submit));
        ((Button)ctx.findViewById(R.id.submit_id)).setClickable(true);
        ((Button)ctx.findViewById(R.id.clear_id)).setClickable(true);
        Toast.makeText(ctx, "Operation Complete!", Toast.LENGTH_SHORT).show();
    }
}
