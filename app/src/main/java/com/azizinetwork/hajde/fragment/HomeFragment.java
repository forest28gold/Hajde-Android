package com.azizinetwork.hajde.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.activity.MyKarmaActivity;
import com.azizinetwork.hajde.posts.MostCommentedFragment;
import com.azizinetwork.hajde.posts.MostVotesFragment;
import com.azizinetwork.hajde.posts.NewestFragment;
import com.azizinetwork.hajde.settings.Global;
import com.flyco.tablayout.SegmentTabLayout;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private final static String TAG = "HomeFragment";
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    Button btn_karma;

    private Handler karmaHandler = new Handler();
    private static boolean karmacallIsOn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_home, container, false);

        String[] mTitles = {getContext().getResources().getString(R.string.tab_newest),
                getContext().getResources().getString(R.string.tab_most_commented),
                getContext().getResources().getString(R.string.tab_most_votes)};

        mFragments.add(NewestFragment.getInstance(mTitles[0]));
        mFragments.add(MostCommentedFragment.getInstance(mTitles[1]));
        mFragments.add(MostVotesFragment.getInstance(mTitles[2]));

        SegmentTabLayout tabLayout = (SegmentTabLayout) layout.findViewById(R.id.tl_segment);
        tabLayout.setTabData(mTitles, getActivity(), R.id.fl_change, mFragments);

        btn_karma = (Button)layout.findViewById(R.id.button_karma_score);
        if (Global.g_userInfo != null)
            btn_karma.setText(String.valueOf(Global.g_userInfo.getKarmaScore()));

        btn_karma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MyKarmaActivity.class);
                startActivity(i);
            }
        });

        return layout;

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
