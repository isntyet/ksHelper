package com.ks.dblab.kshelper.request;

import android.content.Context;

import com.ks.dblab.kshelper.util.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by jo on 2016-05-03.
 */
public class BusRequest extends Request {
    public BusRequest(Context context) {
        super(context);
    }

    public void setParams(String resId, String userId) {
        super.clearParams();
        super.setParam("reg_id", resId);
        super.setParam("user_id", userId);
    }

    public HashMap<String, String> startRequest() {
        setUrlName("select_test.php");
        return jsondata(request());
    }

    public HashMap<String, String> jsondata(String data){

        HashMap<String, String> resData = new HashMap<String, String>();

        try{
            JSONObject jsonobj = new JSONObject(data);

            JSONArray jsonary = jsonobj.getJSONArray("result");

            for(int i = 0; i < jsonary.length(); i++){
                JSONObject obj = jsonary.getJSONObject(i);
                resData.put("user_id", obj.getString("user_id"));
                resData.put("test2", obj.getString("test2"));
                resData.put("test3", obj.getString("test3"));
            }

        }catch (Exception e){
            //Log.d("json error ===", e.getMessage());
        }

        return resData;
    }
}
