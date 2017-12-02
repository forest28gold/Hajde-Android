package com.azizinetwork.hajde.posts;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.model.backend.Post;
import com.azizinetwork.hajde.model.parse.PostData;
import com.azizinetwork.hajde.settings.Global;
import com.azizinetwork.hajde.settings.GlobalSharedData;
import com.azizinetwork.hajde.settings.PostDataAdapter;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.qbw.customview.RefreshLoadMoreLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint("ValidFragment")
public class NewestFragment extends Fragment implements RefreshLoadMoreLayout.CallBack {

    private final static String TAG = "NewestFragment";
    protected ListView mListview;
    protected PostDataAdapter mAdapter;
    RelativeLayout relativeLayout_no_result;
    ImageButton btn_refresh;

    private SVProgressHUD mSVProgressHUD;
    protected RefreshLoadMoreLayout mRefreshloadmore;

    private int dataCount = 0;
    private boolean tempIsOn = false;
    private int count = 0;

    public static NewestFragment getInstance(String title) {
        NewestFragment sf = new NewestFragment();
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSVProgressHUD = new SVProgressHUD(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view_newest = inflater.inflate(R.layout.fragment_newest, container, false);

        mRefreshloadmore = (RefreshLoadMoreLayout)view_newest.findViewById(R.id.refreshloadmore);
        mRefreshloadmore.init(new RefreshLoadMoreLayout.Config(this));

        mListview = (ListView)view_newest.findViewById(R.id.listview);
        mListview.setAdapter(mAdapter = new PostDataAdapter(getActivity()));

        relativeLayout_no_result = (RelativeLayout)view_newest.findViewById(R.id.relativeLayout_no_result);
        relativeLayout_no_result.setVisibility(View.INVISIBLE);
        btn_refresh = (ImageButton)view_newest.findViewById(R.id.imageButton_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);
                initLoadNewestPostData();
            }
        });

        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);
        initLoadNewestPostData();

        return view_newest;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            Log.v(TAG, "============== Hidden ==================");
        } else {
            Log.v(TAG, "============== show ==================");
            if (tempIsOn) {
                onRefresh();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (tempIsOn) {
            onRefresh();
        }
    }

    public void initLoadNewestPostData() {

        dataCount += Global.LOAD_DATA_COUNT;

        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add( "time DESC" );
        queryOptions.setSortBy( sortBy );
        queryOptions.setPageSize(dataCount);
        queryOptions.setOffset(0);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( Post.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Post>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Post> postBackendlessCollection )
                    {
                        mSVProgressHUD.dismiss();

                        if (postBackendlessCollection.getData().size() > 0) {

                            relativeLayout_no_result.setVisibility(View.INVISIBLE);

                            count = 0;

                            for (int i=0; i<postBackendlessCollection.getData().size(); i++){
                                Post post = postBackendlessCollection.getData().get(i);

                                PostData record = new PostData();
                                record.setObjectId(post.getObjectId());
                                record.setContent(post.getContent());
                                record.setBackColor(post.getBackColor());
                                record.setType(post.getType());
                                record.setFilePath(post.getFilePath());
                                record.setUserID(post.getUserID());
                                record.setLatitude(post.getLatitude());
                                record.setLongitude(post.getLongitude());
                                record.setTime(post.getTime());
                                record.setPeriod(post.getPeriod());
                                record.setImgHeight(post.getImgHeight());
                                record.setCommentCount(post.getCommentCount());
                                record.setLikeCount(post.getLikeCount());
                                record.setCommentType(post.getCommentType());
                                record.setLikeType(post.getLikeType());
                                record.setReportCount(post.getReportCount());
                                record.setReportType(post.getReportType());

                                ArrayList<String> commentTypeArray;
                                ArrayList<String> likeTypeArray;
                                ArrayList<String> reportTypeArray;

                                if (record.getCommentType() != null && !record.getCommentType().equals("")) {

                                    if (record.getCommentType().contains(";")) {
                                        String[] itemsComment = record.getCommentType().split(";");
                                        commentTypeArray = new ArrayList<String>(Arrays.asList(itemsComment));
                                        record.setCommentTypeArray(commentTypeArray);
                                    } else {
                                        commentTypeArray = new ArrayList<String>();
                                        commentTypeArray.add(record.getCommentType());
                                        record.setCommentTypeArray(commentTypeArray);
                                    }
                                }

                                if (record.getLikeType() != null && !record.getLikeType().equals("")) {

                                    if (record.getLikeType().contains(";")) {
                                        String[] itemsLike = record.getLikeType().split(";");
                                        likeTypeArray = new ArrayList<String>(Arrays.asList(itemsLike));
                                        record.setLikeTypeArray(likeTypeArray);
                                    } else {
                                        likeTypeArray = new ArrayList<String>();
                                        likeTypeArray.add(record.getLikeType());
                                        record.setLikeTypeArray(likeTypeArray);
                                    }
                                }

                                if (record.getReportType() != null && !record.getReportType().equals("")) {

                                    if (record.getReportType().contains(";")) {
                                        String[] itemsReport = record.getReportType().split(";");
                                        reportTypeArray = new ArrayList<String>(Arrays.asList(itemsReport));
                                        record.setReportTypeArray(reportTypeArray);
                                    } else {
                                        reportTypeArray = new ArrayList<String>();
                                        reportTypeArray.add(record.getReportType());
                                        record.setReportTypeArray(reportTypeArray);
                                    }
                                }

//                                int distance = GlobalSharedData.getDistance(String.valueOf(Global.g_userInfo.getLatitude()),
//                                        String.valueOf(Global.g_userInfo.getLongitude()), record.getLatitude(), record.getLongitude());
//
//                                if (!record.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {
//                                    if (distance <= Global.NEARBY_RADIUS) {
//                                        mAdapter.addItem(record);
//                                        count++;
//                                    }
//                                }
                                if (!record.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {
                                    mAdapter.addItem(record);
                                    count++;
                                }
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
        tempIsOn = true;
        if (count == 0) {
            relativeLayout_no_result.setVisibility(View.VISIBLE);
        } else {
            relativeLayout_no_result.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        Log.v(TAG, " ------------------------- onRefresh ----------------------------" + dataCount);
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
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( Post.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Post>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Post> postBackendlessCollection )
                    {
                        if (postBackendlessCollection.getData().size() > 0) {

                            mAdapter.removeAll();
                            count = 0;
                            for (int i=0; i<postBackendlessCollection.getData().size(); i++){
                                Post post = postBackendlessCollection.getData().get(i);

                                PostData record = new PostData();
                                record.setObjectId(post.getObjectId());
                                record.setContent(post.getContent());
                                record.setBackColor(post.getBackColor());
                                record.setType(post.getType());
                                record.setFilePath(post.getFilePath());
                                record.setUserID(post.getUserID());
                                record.setLatitude(post.getLatitude());
                                record.setLongitude(post.getLongitude());
                                record.setTime(post.getTime());
                                record.setPeriod(post.getPeriod());
                                record.setImgHeight(post.getImgHeight());
                                record.setCommentCount(post.getCommentCount());
                                record.setLikeCount(post.getLikeCount());
                                record.setCommentType(post.getCommentType());
                                record.setLikeType(post.getLikeType());
                                record.setReportCount(post.getReportCount());
                                record.setReportType(post.getReportType());

                                ArrayList<String> commentTypeArray;
                                ArrayList<String> likeTypeArray;
                                ArrayList<String> reportTypeArray;

                                if (record.getCommentType() != null && !record.getCommentType().equals("")) {

                                    if (record.getCommentType().contains(";")) {
                                        String[] itemsComment = record.getCommentType().split(";");
                                        commentTypeArray = new ArrayList<String>(Arrays.asList(itemsComment));
                                        record.setCommentTypeArray(commentTypeArray);
                                    } else {
                                        commentTypeArray = new ArrayList<String>();
                                        commentTypeArray.add(record.getCommentType());
                                        record.setCommentTypeArray(commentTypeArray);
                                    }
                                }

                                if (record.getLikeType() != null && !record.getLikeType().equals("")) {

                                    if (record.getLikeType().contains(";")) {
                                        String[] itemsLike = record.getLikeType().split(";");
                                        likeTypeArray = new ArrayList<String>(Arrays.asList(itemsLike));
                                        record.setLikeTypeArray(likeTypeArray);
                                    } else {
                                        likeTypeArray = new ArrayList<String>();
                                        likeTypeArray.add(record.getLikeType());
                                        record.setLikeTypeArray(likeTypeArray);
                                    }
                                }

                                if (record.getReportType() != null && !record.getReportType().equals("")) {

                                    if (record.getReportType().contains(";")) {
                                        String[] itemsReport = record.getReportType().split(";");
                                        reportTypeArray = new ArrayList<String>(Arrays.asList(itemsReport));
                                        record.setReportTypeArray(reportTypeArray);
                                    } else {
                                        reportTypeArray = new ArrayList<String>();
                                        reportTypeArray.add(record.getReportType());
                                        record.setReportTypeArray(reportTypeArray);
                                    }
                                }

//                                int distance = GlobalSharedData.getDistance(String.valueOf(Global.g_userInfo.getLatitude()),
//                                        String.valueOf(Global.g_userInfo.getLongitude()), record.getLatitude(), record.getLongitude());
//
//                                if (!record.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {
//                                    if (distance <= Global.NEARBY_RADIUS) {
//                                        mAdapter.addItem(record);
//                                        count++;
//                                    }
//                                }

                                if (!record.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {
                                    mAdapter.addItem(record);
                                    count++;
                                }
                            }

                            onShowResult();
                            mRefreshloadmore.stopRefresh();
                            Log.v(TAG, "============== onRefresh finish ==================");

                        } else {
                            mRefreshloadmore.stopRefresh();
                            Log.v(TAG, "============== onRefresh empty ==================");
                            onShowResult();
                        }
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        mRefreshloadmore.stopRefresh();
                        Log.v(TAG, "============== onRefresh failed ==================");
                        onShowResult();
                    }
                });
    }

    @Override
    public void onLoadMore() {
        Log.v(TAG, "---------------------- onLoadMore ---------------------------------" + dataCount);

        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add( "time DESC" );
        queryOptions.setSortBy( sortBy );
        queryOptions.setPageSize(Global.LOAD_DATA_COUNT);
        queryOptions.setOffset(dataCount);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( Post.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Post>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Post> postBackendlessCollection )
                    {
                        if (postBackendlessCollection.getData().size() > 0) {

                            for (int i=0; i<postBackendlessCollection.getData().size(); i++){
                                Post post = postBackendlessCollection.getData().get(i);

                                PostData record = new PostData();
                                record.setObjectId(post.getObjectId());
                                record.setContent(post.getContent());
                                record.setBackColor(post.getBackColor());
                                record.setType(post.getType());
                                record.setFilePath(post.getFilePath());
                                record.setUserID(post.getUserID());
                                record.setLatitude(post.getLatitude());
                                record.setLongitude(post.getLongitude());
                                record.setTime(post.getTime());
                                record.setPeriod(post.getPeriod());
                                record.setImgHeight(post.getImgHeight());
                                record.setCommentCount(post.getCommentCount());
                                record.setLikeCount(post.getLikeCount());
                                record.setCommentType(post.getCommentType());
                                record.setLikeType(post.getLikeType());
                                record.setReportCount(post.getReportCount());
                                record.setReportType(post.getReportType());

                                ArrayList<String> commentTypeArray;
                                ArrayList<String> likeTypeArray;
                                ArrayList<String> reportTypeArray;

                                if (record.getCommentType() != null && !record.getCommentType().equals("")) {

                                    if (record.getCommentType().contains(";")) {
                                        String[] itemsComment = record.getCommentType().split(";");
                                        commentTypeArray = new ArrayList<String>(Arrays.asList(itemsComment));
                                        record.setCommentTypeArray(commentTypeArray);
                                    } else {
                                        commentTypeArray = new ArrayList<String>();
                                        commentTypeArray.add(record.getCommentType());
                                        record.setCommentTypeArray(commentTypeArray);
                                    }
                                }

                                if (record.getLikeType() != null && !record.getLikeType().equals("")) {

                                    if (record.getLikeType().contains(";")) {
                                        String[] itemsLike = record.getLikeType().split(";");
                                        likeTypeArray = new ArrayList<String>(Arrays.asList(itemsLike));
                                        record.setLikeTypeArray(likeTypeArray);
                                    } else {
                                        likeTypeArray = new ArrayList<String>();
                                        likeTypeArray.add(record.getLikeType());
                                        record.setLikeTypeArray(likeTypeArray);
                                    }
                                }

                                if (record.getReportType() != null && !record.getReportType().equals("")) {

                                    if (record.getReportType().contains(";")) {
                                        String[] itemsReport = record.getReportType().split(";");
                                        reportTypeArray = new ArrayList<String>(Arrays.asList(itemsReport));
                                        record.setReportTypeArray(reportTypeArray);
                                    } else {
                                        reportTypeArray = new ArrayList<String>();
                                        reportTypeArray.add(record.getReportType());
                                        record.setReportTypeArray(reportTypeArray);
                                    }
                                }

//                                int distance = GlobalSharedData.getDistance(String.valueOf(Global.g_userInfo.getLatitude()),
//                                        String.valueOf(Global.g_userInfo.getLongitude()), record.getLatitude(), record.getLongitude());
//
//                                if (!record.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {
//                                    if (distance <= Global.NEARBY_RADIUS) {
//                                        mAdapter.addItem(record);
//                                    }
//                                }

                                if (!record.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {
                                    mAdapter.addItem(record);
                                    Log.v(TAG, " ------------------------- onLoadMore ----------------------------" + i);
                                }
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

    }
}
