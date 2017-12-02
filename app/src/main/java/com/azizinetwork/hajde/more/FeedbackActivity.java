package com.azizinetwork.hajde.more;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.settings.GlobalSharedData;
import com.wevey.selector.dialog.NormalAlertDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeedbackActivity extends AppCompatActivity {

    private NormalAlertDialog dialog;
    EditText edit_from_email, edit_subject, edit_to_email, edit_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Button btn_back = (Button)findViewById(R.id.button_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edit_from_email = (EditText)findViewById(R.id.editText_from);
        edit_subject = (EditText)findViewById(R.id.editText_subject);
        edit_to_email = (EditText)findViewById(R.id.editText_to);
        edit_message = (EditText)findViewById(R.id.editText_message);

        edit_to_email.setEnabled(false);

        Button btn_send = (Button)findViewById(R.id.button_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strSubject = edit_subject.getText().toString();
                String strFrom = edit_from_email.getText().toString();
                String strTo = edit_to_email.getText().toString();
                String strMessage = edit_message.getText().toString();

                if (strSubject.equals("")) {
                    dialog = new NormalAlertDialog.Builder(FeedbackActivity.this)
                            .setHeight(0.2f)
                            .setWidth(0.8f)
                            .setTitleVisible(true)
                            .setTitleText(getString(R.string.alert))
                            .setTitleTextColor(R.color.black)
                            .setContentText(getString(R.string.alert_input_subject))
                            .setContentTextColor(R.color.dark)
                            .setSingleMode(true)
                            .setSingleButtonText(getString(R.string.ok))
                            .setSingleButtonTextColor(R.color.blue)
                            .setCanceledOnTouchOutside(false)
                            .setSingleListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            })
                            .build();

                    dialog.show();
                    return;
                } else if (strFrom.equals("")) {
                    dialog = new NormalAlertDialog.Builder(FeedbackActivity.this)
                            .setHeight(0.2f)
                            .setWidth(0.8f)
                            .setTitleVisible(true)
                            .setTitleText(getString(R.string.alert))
                            .setTitleTextColor(R.color.black)
                            .setContentText(getString(R.string.alert_input_from_email))
                            .setContentTextColor(R.color.dark)
                            .setSingleMode(true)
                            .setSingleButtonText(getString(R.string.ok))
                            .setSingleButtonTextColor(R.color.blue)
                            .setCanceledOnTouchOutside(false)
                            .setSingleListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            })
                            .build();

                    dialog.show();
                    return;
                } else if (!GlobalSharedData.isEmailValid(strFrom)) {
                    dialog = new NormalAlertDialog.Builder(FeedbackActivity.this)
                            .setHeight(0.2f)
                            .setWidth(0.8f)
                            .setTitleVisible(true)
                            .setTitleText(getString(R.string.alert))
                            .setTitleTextColor(R.color.black)
                            .setContentText(getString(R.string.alert_input_from_email_correctly))
                            .setContentTextColor(R.color.dark)
                            .setSingleMode(true)
                            .setSingleButtonText(getString(R.string.ok))
                            .setSingleButtonTextColor(R.color.blue)
                            .setCanceledOnTouchOutside(false)
                            .setSingleListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            })
                            .build();

                    dialog.show();
                    return;
                } else if (strTo.equals("")) {
                    dialog = new NormalAlertDialog.Builder(FeedbackActivity.this)
                            .setHeight(0.2f)
                            .setWidth(0.8f)
                            .setTitleVisible(true)
                            .setTitleText(getString(R.string.alert))
                            .setTitleTextColor(R.color.black)
                            .setContentText(getString(R.string.alert_input_to_email))
                            .setContentTextColor(R.color.dark)
                            .setSingleMode(true)
                            .setSingleButtonText(getString(R.string.ok))
                            .setSingleButtonTextColor(R.color.blue)
                            .setCanceledOnTouchOutside(false)
                            .setSingleListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            })
                            .build();

                    dialog.show();
                    return;
                } else if (!GlobalSharedData.isEmailValid(strTo)) {
                    dialog = new NormalAlertDialog.Builder(FeedbackActivity.this)
                            .setHeight(0.2f)
                            .setWidth(0.8f)
                            .setTitleVisible(true)
                            .setTitleText(getString(R.string.alert))
                            .setTitleTextColor(R.color.black)
                            .setContentText(getString(R.string.alert_input_to_email_correctly))
                            .setContentTextColor(R.color.dark)
                            .setSingleMode(true)
                            .setSingleButtonText(getString(R.string.ok))
                            .setSingleButtonTextColor(R.color.blue)
                            .setCanceledOnTouchOutside(false)
                            .setSingleListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            })
                            .build();

                    dialog.show();
                    return;
                } else if (strMessage.equals("")) {
                    dialog = new NormalAlertDialog.Builder(FeedbackActivity.this)
                            .setHeight(0.2f)
                            .setWidth(0.8f)
                            .setTitleVisible(true)
                            .setTitleText(getString(R.string.alert))
                            .setTitleTextColor(R.color.black)
                            .setContentText(getString(R.string.alert_input_message))
                            .setContentTextColor(R.color.dark)
                            .setSingleMode(true)
                            .setSingleButtonText(getString(R.string.ok))
                            .setSingleButtonTextColor(R.color.blue)
                            .setCanceledOnTouchOutside(false)
                            .setSingleListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            })
                            .build();

                    dialog.show();
                    return;
                }

                finish();
            }
        });
    }

}
