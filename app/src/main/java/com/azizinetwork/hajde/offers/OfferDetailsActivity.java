package com.azizinetwork.hajde.offers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.activity.PhotoDetailsActivity;
import com.azizinetwork.hajde.settings.Global;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.wang.avi.AVLoadingIndicatorView;

public class OfferDetailsActivity extends AppCompatActivity {

    Button btn_back;
    ImageView img_offer;
    AVLoadingIndicatorView avi_indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);

        btn_back = (Button)findViewById(R.id.button_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView txt_title = (TextView)findViewById(R.id.textView_title);
        TextView txt_offer_title = (TextView)findViewById(R.id.textView_offer_title);
        TextView txt_spec_title = (TextView)findViewById(R.id.textView_spec_title);
        TextView txt_spec = (TextView)findViewById(R.id.textView_spec);
        TextView txt_off = (TextView)findViewById(R.id.textView_off);
        TextView txt_price = (TextView)findViewById(R.id.textView_price);
        TextView txt_before = (TextView)findViewById(R.id.textView_before);
        TextView txt_content_title = (TextView)findViewById(R.id.textView_content_title);
        TextView txt_content = (TextView)findViewById(R.id.textView_content);

        img_offer = (ImageView) findViewById(R.id.imageView_offer);
        avi_indicator = (AVLoadingIndicatorView)findViewById(R.id.indicator);

        txt_title.setText(Global.g_offerData.getName());
        txt_content_title.setText(Global.g_offerData.getName());
        txt_content.setText(Global.g_offerData.getIndex());
        String percent = "%";
        txt_off.setText(String.format("%s%s %s", Global.g_offerData.getOffPercent(), percent, getString(R.string.off)));

        if (Global.g_offerData.getPrice().contains(".")) {
            txt_price.setText(String.format("%s%s", Global.g_currency, Global.g_offerData.getPrice()));
        } else {
            txt_price.setText(String.format("%s%s.-", Global.g_currency, Global.g_offerData.getPrice()));
        }

        if (Global.g_offerData.getBeforePrice().contains(".")) {
            txt_before.setText(String.format("%s %s%s", getString(R.string.before), Global.g_currency, Global.g_offerData.getBeforePrice()));
        } else {
            txt_before.setText(String.format("%s %s%s.-", getString(R.string.before), Global.g_currency, Global.g_offerData.getBeforePrice()));
        }

        txt_offer_title.setText(Global.g_offerData.getName());
        txt_spec_title.setText(Global.g_offerData.getSpecTitle());
        txt_spec.setText(Global.g_offerData.getSpecDescription());

        UrlImageViewHelper.setUrlDrawable(img_offer, Global.g_offerData.getMarkFilePath(), R.mipmap.offer_image, new UrlImageViewCallback() {
            @Override
            public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                avi_indicator.setVisibility(View.INVISIBLE);
            }
        });

        img_offer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent intent = new Intent(OfferDetailsActivity.this, PhotoDetailsActivity.class);
                intent.putExtra("photoUrl", Global.g_offerData.getMarkFilePath());
                startActivity(intent);
            }
        });

    }
}
