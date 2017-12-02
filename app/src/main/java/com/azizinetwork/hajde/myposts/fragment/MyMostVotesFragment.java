package com.azizinetwork.hajde.myposts.fragment;

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


public class MyMostVotesFragment extends Fragment implements RefreshLoadMoreLayout.CallBack {

    private final static String TAG = "MyMostVotesFragment";
    protected ListView mListview;
    RelativeLayout relativeLayout_no_result;
    ImageButton btn_refresh;

    private SVProgressHUD mSVProgressHUD;
    protected RefreshLoadMoreLayout mRefreshloadmore;

    private int dataCount = 0;
    private int tempCount = 0;
    private boolean tempIsOn = false;
    private int count = 0;
    protected PostDataAdapter mAdapter;

    public static MyMostVotesFragment getInstance(String title) {
        MyMostVotesFragment sf = new MyMostVotesFragment();
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSVProgressHUD = new SVProgressHUD(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_most_votes, container, false);

        mRefreshloadmore = (RefreshLoadMoreLayout)view.findViewById(R.id.refreshloadmore);
        mRefreshloadmore.init(new RefreshLoadMoreLayout.Config(this));

        mListview = (ListView)view.findViewById(R.id.listview);
        mListview.setAdapter(mAdapter = new PostDataAdapter(getActivity()));

        relativeLayout_no_result = (RelativeLayout)view.findViewById(R.id.relativeLayout_no_result);
        relativeLayout_no_result.setVisibility(View.INVISIBLE);
        btn_refresh = (ImageButton)view.findViewById(R.id.imageButton_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);
                initLoadMyMostVotesData();
            }
        });

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            Log.v(TAG, "============== Hidden ==================");
        } else {
            Log.v(TAG, "============== show ==================");
            if (!tempIsOn){
                initLoadMyMostVotesData();
            } else {
                onRefresh();
            }
        }
    }

    public void initLoadMyMostVotesData() {

        dataCount += Global.LOAD_DATA_COUNT;

        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add( "likeCount DESC" );
        queryOptions.setSortBy( sortBy );
        queryOptions.setPageSize(dataCount);
        queryOptions.setOffset(0);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_USER_ID, Global.g_userInfo.getUserID());
//        String whereClause = Global.KEY_USER_ID + " = '" + Global.g_userInfo.getUserID() + "'";
        dataQuery.setWhereClause( whereClause );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( Post.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Post>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Post> postBackendlessCollection )
                    {
                        mSVProgressHUD.dismiss();

                        if (postBackendlessCollection.getData().size() > 0) {

                            relativeLayout_no_result.setVisibility(View.INVISIBLE);
                            tempCount = 0;

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

                                if (!record.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {
                                    tempCount++;
                                    mAdapter.addItem(record);
                                }

                            }
                            tempIsOn = true;
                            if (Global.g_userInfo.getPostCount() < tempCount) {
                                Global.g_userInfo.setPostCount(tempCount);
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
        tempIsOn = true;
        if (tempCount == 0) {
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
        sortBy.add( "likeCount DESC" );
        queryOptions.setSortBy( sortBy );
        queryOptions.setPageSize(dataCount);
        queryOptions.setOffset(0);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_USER_ID, Global.g_userInfo.getUserID());
//        String whereClause = Global.KEY_USER_ID + " = '" + Global.g_userInfo.getUserID() + "'";
        dataQuery.setWhereClause( whereClause );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( Post.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Post>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Post> postBackendlessCollection )
                    {
                        if (postBackendlessCollection.getData().size() > 0) {

                            mAdapter.removeAll();
                            tempCount = 0;

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

                                if (!record.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {
                                    tempCount++;
                                    mAdapter.addItem(record);
                                }

                            }

                            if (Global.g_userInfo.getPostCount() < tempCount) {
                                Global.g_userInfo.setPostCount(tempCount);
                                GlobalSharedData.updateUserDBData();
                            }

                            onShowResult();

                            mRefreshloadmore.stopRefresh();
                            Log.v(TAG, "============== onRefresh finish ==================");

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
        Log.v(TAG, "---------------------- onLoadMore ---------------------------------" + dataCount);

        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add( "likeCount DESC" );
        queryOptions.setSortBy( sortBy );
        queryOptions.setPageSize(Global.LOAD_DATA_COUNT);
        queryOptions.setOffset(dataCount);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_USER_ID, Global.g_userInfo.getUserID());
//        String whereClause = Global.KEY_USER_ID + " = '" + Global.g_userInfo.getUserID() + "'";
        dataQuery.setWhereClause( whereClause );
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

                                if (!record.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {
                                    tempCount++;
                                    mAdapter.addItem(record);
                                }

                            }

                            if (Global.g_userInfo.getPostCount() < tempCount) {
                                Global.g_userInfo.setPostCount(tempCount);
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

    }

}
