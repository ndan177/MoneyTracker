package com.example.nohai.moneytracker;

import android.util.Log;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * An activity to illustrate how to create a file.
 */
public class CreateFileActivity extends DriveActivity {
    private static final String TAG = "CreateFileActivity";

    @Override
    protected void onDriveClientReady() {
        createFile();
    }

    private void createFile() {
        // [START create_file]
        final Task<DriveFolder> rootFolderTask = getDriveResourceClient().getRootFolder();
        final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = rootFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();

                    OutputStream outputStream = contents.getOutputStream();

                    final String inFileName = getDatabasePath("Database").toString();
                    File dbFile = new File(inFileName);

                    FileInputStream inputStream = null;
                    try {
                        inputStream = new FileInputStream(dbFile);
                    } catch (FileNotFoundException e) {

                        e.printStackTrace();
                    }

                    byte[] buf = new byte[4 * 1024];
                    int bytesRead;
                    try {
                        if (inputStream != null) {
                            while ((bytesRead = inputStream.read(buf)) > 0) {
                                outputStream.write(buf, 0, bytesRead);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("Database.db")
                            .setMimeType("text/plain")
                            .setStarred(true)
                            .build();

                    return getDriveResourceClient().createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(this,
                        driveFile -> {
                            showMessage(getString(R.string.file_created,
                                    driveFile.getDriveId().encodeToString()));
                            finish();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                    showMessage(getString(R.string.file_create_error));
                    finish();
                });
        // [END create_file]
    }
}
