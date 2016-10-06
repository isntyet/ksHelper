package com.ks.dblab.kshelper.request;

import android.content.Context;

import com.ks.dblab.kshelper.util.Request;

import org.json.JSONObject;

/**
 * Created by jojo on 2016-07-02.
 */
public class LostImageRequest extends Request{
    public LostImageRequest(Context context) {
        super(context);
    }

    public void setParams(String title, String content, String user, String user_no, String pwd, String image) {
        super.clearParams();
        super.setParam("title", title);
        super.setParam("content", content);
        super.setParam("user", user);
        super.setParam("user_no", user_no);
        super.setParam("pwd", pwd);
        if((image != null) && (!"".equals(image))){
            super.setParam("image", image);
        }
    }

    public String startRequest() {
        setUrlName("lost/upload_image.php");
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
