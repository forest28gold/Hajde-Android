package com.azizinetwork.hajde.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.model.backend.Shop;
import com.azizinetwork.hajde.offers.OffersActivity;
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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class KarmaShopFragment extends Fragment implements RefreshLoadMoreLayout.CallBack {

    private final static String TAG = "KarmaShopFragment";
    private SVProgressHUD mSVProgressHUD;
    protected GridView mGridview;
    RelativeLayout relativeLayout_no_result;
    ImageButton btn_refresh;
    protected RefreshLoadMoreLayout mRefreshloadmore;
    private Adapter mAdapter;
    protected Handler mHandler = new Handler();
    private boolean tempIsOn = false;
    private int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_karma_shop, container, false);

        mSVProgressHUD = new SVProgressHUD(getActivity());

        relativeLayout_no_result = (RelativeLayout)layout.findViewById(R.id.relativeLayout_no_result);
        relativeLayout_no_result.setVisibility(View.INVISIBLE);
        btn_refresh = (ImageButton)layout.findViewById(R.id.imageButton_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);
                initLoadShopData();
            }
        });
        mGridview = (GridView) layout.findViewById(R.id.gridview);
        mGridview.setAdapter(mAdapter = new Adapter(getActivity()));
        mRefreshloadmore = (RefreshLoadMoreLayout) layout.findViewById(R.id.refreshloadmore);
        mRefreshloadmore.init(new RefreshLoadMoreLayout.Config(this));
//        mRefreshloadmore.startAutoRefresh();
        mRefreshloadmore.setCanLoadMore(false);
        mRefreshloadmore.setCanRefresh(false);

        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

        initLoadShopData();

        return layout;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            Log.v(TAG, "============== Hidden ==================");
        } else {
            Log.v(TAG, "============== show ==================");
            if (tempIsOn) {
                initLoadShopData();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (tempIsOn) {
            initLoadShopData();
        }
    }

    public String getOfferCurrency(String country) {

        if (country.equals(Global.COUNTRY_ALBANIA)) {
            return Global.CURRENCY_ALBANIA;
        } else if (country.equals(Global.COUNTRY_BOSNIA_HEREZE)) {
            return Global.CURRENCY_BOSNIA_HEREZE;
        } else if (country.equals(Global.COUNTRY_KOSOVO)) {
            return Global.CURRENCY_KOSOVO;
        } else if (country.equals(Global.COUNTRY_MACEDONIA)) {
            return Global.CURRENCY_MACEDONIA;
        } else if (country.equals(Global.COUNTRY_MONTENEGRO)) {
            return Global.CURRENCY_MONTENEGRO;
        } else if (country.equals(Global.COUNTRY_SERBIA)) {
            return Global.CURRENCY_SERBIA;
        } else if (country.equals(Global.COUNTRY_SWISS)) {
            return Global.CURRENCY_SWISS;
        } else if (country.equals(Global.COUNTRY_TURKEY)) {
            return Global.CURRENCY_TURKEY;
        } else {
            return Global.CURRENCY_OTHERS;
        }
    }

    public void initLoadShopData() {

        if (Global.g_userInfo.getCountry() == null) {
            Global.g_userInfo.setCountry(Global.COUNTRY_OTHERS);
        }

        Global.g_currency = getOfferCurrency(Global.g_userInfo.getCountry());

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add( "created ASC" );
        queryOptions.setSortBy( sortBy );
        String whereClause = String.format("%s = '%s'", "country", Global.g_userInfo.getCountry());
        dataQuery.setWhereClause( whereClause );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( Shop.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Shop>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Shop> shopBackendlessCollection )
                    {
                        mSVProgressHUD.dismiss();

                        if (shopBackendlessCollection.getData().size() > 0) {

                            count = 0;

                            for (int i=0; i<shopBackendlessCollection.getData().size(); i++){
                                Shop shopData = shopBackendlessCollection.getData().get(i);
                                mAdapter.addItem(i, shopData);
                                count++;
                            }

                        } else {
                            onShowResult();
                        }
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        mSVProgressHUD.dismiss();
                        onShowResult();
                    }
                });

    }

    public void onShowResult() {
        tempIsOn = true;
        if (count == 0) {
            relativeLayout_no_result.setVisibility(View.VISIBLE);
        } else {
            relativeLayout_no_result.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshloadmore.stopRefresh();
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshloadmore.stopLoadMore();
            }
        }, 1000);
    }

    public class Adapter extends BaseAdapter {
        private Context mContext;
        private List<Shop> mShopList = new ArrayList<>();

        public Adapter(Context context) {
            mContext = context;
        }

        public void addItem(Shop item) {
            mShopList.add(item);
            notifyDataSetChanged();
        }

        public void addItem(int pos, Shop item) {
            mShopList.add(pos, item);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mShopList.size();
        }

        @Override
        public Shop getItem(int position) {
            return mShopList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (null == convertView) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_grid_shop, parent, false);
                convertView.setTag(viewHolder = new ViewHolder(convertView));
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.img_shop.setImageResource(R.mipmap.shop_empty);
            viewHolder.avi_indicator.setVisibility(View.VISIBLE);

            final Shop record = mShopList.get(position);
            UrlImageViewHelper.setUrlDrawable(viewHolder.img_shop, record.getMarkFilePath(), R.mipmap.shop_empty, new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                    ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                    scale.setDuration(300);
                    scale.setInterpolator(new OvershootInterpolator());
                    imageView.startAnimation(scale);
                    viewHolder.avi_indicator.setVisibility(View.INVISIBLE);
                }
            });

            viewHolder.img_shop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Global.g_shopData = new Shop();
                    Global.g_shopData = record;
                    Intent i = new Intent(getActivity(), OffersActivity.class);
                    startActivity(i);
                }
            });


            return convertView;
        }

        class ViewHolder {
            protected CircleImageView img_shop;
            protected AVLoadingIndicatorView avi_indicator;

            ViewHolder(View rootView) {
                initView(rootView);
            }

            private void initView(View rootView) {
                img_shop = (CircleImageView) rootView.findViewById(R.id.imageView_shop);
                avi_indicator = (AVLoadingIndicatorView) rootView.findViewById(R.id.indicator);
            }
        }
    }
}
