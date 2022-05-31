package com.example.smsread;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VoiceRecorder {

    private MediaRecorder mediaRecorder = null;

    private String filePath;

    public boolean IsPlaying = false;

    public VoiceRecorder() {

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "VoiceRecords");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        filePath = folder.getPath();
        String phoneNumber = PhoneStateReceiver.GetIncomingPhoneNumber() == null ? " UNKNOWN" : " " + PhoneStateReceiver.GetIncomingPhoneNumber();
        filePath += "/audio--" + getCurrentTime() + phoneNumber + ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(filePath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            IsPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentTime() {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy--HH-mm-ss");
        Date date = new Date();

        return dateFormat.format(date);
    }

    public void StopRecorder() {
        if (mediaRecorder == null)
            return;

        try {
            mediaRecorder.stop();
            //mediaRecorder.reset();
            IsPlaying = false;
        } catch (Exception e) {
            Log.e("VOICE STOP", e.getMessage());
        }

    }

    public void Reset() {
        if (mediaRecorder == null)
            return;

        mediaRecorder.reset();
    }

    public void Release() {
        if (mediaRecorder == null)
            return;

        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
        IsPlaying = false;
    }
}
