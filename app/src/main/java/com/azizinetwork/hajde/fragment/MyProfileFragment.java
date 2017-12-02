package com.azizinetwork.hajde.fragment;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.activity.MyKarmaActivity;
import com.azizinetwork.hajde.myposts.MyCommentsActivity;
import com.azizinetwork.hajde.myposts.MyPostsActivity;
import com.azizinetwork.hajde.myposts.MyVotesActivity;
import com.azizinetwork.hajde.settings.Global;

public class MyProfileFragment extends Fragment {

    private final static String TAG = "MyProfileFragment";
    Button btn_karma;
    Button btn_my_posts, btn_my_comments, btn_my_votes;
    TextView txt_days, txt_hours, txt_minutes;

    private Handler timerHandler = new Handler();
    private static boolean callbackIsOn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_my_profile, container, false);

        btn_karma = (Button)layout.findViewById(R.id.button_karma_score);
        btn_my_posts = (Button)layout.findViewById(R.id.button_my_posts);
        btn_my_comments = (Button)layout.findViewById(R.id.button_my_comments);
        btn_my_votes = (Button)layout.findViewById(R.id.button_my_votes);

        txt_days = (TextView)layout.findViewById(R.id.textView_days);
        txt_hours = (TextView)layout.findViewById(R.id.textView_hours);
        txt_minutes = (TextView)layout.findViewById(R.id.textView_minutes);

        if (Global.g_userInfo != null)
            btn_karma.setText(String.valueOf(Global.g_userInfo.getKarmaScore()));

        txt_days.setText(String.valueOf(Global.g_spentDays));
        txt_hours.setText(String.valueOf(Global.g_spentHours));
        txt_minutes.setText(String.valueOf(Global.g_spentMins));

        btn_karma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MyKarmaActivity.class);
                startActivity(i);
            }
        });

        btn_my_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MyPostsActivity.class);
                startActivity(i);
            }
        });

        btn_my_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MyCommentsActivity.class);
                startActivity(i);
            }
        });

        btn_my_votes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MyVotesActivity.class);
                startActivity(i);
            }
        });

        return layout;
    }

    @Override
    public void onResume() {
        initSetMyProfileData();
        if (!callbackIsOn) {
            callbackIsOn = true;
            timerHandler.postDelayed(timerRunnable, 0);
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        timerHandler.removeCallbacks(timerRunnable);
        callbackIsOn = false;
        super.onStop();
    }

    public void initSetMyProfileData() {

        btn_my_posts.setText(String.valueOf(Global.g_userInfo.getPostCount()));
        btn_my_comments.setText(String.valueOf(Global.g_userInfo.getCommentCount()));
        btn_my_votes.setText(String.valueOf(Global.g_userInfo.getVoteCount()));
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            btn_karma.setText(String.valueOf(Global.g_userInfo.getKarmaScore()));
            txt_days.setText(String.valueOf(Global.g_spentDays));
            txt_hours.setText(String.valueOf(Global.g_spentHours));
            txt_minutes.setText(String.valueOf(Global.g_spentMins));

            timerHandler.postDelayed(this, 1000 * 5);
        }
    };
}
