package com.totsp.crossword.firstrun;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.totsp.crossword.BrowseActivity;
import com.totsp.crossword.gmail.GMConstants;
import com.totsp.crossword.shortyz.ShortyzApplication;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rcooper on 6/27/15.
 */
public class FirstrunActivity extends AppIntro {

    private static final Logger LOGGER = Logger.getLogger(FirstrunActivity.class.getCanonicalName());


    protected String mDebugTag = "FirstrunActivity";
    protected boolean mDebugLog = false;


    GmailListener gmailListener;
    Boolean playServicesAvailable;
    private SparseArray<PermissionCallback> requestedPermissions = new SparseArray<>();

    @Override
    public void init(Bundle bundle) {
        addSlide(new Slide1(), this.getApplicationContext());
        if(isGooglePlayServicesAvailable()){
            addSlide(new PlayGames(), this.getApplicationContext());

        }
        addSlide(new NYTimes(), this.getApplicationContext());
        if(isGooglePlayServicesAvailable()){
            addSlide(new Gmail(), this.getApplicationContext());
        }
        setBarColor(Color.argb(100, 0, 0, 0));
    }

    @Override
    public void onSkipPressed() {
        LOGGER.warning("xxxxxxxxxxxxxxxxxxxxx");
        ((ShortyzApplication) getApplication()).getSettings().edit().putBoolean("didFirstRun", true).apply();
        Intent i = new Intent(Intent.ACTION_VIEW, null, this, BrowseActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onDonePressed() {
        ((ShortyzApplication) getApplication()).getSettings().edit().putBoolean("didFirstRun", true).apply();
        Intent i = new Intent(Intent.ACTION_VIEW, null, this, BrowseActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        switch(request) {
            case GMConstants.REQUEST_GOOGLE_PLAY_SERVICES:
                if (response != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case GMConstants.REQUEST_ACCOUNT_PICKER:
                if (response == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        final ShortyzApplication application = ((ShortyzApplication) getApplication());
                        application.getCredential().setSelectedAccountName(accountName);
                        SharedPreferences settings = application.getSettings();
                        settings.edit()
                                .putString(GMConstants.PREF_ACCOUNT_NAME, accountName)
                                .apply();
                        application.updateCredential(settings);
                        //
                        // You have to force the OAuth grant screen up the first time by calling
                        // getToken() on the credential. You can't do this from the UI thread
                        // so we do a little thread dance here to kick it off.
                        //
                        Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        application.getCredential().getToken();
                                    } catch (final UserRecoverableAuthException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivityForResult(e.getIntent(), GMConstants.REQUEST_AUTHORIZATION);
                                            }
                                        });
                                    } catch (Exception e) {
                                        LOGGER.log(Level.SEVERE, "WTF", e);
                                    }
                                }
                            };
                        new Thread(r).start();



                        if(gmailListener != null){
                            gmailListener.onGmailCredentialed();
                        }
                    }
                } else if (response == RESULT_CANCELED) {
                    Toast.makeText(this, "Account unspecified.", Toast.LENGTH_SHORT).show();
                }
                break;
            case GMConstants.REQUEST_AUTHORIZATION:
                if (response != RESULT_OK) {
                    chooseAccount();
                }
                break;
            default:

        }
    }

    private boolean isGooglePlayServicesAvailable() {
        if(this.playServicesAvailable != null){
            return this.playServicesAvailable;
        }
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return this.playServicesAvailable = Boolean.FALSE;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return this.playServicesAvailable = Boolean.FALSE;
        }
        return this.playServicesAvailable = Boolean.TRUE;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        FirstrunActivity.this,
                        GMConstants.REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    public void chooseAccount() {
        startActivityForResult(
                ((ShortyzApplication) getApplication()).getCredential().newChooseAccountIntent(), GMConstants.REQUEST_ACCOUNT_PICKER);
    }




    interface GmailListener {
        void onGmailCredentialed();
    }


    public void requestPermission(int requestCode, String permission, PermissionCallback callback){
        ActivityCompat.requestPermissions(this,
                new String[]{permission},
                requestCode);
        this.requestedPermissions.put(requestCode, callback);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        PermissionCallback callback = this.requestedPermissions.get(requestCode);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           callback.success();
        } else {
            callback.fail();
        }
    }

    interface PermissionCallback {
        void success();
        void fail();
    }
}
