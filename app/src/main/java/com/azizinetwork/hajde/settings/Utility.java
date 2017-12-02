package com.azizinetwork.hajde.settings;

import android.content.Context;
import android.util.Log;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.activity.PostDetailsActivity;
import com.azizinetwork.hajde.model.backend.ActivityPost;
import com.azizinetwork.hajde.model.backend.Karma;
import com.azizinetwork.hajde.model.backend.Post;
import com.azizinetwork.hajde.model.parse.ActivityPostData;
import com.azizinetwork.hajde.model.parse.PostData;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.PublishOptions;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.services.messaging.MessageStatus;

import java.io.Serializable;
import java.util.ArrayList;

public class Utility implements Serializable {
    private static final long serialVersionUID = 1L;

    public static String setCommentBackColor(String postColor) {
        if (postColor.equals(Global.COLOR1)) {
            return Global.COMMENT_COLOR1;
        } else if (postColor.equals(Global.COLOR2)) {
            return Global.COMMENT_COLOR2;
        } else if (postColor.equals(Global.COLOR3)) {
            return Global.COMMENT_COLOR3;
        } else if (postColor.equals(Global.COLOR4)) {
            return Global.COMMENT_COLOR4;
        } else if (postColor.equals(Global.COLOR5)) {
            return Global.COMMENT_COLOR5;
        } else if (postColor.equals(Global.COLOR6)) {
            return Global.COMMENT_COLOR6;
        } else if (postColor.equals(Global.COLOR7)) {
            return Global.COMMENT_COLOR7;
        } else if (postColor.equals(Global.COLOR8)) {
            return Global.COMMENT_COLOR8;
        } else if (postColor.equals(Global.COLOR10)) {
            return Global.COMMENT_COLOR10;
        } else if (postColor.equals(Global.COLOR11)) {
            return Global.COMMENT_COLOR11;
        } else {
            return Global.COMMENT_COLOR1;
        }
    }

    public static void karmaInBackground(String karmaType) {

        Karma karma = new Karma();
        karma.setUserID(Global.g_userInfo.getUserID());
        karma.setTime(GlobalSharedData.getCurrentDate());
        karma.setType(karmaType);

        if (karmaType.equals(Global.KARMA_POST)) {
            karma.setBackColor(Global.COLOR1);
            karma.setScore(Global.KARMA_SCORE_POST);
        } else if (karmaType.equals(Global.KARMA_COMMENT)) {
            karma.setBackColor(Global.COLOR9);
            karma.setScore(Global.KARMA_SCORE_COMMENT);
        } else if (karmaType.equals(Global.KARMA_LOGIN)) {
            karma.setBackColor(Global.COLOR3);
            karma.setScore(Global.KARMA_SCORE_LOGIN);
        } else if (karmaType.equals(Global.KARMA_VOTE_LIKE) || karmaType.equals(Global.KARMA_COMMENT_LIKE)) {
            karma.setBackColor(Global.COLOR10);
            karma.setScore(Global.KARMA_SCORE_VOTE_LIKE);
        } else if (karmaType.equals(Global.KARMA_VOTE_DISLIKE) || karmaType.equals(Global.KARMA_COMMENT_DISLIKE)) {
            karma.setBackColor(Global.COLOR5);
            karma.setScore(Global.KARMA_SCORE_VOTE_DISLIKE);
        } else if (karmaType.equals(Global.KARMA_ABUSE) || karmaType.equals(Global.KARMA_COMMENT_ABUSE)) {
            karma.setBackColor(Global.COLOR7);
            karma.setScore(Global.KARMA_SCORE_ABUSE);
        }

        //------------------------------

        Backendless.Persistence.save(karma, new AsyncCallback<Karma>() {
            @Override
            public void handleResponse(Karma response) {
                Log.d("Karma", "Saved karma data");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e("Karma", String.format("Failed save in background of karma data, = %s", fault.toString()));
            }
        });

        //------------------------------
    }

    public static void karmaDecreaseInBackground(String karmaType) {

        Karma karma = new Karma();
        karma.setUserID(Global.g_userInfo.getUserID());
        karma.setTime(GlobalSharedData.getCurrentDate());
        karma.setType(karmaType);

        if (karmaType.equals(Global.KARMA_DECREASE_POST) || karmaType.equals(Global.KARMA_DECREASE_COMMENT)) {
            karma.setBackColor(Global.COLOR4);
            karma.setScore(Global.KARMA_SCORE_DECREASE_POST);
        } else if (karmaType.equals(Global.KARMA_DECREASE_DELETE_POST) || karmaType.equals(Global.KARMA_DECREASE_DELETE_COMMENT)) {
            karma.setBackColor(Global.COLOR4);
            karma.setScore(Global.KARMA_SCORE_DELETE_POST);
        } else if (karmaType.equals(Global.KARMA_REPORT_DELETE_POST) || karmaType.equals(Global.KARMA_REPORT_DELETE_COMMENT)) {
            karma.setBackColor(Global.COLOR7);
            karma.setScore(Global.KARMA_SCORE_DELETE_POST);
        }

        //------------------------------

        Backendless.Persistence.save(karma, new AsyncCallback<Karma>() {
            @Override
            public void handleResponse(Karma response) {
                Log.d("Karma", "Saved karma data");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e("Karma", String.format("Failed save in background of karma data, = %s", fault.toString()));
            }
        });

        //------------------------------
    }

    public static void likePostInBackground(final PostData postData, PostDataAdapter.ViewHolder viewHolder, Context mContext, PostDataAdapter adapter, int position)  {

        String item = String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE);
        ArrayList<String> likeTypeArray = new ArrayList<String>();
        likeTypeArray = postData.getLikeTypeArray();
        likeTypeArray.add(item);
        postData.setLikeTypeArray(likeTypeArray);
        int likeCount = postData.getLikeCount() + 1;
        postData.setLikeCount(likeCount);

        postData.setLikeType("");
        if (postData.getLikeTypeArray().size() > 1) {
            postData.setLikeType(postData.getLikeTypeArray().get(0));
            for (int i=1; i<postData.getLikeTypeArray().size(); i++) {
                postData.setLikeType(String.format("%s;%s", postData.getLikeType(), postData.getLikeTypeArray().get(i)));
            }
        } else if (postData.getLikeTypeArray().size() == 1) {
            postData.setLikeType(postData.getLikeTypeArray().get(0));
        }

        if (adapter != null) {
            viewHolder.btn_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.like));
            viewHolder.txt_like_count.setText(GlobalSharedData.getFormattedCount(postData.getLikeCount()));
            adapter.replaceItem(position, postData);
        }

        Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_VOTE_LIKE);
        Global.g_userInfo.setVoteCount(Global.g_userInfo.getVoteCount() + 1);
        karmaInBackground(Global.KARMA_VOTE_LIKE);

        //-----------------------------------------

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_OBJECT_ID, postData.getObjectId());
        dataQuery.setWhereClause( whereClause );

        Backendless.Persistence.of( Post.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Post>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Post> postBackendlessCollection )
                    {
                        if (postBackendlessCollection.getData().size() > 0) {

                            Post updatedData = postBackendlessCollection.getData().get(0);
                            updatedData.setLikeCount(postData.getLikeCount());
                            updatedData.setLikeType(postData.getLikeType());

                            Backendless.Persistence.save(updatedData, new AsyncCallback<Post>() {
                                @Override
                                public void handleResponse(Post response) {
                                    Log.d("LikePost", "****************** Post Like is succeed! *************************");
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Log.d("LikePost", "=========================== Post Like is failed! ===========================");
                                }
                            });

                        } else {
                            Log.d("LikePost", "=========================== Post Like is not found! ===========================");
                        }
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        Log.d("LikePost", "=========================== Post Like is failed! ===========================");
                    }
                });

        //------------------------------------------
    }

    public static void dislikePostInBackground(final PostData postData, PostDataAdapter.ViewHolder viewHolder, Context mContext, PostDataAdapter adapter, int position) {

        if (postData.getLikeCount() == -4 && !postData.getUserID().equals(Global.ADMIN_EMAIL)) {

            //------------------------------------

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            String whereClause = String.format("%s = '%s'", Global.KEY_OBJECT_ID, postData.getObjectId());
            dataQuery.setWhereClause( whereClause );

            Backendless.Persistence.of( Post.class ).find( dataQuery,
                    new AsyncCallback<BackendlessCollection<Post>>(){
                        @Override
                        public void handleResponse( BackendlessCollection<Post> postBackendlessCollection )
                        {
                            if (postBackendlessCollection.getData().size() > 0) {

                                Post updatedData = postBackendlessCollection.getData().get(0);

                                Backendless.Persistence.of(Post.class).remove(updatedData, new AsyncCallback<Long>() {
                                    @Override
                                    public void handleResponse(Long response) {
                                        Log.d("DislikePost", "=========================== Post Delete is succeed! ===========================");
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Log.d("DislikePost", "=========================== Post Delete is failed! ===========================");
                                    }
                                });

                            } else {
                                Log.d("DislikePost", "=========================== Post Delete is not found! ===========================");
                            }
                        }
                        @Override
                        public void handleFault( BackendlessFault fault )
                        {
                            Log.d("DislikePost", "=========================== Post Delete is failed! ===========================");
                        }
                    });

            //-------------------------------------------

            if (adapter != null) {
                adapter.removeItem(position);
            }

            if (postData.getUserID().equals(Global.g_userInfo.getUserID())) {
                int karmaScore = Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST;
                Global.g_userInfo.setKarmaScore(karmaScore);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_DECREASE_DELETE_POST);

            } else {
                int karmaScore = Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_VOTE_DISLIKE;
                Global.g_userInfo.setKarmaScore(karmaScore);
                GlobalSharedData.updateUserDBData();
                Utility.karmaInBackground(Global.KARMA_VOTE_DISLIKE);

                //------------------------------

                DeliveryOptions deliveryOptions = new DeliveryOptions();
                deliveryOptions.addPushSinglecast( postData.getUserID() );

                PublishOptions publishOptions = new PublishOptions();
                publishOptions.putHeader( "android-ticker-text", mContext.getString(R.string.ios_post_deleted) );
                publishOptions.putHeader( "android-content-title", mContext.getString(R.string.ios_post_deleted) );
                publishOptions.putHeader( "android-content-text", mContext.getString(R.string.karma_post_5_downvotes) );
                publishOptions.putHeader( "ios-alert", mContext.getString(R.string.karma_post_5_downvotes) );
                publishOptions.putHeader( "ios-badge", Global.PUSH_BADGE );
                publishOptions.putHeader( "ios-sound", Global.PUSH_SOUND );

                Backendless.Messaging.publish(Global.MESSAGE_CHANNEL, mContext.getString(R.string.karma_post_5_downvotes), publishOptions, deliveryOptions, new AsyncCallback<MessageStatus>() {
                    @Override
                    public void handleResponse(MessageStatus response) {
                        Log.v("DislikePost", String.format("showMessageStatus :  %s", response.getStatus().toString()));
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e("DislikePost", String.format("showMessageStatus :  %s", fault.toString()));
                    }
                });
                //---------------------------------
            }

        } else {

            String item = String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE);
            ArrayList<String> likeTypeArray = new ArrayList<String>();
            likeTypeArray = postData.getLikeTypeArray();
            likeTypeArray.add(item);
            postData.setLikeTypeArray(likeTypeArray);
            int likeCount = postData.getLikeCount() - 1;
            postData.setLikeCount(likeCount);

            postData.setLikeType("");
            if (postData.getLikeTypeArray().size() > 1) {
                postData.setLikeType(postData.getLikeTypeArray().get(0));
                for (int i=1; i<postData.getLikeTypeArray().size(); i++) {
                    postData.setLikeType(String.format("%s;%s", postData.getLikeType(), postData.getLikeTypeArray().get(i)));
                }
            } else if (postData.getLikeTypeArray().size() == 1) {
                postData.setLikeType(postData.getLikeTypeArray().get(0));
            }

            if (adapter != null) {
                viewHolder.btn_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.like));
                viewHolder.txt_like_count.setText(GlobalSharedData.getFormattedCount(postData.getLikeCount()));
                adapter.replaceItem(position, postData);
            }

            Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_VOTE_DISLIKE);
            Global.g_userInfo.setVoteCount(Global.g_userInfo.getVoteCount() + 1);
            Utility.karmaInBackground(Global.KARMA_VOTE_DISLIKE);

            //-----------------------------------------

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            String whereClause = String.format("%s = '%s'", Global.KEY_OBJECT_ID, postData.getObjectId());
            dataQuery.setWhereClause( whereClause );

            Backendless.Persistence.of( Post.class ).find( dataQuery,
                    new AsyncCallback<BackendlessCollection<Post>>(){
                        @Override
                        public void handleResponse( BackendlessCollection<Post> postBackendlessCollection )
                        {
                            if (postBackendlessCollection.getData().size() > 0) {

                                Post updatedData = postBackendlessCollection.getData().get(0);
                                updatedData.setLikeCount(postData.getLikeCount());
                                updatedData.setLikeType(postData.getLikeType());

                                Backendless.Persistence.save(updatedData, new AsyncCallback<Post>() {
                                    @Override
                                    public void handleResponse(Post response) {
                                        Log.d("DislikePost", "****************** Post Dislike is succeed! *************************");
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Log.d("DislikePost", "=========================== Post Dislike is failed! ===========================");
                                    }
                                });

                            } else {
                                Log.d("DislikePost", "=========================== Post Dislike is not found! ===========================");
                            }
                        }
                        @Override
                        public void handleFault( BackendlessFault fault )
                        {
                            Log.d("DislikePost", "=========================== Post Dislike is failed! ===========================");
                        }
                    });

            //------------------------------------------

            DeliveryOptions deliveryOptions = new DeliveryOptions();
            deliveryOptions.addPushSinglecast( postData.getUserID() );

            PublishOptions publishOptions = new PublishOptions();
            publishOptions.putHeader( "android-ticker-text", mContext.getString(R.string.karma_decreased) );
            publishOptions.putHeader( "android-content-title", mContext.getString(R.string.karma_decreased) );
            publishOptions.putHeader( "android-content-text", mContext.getString(R.string.karma_someone_downvote_post) );
            publishOptions.putHeader( "ios-alert", mContext.getString(R.string.karma_someone_downvote_post) );
            publishOptions.putHeader( "ios-badge", Global.PUSH_BADGE );
            publishOptions.putHeader( "ios-sound", Global.PUSH_SOUND );

            Backendless.Messaging.publish(Global.MESSAGE_CHANNEL, mContext.getString(R.string.karma_someone_downvote_post), publishOptions, deliveryOptions, new AsyncCallback<MessageStatus>() {
                @Override
                public void handleResponse(MessageStatus response) {
                    Log.v("DislikePost", String.format("showMessageStatus :  %s", response.getStatus().toString()));
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e("DislikePost", String.format("showMessageStatus :  %s", fault.toString()));
                }
            });

            //------------------------------------------
        }
    }

    public static void likeCommentInBackground(final ActivityPostData commentData, PostDetailsActivity.Adapter.ViewHolder viewHolder, Context mContext, PostDetailsActivity.Adapter adapter, int position) {

        String item = String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE);
        ArrayList<String> likeTypeArray = new ArrayList<String>();
        likeTypeArray = commentData.getLikeTypeArray();
        likeTypeArray.add(item);
        commentData.setLikeTypeArray(likeTypeArray);
        int likeCount = commentData.getLikeCount() + 1;
        commentData.setLikeCount(likeCount);

        commentData.setLikeType("");
        if (commentData.getLikeTypeArray().size() > 1) {
            commentData.setLikeType(commentData.getLikeTypeArray().get(0));
            for (int i=1; i<commentData.getLikeTypeArray().size(); i++) {
                commentData.setLikeType(String.format("%s;%s", commentData.getLikeType(), commentData.getLikeTypeArray().get(i)));
            }
        } else if (commentData.getLikeTypeArray().size() == 1) {
            commentData.setLikeType(commentData.getLikeTypeArray().get(0));
        }

        viewHolder.btn_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.like));
        viewHolder.txt_like_count.setText(GlobalSharedData.getFormattedCount(commentData.getLikeCount()));
        adapter.replaceItem(position, commentData);

        Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_VOTE_LIKE);
        Global.g_userInfo.setVoteCount(Global.g_userInfo.getVoteCount() + 1);
        Utility.karmaInBackground(Global.KARMA_COMMENT_LIKE);

        //-----------------------------------------

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_OBJECT_ID, commentData.getObjectId());
        dataQuery.setWhereClause( whereClause );

        Backendless.Persistence.of( ActivityPost.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<ActivityPost>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<ActivityPost> activityPostBackendlessCollection )
                    {
                        if (activityPostBackendlessCollection.getData().size() > 0) {

                            ActivityPost updatedData = activityPostBackendlessCollection.getData().get(0);
                            updatedData.setLikeCount(commentData.getLikeCount());
                            updatedData.setLikeType(commentData.getLikeType());

                            Backendless.Persistence.save(updatedData, new AsyncCallback<ActivityPost>() {
                                @Override
                                public void handleResponse(ActivityPost response) {
                                    Log.d("likeComment", "****************** Comment Like is succeed! *************************");
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Log.d("likeComment", "=========================== Comment Like is failed! ===========================");
                                }
                            });

                        } else {
                            Log.d("likeComment", "=========================== Comment Like is not found! ===========================");
                        }
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        Log.d("likeComment", "=========================== Comment Like is failed! ===========================");
                    }
                });

        //------------------------------------------
    }

    public static void dislikeCommentInBackground(final ActivityPostData commentData, PostDetailsActivity.Adapter.ViewHolder viewHolder, Context mContext, PostDetailsActivity.Adapter adapter, int position) {

        if (commentData.getLikeCount() == -4) {

            //------------------------------------

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            String whereClause = String.format("%s = '%s'", Global.KEY_OBJECT_ID, commentData.getObjectId());
            dataQuery.setWhereClause( whereClause );

            Backendless.Persistence.of( ActivityPost.class ).find( dataQuery,
                    new AsyncCallback<BackendlessCollection<ActivityPost>>(){
                        @Override
                        public void handleResponse( BackendlessCollection<ActivityPost> activityPostBackendlessCollection )
                        {
                            if (activityPostBackendlessCollection.getData().size() > 0) {

                                ActivityPost updatedData = activityPostBackendlessCollection.getData().get(0);

                                Backendless.Persistence.of(ActivityPost.class).remove(updatedData, new AsyncCallback<Long>() {
                                    @Override
                                    public void handleResponse(Long response) {
                                        Log.d("DislikePost", "=========================== Comment Delete is succeed! ===========================");
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Log.d("DislikePost", "=========================== Comment Delete is failed! ===========================");
                                    }
                                });

                            } else {
                                Log.d("DislikeComment", "=========================== Comment Delete is not found! ===========================");
                            }
                        }
                        @Override
                        public void handleFault( BackendlessFault fault )
                        {
                            Log.d("DislikeComment", "=========================== Comment Delete is failed! ===========================");
                        }
                    });

            //-------------------------------------------

            adapter.removeItem(position);

            if (commentData.getToUser().equals(Global.g_userInfo.getUserID())) {
                int karmaScore = Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST;
                Global.g_userInfo.setKarmaScore(karmaScore);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_DECREASE_DELETE_COMMENT);

            } else {
                int karmaScore = Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_VOTE_DISLIKE;
                Global.g_userInfo.setKarmaScore(karmaScore);
                GlobalSharedData.updateUserDBData();
                Utility.karmaInBackground(Global.KARMA_COMMENT_DISLIKE);

                //------------------------------

                DeliveryOptions deliveryOptions = new DeliveryOptions();
                deliveryOptions.addPushSinglecast( commentData.getToUser() );

                PublishOptions publishOptions = new PublishOptions();
                publishOptions.putHeader( "android-ticker-text", mContext.getString(R.string.ios_comment_deleted) );
                publishOptions.putHeader( "android-content-title", mContext.getString(R.string.ios_comment_deleted) );
                publishOptions.putHeader( "android-content-text", mContext.getString(R.string.karma_comment_5_downvotes) );
                publishOptions.putHeader( "ios-alert", mContext.getString(R.string.karma_comment_5_downvotes) );
                publishOptions.putHeader( "ios-badge", Global.PUSH_BADGE );
                publishOptions.putHeader( "ios-sound", Global.PUSH_SOUND );

                Backendless.Messaging.publish(Global.MESSAGE_CHANNEL, mContext.getString(R.string.karma_comment_5_downvotes), publishOptions, deliveryOptions, new AsyncCallback<MessageStatus>() {
                    @Override
                    public void handleResponse(MessageStatus response) {
                        Log.v("DislikeComment", String.format("showMessageStatus :  %s", response.getStatus().toString()));
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e("DislikeComment", String.format("showMessageStatus :  %s", fault.toString()));
                    }
                });
                //---------------------------------
            }

        } else {

            String item = String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE);
            ArrayList<String> likeTypeArray = new ArrayList<String>();
            likeTypeArray = commentData.getLikeTypeArray();
            likeTypeArray.add(item);
            commentData.setLikeTypeArray(likeTypeArray);
            int likeCount = commentData.getLikeCount() - 1;
            commentData.setLikeCount(likeCount);

            commentData.setLikeType("");
            if (commentData.getLikeTypeArray().size() > 1) {
                commentData.setLikeType(commentData.getLikeTypeArray().get(0));
                for (int i=1; i<commentData.getLikeTypeArray().size(); i++) {
                    commentData.setLikeType(String.format("%s;%s", commentData.getLikeType(), commentData.getLikeTypeArray().get(i)));
                }
            } else if (commentData.getLikeTypeArray().size() == 1) {
                commentData.setLikeType(commentData.getLikeTypeArray().get(0));
            }

            viewHolder.btn_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.like));
            viewHolder.txt_like_count.setText(GlobalSharedData.getFormattedCount(commentData.getLikeCount()));
            adapter.replaceItem(position, commentData);

            Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_VOTE_DISLIKE);
            Global.g_userInfo.setVoteCount(Global.g_userInfo.getVoteCount() + 1);
            Utility.karmaInBackground(Global.KARMA_COMMENT_DISLIKE);

            //-----------------------------------------

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            String whereClause = String.format("%s = '%s'", Global.KEY_OBJECT_ID, commentData.getObjectId());
            dataQuery.setWhereClause( whereClause );

            Backendless.Persistence.of( ActivityPost.class ).find( dataQuery,
                    new AsyncCallback<BackendlessCollection<ActivityPost>>(){
                        @Override
                        public void handleResponse( BackendlessCollection<ActivityPost> activityPostBackendlessCollection )
                        {
                            if (activityPostBackendlessCollection.getData().size() > 0) {

                                ActivityPost updatedData = activityPostBackendlessCollection.getData().get(0);
                                updatedData.setLikeCount(commentData.getLikeCount());
                                updatedData.setLikeType(commentData.getLikeType());

                                Backendless.Persistence.save(updatedData, new AsyncCallback<ActivityPost>() {
                                    @Override
                                    public void handleResponse(ActivityPost response) {
                                        Log.d("DislikeComment", "****************** Comment Dislike is succeed! *************************");
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Log.d("DislikeComment", "=========================== Comment Dislike is failed! ===========================");
                                    }
                                });

                            } else {
                                Log.d("DislikeComment", "=========================== Comment Dislike is not found! ===========================");
                            }
                        }
                        @Override
                        public void handleFault( BackendlessFault fault )
                        {
                            Log.d("DislikeComment", "=========================== Comment Dislike is failed! ===========================");
                        }
                    });

            //------------------------------------------

            DeliveryOptions deliveryOptions = new DeliveryOptions();
            deliveryOptions.addPushSinglecast( commentData.getToUser() );

            PublishOptions publishOptions = new PublishOptions();
            publishOptions.putHeader( "android-ticker-text", mContext.getString(R.string.karma_decreased) );
            publishOptions.putHeader( "android-content-title", mContext.getString(R.string.karma_decreased) );
            publishOptions.putHeader( "android-content-text", mContext.getString(R.string.karma_someone_downvote_comment) );
            publishOptions.putHeader( "ios-alert", mContext.getString(R.string.karma_someone_downvote_comment) );
            publishOptions.putHeader( "ios-badge", Global.PUSH_BADGE );
            publishOptions.putHeader( "ios-sound", Global.PUSH_SOUND );

            Backendless.Messaging.publish(Global.MESSAGE_CHANNEL, mContext.getString(R.string.karma_someone_downvote_comment), publishOptions, deliveryOptions, new AsyncCallback<MessageStatus>() {
                @Override
                public void handleResponse(MessageStatus response) {
                    Log.v("DislikeComment", String.format("showMessageStatus :  %s", response.getStatus().toString()));
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e("DislikeComment", String.format("showMessageStatus :  %s", fault.toString()));
                }
            });

            //------------------------------------------
        }
    }

}
