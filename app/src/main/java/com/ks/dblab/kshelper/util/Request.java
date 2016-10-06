package com.ks.dblab.kshelper.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.ks.dblab.kshelper.app.EndPoints;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

/**
 * Created by jo on 2016-05-02.
 */
public abstract class Request {
    private HashMap<String, Object> params = new HashMap<String, Object>();
    private ProgressDialog loadingDialog;

    private Context context = null;
    private String baseUrl = EndPoints.URL;
    private String urlName;
    private String myResult;

    public Request(Context context) {
        this.context = context;
    }

    public String request() {
        String data = null;
        try {
            data = new InsertTask().execute(urlName).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return data;
    }

    private class InsertTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(context, "불러오기..", "Please wait..", true, false);
        }

        @Override
        protected String doInBackground(String... params) {
            return HttpPostData(params[0]);
        }

        protected void onPostExecute(String result) {
            loadingDialog.dismiss();
        }
    }

    public String HttpPostData(String name) {
        try {
            URL url = new URL(baseUrl + name); // URL
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");

            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();

            Iterator<Entry<String, Object>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();
                if (buffer.length() > 0) {
                    buffer.append("&");
                }
                buffer.append(String.format("%s=%s", entry.getKey(),
                        URLEncoder.encode(entry.getValue().toString(), "UTF-8")));
            }

            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            reader.close();

            myResult = builder.toString();
            //Log.d("URL INFO", url.toString());
            //Log.d("RESPONSE", myResult);

        } catch (MalformedURLException e) {

        } catch (IOException e) {

        } // try
        return myResult;
    }

    public void clearParams() {
        this.params = new HashMap<String, Object>();
    }

    public void setParam(String key, String value) {
        this.params.put(key, value);
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

}
