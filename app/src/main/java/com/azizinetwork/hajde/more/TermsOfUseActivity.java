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

public class TermsOfUseActivity extends AppCompatActivity {

    private SVProgressHUD mSVProgressHUD;
    RelativeLayout relativeLayout_no_result;
    ImageButton btn_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);

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
                onGetTermsOfUseData();
            }
        });

        onGetTermsOfUseData();
    }

    public void onGetTermsOfUseData() {

        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add( "index ASC" );
//        sortBy.add( "index DESC" );
        queryOptions.setSortBy( sortBy );
//        String whereClause = String.format("%s = '%s'", "name", "Jack");
//        dataQuery.setWhereClause( whereClause );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( TermsOfUse.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<TermsOfUse>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<TermsOfUse> termsOfUseBackendlessCollection )
                    {
                        mSVProgressHUD.dismiss();

                        if (termsOfUseBackendlessCollection.getData().size() > 0) {

                            relativeLayout_no_result.setVisibility(View.INVISIBLE);

                            for (int i=1; i<termsOfUseBackendlessCollection.getData().size(); i++){
                                TermsOfUse termsData = termsOfUseBackendlessCollection.getData().get(i);
                                onShowTermsOfUse(i, termsData);
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
    }

    public void onShowNoResult() {
        relativeLayout_no_result.setVisibility(View.VISIBLE);
    }

    public void onShowTermsOfUse(int number, TermsOfUse termsData) {

        LinearLayout list = (LinearLayout) findViewById(R.id.linearLayout_terms_use);

        final LinearLayout newCell = (LinearLayout) (View.inflate(this, R.layout.row_list_terms, null));

        TextView txt_title = (TextView)newCell.findViewById(R.id.textView_title);
        TextView txt_content = (TextView)newCell.findViewById(R.id.textView_content);

        String str_content = termsData.getContent().replace("\\n", "\n");

        txt_title.setText(termsData.getTitle());
        txt_content.setText(str_content);

        newCell.setTag(number);
        registerForContextMenu(newCell);
        list.addView(newCell);

        newCell.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

            }
        });

    }
}
