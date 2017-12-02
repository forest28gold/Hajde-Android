package com.azizinetwork.hajde.offers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.activity.PhotoDetailsActivity;
import com.azizinetwork.hajde.model.backend.Offer;
import com.azizinetwork.hajde.settings.Global;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.qbw.customview.RefreshLoadMoreLayout;
import com.wang.avi.AVLoadingIndicatorView;

import de.hdodenhof.circleimageview.CircleImageView;


public class OffersActivity extends AppCompatActivity implements RefreshLoadMoreLayout.CallBack {

    private final static String TAG = "OffersActivity";
    TextView txt_title, txt_shop;
    CircleImageView img_shop;
    AVLoadingIndicatorView avi_indicator;
    RelativeLayout relativeLayout_no_result;
    ImageButton btn_refresh;

    private SVProgressHUD mSVProgressHUD;
    protected RefreshLoadMoreLayout mRefreshloadmore;

    int dataCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        Button btn_back = (Button)findViewById(R.id.button_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txt_title = (TextView)findViewById(R.id.textView_title);
        txt_shop = (TextView)findViewById(R.id.textView_shop);
        img_shop = (CircleImageView)findViewById(R.id.imageView_shop);
        avi_indicator = (AVLoadingIndicatorView)findViewById(R.id.indicator);

        txt_title.setText(Global.g_shopData.getShopTitle());
        txt_shop.setText(Global.g_shopData.getShopDescription());
        UrlImageViewHelper.setUrlDrawable(img_shop, Global.g_shopData.getMarkFilePath(), R.mipmap.shop_empty, new UrlImageViewCallback() {
            @Override
            public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                avi_indicator.setVisibility(View.INVISIBLE);
            }
        });

        relativeLayout_no_result = (RelativeLayout)findViewById(R.id.relativeLayout_no_result);
        relativeLayout_no_result.setVisibility(View.INVISIBLE);
        btn_refresh = (ImageButton)findViewById(R.id.imageButton_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initLoadOfferData();
            }
        });

        mSVProgressHUD = new SVProgressHUD(this);
        mRefreshloadmore = (RefreshLoadMoreLayout)findViewById(R.id.refreshloadmore);
        mRefreshloadmore.init(new RefreshLoadMoreLayout.Config(this));
//        mRefreshloadmore.startAutoRefresh();

        initLoadOfferData();
    }

    public void initLoadOfferData() {

        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

        dataCount += Global.LOAD_DATA_COUNT;

        QueryOptions queryOptions = new QueryOptions();
        queryOptions.setPageSize(dataCount);
        queryOptions.setOffset(0);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_SHOP_ID, Global.g_shopData.getObjectId());
        dataQuery.setWhereClause( whereClause );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( Offer.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Offer>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Offer> offerBackendlessCollection )
                    {
                        mSVProgressHUD.dismiss();

                        if (offerBackendlessCollection.getData().size() > 0) {

                            for (int i=0; i<offerBackendlessCollection.getData().size(); i++){
                                Offer offerData = offerBackendlessCollection.getData().get(i);
                                onShowOfferData(i, offerData);
                            }

                        } else {
                            onShowNoResult();
                        }
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        mSVProgressHUD.dismiss();
                        onShowNoResult();
                    }
                });
    }

    public void onShowNoResult() {
        relativeLayout_no_result.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        Log.v(TAG, "------------- onRefresh start --------------");
//        mRefreshloadmore.setCanLoadMore(true);

        if (dataCount == 0) {
            dataCount = Global.LOAD_DATA_COUNT;
        }

        QueryOptions queryOptions = new QueryOptions();
        queryOptions.setPageSize(dataCount);
        queryOptions.setOffset(0);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_SHOP_ID, Global.g_shopData.getObjectId());
        dataQuery.setWhereClause( whereClause );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( Offer.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Offer>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Offer> offerBackendlessCollection )
                    {
                        if (offerBackendlessCollection.getData().size() > 0) {

                            LinearLayout list = (LinearLayout) findViewById(R.id.linearLayout_offer);
                            list.removeAllViews();

                            for (int i=0; i<offerBackendlessCollection.getData().size(); i++){
                                Offer offerData = offerBackendlessCollection.getData().get(i);
                                onShowOfferData(i, offerData);
                            }

                            mRefreshloadmore.stopRefresh();
                            Log.v(TAG, "============== onRefresh finish ==================");

                        } else {
                            mRefreshloadmore.stopRefresh();
                            Log.v(TAG, "============== onRefresh finish ==================");
                        }
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        mRefreshloadmore.stopRefresh();
                        Log.v(TAG, "============== onRefresh finish ==================");
                    }
                });

    }

    @Override
    public void onLoadMore() {

        Log.v(TAG, "------------------------ onLoadMore start ---------------------");

        QueryOptions queryOptions = new QueryOptions();
        queryOptions.setPageSize(Global.LOAD_DATA_COUNT);
        queryOptions.setOffset(dataCount);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_SHOP_ID, Global.g_shopData.getObjectId());
        dataQuery.setWhereClause( whereClause );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( Offer.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Offer>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Offer> offerBackendlessCollection )
                    {
                        if (offerBackendlessCollection.getData().size() > 0) {

                            for (int i=0; i<offerBackendlessCollection.getData().size(); i++){
                                Offer offerData = offerBackendlessCollection.getData().get(i);
                                onShowOfferData(i, offerData);
                            }

                            mRefreshloadmore.stopLoadMore();
                            Log.v(TAG, "============== onLoadMore finish ==================");

                        } else {
                            mRefreshloadmore.stopLoadMore();
//                            mRefreshloadmore.setCanLoadMore(false);
                            Log.v(TAG, "============== onLoadMore finish ==================");
                        }
                        dataCount += Global.LOAD_DATA_COUNT;
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        mRefreshloadmore.stopLoadMore();
                        Log.v(TAG, "============== onLoadMore finish ==================");
                    }
                });

    }

    public void onShowOfferData(int number, final Offer offerData) {

        LinearLayout list = (LinearLayout) findViewById(R.id.linearLayout_offer);

        final LinearLayout newCell = (LinearLayout) (View.inflate(this, R.layout.row_list_offer, null));

        final ImageView img_offer = (ImageView)newCell.findViewById(R.id.imageView_offer);
        final AVLoadingIndicatorView avi_indicator = (AVLoadingIndicatorView) newCell.findViewById(R.id.indicator);
        TextView txt_name = (TextView)newCell.findViewById(R.id.textView_content_title);
        TextView txt_index = (TextView)newCell.findViewById(R.id.textView_content);
        TextView txt_off = (TextView)newCell.findViewById(R.id.textView_off);
        TextView txt_price = (TextView)newCell.findViewById(R.id.textView_price);
        TextView txt_before = (TextView)newCell.findViewById(R.id.textView_before);

        UrlImageViewHelper.setUrlDrawable(img_offer, offerData.getMarkFilePath(), R.mipmap.offer_image, new UrlImageViewCallback() {
            @Override
            public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                avi_indicator.setVisibility(View.INVISIBLE);
            }
        });

        txt_name.setText(offerData.getName());
        txt_index.setText(offerData.getIndex());
        String percent = "%";
        txt_off.setText(String.format("%s%s %s", offerData.getOffPercent(), percent, getString(R.string.off)));

        if (offerData.getPrice().contains(".")) {
            txt_price.setText(String.format("%s%s", Global.g_currency, offerData.getPrice()));
        } else {
            txt_price.setText(String.format("%s%s.-", Global.g_currency, offerData.getPrice()));
        }

        if (offerData.getBeforePrice().contains(".")) {
            txt_before.setText(String.format("%s %s%s", getString(R.string.before), Global.g_currency, offerData.getBeforePrice()));
        } else {
            txt_before.setText(String.format("%s %s%s.-", getString(R.string.before), Global.g_currency, offerData.getBeforePrice()));
        }

        img_offer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Global.g_offerData = new Offer();
                Global.g_offerData = offerData;
                Intent i = new Intent(OffersActivity.this, OfferDetailsActivity.class);
                startActivity(i);
            }
        });

        img_offer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(OffersActivity.this, PhotoDetailsActivity.class);
                intent.putExtra("photoUrl", offerData.getMarkFilePath());
                startActivity(intent);
                return true;
            }
        });

        newCell.setTag(number);
        registerForContextMenu(newCell);
        list.addView(newCell);

        newCell.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Global.g_offerData = new Offer();
                Global.g_offerData = offerData;
                Intent i = new Intent(OffersActivity.this, OfferDetailsActivity.class);
                startActivity(i);
            }
        });

    }
}
