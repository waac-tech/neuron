package com.jo.neuron.globals;

import android.app.ProgressDialog;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GlobalConstants {

    public static ProgressDialog progressDialog;
    public static final int REQUEST_CODE_SELECT_FILE = 999;
    public static final int REQUEST_CODE_PERMISSION_GRANT = 1000;

    public static List<File> files = new ArrayList<>();

    public static String requestedFilePath = "";

    public static ProgressDialog copyingDialog = null;


    public static String getBasePath() {
        String path = Environment.getExternalStorageDirectory() + File.separator + ".neuron";
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return path;
    }
}
