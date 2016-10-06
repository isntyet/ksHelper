package com.ks.dblab.kshelper.mcc;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;
import com.ks.dblab.kshelper.app.MyApplication;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by jojo on 2016-06-29.
 */
public class MccTimeTableActivity extends BaseActivity {

    private PhotoViewAttacher mAttacher;
    private NetworkImageView nivPhoto;
    public ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
    private ImageDownlodTask task;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_mcc_timetable);
        getSupportActionBar().setTitle("방송 편성표");

        nivPhoto = (NetworkImageView) view.findViewById(R.id.iv_photo);

        task = new ImageDownlodTask(this);
        task.execute();
    }

    private class ImageDownlodTask extends AsyncTask<String, Integer, String> {

        private ProgressDialog dialog;
        private Context mContext;

        ImageDownlodTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(mContext);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("로딩중 입니다...");
            dialog.show();

            super.onPreExecute();
        }

        protected String doInBackground(String... str) {
            String img_url = null;

            try {
                Source source = new Source(new URL("https://cms1.ks.ac.kr/mcc/Contents.do?mCode=MN0012"));
                source.fullSequentialParse();

                Element div = source.getAllElementsByClass("contents_view_wrap").get(0);

                Element contentDiv = div.getAllElements(HTMLElementName.DIV).get(0);
                Element img = contentDiv.getAllElements(HTMLElementName.IMG).get(0);

                img_url = img.getAttributeValue("src");

                img_url = "http://cms1.ks.ac.kr"+img_url;

                String sUrl="";
                String eUrl="";
                sUrl = img_url.substring(0, img_url.lastIndexOf("/")+1);
                eUrl = img_url.substring(img_url.lastIndexOf("/")+1, img_url.length()); // 한글과 공백을 포함한 부분
                eUrl = URLEncoder.encode(eUrl,"UTF-8").replace("+", "%20");
                img_url = sUrl+ eUrl;

            } catch (MalformedURLException e) {
                //Log.d("MalformedURLException", e.toString());
                return null;
            } catch (IOException e) {
                //Log.d("IOException", e.toString());
                return null;
            }

            return img_url;
        }

        protected void onProgressUpdate(Integer... progress) {
            // do something
        }

        protected void onPostExecute(String result) {
            nivPhoto.setImageUrl(result, imageLoader);

            mAttacher = new PhotoViewAttacher(nivPhoto);

            dialog.setCancelable(true);
            if (dialog.isShowing() == true) {
                dialog.dismiss();
            }
        }
    }

    @Override
    protected void destroyActivity() {

    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        }
    }
}
