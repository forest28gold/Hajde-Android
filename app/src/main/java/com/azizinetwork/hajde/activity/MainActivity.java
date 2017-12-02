package com.azizinetwork.hajde.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.fragment.HomeFragment;
import com.azizinetwork.hajde.fragment.KarmaShopFragment;
import com.azizinetwork.hajde.fragment.MoreFragment;
import com.azizinetwork.hajde.fragment.MyProfileFragment;
import com.azizinetwork.hajde.newpost.NewImageActivity;
import com.azizinetwork.hajde.newpost.NewPostActivity;
import com.azizinetwork.hajde.newpost.NewVoiceActivity;
import com.azizinetwork.hajde.settings.Global;
import com.azizinetwork.hajde.settings.LocationService;
import com.startsmake.mainnavigatetabbar.widget.MainNavigateTabBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TITLE_PAGE_HOME;
    private static String TITLE_PAGE_MY_PROFILE;
    private static String TITLE_PAGE_PUBLISH = " ";
    private static String TITLE_PAGE_KARMA_SHOP;
    private static String TITLE_PAGE_MORE;

    public MainNavigateTabBar mNavigateTabBar;
    private RelativeLayout relativeLayout_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigateTabBar = (MainNavigateTabBar) findViewById(R.id.mainTabBar);
        mNavigateTabBar.onRestoreInstanceState(savedInstanceState);

        TITLE_PAGE_HOME = getResources().getString(R.string.tab_home);
        TITLE_PAGE_MY_PROFILE = getResources().getString(R.string.tab_profile);
        TITLE_PAGE_KARMA_SHOP = getResources().getString(R.string.tab_shop);
        TITLE_PAGE_MORE = getResources().getString(R.string.tab_hajde);

        mNavigateTabBar.addTab(HomeFragment.class, new MainNavigateTabBar.TabParam(R.mipmap.comui_tab_home, R.mipmap.comui_tab_home_selected, TITLE_PAGE_HOME));
        mNavigateTabBar.addTab(MyProfileFragment.class, new MainNavigateTabBar.TabParam(R.mipmap.comui_tab_city, R.mipmap.comui_tab_city_selected, TITLE_PAGE_MY_PROFILE));
        mNavigateTabBar.addTab(null, new MainNavigateTabBar.TabParam(0, 0, TITLE_PAGE_PUBLISH));
        mNavigateTabBar.addTab(KarmaShopFragment.class, new MainNavigateTabBar.TabParam(R.mipmap.comui_tab_message, R.mipmap.comui_tab_message_selected, TITLE_PAGE_KARMA_SHOP));
        mNavigateTabBar.addTab(MoreFragment.class, new MainNavigateTabBar.TabParam(R.mipmap.comui_tab_person, R.mipmap.comui_tab_person_selected, TITLE_PAGE_MORE));

        relativeLayout_new = (RelativeLayout)findViewById(R.id.relativeLayout_new);
        ImageButton btn_close = (ImageButton)findViewById(R.id.imageButton_close);
        ImageButton btn_post = (ImageButton)findViewById(R.id.imageButton_post);
        ImageButton btn_image = (ImageButton)findViewById(R.id.imageButton_image);
        ImageButton btn_voice = (ImageButton)findViewById(R.id.imageButton_voice);

        relativeLayout_new.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        btn_post.setOnClickListener(this);
        btn_image.setOnClickListener(this);
        btn_voice.setOnClickListener(this);

        relativeLayout_new.setVisibility(View.GONE);

        String default_tab = "";
        Intent intent = getIntent();
        default_tab = intent.getStringExtra("start_mode");

        if (default_tab != null && default_tab.equals("refresh")) {
            mNavigateTabBar.setDefaultSelectedTab(3);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mNavigateTabBar.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Global.g_offerIsOn) {
            Global.g_offerIsOn = false;
            mNavigateTabBar.setCurrentSelectedTab(2);
        }
    }

    public void onClickPublish(View v) {
        relativeLayout_new.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.relativeLayout_new) {
            relativeLayout_new.setVisibility(View.GONE);
        } else if (view.getId() == R.id.imageButton_close) {
            relativeLayout_new.setVisibility(View.GONE);
        } else if (view.getId() == R.id.imageButton_post) {
            relativeLayout_new.setVisibility(View.GONE);
            Intent i = new Intent(MainActivity.this, NewPostActivity.class);
            startActivity(i);
        } else if (view.getId() == R.id.imageButton_image) {
            relativeLayout_new.setVisibility(View.GONE);
            Intent i = new Intent(MainActivity.this, NewImageActivity.class);
            startActivity(i);
        } else if (view.getId() == R.id.imageButton_voice) {
            relativeLayout_new.setVisibility(View.GONE);
            Intent i = new Intent(MainActivity.this, NewVoiceActivity.class);
            startActivity(i);
        }
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            if (isServiceRunning(LocationService.class)) {
                stopService(Global.g_locationService);
            }
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }
}
