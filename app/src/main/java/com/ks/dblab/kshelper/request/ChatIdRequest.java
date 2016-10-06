package com.ks.dblab.kshelper.request;

import android.content.Context;

import com.ks.dblab.kshelper.util.VolleyRequest;

/**
 * Created by jojo on 2016-06-16.
 */
public class ChatIdRequest extends VolleyRequest{
    public ChatIdRequest(Context context) {
        super(context);
    }

    public void setParams(String name, String email) {
        super.clearParams();
        super.setParam("name", name);
        super.setParam("email", email);
    }

    public void startRequest(final VolleyCallback callback ) {
        setUrlName("v1/user/login");

        request(callback);
    }
}
