package com.example.finalproject.ui.weather;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.finalproject.R;

import static android.app.Activity.RESULT_OK;
import static com.example.finalproject.ui.weather.EncodingUtils.createQRCode;

public class RecommendFragment extends Fragment {

    private ImageView enCodeImage;
    private ImageView img_bing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_recommend, container, false);
        enCodeImage= root.findViewById(R.id.enCode);
        qrCode(root);

        img_bing=root.findViewById(R.id.img_bing);

        ((Button) root.findViewById(R.id.bt_bing)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            }
        });


        return root;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            img_bing.setImageBitmap(photo);
        }
    }

    public void qrCode (View view){

        //获取logo资源,
        //R.drawable.logo为logo图片
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.logo_hznu);
        //生成二维码
        Bitmap codeBitmap = EncodingUtils.createQRCode("https://www.hznu.edu.cn",500,500,logoBitmap);
        enCodeImage.setImageBitmap(codeBitmap);//显示二维码
    }

}