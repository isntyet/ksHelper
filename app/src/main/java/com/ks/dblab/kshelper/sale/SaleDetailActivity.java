package com.ks.dblab.kshelper.sale;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;
import com.ks.dblab.kshelper.app.MyApplication;
import com.ks.dblab.kshelper.etc.CommentAdapter;
import com.ks.dblab.kshelper.etc.CommentItem;
import com.ks.dblab.kshelper.etc.SimpleDividerItemDecoration;
import com.ks.dblab.kshelper.request.ArticleDeleteRequest;
import com.ks.dblab.kshelper.request.CommentCntRequest;
import com.ks.dblab.kshelper.request.CommentRequest;
import com.ks.dblab.kshelper.request.CommentWriteRequest;

import java.util.List;

/**
 * Created by Administrator on 2016-07-06.
 */
public class SaleDetailActivity extends BaseActivity {
    private String no;
    private String title;
    private String content;
    private String user;
    private String pwd;
    private String image;
    private String reg_date;

    public ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvUser;
    private TextView tvDate;
    private NetworkImageView ivImage;
    private RecyclerView rvSaleCommentList;
    private List<CommentItem> saleCommentList;
    private Dialog dialog = null;

    private Button btnCommentWrite;
    private Button btnDelete;


    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_sale_detail);
        getSupportActionBar().setTitle("중고 장터");


        no = getIntent().getExtras().getString("no");
        title = getIntent().getExtras().getString("title");
        content = getIntent().getExtras().getString("content");
        user = getIntent().getExtras().getString("user");
        pwd = getIntent().getExtras().getString("pwd");
        image = getIntent().getExtras().getString("image");
        reg_date = getIntent().getExtras().getString("reg_date");
        reg_date = reg_date.substring(0, 10);

        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        //tvContent.setVerticalScrollBarEnabled(true);
        //tvContent.setMovementMethod(new ScrollingMovementMethod());

        tvUser = (TextView) view.findViewById(R.id.tv_user);
        tvDate = (TextView) view.findViewById(R.id.tv_date);
        ivImage = (NetworkImageView) view.findViewById(R.id.iv_image);

        btnCommentWrite = (Button) view.findViewById(R.id.btn_comment_write);
        btnCommentWrite.setOnClickListener(this);

        btnDelete = (Button) view.findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);

        rvSaleCommentList = (RecyclerView) view.findViewById(R.id.rv_sale_comment_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvSaleCommentList.setHasFixedSize(true);
        rvSaleCommentList.setLayoutManager(layoutManager);

        saleCommentList = commentListRequest();

        //Log.d("size : " , saleCommentList.size() + "");
        resizeCommentList(saleCommentList.size());

        rvSaleCommentList.setAdapter(new CommentAdapter(getApplicationContext(),saleCommentList, R.layout.activity_sale_detail));
        rvSaleCommentList.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));

        initViewData();
        /*Log.d("no", no);
        Log.d("title", title);
        Log.d("content", content);
        Log.d("user", user);
        Log.d("pwd", pwd);
        Log.d("image", image);
        Log.d("reg_date", reg_date);*/

    }

    //리사이클 뷰의 길이가 고정되어야 스크롤뷰와 연동 가능
    private void resizeCommentList(int item_size){
        ViewGroup.LayoutParams params = rvSaleCommentList.getLayoutParams();
        params.height = 250 * item_size;
        rvSaleCommentList.setLayoutParams(params);
    }

    //댓글 조회 리퀘스트
    private List<CommentItem> commentListRequest(){
        CommentRequest request = new CommentRequest(this);
        request.setParams(no);
        return request.startRequest("sale");
    }

    //댓글 업로드 리퀘스트
    private String commentWriteRequest(String content){
        CommentWriteRequest request = new CommentWriteRequest(this);
        request.setParams(no, content, preference.getKakaoNickName(), preference.getKakaoId(), preference.getKakaoImage());
        String responData = request.startRequest("sale");
        //Log.d("commentWriteRequest", responData);
        return responData;
    }

    //분실물 게시판 댓글 카운트 +1 리퀘스트
    private String commentCntRequest(){
        CommentCntRequest request = new CommentCntRequest(this);
        request.setParams(no);
        String responData = request.startRequest("sale");
        //Log.d("commentCntRequest", responData);
        return responData;
    }

    //분실물 게시판 게시글 삭제 리퀘스트
    private String deleteRequest(){
        ArticleDeleteRequest request = new ArticleDeleteRequest(this);
        request.setParams(no);
        String responData = request.startRequest("sale");
        //Log.d("deleteRequest", responData);
        return responData;
    }

    private void loadDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_dialog_sale_comment);
        dialog.setTitle("댓글 작성");

        final EditText etContent = (EditText) dialog.findViewById(R.id.et_content);

        dialog.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etContent.length() == 0){
                    Toast.makeText(SaleDetailActivity.this, "내용을 제대로 작성해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    //commentWriteRequest(etContent.getText().toString());
                    if("success".equals(commentWriteRequest(etContent.getText().toString()))){
                        commentCntRequest();
                        Toast.makeText(SaleDetailActivity.this, "저장 완료", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SaleDetailActivity.this, "저장 실패", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                    onResume();

                }
            }
        });
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onResume();
            }
        });
        dialog.show();
    }

    private void loadDeleteDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_dialog_sale_pwd);
        dialog.setTitle("게시글 삭제");

        final EditText etPwd = (EditText) dialog.findViewById(R.id.et_pwd);

        dialog.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etPwd.length() != 6){
                    Toast.makeText(SaleDetailActivity.this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else if(!pwd.equals(etPwd.getText().toString())){
                    Toast.makeText(SaleDetailActivity.this, "비밀번호가 틀렸습니다!", Toast.LENGTH_SHORT).show();
                } else{
                    String result = deleteRequest();
                    if("success".equals(result)){
                        Toast.makeText(SaleDetailActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SaleDetailActivity.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                    finish();
                }
            }
        });
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        saleCommentList = null;
        rvSaleCommentList.removeAllViewsInLayout();

        saleCommentList = commentListRequest();
        resizeCommentList(saleCommentList.size());
        rvSaleCommentList.setAdapter(new CommentAdapter(getApplicationContext(),saleCommentList, R.layout.activity_sale_detail));
        rvSaleCommentList.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        super.onResume();
    }

    private void initViewData(){

        if (imageLoader == null) {
            imageLoader = MyApplication.getInstance().getImageLoader();
        }

        tvTitle.setText(title);
        tvContent.setText(content);
        tvUser.setText(user);
        tvDate.setText(reg_date);
        if((!"".equals(image)) && (!"default".equals(image)) && (image != null)){
            ivImage.setImageUrl(image, imageLoader );
        } else {
            ivImage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void destroyActivity() {

    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        } else if(view.getId() == btnCommentWrite.getId()){
            loadDialog();
        } else if(view.getId() == btnDelete.getId()){
            loadDeleteDialog();
        }
    }
}
