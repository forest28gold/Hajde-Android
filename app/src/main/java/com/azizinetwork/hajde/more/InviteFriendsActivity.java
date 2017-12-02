package com.azizinetwork.hajde.more;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.model.parse.InviteFriendData;
import com.azizinetwork.hajde.settings.Global;
import com.wevey.selector.dialog.NormalAlertDialog;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InviteFriendsActivity extends AppCompatActivity {

    private final static String TAG = "InviteFriendsActivity";
    protected ListView mListview;
    protected ContactAdapter mAdapter;
    private NormalAlertDialog dialog;
    private String address_sms = "";
    private String seperate = ";";
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            seperate = ",";
        }

        Button btn_back = (Button)findViewById(R.id.button_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mListview = (ListView)findViewById(R.id.listview);
        mListview.setAdapter(mAdapter = new ContactAdapter(this));
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.replaceItem(position);
            }
        });

        Button btn_send = (Button)findViewById(R.id.button_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count > 0) {

                    Log.v(TAG, "========================" + address_sms + "=========================");

                    try {

                        Intent sms_intent = new Intent(Intent.ACTION_VIEW);
                        sms_intent.putExtra("address", address_sms);
                        sms_intent.putExtra("sms_body", getString(R.string.signup_hajde) + "    " + Global.HAJDE_APP_LINK);
                        sms_intent.setType("vnd.android-dir/mms-sms");
                        startActivity(sms_intent);

                    } catch (Exception e) {

                        dialog = new NormalAlertDialog.Builder(InviteFriendsActivity.this)
                                .setHeight(0.2f)
                                .setWidth(0.8f)
                                .setTitleVisible(true)
                                .setTitleText(getString(R.string.alert))
                                .setTitleTextColor(R.color.black)
                                .setContentText(getString(R.string.alert_failed_sms))
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
                    }
                }
            }
        });

        initLoadContactData();
    }

    public void initLoadContactData() {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor people = getContentResolver().query(uri, projection, null, null, null);

        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int indexEmail = people.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

        people.moveToFirst();
        do {
            String name   = people.getString(indexName);
            String number = people.getString(indexNumber);
            String email = people.getString(indexEmail);
            Uri imgUri = null;

            InviteFriendData contactData = new InviteFriendData();
            contactData.setSelectIsOn(false);
            contactData.setUserName(name);
            contactData.setPhotoUri(imgUri);
            if (!number.equals("")) {
                contactData.setPhoneNumber(number);
            } else if (!email.equals("")){
                contactData.setPhoneNumber(email);
            } else {
                contactData.setPhoneNumber("");
            }

            mAdapter.addItem(contactData);

            // Do work...
        } while (people.moveToNext());

    }

    public class ContactAdapter extends BaseAdapter {
        private Context mContext;
        private List<InviteFriendData> mContactList = new ArrayList<>();

        public ContactAdapter(Context context) {
            mContext = context;
        }

        public void addItem(InviteFriendData item) {
            mContactList.add(item);
            notifyDataSetChanged();
        }

        public void addItem(int pos, InviteFriendData item) {
            mContactList.add(pos, item);
            notifyDataSetChanged();
        }

        public void replaceItem(int pos) {

            InviteFriendData contactData = mContactList.get(pos);
            if (contactData.isSelectIsOn()) {
                contactData.setSelectIsOn(false);

                if (address_sms.contains(seperate))
                    address_sms = address_sms.replace(seperate + contactData.getPhoneNumber(), "");
                else
                    address_sms = address_sms.replace(contactData.getPhoneNumber(), "");

                count--;

            } else {
                contactData.setSelectIsOn(true);

                if (count > 0)
                    address_sms = address_sms + seperate + contactData.getPhoneNumber();
                else
                    address_sms = contactData.getPhoneNumber();

                count++;
            }
            mContactList.set(pos, contactData);

            Log.v(TAG, "========================" + address_sms + "=========================");

            notifyDataSetChanged();
        }

        public void removeAll() {
            mContactList = new ArrayList<>();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mContactList.size();
        }

        @Override
        public InviteFriendData getItem(int position) {
            return mContactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_list_contact, parent, false);
                convertView.setTag(viewHolder = new ViewHolder(convertView));
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            InviteFriendData contactData = mContactList.get(position);

            Uri uri = contactData.getPhotoUri();
            if (uri != null) {
                viewHolder.img_photo.setImageURI(uri);
            } else {
                viewHolder.img_photo.setImageResource(R.mipmap.avatar);
            }

            viewHolder.txt_name.setText(contactData.getUserName());
            viewHolder.txt_email_phone.setText(contactData.getPhoneNumber());

            if (contactData.isSelectIsOn()) {
                viewHolder.img_selector.setVisibility(View.VISIBLE);
            } else {
                viewHolder.img_selector.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

        class ViewHolder {
            protected RelativeLayout relativeLayout_contact;
            protected TextView txt_name, txt_email_phone;
            protected CircleImageView img_photo;
            protected ImageView img_selector;

            ViewHolder(View rootView) {
                initView(rootView);
            }

            private void initView(View rootView) {

                relativeLayout_contact = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_contact);
                txt_name = (TextView)rootView.findViewById(R.id.textView_name);
                txt_email_phone = (TextView)rootView.findViewById(R.id.textView_phone_email);
                img_photo = (CircleImageView) rootView.findViewById(R.id.imageView_contact);
                img_selector = (ImageView) rootView.findViewById(R.id.imageView_selector);
            }
        }

    }
}
