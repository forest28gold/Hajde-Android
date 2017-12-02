package com.azizinetwork.hajde.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.activity.MainActivity;
import com.azizinetwork.hajde.more.FeedbackActivity;
import com.azizinetwork.hajde.more.InviteFriendsActivity;
import com.azizinetwork.hajde.more.LegalAgreementActivity;
import com.azizinetwork.hajde.more.WhatsKarmaActivity;
import com.azizinetwork.hajde.settings.Global;
import com.azizinetwork.hajde.settings.GlobalSharedData;

import java.util.Locale;

public class MoreFragment extends Fragment {

    Locale myLocale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_more, container, false);

        final RelativeLayout dia_lang = (RelativeLayout) layout.findViewById(R.id.relativeLayout_lang);
        dia_lang.setVisibility(View.INVISIBLE);

        final Button btn_lang = (Button) layout.findViewById(R.id.button_laguage);
        btn_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dia_lang.getVisibility() == View.VISIBLE) {
                    dia_lang.setVisibility(View.INVISIBLE);
                } else {
                    dia_lang.setVisibility(View.VISIBLE);
                }
            }
        });

        if (Global.g_userInfo.getLanguage().equals(Global.LANG_ALB)) {
            btn_lang.setText(getString(R.string.albanian));
        } else {
            btn_lang.setText(getString(R.string.english));
        }

        Button btn_lang_eng = (Button) layout.findViewById(R.id.button_eng);
        btn_lang_eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dia_lang.setVisibility(View.INVISIBLE);
                btn_lang.setText(getString(R.string.english));
                Global.g_userInfo.setLanguage(Global.LANG_ENG);
                GlobalSharedData.updateUserDBData();
                setLocale(Global.LANG_ENG);
            }
        });

        Button btn_lang_alba = (Button) layout.findViewById(R.id.button_alb);
        btn_lang_alba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dia_lang.setVisibility(View.INVISIBLE);
                btn_lang.setText(getString(R.string.albanian));
                Global.g_userInfo.setLanguage(Global.LANG_ALB);
                GlobalSharedData.updateUserDBData();
                setLocale(Global.LANG_ALB);
            }
        });

        Button btn_love = (Button) layout.findViewById(R.id.button_love);
        btn_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dia_lang.setVisibility(View.INVISIBLE);
                goToHajdeApp();
            }
        });

        Button btn_what_karma = (Button) layout.findViewById(R.id.button_whats);
        btn_what_karma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dia_lang.setVisibility(View.INVISIBLE);
                Intent i = new Intent(getActivity(), WhatsKarmaActivity.class);
                startActivity(i);
            }
        });

        Button btn_invite = (Button) layout.findViewById(R.id.button_invite);
        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dia_lang.setVisibility(View.INVISIBLE);
                Intent i = new Intent(getActivity(), InviteFriendsActivity.class);
                startActivity(i);
            }
        });

        Button btn_feedback = (Button) layout.findViewById(R.id.button_feedback);
        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dia_lang.setVisibility(View.INVISIBLE);
                Intent i = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(i);
            }
        });

        Button btn_legal = (Button) layout.findViewById(R.id.button_legal);
        btn_legal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dia_lang.setVisibility(View.INVISIBLE);
                Intent i = new Intent(getActivity(), LegalAgreementActivity.class);
                startActivity(i);
            }
        });

        Button btn_facebook = (Button) layout.findViewById(R.id.button_facebook);
        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dia_lang.setVisibility(View.INVISIBLE);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Global.HAJDE_FACEBOOK_LINK)));
            }
        });

        Button btn_twitter = (Button) layout.findViewById(R.id.button_twitter);
        btn_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dia_lang.setVisibility(View.INVISIBLE);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Global.HAJDE_TWITTER_LINK)));
            }
        });

        Button btn_google = (Button) layout.findViewById(R.id.button_google);
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dia_lang.setVisibility(View.INVISIBLE);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Global.HAJDE_GOOGLE_LINK)));
            }
        });

        return layout;
    }

    public void setLocale(String selectedLanguage) {

        if(selectedLanguage.equals(Global.LANG_ALB))
            myLocale = new Locale("sq");
        else
            myLocale = Locale.ENGLISH;

        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());

        Intent refresh = new Intent(getActivity(), MainActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        refresh.putExtra("start_mode", "refresh");
        startActivity(refresh);
        getActivity().finish();
        getActivity().overridePendingTransition(0, 0);
    }

    public void goToHajdeApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.azizinetwork.hajde")));
        } catch (ActivityNotFoundException e1) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Global.HAJDE_APP_LINK)));
            } catch (ActivityNotFoundException e2) {
//                Toast.makeText(getActivity(), "You don't have any app that can open this link", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
