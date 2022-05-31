package com.example.smsread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class GLOBAL {

    public static final String GET_USER_PHONE_NUMBER = getUserPhoneNumber();

    public static String GetCalendarDate(String pattern){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String stringRepresentation = sdf.format(calendar.getTime());

        return stringRepresentation;
    }

    public static String GetCalendarDate(String pattern, long timeInMillis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String stringRepresentation = sdf.format(calendar.getTime());

        return stringRepresentation;
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    private static String getUserPhoneNumber(){
        String invalidPhoneNumber = "NO_NUMBER";
        String phoneID = null;

        TelephonyManager tMgr = (TelephonyManager)MainActivity.CONTEXT.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            phoneID = tMgr.getImei() + "--" + tMgr.getMeid();
        }
        else {
            phoneID = tMgr.getSubscriberId();
        }

        invalidPhoneNumber += "_" + (phoneID == null ? "NO_ID" : phoneID);

         return tMgr.getLine1Number() == null ? invalidPhoneNumber : tMgr.getLine1Number().length() < 2 ? invalidPhoneNumber :tMgr.getLine1Number();
    }

    public static File WriteToTextFile(String fileName, Object data) {

        File file = new File(Environment.getExternalStorageDirectory(), fileName);

        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(file);
            printWriter.println(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null)
                printWriter.close();
        }

        return file;
    }
}
