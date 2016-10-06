package com.ks.dblab.kshelper.request;

import android.content.Context;

import com.ks.dblab.kshelper.etc.CommentItem;
import com.ks.dblab.kshelper.util.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jojo on 2016-07-03.
 */
public class CommentRequest extends Request {
    public CommentRequest(Context context) {
        super(context);
    }

    public void setParams(String article_no) {
        super.clearParams();
        super.setParam("article_no", article_no);
    }

    public List<CommentItem> startRequest(String code) {
        if("lost".equals(code)){
            setUrlName("lost/select_comment.php");
        } else if("sale".equals(code)){
            setUrlName("sale/select_comment.php");
        }
        return jsondata(request());
    }

    public List<CommentItem> jsondata(String data){

        List<CommentItem> resData = new ArrayList<>();

        try{
            JSONObject jsonobj = new JSONObject(data);

            JSONArray jsonary = jsonobj.getJSONArray("result");

            //for(int i = 0; i < jsonary.length(); i++){
            for(int i = jsonary.length() - 1; i >= 0; i--){
                JSONObject obj = jsonary.getJSONObject(i);

                CommentItem item = new CommentItem(obj.getString("no"), obj.getString("article_no"), obj.getString("content"), obj.getString("user"), obj.getString("user_no"), obj.getString("user_image"), obj.getString("reg_date"));

                resData.add(item);
            }

        }catch (Exception e){
            //Log.d("json error ===", e.getMessage());
        }

        return resData;
    }
}
