package com.ks.dblab.kshelper.request;

import android.content.Context;

import com.ks.dblab.kshelper.util.Request;

import org.json.JSONObject;

/**
 * Created by jo on 2016-09-05.
 */
public class UserDeleteRequest extends Request {
    public UserDeleteRequest(Context context) {
        super(context);
    }

    public void setParams(String nickName, String userId) {
        super.clearParams();
        super.setParam("nick_name", nickName);
        super.setParam("user_id", userId);
    }

    public String startRequest() {
        setUrlName("setup/delete_user.php");
        return jsondata(request());
    }

    public String jsondata(String data){

        String resData = "";

        try{
            JSONObject jsonobj = new JSONObject(data);

            resData = jsonobj.getString("result");

        }catch (Exception e){
            //Log.d("json error ===", e.getMessage());
        }

        return resData;
    }
}
