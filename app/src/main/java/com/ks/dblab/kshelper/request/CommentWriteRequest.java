package com.ks.dblab.kshelper.request;

import android.content.Context;

import com.ks.dblab.kshelper.util.Request;

import org.json.JSONObject;

/**
 * Created by jojo on 2016-07-03.
 */
public class CommentWriteRequest extends Request {
    public CommentWriteRequest(Context context) {
        super(context);
    }

    public void setParams(String article_no, String content, String user, String user_no, String user_image) {
        super.clearParams();
        super.setParam("article_no", article_no);
        super.setParam("content", content);
        super.setParam("user", user);
        super.setParam("user_no", user_no);
        super.setParam("user_image", user_image);
    }

    public String startRequest(String code) {
        if("lost".equals(code)){
            setUrlName("lost/upload_comment.php");
        } else if("sale".equals(code)){
            setUrlName("sale/upload_comment.php");
        }
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
