package com.azizinetwork.hajde.settings;

import android.content.Intent;
import android.graphics.Bitmap;

import com.azizinetwork.hajde.model.backend.Offer;
import com.azizinetwork.hajde.model.backend.Shop;
import com.azizinetwork.hajde.model.parse.PostData;
import com.azizinetwork.hajde.model.parse.UserData;


public class Global {

    public static DBService                                             dbService = null;
    public static Intent                                                g_locationService = null;
    public static UserData                                              g_userInfo = null;
    public static int                                                   g_spentDays = 0;
    public static int                                                   g_spentHours = 0;
    public static int                                                   g_spentMins = 0;
    public static Bitmap                                                g_bitmap = null;
    public static PostData                                              g_postData = null;
    public static Shop                                                  g_shopData = null;
    public static Offer                                                 g_offerData = null;
    public static String                                                g_currency = "";
    public static boolean                                               g_offerIsOn = false;
    public static boolean                                               g_cameraIsOn = false;

    public static final String BACKEND_APP_ID                           = "139E089A-02CE-4977-FF83-86A71B6DAF00";   // Hajde
    public static final String BACKEND_SECRET_KEY                       = "B028DF5C-029B-76BA-FF09-173BF13EC300";
    public static final String BACKEND_VERSION_NUM                      = "v1";

//    public static final String BACKEND_APP_ID                           = "369D443F-F691-EFF0-FF55-FB7F66E34900";
//    public static final String BACKEND_SECRET_KEY                       = "12F88025-A029-C9B0-FF88-4F4066C25200";
//    public static final String BACKEND_VERSION_NUM                      = "v1";
    public static final String BACKEND_SERVER_URL                       = "https://api.backendless.com";

    public static final String GCM_SERVER_API_Key                       = "AIzaSyD396gucgvHyh4EPBFBmsIg8OkYsiRyuVk";
    public static final String GCM_SENDER_ID                            = "605598898300";

    public static final String HAJDE_FACEBOOK_LINK                      = "https://www.facebook.com/hajdeapp";
    public static final String HAJDE_TWITTER_LINK                       = "https://twitter.com/Hajdeapp";
    public static final String HAJDE_GOOGLE_LINK                        = "https://plus.google.com/u/0/115676550960064541720";
    public static final String HAJDE_APP_LINK                           = "https://play.google.com/store/apps/details?id=com.azizinetwork.hajde";

    public static final String COLOR1                                   = "36d7b7";     //green
    public static final String COLOR2                                   = "fece4c";     //yellow
    public static final String COLOR3                                   = "aab2bd";     //grey
    public static final String COLOR4                                   = "fe4c4c";     //red
    public static final String COLOR5                                   = "6686ff";     //orion
    public static final String COLOR6                                   = "ff78d4";     //pink
    public static final String COLOR7                                   = "fe824c";     //yellowish
    public static final String COLOR8                                   = "50e0f5";     //verdant green
    public static final String COLOR9                                   = "9878ff";     //fuchsia
    public static final String COLOR10                                  = "43b9f6";     //blue
    public static final String COLOR11                                  = "43f6d6";     //absinthe

    public static final String COMMENT_COLOR1                           = "6de1ca";     //green
    public static final String COMMENT_COLOR2                           = "f8d882";     //yellow
    public static final String COMMENT_COLOR3                           = "c2cad6";     //grey
    public static final String COMMENT_COLOR4                           = "ed7d7d";     //red
    public static final String COMMENT_COLOR5                           = "849eff";     //orion
    public static final String COMMENT_COLOR6                           = "fe99de";     //pink
    public static final String COMMENT_COLOR7                           = "fe9669";     //yellowish
    public static final String COMMENT_COLOR8                           = "7decfc";     //verdant green
    public static final String COMMENT_COLOR9                           = "ac92ff";     //fuchsia
    public static final String COMMENT_COLOR10                          = "6cccff";     //blue
    public static final String COMMENT_COLOR11                          = "78f6df";     //absinthe

    public static final int LOAD_DATA_COUNT                             = 50;
    public static String RECORD_DATE                                    = "";
    public static final String RECORD_FILE_FORMAT                       = ".m4a";

    public static final String USER_SIGNUP                              = "signup";
    public static final String USER_LOGIN                               = "login";
    public static final String USER_BEGIN                               = "begin";

    public static final String POST_TYPE_PHOTO                          = "photo";
    public static final String POST_TYPE_TEXT                           = "text";
    public static final String POST_TYPE_AUDIO                          = "audio";

    public static final String COMMENT_TYPE_ME                          = "mycomment";
    public static final String LIKE_TYPE                                = "like";
    public static final String DISLIKE_TYPE                             = "dislike";

    public static final int KARMA_SCORE_LOGIN                           = 20;
    public static final int KARMA_SCORE_POST                            = 10;
    public static final int KARMA_SCORE_COMMENT                         = 10;
    public static final int KARMA_SCORE_VOTE_LIKE                       = 5;
    public static final int KARMA_SCORE_VOTE_DISLIKE                    = 5;
    public static final int KARMA_SCORE_ABUSE                           = 5;
    public static final int KARMA_SCORE_DECREASE_POST                   = -5;
    public static final int KARMA_SCORE_DELETE_POST                     = -20;

    public static final String KARMA_LOGIN                              = "karma_login";
    public static final String KARMA_POST                               = "karma_post";
    public static final String KARMA_COMMENT                            = "karma_comment";
    public static final String KARMA_VOTE_LIKE                          = "karma_vote_like";
    public static final String KARMA_VOTE_DISLIKE                       = "karma_vote_dislike";
    public static final String KARMA_ABUSE                              = "karma_abuse";
    public static final String KARMA_COMMENT_LIKE                       = "karma_comment_like";
    public static final String KARMA_COMMENT_DISLIKE                    = "karma_comment_dislike";
    public static final String KARMA_COMMENT_ABUSE                      = "karma_comment_abuse";
    public static final String KARMA_DECREASE_POST                      = "karma_decrease_post";
    public static final String KARMA_DECREASE_COMMENT                   = "karma_decrease_comment";
    public static final String KARMA_DECREASE_DELETE_POST               = "karma_decrease_delete_post";
    public static final String KARMA_DECREASE_DELETE_COMMENT            = "karma_decrease_delete_comment";
    public static final String KARMA_REPORT_DELETE_POST                 = "karma_report_delete_post";
    public static final String KARMA_REPORT_DELETE_COMMENT              = "karma_report_delete_comment";

    public static final String SELECT_NEWEST                            = "select_newest";
    public static final String SELECT_MOST_COMMENTED                    = "select_most_commented";
    public static final String SELECT_MOST_VOTES                        = "select_most_voted";
    public static final String SELECT_MY_COMMENTED                      = "select_my_commented";
    public static final String SELECT_MY_VOTES                          = "select_my_votes";

    public static final String MESSAGE_CHANNEL                          = "default";
    public static final String HAJDE_PASSWORD                           = "2D498E77-5D46-4205-BCCC-2558D2EDCA0D";

    public static final int NEARBY_RADIUS                               = 20;
    public static final String ADMIN_EMAIL                              = "hajde@hajde.com";

    public static final String PUSH_BADGE                               = "1";
    public static final String PUSH_SOUND                               = "chime";

    public static final String LANG_ENG                                 = "English";
    public static final String LANG_ALB                                 = "Albanian";

    public static final String COUNTRY_ALBANIA                          = "Albania";
    public static final String COUNTRY_BOSNIA_HEREZE                    = "Bosnia and Herzegovina";
    public static final String COUNTRY_KOSOVO                           = "Kosovo";
    public static final String COUNTRY_MACEDONIA                        = "Macedonia (FYROM)";
    public static final String COUNTRY_MONTENEGRO                       = "Montenegro";
    public static final String COUNTRY_SERBIA                           = "Serbia";
    public static final String COUNTRY_SWISS                            = "Switzerland";
    public static final String COUNTRY_TURKEY                           = "Turkey";
    public static final String COUNTRY_OTHERS                           = "Others";

    public static final String CURRENCY_ALBANIA                         = "Lek";
    public static final String CURRENCY_BOSNIA_HEREZE                   = "KM";
    public static final String CURRENCY_KOSOVO                          = "€";
    public static final String CURRENCY_MACEDONIA                       = "MKD";
    public static final String CURRENCY_MONTENEGRO                      = "€";
    public static final String CURRENCY_SERBIA                          = "DIN";
    public static final String CURRENCY_SWISS                           = "CHF";
    public static final String CURRENCY_TURKEY                          = "TL";
    public static final String CURRENCY_OTHERS                          = "$";

    public static final String SUPPORT_EMAIL                            = "support@hajdeapp.com";

    /*
	 * Local DB Info.
	 */

    public static final String LOCAL_DB_NAME                            = "azizinetworkhajde.db";
    public static final String LOCAL_TABLE_USER                         = "hajde_user_table";

    public static final String LOCAL_FIELD_USER_ID                      = "userID";
    public static final String LOCAL_FIELD_DEVICE_UUID                  = "deviceUUID";
    public static final String LOCAL_FIELD_PASSWORD                     = "password";
    public static final String LOCAL_FIELD_SIGNUP                       = "signup";
    public static final String LOCAL_FIELD_SPENT_TIME                   = "spentTime";
    public static final String LOCAL_FIELD_POST_COUNT                   = "postCount";
    public static final String LOCAL_FIELD_COMMENT_COUNT                = "commentCount";
    public static final String LOCAL_FIELD_VOTE_COUNT                   = "voteCount";
    public static final String LOCAL_FIELD_KARMA_SCORE                  = "karmaScore";
    public static final String LOCAL_FIELD_LOGIN_COUNT                  = "loginCount";
    public static final String LOCAL_FIELD_LAST_LOGIN                   = "lastLogin";
    public static final String LOCAL_FIELD_LANGUAGE                     = "language";
    public static final String LOCAL_FIELD_COUNTRY                      = "country";

    /*
        Backendless key
     */

    public static final String BACKEND_URL_IMAGE                        = "imgBackground";
    public static final String BACKEND_URL_AUDIO                        = "audio";
    public static final String BACKEND_URL_OFFER                        = "offerImages";

    public static final String KEY_OBJECT_ID                            = "objectId";
    public static final String KEY_DEVICE_UUID                          = "deviceUUID";
    public static final String KEY_PASSWORD                             = "password";
    public static final String KEY_SPENT_TIME                           = "spentTime";
    public static final String KEY_KARMA_SCORE                          = "karmaScore";
    public static final String KEY_POST_COUNT                           = "postCount";
    public static final String KEY_COMMENT_COUNT                        = "commentCount";
    public static final String KEY_VOTE_COUNT                           = "voteCount";
    public static final String KEY_LAST_LOGIN                           = "lastLogin";

    public static final String KEY_USER_ID                              = "userID";
    public static final String KEY_TYPE                                 = "type";
    public static final String KEY_LATITUDE                             = "latitude";
    public static final String KEY_LONGITUDE                            = "longitude";
    public static final String KEY_POST_ID                              = "postID";
    public static final String KEY_LIKE_TYPE                            = "likeType";
    public static final String KEY_COMMENT_TYPE                         = "commentType";

    public static final String KEY_SHOP_ID                              = "shopID";

}
