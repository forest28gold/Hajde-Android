package com.azizinetwork.hajde.myposts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.activity.MyKarmaActivity;
import com.azizinetwork.hajde.myposts.fragment.MyMostCommentedFragment;
import com.azizinetwork.hajde.myposts.fragment.MyMostVotesFragment;
import com.azizinetwork.hajde.myposts.fragment.MyNewestFragment;
import com.azizinetwork.hajde.settings.Global;
import com.flyco.tablayout.SegmentTabLayout;

import java.util.ArrayList;

public class MyPostsActivity extends AppCompatActivity {

    private final static String TAG = "MyPostsActivity";
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    Button btn_karma;

    private Handler karmaHandler = new Handler();
    private static boolean karmacallIsOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        String[] mTitles = {getResources().getString(R.string.tab_newest),
                getResources().getString(R.string.tab_most_commented),
                getResources().getString(R.string.tab_most_votes)};

        mFragments.add(MyNewestFragment.getInstance(mTitles[0]));
        mFragments.add(MyMostCommentedFragment.getInstance(mTitles[1]));
        mFragments.add(MyMostVotesFragment.getInstance(mTitles[2]));

        SegmentTabLayout tabLayout = (SegmentTabLayout)findViewById(R.id.tl_segment);
        tabLayout.setTabData(mTitles, this, R.id.fl_change, mFragments);

        Button btn_back = (Button)findViewById(R.id.button_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_karma = (Button)findViewById(R.id.button_karma_score);
        btn_karma.setText(String.valueOf(Global.g_userInfo.getKarmaScore()));

        btn_karma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MyPostsActivity.this, MyKarmaActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onResume() {
        if (!karmacallIsOn) {
            karmacallIsOn = true;
            karmaHandler.postDelayed(karmaRunnable, 0);
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        karmaHandler.removeCallbacks(karmaRunnable);
        karmacallIsOn = false;
        super.onStop();
    }

    private Runnable karmaRunnable = new Runnable() {
        @Override
        public void run() {
            if (Global.g_userInfo != null)
                btn_karma.setText(String.valueOf(Global.g_userInfo.getKarmaScore()));
            karmaHandler.postDelayed(this, 1000 * 5);
        }
    };
}
