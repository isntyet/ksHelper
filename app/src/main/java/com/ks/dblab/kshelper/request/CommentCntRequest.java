package com.ks.dblab.kshelper.request;

import android.content.Context;

import com.ks.dblab.kshelper.util.Request;

import org.json.JSONObject;

/**
 * Created by jojo on 2016-07-03.
 */
public class CommentCntRequest extends Request {
    public CommentCntRequest(Context context) {
        super(context);
    }

    public void setParams(String article_no) {
        super.clearParams();
        super.setParam("article_no", article_no);
    }

    public String startRequest(String code) {
        if("lost".equals(code)){
            setUrlName("lost/update_comment_cnt.php");
        } else if("sale".equals(code)){
            setUrlName("sale/update_comment_cnt.php");
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
