package com.azizinetwork.hajde.more;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.activity.MainActivity;
import com.azizinetwork.hajde.model.backend.WhatsKarma;
import com.azizinetwork.hajde.settings.Global;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.bigkoo.svprogresshud.SVProgressHUD;

import java.util.ArrayList;
import java.util.List;

public class WhatsKarmaActivity extends AppCompatActivity {

    private SVProgressHUD mSVProgressHUD;
    RelativeLayout relativeLayout_no_result;
    ImageButton btn_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_karma);

        mSVProgressHUD = new SVProgressHUD(this);

        Button btn_back = (Button) findViewById(R.id.button_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button btn_offer = (Button) findViewById(R.id.button_goto_offers);
        btn_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.g_offerIsOn = true;
                finish();
            }
        });

        relativeLayout_no_result = (RelativeLayout) findViewById(R.id.relativeLayout_no_result);
        relativeLayout_no_result.setVisibility(View.INVISIBLE);
        btn_refresh = (ImageButton) findViewById(R.id.imageButton_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGetWhatsKarmaData();
            }
        });

        onGetWhatsKarmaData();
    }

    public void onGetWhatsKarmaData() {

        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add("index ASC");
        queryOptions.setSortBy(sortBy);
        dataQuery.setQueryOptions(queryOptions);

        Backendless.Persistence.of(WhatsKarma.class).find(dataQuery,
                new AsyncCallback<BackendlessCollection<WhatsKarma>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<WhatsKarma> whatsKarmaBackendlessCollection) {
                        mSVProgressHUD.dismiss();

                        if (whatsKarmaBackendlessCollection.getData().size() > 0) {

                            relativeLayout_no_result.setVisibility(View.INVISIBLE);

                            for (int i = 0; i < whatsKarmaBackendlessCollection.getData().size(); i++) {
                                WhatsKarma whatsKarmaData = whatsKarmaBackendlessCollection.getData().get(i);
                                onShowWhatsKarma(i, whatsKarmaData);
                            }

                        } else {
                            onShowNoResult();
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        mSVProgressHUD.dismiss();
                        onShowNoResult();
                    }
                });
    }

    public void onShowNoResult() {
        relativeLayout_no_result.setVisibility(View.VISIBLE);
    }

    public void onShowWhatsKarma(int number, WhatsKarma whatsKarmaData) {

        LinearLayout list = (LinearLayout) findViewById(R.id.linearLayout_whats_karma);

        final LinearLayout newCell = (LinearLayout) (View.inflate(this, R.layout.row_list_terms, null));

        TextView txt_title = (TextView)newCell.findViewById(R.id.textView_title);
        TextView txt_content = (TextView)newCell.findViewById(R.id.textView_content);

        if (Global.g_userInfo.getLanguage().equals(Global.LANG_ALB)) {
            String str_content = whatsKarmaData.getContentAlb().replace("\\n", "\n");
            txt_title.setText(whatsKarmaData.getTitleAlb());
            txt_content.setText(str_content);
        } else {
            String str_content = whatsKarmaData.getContent().replace("\\n", "\n");
            txt_title.setText(whatsKarmaData.getTitle());
            txt_content.setText(str_content);
        }

        newCell.setTag(number);
        registerForContextMenu(newCell);
        list.addView(newCell);

        newCell.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

            }
        });

    }

}
