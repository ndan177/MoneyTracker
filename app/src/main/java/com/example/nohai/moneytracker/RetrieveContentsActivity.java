package com.example.nohai.moneytracker;


import android.util.Log;


import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.tasks.Task;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;

public class RetrieveContentsActivity extends DriveActivity {
    private static final String TAG = "RetrieveContents";


    @Override
    protected void onDriveClientReady() {
        pickTextFile()
                .addOnSuccessListener(this,
                        driveId -> retrieveContents(driveId.asDriveFile()))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "No file selected", e);
                    showMessage("file not selected");
                    finish();
                });
    }

    private void retrieveContents(DriveFile file) {
        // [START open_file]
        Task<DriveContents> openFileTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);
        // [END open_file]
        // [START read_contents]
        openFileTask
                .continueWithTask(task -> {
                    DriveContents contents = task.getResult();
                    // Process contents...
                    // [START_EXCLUDE]
                    // [START read_as_string]
                    InputStream input = contents.getInputStream();

                    try {
                        File filedb = new File(getDatabasePath("Database").toString());
                        OutputStream output = new FileOutputStream(filedb);
                        try {
                            try {
                                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                int read;

                                while ((read = input.read(buffer)) != -1) {
                                    output.write(buffer, 0, read);
                                }
                                output.flush();
                            } finally {
                                output.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // [END read_as_string]
                    // [END_EXCLUDE]
                    // [START discard_contents]
                    Task<Void> discardTask = getDriveResourceClient().discardContents(contents);
                    // [END discard_contents]
                    return discardTask;
                })        .addOnSuccessListener(this,
                driveFile -> {
                    showMessage("read succesed");
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    // [START_EXCLUDE]
                    Log.e(TAG, "Unable to read contents", e);
                    showMessage("read failed");
                    finish();
                    // [END_EXCLUDE]
                });
        // [END read_contents]
    }

}
