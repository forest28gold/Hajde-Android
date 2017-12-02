package com.azizinetwork.hajde.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.model.backend.Karma;
import com.azizinetwork.hajde.settings.Global;
import com.azizinetwork.hajde.settings.GlobalSharedData;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.qbw.customview.RefreshLoadMoreLayout;

import java.util.ArrayList;
import java.util.List;

public class MyKarmaActivity extends AppCompatActivity implements RefreshLoadMoreLayout.CallBack {

    private final static String TAG = "MyKarmaActivity";
    private SVProgressHUD mSVProgressHUD;
    protected RefreshLoadMoreLayout mRefreshloadmore;
//    private Handler handler = new Handler();

    protected ListView mListview;
    protected Adapter mAdapter;

    Button btn_karma;
    RelativeLayout relativeLayout_no_result;
    ImageButton btn_refresh;
    int tempKarmaScore = 0;
    int dataCount = 0;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_karma);

        Button btn_back = (Button)findViewById(R.id.button_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_karma = (Button)findViewById(R.id.button_karma_score);
        btn_karma.setText(String.valueOf(Global.g_userInfo.getKarmaScore()));

        mListview = (ListView)findViewById(R.id.listview);
        mListview.setAdapter(mAdapter = new Adapter(this));

        relativeLayout_no_result = (RelativeLayout)findViewById(R.id.relativeLayout_no_result);
        relativeLayout_no_result.setVisibility(View.INVISIBLE);

        btn_refresh = (ImageButton)findViewById(R.id.imageButton_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initLoadKarmaScoreData();
            }
        });

        mSVProgressHUD = new SVProgressHUD(this);
        mRefreshloadmore = (RefreshLoadMoreLayout)findViewById(R.id.refreshloadmore);
        mRefreshloadmore.init(new RefreshLoadMoreLayout.Config(this));
//        mRefreshloadmore.startAutoRefresh();

        initLoadKarmaScoreData();
    }

    public void initLoadKarmaScoreData() {

        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);
        tempKarmaScore = 0;

        dataCount += Global.LOAD_DATA_COUNT;

        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add( "time DESC" );
        queryOptions.setSortBy( sortBy );
        queryOptions.setPageSize(dataCount);
        queryOptions.setOffset(0);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_USER_ID, Global.g_userInfo.getUserID());
        dataQuery.setWhereClause( whereClause );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( Karma.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Karma>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Karma> karmaBackendlessCollection )
                    {
                        mSVProgressHUD.dismiss();

                        if (karmaBackendlessCollection.getData().size() > 0) {

                            count = 0;

                            for (int i=0; i<karmaBackendlessCollection.getData().size(); i++){
                                Karma karmaData = karmaBackendlessCollection.getData().get(i);
//                                onShowKarmaData(i, karmaData);
                                mAdapter.addItem(karmaData);
                                tempKarmaScore += karmaData.getScore();
                                count++;
                            }

                            if (Global.g_userInfo.getKarmaScore() < tempKarmaScore) {
                                Global.g_userInfo.setKarmaScore(tempKarmaScore);
                                btn_karma.setText(String.valueOf(Global.g_userInfo.getKarmaScore()));
                                GlobalSharedData.updateUserDBData();
                            }

                            onShowResult();

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
        if (count == 0) {
            relativeLayout_no_result.setVisibility(View.VISIBLE);
        } else {
            relativeLayout_no_result.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        Log.v(TAG, "------------- onRefresh start --------------");
//        mRefreshloadmore.setCanLoadMore(true);

        if (dataCount == 0) {
            dataCount = Global.LOAD_DATA_COUNT;
        }

        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add( "time DESC" );
        queryOptions.setSortBy( sortBy );
        queryOptions.setPageSize(dataCount);
        queryOptions.setOffset(0);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_USER_ID, Global.g_userInfo.getUserID());
        dataQuery.setWhereClause( whereClause );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( Karma.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Karma>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Karma> karmaBackendlessCollection )
                    {
                        if (karmaBackendlessCollection.getData().size() > 0) {

                            mAdapter.removeAll();
                            tempKarmaScore = 0;
                            count = 0;

                            for (int i=0; i<karmaBackendlessCollection.getData().size(); i++){
                                Karma karmaData = karmaBackendlessCollection.getData().get(i);
//                                onShowKarmaData(i, karmaData);
                                mAdapter.addItem(karmaData);
                                tempKarmaScore += karmaData.getScore();
                                count++;
                            }

                            if (Global.g_userInfo.getKarmaScore() < tempKarmaScore) {
                                Global.g_userInfo.setKarmaScore(tempKarmaScore);
                                btn_karma.setText(String.valueOf(Global.g_userInfo.getKarmaScore()));
                                GlobalSharedData.updateUserDBData();
                            }

                            mRefreshloadmore.stopRefresh();
                            Log.v(TAG, "============== onRefresh finish ==================");

                            onShowResult();

                        } else {
                            mRefreshloadmore.stopRefresh();
                            onShowResult();
                            Log.v(TAG, "============== onRefresh empty ==================");
                        }
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        mRefreshloadmore.stopRefresh();
                        onShowResult();
                        Log.v(TAG, "============== onRefresh failed ==================");
                    }
                });
    }

    @Override
    public void onLoadMore() {
        Log.v(TAG, "------------------------ onLoadMore start ---------------------");

        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add( "time DESC" );
        queryOptions.setSortBy( sortBy );
        queryOptions.setPageSize(Global.LOAD_DATA_COUNT);
        queryOptions.setOffset(dataCount);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_USER_ID, Global.g_userInfo.getUserID());
        dataQuery.setWhereClause( whereClause );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( Karma.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Karma>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Karma> karmaBackendlessCollection )
                    {
                        if (karmaBackendlessCollection.getData().size() > 0) {

                            for (int i=0; i<karmaBackendlessCollection.getData().size(); i++){
                                Karma karmaData = karmaBackendlessCollection.getData().get(i);
//                                onShowKarmaData(dataCount + i, karmaData);
                                mAdapter.addItem(karmaData);
                                tempKarmaScore += karmaData.getScore();
                            }

                            if (Global.g_userInfo.getKarmaScore() < tempKarmaScore) {
                                Global.g_userInfo.setKarmaScore(tempKarmaScore);
                                btn_karma.setText(String.valueOf(Global.g_userInfo.getKarmaScore()));
                                GlobalSharedData.updateUserDBData();
                            }

                            mRefreshloadmore.stopLoadMore();
                            Log.v(TAG, "============== onLoadMore finish ==================");

                        } else {
                            mRefreshloadmore.stopLoadMore();
//                            mRefreshloadmore.setCanLoadMore(false);
                            Log.v(TAG, "============== onLoadMore empty ==================");
                        }
                        dataCount += Global.LOAD_DATA_COUNT;
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        mRefreshloadmore.stopLoadMore();
                        Log.v(TAG, "============== onLoadMore failed ==================");
                    }
                });

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mRefreshloadmore.stopLoadMore();
////                mRefreshloadmore.stopLoadMore(false);
//                Log.v(TAG, "======================== onLoadMore finish =============================");
//            }
//        }, 2000);
    }

    public class Adapter extends BaseAdapter {
        private Context mContext;
        private List<Karma> mKarmaList = new ArrayList<>();

        public Adapter(Context context) {
            mContext = context;
        }

        public void addItem(Karma item) {
            mKarmaList.add(item);
            notifyDataSetChanged();
        }

        public void addItem(int pos, Karma item) {
            mKarmaList.add(pos, item);
            notifyDataSetChanged();
        }

        public void removeAll() {
            mKarmaList = new ArrayList<>();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mKarmaList.size();
        }

        @Override
        public Karma getItem(int position) {
            return mKarmaList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_list_karma, parent, false);
                convertView.setTag(viewHolder = new ViewHolder(convertView));
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Karma karmaData = mKarmaList.get(position);

            if (karmaData.getType().equals(Global.KARMA_POST)) {
                viewHolder.txt_title.setText(getString(R.string.karma_increased));
                viewHolder.txt_description.setText(getString(R.string.karma_from_posting));
                viewHolder.txt_score.setText(String.format("+%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_COMMENT)) {
                viewHolder.txt_title.setText(getString(R.string.karma_increased));
                viewHolder.txt_description.setText(getString(R.string.karma_from_commenting));
                viewHolder.txt_score.setText(String.format("+%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_VOTE_LIKE)) {
                viewHolder.txt_title.setText(getString(R.string.karma_increased));
                viewHolder.txt_description.setText(getString(R.string.karma_from_upvote_post));
                viewHolder.txt_score.setText(String.format("+%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_VOTE_DISLIKE)) {
                viewHolder.txt_title.setText(getString(R.string.karma_increased));
                viewHolder.txt_description.setText(getString(R.string.karma_from_downvote_post));
                viewHolder.txt_score.setText(String.format("+%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_COMMENT_LIKE)) {
                viewHolder.txt_title.setText(getString(R.string.karma_increased));
                viewHolder.txt_description.setText(getString(R.string.karma_from_upvote_comment));
                viewHolder.txt_score.setText(String.format("+%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_COMMENT_DISLIKE)) {
                viewHolder.txt_title.setText(getString(R.string.karma_increased));
                viewHolder.txt_description.setText(getString(R.string.karma_from_downvote_comment));
                viewHolder.txt_score.setText(String.format("+%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_ABUSE)) {
                viewHolder.txt_title.setText(getString(R.string.karma_increased));
                viewHolder.txt_description.setText(getString(R.string.karma_from_report_post));
                viewHolder.txt_score.setText(String.format("+%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_COMMENT_ABUSE)) {
                viewHolder.txt_title.setText(getString(R.string.karma_increased));
                viewHolder.txt_description.setText(getString(R.string.karma_from_report_comment));
                viewHolder.txt_score.setText(String.format("+%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_DECREASE_COMMENT)) {
                viewHolder.txt_title.setText(getString(R.string.karma_decreased));
                viewHolder.txt_description.setText(getString(R.string.karma_someone_downvote_comment));
                viewHolder.txt_score.setText(String.format("%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_DECREASE_POST)) {
                viewHolder.txt_title.setText(getString(R.string.karma_decreased));
                viewHolder.txt_description.setText(getString(R.string.karma_someone_downvote_post));
                viewHolder.txt_score.setText(String.format("%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_DECREASE_DELETE_COMMENT)) {
                viewHolder.txt_title.setText(getString(R.string.karma_decreased));
                viewHolder.txt_description.setText(getString(R.string.karma_comment_5_downvotes));
                viewHolder.txt_score.setText(String.format("%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_DECREASE_DELETE_POST)) {
                viewHolder.txt_title.setText(getString(R.string.karma_decreased));
                viewHolder.txt_description.setText(getString(R.string.karma_post_5_downvotes));
                viewHolder.txt_score.setText(String.format("%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_LOGIN)) {
                viewHolder.txt_title.setText(getString(R.string.karma_daily_add));
                viewHolder.txt_description.setText(getString(R.string.karma_daily_login));
                viewHolder.txt_score.setText(String.format("+%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_REPORT_DELETE_COMMENT)) {
                viewHolder.txt_title.setText(getString(R.string.karma_decreased));
                viewHolder.txt_description.setText(getString(R.string.karma_delete_comment));
                viewHolder.txt_score.setText(String.format("%d", karmaData.getScore()));
            } else if (karmaData.getType().equals(Global.KARMA_REPORT_DELETE_POST)) {
                viewHolder.txt_title.setText(getString(R.string.karma_decreased));
                viewHolder.txt_description.setText(getString(R.string.karma_delete_post));
                viewHolder.txt_score.setText(String.format("%d", karmaData.getScore()));
            }

            viewHolder.relativeLayout_back.setBackgroundColor(Color.parseColor(String.format("#%s", karmaData.getBackColor())));
            viewHolder.txt_timeStamp.setText(String.format("%s %s", GlobalSharedData.getFormattedTimeStamp(karmaData.getTime()), getString(R.string.ago)));

            return convertView;
        }

        class ViewHolder {
            protected RelativeLayout relativeLayout_back;
            protected TextView txt_title, txt_description, txt_score, txt_timeStamp;

            ViewHolder(View rootView) {
                initView(rootView);
            }

            private void initView(View rootView) {

                relativeLayout_back = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_karma);
                txt_title = (TextView)rootView.findViewById(R.id.textView_karma_title);
                txt_description = (TextView)rootView.findViewById(R.id.textView_description);
                txt_score = (TextView)rootView.findViewById(R.id.textView_score);
                txt_timeStamp = (TextView)rootView.findViewById(R.id.textView_timestamp);
            }
        }

    }
}
