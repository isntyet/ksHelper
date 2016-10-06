package com.ks.dblab.kshelper.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ks.dblab.kshelper.app.EndPoints;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jojo on 2016-06-16.
 */
public class VolleyRequest {
    private HashMap<String, String> params = new HashMap<String, String>();
    private ProgressDialog loadingDialog;

    private Context context = null;
    private String baseUrl = EndPoints.URL;
    private String urlName;
    private String resultString="";

    public VolleyRequest(Context context) {
        this.context = context;
    }

    public void request(final VolleyCallback callback) {
        RequestQueue queue = VolleyUtill.getInstance(context).getRequestQueue();
        loadingDialog = ProgressDialog.show(context, "불러오는 중입니다..","Please wait..", true, false);

        StringRequest stringRequest  = new StringRequest(
                com.android.volley.Request.Method.POST, baseUrl+urlName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.i("Response", response.toString());

                        resultString = response.toString();
                        callback.onSuccess(resultString);
                        loadingDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.i("Response_error", error.toString());
                        loadingDialog.dismiss();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };

        // Get the ImageLoader through your singleton class.
        //mImageLoader = VolleyUtill.getInstance(this).getImageLoader();

        VolleyUtill.getInstance(context).addToRequestQueue(stringRequest);
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }

    public void clearParams() {
        this.params = new HashMap<String, String>();
    }

    public void setParam(String key, String value) {
        this.params.put(key, value);
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }
}
