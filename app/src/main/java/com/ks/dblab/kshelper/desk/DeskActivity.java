package com.ks.dblab.kshelper.desk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;
import com.ks.dblab.kshelper.activity.WebviewActivity;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016-07-11.
 */
public class DeskActivity extends BaseActivity {

    private Timer timer1;
    private CircleProgress circle_1;
    private dataDownlodTask task;

    private TextView tvAll_1;
    private TextView tvUse_1;
    private TextView tvEmpty_1;

    private int all_1 = 0;
    private int use_1 = 0;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_desk);
        getSupportActionBar().setTitle("열람실 정보");

        circle_1 = (CircleProgress) view.findViewById(R.id.circle_1);
        tvAll_1 = (TextView) view.findViewById(R.id.tv_all_1);
        tvUse_1 = (TextView) view.findViewById(R.id.tv_use_1);
        tvEmpty_1 = (TextView) view.findViewById(R.id.tv_empty_1);

        task = new dataDownlodTask(this);
        task.execute();

    }

    private class dataDownlodTask extends AsyncTask<String, Integer, String> {

        private ProgressDialog dialog;
        private Context mContext;

        dataDownlodTask(Context context) {
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


            try {
                Source source = new Source(new URL("http://library.ks.ac.kr/main/readingroom/main.jsp"));
                source.fullSequentialParse();

                Element table = source.getAllElements(HTMLElementName.TABLE).get(0);
                Element tbody = table.getAllElements(HTMLElementName.TBODY).get(0);

                Element tr1 = tbody.getAllElements(HTMLElementName.TR).get(0);

                Element td_all_1 = tr1.getAllElements(HTMLElementName.TD).get(1);
                Element td_use_1 = tr1.getAllElements(HTMLElementName.TD).get(2);

                all_1 =  Integer.parseInt(td_all_1.getContent().toString());
                use_1 = Integer.parseInt(td_use_1.getContent().toString());


            } catch (MalformedURLException e) {
                //Log.d("MalformedURLException", e.toString());
            } catch (IOException e) {
                //Log.d("IOException", e.toString());
            }

            return "";

        }

        protected void onProgressUpdate(Integer... progress) {
            // do something
        }

        protected void onPostExecute(String result) {

            int persent_1 = (use_1 * 100) / all_1;

            int empty_1 = all_1 - use_1;


            dialog.setCancelable(true);
            if (dialog.isShowing() == true) {
                dialog.dismiss();
            }

            startProgress_1(persent_1);

            tvAll_1.setText(String.valueOf(all_1));
            tvUse_1.setText(String.valueOf(use_1));
            tvEmpty_1.setText(String.valueOf(empty_1));
        }
    }

    private void startProgress_1(final int value){
        timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(circle_1.getProgress() != value) {
                            circle_1.setProgress(circle_1.getProgress() + 1);
                        } else {
                            timer1.cancel();
                        }
                    }
                });
            }
        }, 500, 25);
    }

    @Override
    protected void destroyActivity() {

    }

    private void startWebviewActivity(String url, String name){
        Intent it = new Intent(DeskActivity.this, WebviewActivity.class);
        it.putExtra("url", url);
        it.putExtra("name", name);
        startActivity(it);
    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        }/* else if(view.getId() == btnDetail_1.getId()){
            startWebviewActivity("http://library.ks.ac.kr/main/readingroom/readingroom1.jsp", "제1열람실 실시간 정보");
        }*/
    }
}
