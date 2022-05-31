package com.example.smsread;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//TODO probably change recursion to a tailed recursion in case of a huge amount of files to avoid stackoverflow

public class FilesCollection {

    public static List<File> FileNames = new ArrayList<>();

    public static final String[] IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".gif", ".png", ".tif", ".bmp"};
    public static final String[] VIDEO_EXTENSIONS = {".3gp", ".mp4", ".ts", ".webm", ".mkv", "."};
    public static final String[] AUDIO_EXTENSIONS = {".m4a", ".aac", ".flac", ".mkv", ".wav", ".rtx",
                                                    ".mid", ".gsm", ".xmf", ".rtttl", ".rtx", ".ota", ".imy", ".ogg", ".ts"};

    public static final String[] ALL_EXTENSIONS = FilesCollection.joinMultipleArrays(IMAGE_EXTENSIONS, AUDIO_EXTENSIONS, VIDEO_EXTENSIONS);

    /**
     * Scans all files
     * @param dir current directory
     * @param fileExtension File extension to search
     * @return list of all files
     */
    public static List<File> ScannAllFiles(@NotNull File dir, String ...fileExtension) {

        File[] listFile = dir.listFiles();

        if (listFile == null)
            return new ArrayList<>();

        for (int i = 0; i < listFile.length; i++) {

            if (listFile[i].isDirectory()) {
                ScannAllFiles(listFile[i], fileExtension);
            } else {
                for (int j = 0; j < fileExtension.length; j++) {
                    if (listFile[i].getName().toLowerCase().endsWith(fileExtension[j].toLowerCase())) {
                        FileNames.add(new File(listFile[i].getAbsolutePath()));
                    }
                }
            }
        }
        return FileNames;
    }

    public static List<File> ScannAllFiles(@NotNull File dir, String[] ...fileExtension) {

        File[] listFile = dir.listFiles();

        String[] combinedArrays = joinMultipleArrays(fileExtension);

        if (listFile == null)
            return new ArrayList<>();

        for (int i = 0; i < listFile.length; i++) {

            if (listFile[i].isDirectory()) {
                ScannAllFiles(listFile[i], fileExtension);
            } else {
                for (int j = 0; j < combinedArrays.length; j++) {
                    if (listFile[i].getName().toLowerCase().endsWith(combinedArrays[j].toLowerCase())) {
                        FileNames.add(new File(listFile[i].getAbsolutePath()));
                    }
                }
            }
        }
        return FileNames;
    }

    /**
     * Joins multiple arrays together into one single array
     * @param arrays arrays to join together
     * @return all arrays joined together into one
     */
    private static String[] joinMultipleArrays(String[] ...arrays){
        int combinedArrayLength = 0;

        for(String[] array : arrays){
            combinedArrayLength += array.length;
        }

        String[] result = new String[combinedArrayLength];

        int arrayCounter = 0;
        int dataCounter = 0;

        for (int i = 0; i < combinedArrayLength; i++){
            if(arrays[arrayCounter].length <= dataCounter){
                arrayCounter++;
                dataCounter = 0;
            }
            result[i] = arrays[arrayCounter][dataCounter];
            dataCounter++;
        }

        return result;
    }

    /**
     * creates an array of AbsolutePath() of each file
     * @param separator separator to distinguish each absolute path from one file to other
     * @param files list of files
     * @return array of strings that contain the absolute path of all files
     */
    public static String[] FilesListToAbsolutePathArray(String separator, List<File> files) {

        if(files == null)
            return new String[]{};

        String[] tempFileNames = new String[files.size()];

        for (int i = 0; i < files.size(); i++) {
            tempFileNames[i] = files.get(i).getAbsolutePath();
            if(separator != null){
                if (!separator.isEmpty()) {   //if there is a declared separator we need to insert it into the array for clarity output
                    tempFileNames[i] += separator;
                }
            }
        }

        return tempFileNames;
    }
}
