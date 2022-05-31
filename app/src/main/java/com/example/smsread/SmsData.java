package com.example.smsread;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.example.smsread.MainActivity.SMS;

public class SmsData {

    private Activity activity;
    private Cursor smsCursor;
    private Thread thread;

    private String _id;
    private String _address;
    private String _msg;
    private String _readState; //"0" - not read, 1 read
    private String _time;
    private String _folderName;

    public SmsData(Activity activity) {
        this.activity = activity;
    }

    public String getId() {
        return _id;
    }

    public String getNumber() {
        return _address;
    }

    public String getMsg() {
        return _msg;
    }

    public String getReadState() {
        return _readState;
    }

    public String getTime() {
        return _time;
    }

    public String getFolderName() {
        return _folderName;
    }


    public void setId(String id) {
        _id = id;
    }

    public void setAddress(String address) {
        _address = address;
    }

    public void setMsg(String msg) {
        _msg = msg;
    }

    public void setReadState(String readState) {
        _readState = readState;
    }

    public void setTime(String time) {
        _time = time;
    }

    public void setFolderName(String folderName) {
        _folderName = folderName;
    }

    public List<SmsData> getAllSms() {
        if (activity == null)
            return null;

        final List<SmsData> listSMS = new ArrayList<>();

        thread = new Thread(() -> {
            Uri message = Uri.parse(SMS);
            ContentResolver cr = activity.getContentResolver();

            smsCursor = cr.query(message, null, null, null, null);
            activity.startManagingCursor(smsCursor);
            int totalSMS = smsCursor.getCount();

            if (smsCursor.moveToFirst()) {
                for (int i = 0; i < totalSMS; i++) {

                    SmsData tempSmsData = catchSmsData(smsCursor);

                    if (smsCursor.getString(smsCursor.getColumnIndexOrThrow("type")).contains("1")) {
                        tempSmsData.setFolderName("INBOX");
                    } else {
                        tempSmsData.setFolderName("SENT");
                    }

                    listSMS.add(tempSmsData);
                    smsCursor.moveToNext();
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return listSMS;
    }

    public static StringBuilder joinList(List<SmsData> sms) {
        StringBuilder sBuilder = new StringBuilder();

        Calendar calendar = Calendar.getInstance();

        for (SmsData temp : sms) {
            calendar.setTimeInMillis(Long.parseLong(temp.getTime()));

            sBuilder.append(temp);
            sBuilder.append("\n--------------------------\n");
        }
        return sBuilder;
    }

    private SmsData catchSmsData(Cursor cursor) {
        SmsData objSms = new SmsData(activity);

        objSms.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
        objSms.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
        objSms.setMsg(cursor.getString(cursor.getColumnIndexOrThrow("body")));
        objSms.setReadState(cursor.getString(cursor.getColumnIndex("read")));
        objSms.setTime(cursor.getString(cursor.getColumnIndexOrThrow("date")));

        return objSms;
    }

    public void CloseCursor() {
        if (smsCursor != null)
            smsCursor.close();
    }

    @Override
    public String toString() {
        String dateSmsWasReceivedSent = GLOBAL.GetCalendarDate("dd-MM-yyyy, HH:mm:ss", Long.parseLong(getTime()));

        ContactsData contactsData = new ContactsData();

        return "FOLDER: " + this.getFolderName() + "\nNumber: " + this.getNumber() + " " + contactsData.getContactName(this.getNumber())
                + ", Date: " + dateSmsWasReceivedSent + ", Body: " + this.getMsg();
    }

    private class ContactsData {

        public String getContactName(final String phoneNumber) {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

            String contactName = " *NO NAME* ";
            Cursor tempCursor = activity.getContentResolver().query(uri, projection, null, null, null);

            if (tempCursor != null) {
                if (tempCursor.moveToFirst()) {
                    contactName = tempCursor.getString(0);
                }
                tempCursor.close();
            }

            return contactName;
        }
    }
}