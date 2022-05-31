package com.example.smsread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class TestVoiceListener extends BroadcastReceiver {

    VoiceRecorder voiceRecorder;

    public TestVoiceListener() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        /*String state = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);

        if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            Toast.makeText(context,"IN CALL", Toast.LENGTH_LONG).show();

            voiceRecorder = new VoiceRecorder(context);
            voiceRecorder.StartRecorder();
        }
        else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
            Toast.makeText(context,"CALL ENDED", Toast.LENGTH_LONG).show();

            if(voiceRecorder.IsPlaying)
                voiceRecorder.StopRecorder();
        }*/
    }
}
