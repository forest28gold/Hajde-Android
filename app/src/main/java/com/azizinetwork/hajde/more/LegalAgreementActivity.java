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
import com.azizinetwork.hajde.model.backend.PrivacyPolicy;
import com.azizinetwork.hajde.model.backend.TermsOfUse;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.bigkoo.svprogresshud.SVProgressHUD;

import java.util.ArrayList;
import java.util.List;

public class LegalAgreementActivity extends AppCompatActivity {

    private SVProgressHUD mSVProgressHUD;
    RelativeLayout relativeLayout_no_result;
    ImageButton btn_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_agreement);

        mSVProgressHUD = new SVProgressHUD(this);

        Button btn_back = (Button)findViewById(R.id.button_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        relativeLayout_no_result = (RelativeLayout)findViewById(R.id.relativeLayout_no_result);
        relativeLayout_no_result.setVisibility(View.INVISIBLE);
        btn_refresh = (ImageButton)findViewById(R.id.imageButton_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGetLegalAgreementData();
            }
        });

        onGetLegalAgreementData();
    }

    public void onGetLegalAgreementData() {

        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

        final BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add( "index ASC" );
        queryOptions.setSortBy( sortBy );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( TermsOfUse.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<TermsOfUse>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<TermsOfUse> termsOfUseBackendlessCollection )
                    {
                        if (termsOfUseBackendlessCollection.getData().size() > 0) {

                            relativeLayout_no_result.setVisibility(View.INVISIBLE);

                            for (int i=0; i<termsOfUseBackendlessCollection.getData().size(); i++){
                                TermsOfUse termsData = termsOfUseBackendlessCollection.getData().get(i);
                                onShowTermsOfUse(i, termsData);
                            }

                            Backendless.Persistence.of( PrivacyPolicy.class ).find( dataQuery,
                                    new AsyncCallback<BackendlessCollection<PrivacyPolicy>>(){
                                        @Override
                                        public void handleResponse( BackendlessCollection<PrivacyPolicy> privacyPolicyBackendlessCollection )
                                        {
                                            mSVProgressHUD.dismiss();

                                            if (privacyPolicyBackendlessCollection.getData().size() > 0) {

                                                relativeLayout_no_result.setVisibility(View.INVISIBLE);

                                                for (int i=0; i<privacyPolicyBackendlessCollection.getData().size(); i++){
                                                    PrivacyPolicy privacyPolicyData = privacyPolicyBackendlessCollection.getData().get(i);
                                                    onShowPrivacyPolicyData(i, privacyPolicyData);
                                                }

                                            } else {
                                                onShowNoResult();
                                            }
                                        }
                                        @Override
                                        public void handleFault( BackendlessFault fault )
                                        {
                                            mSVProgressHUD.dismiss();
                                            onShowNoResult();
                                        }
                                    });

                        } else {
                            onShowNoResult();
                        }
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        mSVProgressHUD.dismiss();
                        onShowNoResult();
                    }
                });
    }

    public void onShowNoResult() {
        relativeLayout_no_result.setVisibility(View.VISIBLE);
    }

    public void onShowTermsOfUse(int number, TermsOfUse termsData) {

        LinearLayout list = (LinearLayout) findViewById(R.id.linearLayout_legal_agreement);

        final LinearLayout newCell = (LinearLayout) (View.inflate(this, R.layout.row_list_terms, null));

        TextView txt_title = (TextView)newCell.findViewById(R.id.textView_title);
        TextView txt_content = (TextView)newCell.findViewById(R.id.textView_content);
        RelativeLayout relativeLayout_seperate = (RelativeLayout)newCell.findViewById(R.id.relativeLayout_seperate);

        txt_title.setText(termsData.getTitle());

        if (termsData.getContent()==null) {
            txt_content.setText("");
            relativeLayout_seperate.setVisibility(View.INVISIBLE);
        } else {
            String str_content = termsData.getContent().replace("\\n", "\n");
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

    public void onShowPrivacyPolicyData(int number, PrivacyPolicy privacyPolicyData){

        LinearLayout list = (LinearLayout) findViewById(R.id.linearLayout_legal_agreement);

        final LinearLayout newCell = (LinearLayout) (View.inflate(this, R.layout.row_list_terms, null));

        TextView txt_title = (TextView)newCell.findViewById(R.id.textView_title);
        TextView txt_content = (TextView)newCell.findViewById(R.id.textView_content);
        RelativeLayout relativeLayout_seperate = (RelativeLayout)newCell.findViewById(R.id.relativeLayout_seperate);

        txt_title.setText(privacyPolicyData.getTitle());

        if (privacyPolicyData.getContent()==null) {
            txt_content.setText("");
            relativeLayout_seperate.setVisibility(View.INVISIBLE);
        } else {
            String str_content = privacyPolicyData.getContent().replace("\\n", "\n");
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
