package com.ks.dblab.kshelper.mcc;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016-06-21.
 */
public class MccNewsActivity extends BaseActivity {

    private static Thread thread = null;
    private ContentsDownlodTask task;
    private NewsAdapter adapter;
    private ListView listView;


    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_mcc_news);
        getSupportActionBar().setTitle("뉴스");

        listView = (ListView) view.findViewById(R.id.listView);

        task = new ContentsDownlodTask(this);
        task.execute();
    }


    @Override
    protected void destroyActivity() {
        listView.setAdapter(null);
    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        }
    }

    private void setupContent(ArrayList<News> list) {
        adapter = new NewsAdapter(getApplicationContext(), R.layout.item_news, list);

        listView.setAdapter(adapter);
    }

    private class ContentsDownlodTask extends AsyncTask<String, Integer, ArrayList<News>> {

        private ProgressDialog dialog;
        private Context mContext;

        ContentsDownlodTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute(){
            dialog = new ProgressDialog(mContext);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("로딩중 입니다...");
            dialog.show();

            super.onPreExecute();
        }

        protected ArrayList<News> doInBackground(String... str){

            ArrayList<News> data = new ArrayList<News>();

            try {
                Source source = new Source(new URL("https://cms1.ks.ac.kr/mcc/Board.do?mCode=MN0008&page=1"));
                source.fullSequentialParse();

                Log.d("asdfasdfasdf", source.toString());
                Element li = source.getAllElementsByClass("news_list").get(0);
                Log.d("hahah", li.toString());
                List<Element> div = li.getAllElements(HTMLElementName.DIV);

                for(int i = 0; i< div.size(); i++){
                    Element e = (Element) div.get(i);
                    Element aTag = e.getAllElements(HTMLElementName.A).get(0);
                    String href = aTag.getAttributeValue("href");
                    String title = aTag.getAttributeValue("title");

                    Element img = aTag.getAllElements(HTMLElementName.IMG).get(0);
                    String img_src = img.getAttributeValue("src");
                    img_src = "http://cms1.ks.ac.kr"+img_src;
                    img_src = img_src.replaceAll(".middle.jpg", "");

                    String sUrl="";
                    String eUrl="";
                    sUrl = img_src.substring(0, img_src.lastIndexOf("/")+1);
                    eUrl = img_src.substring(img_src.lastIndexOf("/")+1, img_src.length()); // 한글과 공백을 포함한 부분
                    eUrl = URLEncoder.encode(eUrl,"UTF-8").replace("+", "%20");
                    img_src = sUrl+ eUrl;

                    Element aTagContent = e.getAllElements(HTMLElementName.A).get(2);
                    String context = aTagContent.getContent().toString();

                    Element tagDate = e.getAllElementsByClass("date").get(0);
                    String date = tagDate.getContent().toString();

                    data.add(new News(title, href, context, img_src, date));

                    //Log.d("img_src", img_src);
                    //Log.d("href", href);
                    //Log.d("title", title);
                    //Log.d("context", context);
                    //Log.d("date", date);

                }

            } catch(Exception e){

            }

            return data;
        }

        protected void onProgressUpdate(Integer... progress ){
            // do something
        }

        protected void onPostExecute(ArrayList<News> result ){
            dialog.setCancelable(true);
            if(dialog.isShowing() == true) {
                dialog.dismiss();
            }
            setupContent(result);
        }
    }
}
