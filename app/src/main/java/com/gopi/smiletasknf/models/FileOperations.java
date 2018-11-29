package com.gopi.smiletasknf.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tohamy on 4/9/16.
 */
public class FileOperations {
    public final static String MAIN_APP_FOLDER_NAME = "Smile";

    public static void saveImageToFileSystem(final Context context
            , byte[] data, final AfterImageTaken callback) {
        final File imageFile = createImageFile(context, null);
        final byte[] mData = data;
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                    fileOutputStream.write(mData);
                    fileOutputStream.close();
                } catch (Exception error) {
                    error.printStackTrace();
                }
                addToGallery(context, imageFile.getAbsolutePath());
                return null;
            }

            /**
             * <p>Runs on the UI thread after {@link #doInBackground}. The
             * specified result is the value returned by {@link #doInBackground}.</p>
             * <p/>
             * <p>This method won't be invoked if the task was cancelled.</p>
             *
             * @param aVoid The result of the operation computed by {@link #doInBackground}.
             * @see #onPreExecute
             * @see #doInBackground
             * @see #onCancelled(Object)
             */
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                callback.afterImageTaken(imageFile.getAbsolutePath());
            }
        }.execute();
    }
    public static File createImageFile(Context context, String fileName){
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName =  (fileName != null) ? fileName :"Smile" + timeStamp + ".jpg";
        File storageDir = getOrCreateFolder(context);
        File image = new File(storageDir, imageFileName);
        return image;
    }

    // Creating Directories
    public static File makeDirectory(Context context, File parent,
                                     String directoryName) {
        File root = new File(parent, directoryName);
        if (!root.exists()) {
//          "Root does not exist"
            root.mkdirs();
//              directoryName + " is created in the " + parent.getName()+ " directory"
        } else {
//            " already exists in the " + parent.getName() + " directory"
        }
        return root;
    }
    public static File getOrCreateFolder(Context context) {
        return makeDirectory(
                context,
                Environment
                        .getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES),
                MAIN_APP_FOLDER_NAME);
    }

    public static void addToGallery(Context context, String imageAbsolutePath) {
        scanForMediaInDirectory(context, imageAbsolutePath);
    }

    public static void scanForMediaInDirectory(Context context, String imageAbsolutePath){
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        String currentPath = imageAbsolutePath;
        File file = new File(currentPath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


    public static Bitmap rotateBitmapAccordingly(Bitmap bitmap, String fileName){
        int orientation = 9999;
        try {
            ExifInterface exif = new ExifInterface(fileName);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        } catch(IOException e){
            e.printStackTrace();
        }
        if(orientation != ExifInterface.ORIENTATION_NORMAL) {
            int dgreesToRotate = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    dgreesToRotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    dgreesToRotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    dgreesToRotate = 270;
                    break;
            }
            return rotateBitmap(bitmap, dgreesToRotate);
        } else{
            return null;
        }

    }
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
//		int screenWidth = getResources().getDisplayMetrics().widthPixels;
//		int screenHeight = getResources().getDisplayMetrics().heightPixels;
        if(source != null) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);

//        float px = this.viewWidth/2;
//        float py = this.viewHeight/2;
//        matrix.postTranslate(-bitmap.getWidth()/2, -bitmap.getHeight()/2);
//        matrix.postRotate(rotation);
//        matrix.postTranslate(px, py);

            return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                    source.getHeight(), matrix, true);
        } else{
            return null;
        }
    }

    public interface AfterImageTaken{
        void afterImageTaken(String imagePath);
    }

}
