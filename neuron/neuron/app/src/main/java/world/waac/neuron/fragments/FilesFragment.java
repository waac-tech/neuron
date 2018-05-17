package world.waac.neuron.fragments;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jo.neuron.R;
import world.waac.neuron.activities.MainActivity;
import world.waac.neuron.adapters.FilesRVAdapter;
import world.waac.neuron.globals.GlobalConstants;
import world.waac.neuron.globals.NotificationToast;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static world.waac.neuron.globals.GlobalConstants.REQUEST_CODE_SELECT_FILE;
import static world.waac.neuron.globals.GlobalConstants.getBasePath;

public class FilesFragment extends Fragment {


    FilesRVAdapter adapter = null;

    @BindView(R.id.rv_files)
    RecyclerView rvFiles;

    public FilesFragment() {
        // Required empty public constructor
    }

    public static FilesFragment newInstance(MainActivity mainActivity) {
        FilesFragment fragment = new FilesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_files, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // do all initial stuff here.


        adapter = new FilesRVAdapter(this.getContext(), GlobalConstants.files);

        rvFiles.setAdapter(adapter);
        rvFiles.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvFiles.setItemAnimator(new DefaultItemAnimator());

        refreshFileList();
    }

    private List<File> getListFiles2(File parentDir) {
        List<File> inFiles = new ArrayList<>();
        Queue<File> files = new LinkedList<>();
        files.addAll(Arrays.asList(parentDir.listFiles()));
        while (!files.isEmpty()) {
            File file = files.remove();
            if (file.isDirectory()) {
                files.addAll(Arrays.asList(file.listFiles()));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

    public void refreshFileList() {
        GlobalConstants.files.clear();
        GlobalConstants.files.addAll(getListFiles2(new File(GlobalConstants.getBasePath())));
        adapter.notifyDataSetChanged();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isVideoUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private void copyFileStream(File dest, Uri uri, Context context) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }


    @OnClick(R.id.fab_add_files)
    public void onClickFabAddFiles() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent = Intent.createChooser(intent, "Choose a file");
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == REQUEST_CODE_SELECT_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;

            if (resultData != null) {
                uri = resultData.getData();

                String mimeType = this.getContext().getContentResolver().getType(uri);

                String filename;


                if (mimeType == null) {
                    String path = getPath(this.getContext(), uri);
                    if (path == null) {
                        filename = FilenameUtils.getName(uri.toString());
                    } else {
                        File file = new File(path);
                        filename = file.getName();
                    }
                } else {
                    Uri returnUri = resultData.getData();
                    Cursor returnCursor = this.getContext().getContentResolver().query(returnUri, null, null, null, null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    filename = returnCursor.getString(nameIndex);
                    String size = Long.toString(returnCursor.getLong(sizeIndex));
                }

                Log.d("fefefefe", filename);

                String destPath = getBasePath() + File.separator + filename;
                File destFile = new File(destPath);

                String msg = "copy file success";

                try {
                    copyFileStream(destFile, uri, this.getContext());

                    refreshFileList();
                } catch (Exception e) {
                    e.printStackTrace();
                    msg = "copy file failed";

                }
                NotificationToast.showToast(FilesFragment.this.getContext(), msg);



            }


        }

    }
}
