package com.ks.dblab.kshelper.request;

import android.content.Context;

import com.ks.dblab.kshelper.lost.LostItem;
import com.ks.dblab.kshelper.util.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-06-30.
 */
public class LostListRequest extends Request {
    public LostListRequest(Context context) {
        super(context);
    }

    public void setParams() {
        super.clearParams();
    }

    public List<LostItem> startRequest() {
        setUrlName("lost/select_lost_list.php");
        return jsondata(request());
    }

    public List<LostItem> jsondata(String data){

        List<LostItem> resData = new ArrayList<>();

        try{
            JSONObject jsonobj = new JSONObject(data);

            JSONArray jsonary = jsonobj.getJSONArray("result");

            //for(int i = 0; i < jsonary.length(); i++){
            for(int i = jsonary.length() - 1; i >= 0; i--){
                JSONObject obj = jsonary.getJSONObject(i);

                LostItem item = new LostItem(obj.getString("no"), obj.getString("title"), obj.getString("content"), obj.getString("img"), obj.getString("user"), obj.getString("user_no"), obj.getString("pwd"), obj.getString("reg_date"), obj.getString("comment_cnt"));

                resData.add(item);
            }

        }catch (Exception e){
            //Log.d("json error ===", e.getMessage());
        }

        return resData;
    }
}
