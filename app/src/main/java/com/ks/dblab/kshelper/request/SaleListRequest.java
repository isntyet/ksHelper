package com.ks.dblab.kshelper.request;

import android.content.Context;

import com.ks.dblab.kshelper.sale.SaleItem;
import com.ks.dblab.kshelper.util.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-07-06.
 */
public class SaleListRequest extends Request{

    public SaleListRequest(Context context) {
        super(context);
    }

    public void setParams() {
        super.clearParams();
    }

    public List<SaleItem> startRequest() {
        setUrlName("sale/select_sale_list.php");
        return jsondata(request());
    }

    public List<SaleItem> jsondata(String data){

        List<SaleItem> resData = new ArrayList<>();

        try{
            JSONObject jsonobj = new JSONObject(data);

            JSONArray jsonary = jsonobj.getJSONArray("result");

            //for(int i = 0; i < jsonary.length(); i++){
            for(int i = jsonary.length() - 1; i >= 0; i--){
                JSONObject obj = jsonary.getJSONObject(i);

                SaleItem item = new SaleItem(obj.getString("no"), obj.getString("title"), obj.getString("content"), obj.getString("img"), obj.getString("user"), obj.getString("user_no"), obj.getString("pwd"), obj.getString("reg_date"), obj.getString("comment_cnt"));

                resData.add(item);
            }

        }catch (Exception e){
            //Log.d("json error ===", e.getMessage());
        }

        return resData;
    }
}
