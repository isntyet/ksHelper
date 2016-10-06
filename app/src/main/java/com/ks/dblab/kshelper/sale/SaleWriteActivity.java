package com.ks.dblab.kshelper.sale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;
import com.ks.dblab.kshelper.request.SaleImageRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016-07-06.
 */
public class SaleWriteActivity extends BaseActivity {
    private int PICK_IMAGE_REQUEST = 1;

    private EditText etTitle;
    private EditText etContent;
    private EditText etPwd;
    private Button btnImg;
    private Button btnImgCancel;
    private Button btnCancel;
    private Button btnSuccess;
    private ImageView ivImg;
    private Bitmap bitmap = null;

    private Uri filePath;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_sale_write);
        getSupportActionBar().setTitle("중고장터 글쓰기");

        etTitle = (EditText) view.findViewById(R.id.et_title);
        etContent = (EditText) view.findViewById(R.id.et_content);
        etPwd = (EditText) view.findViewById(R.id.et_pwd);

        btnImg = (Button) view.findViewById(R.id.btn_img);
        btnImg.setOnClickListener(this);

        btnImgCancel = (Button) view.findViewById(R.id.btn_img_cancel);
        btnImgCancel.setOnClickListener(this);

        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);

        btnSuccess = (Button) view.findViewById(R.id.btn_success);
        btnSuccess.setOnClickListener(this);

        ivImg = (ImageView) view.findViewById(R.id.iv_img);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = null;
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();

                bitmap = resizeBitmapImageFn(bitmap, 1024);

                ivImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void imageRequest(){
        SaleImageRequest request = new SaleImageRequest(this);
        request.setParams(etTitle.getText().toString(), etContent.getText().toString(), preference.getKakaoNickName(), preference.getKakaoId(), etPwd.getText().toString() ,getStringImage(bitmap));
        String responData = request.startRequest();

        Log.d("responData", responData);
    }

    public String getStringImage(Bitmap bmp){
        if(bmp == null){
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }

    public Bitmap resizeBitmapImageFn(Bitmap bmpSource, int maxResolution){
        int iWidth = bmpSource.getWidth();      //비트맵이미지의 넓이
        int iHeight = bmpSource.getHeight();     //비트맵이미지의 높이
        int newWidth = iWidth ;
        int newHeight = iHeight ;
        float rate = 0.0f;

        //이미지의 가로 세로 비율에 맞게 조절
        if(iWidth > iHeight ){
            if(maxResolution < iWidth ){
                rate = maxResolution / (float) iWidth ;
                newHeight = (int) (iHeight * rate);
                newWidth = maxResolution;
            }
        }else{
            if(maxResolution < iHeight ){
                rate = maxResolution / (float) iHeight ;
                newWidth = (int) (iWidth * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(bmpSource, newWidth, newHeight, true);
    }

    @Override
    protected void destroyActivity() {

    }

    private boolean validationCheck(){

        if(etTitle.length() == 0){
            Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if(etContent.length() == 0){
            Toast.makeText(this, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if(etPwd.length() != 6){
            Toast.makeText(this, "비밀번호를 입력해 주세요. (6자리)", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        } else if(view.getId() == btnImg.getId()){
            showFileChooser();
        } else if(view.getId() == btnImgCancel.getId()){
            bitmap = null;
            ivImg.setImageBitmap(null);
        } else if(view.getId() == btnCancel.getId()){
            this.finish();
        } else if(view.getId() == btnSuccess.getId()){
            boolean check = validationCheck();

            if(check == true){
                imageRequest();
                Toast.makeText(this, "판매 등록 완료!", Toast.LENGTH_LONG).show();
                this.finish();
            }
        }
    }
}
