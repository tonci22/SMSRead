package com.example.smsread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class PhoneStateReceiver extends BroadcastReceiver {
    public TelephonyManager TelephonyData = null;

    private static String incomingPhoneNumber = null;
    private Context context;
    private CustomPhoneStateListener customPhoneStateListener = new CustomPhoneStateListener();
    private VoiceRecorder voiceRecorder;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;


        TelephonyData = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyData.listen(customPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
        public static String GetIncomingPhoneNumber (){
            return incomingPhoneNumber;
    }

    public class CustomPhoneStateListener extends PhoneStateListener {

        public CustomPhoneStateListener() {
            super();
        }

        @Override
        public void onCallStateChanged(int state, String phoneNumber) {


            super.onCallStateChanged(state, phoneNumber);
            incomingPhoneNumber = phoneNumber;

            if(voiceRecorder == null)
                voiceRecorder = new VoiceRecorder();


            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK: //in call
                    Toast.makeText(context,"In call",Toast.LENGTH_LONG).show();
                break;
                case TelephonyManager.CALL_STATE_IDLE: //no call
                    if (voiceRecorder.IsPlaying) {
                        voiceRecorder.Release();
                    }
                    Toast.makeText(context,"Idle",Toast.LENGTH_LONG).show();
                    break;

                default:
                    Toast.makeText(context, "default", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
