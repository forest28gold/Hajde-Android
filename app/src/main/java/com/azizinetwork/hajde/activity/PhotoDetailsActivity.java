package com.azizinetwork.hajde.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.azizinetwork.hajde.R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.wang.avi.AVLoadingIndicatorView;

public class PhotoDetailsActivity extends AppCompatActivity {

    AVLoadingIndicatorView avi_indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_details);

        Intent intent = getIntent();
        String photo_url = intent.getStringExtra("photoUrl");

        avi_indicator = (AVLoadingIndicatorView)findViewById(R.id.indicator);
        ImageView img_photo = (ImageView)findViewById(R.id.imageView_photo);
        UrlImageViewHelper.setUrlDrawable(img_photo, photo_url, R.mipmap.post_image, new UrlImageViewCallback() {
            @Override
            public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                avi_indicator.setVisibility(View.INVISIBLE);
            }
        });

        img_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
