package com.ks.dblab.kshelper.request;

import android.content.Context;

import com.ks.dblab.kshelper.setup.NoticeItem;
import com.ks.dblab.kshelper.util.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-07-20.
 */
public class NoticeRequest extends Request {

    public NoticeRequest(Context context) {
        super(context);
    }

    public void setParams() {
        super.clearParams();
    }

    public List<NoticeItem> startRequest() {
        setUrlName("setup/notice/select_notice.php");
        return jsondata(request());
    }

    public List<NoticeItem> jsondata(String data){

        List<NoticeItem> resData = new ArrayList<>();

        try{
            JSONObject jsonobj = new JSONObject(data);

            JSONArray jsonary = jsonobj.getJSONArray("result");

            //for(int i = 0; i < jsonary.length(); i++){
            for(int i = jsonary.length() - 1; i >= 0; i--){
                JSONObject obj = jsonary.getJSONObject(i);

                NoticeItem item = new NoticeItem(obj.getString("no"), obj.getString("title"), obj.getString("content"), obj.getString("create_at"));

                resData.add(item);
            }

        }catch (Exception e){
            //Log.d("json error ===", e.getMessage());
        }

        return resData;
    }
}
