package com.gopi.smiletasknf.models;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class MediaStoreOperations {

    public static Cursor getGalleryAlbumsCursor(Context context){
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final String[] PROJECTION_BUCKET = {
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Thumbnails.DATA
        };

        String ORDER_BY = MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC";
        String GROUP_BY = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME +"= '"
                + ("Smile") + "'";
        return context.getContentResolver().query(
                images, PROJECTION_BUCKET, GROUP_BY, null, ORDER_BY);
    }

    public static ArrayList<Item> getSmileAlbumFromCursor(Cursor cur){
        ArrayList<Item> smileImages = new ArrayList<>();
        if (cur.moveToFirst()) {
            String date;
            String data;
            int id;
            String name;
            int dateColumn = cur.getColumnIndex(
                    MediaStore.Images.ImageColumns.DATE_TAKEN);
            int idColumn = cur.getColumnIndex(
                    MediaStore.Images.ImageColumns.BUCKET_ID);
            int dataColumn = cur.getColumnIndex(
                    MediaStore.Images.Thumbnails.DATA);
            int nameColumn = cur.getColumnIndex(
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
            int orientationColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.ORIENTATION);

            do {
                // Get the field values
                date = cur.getString(dateColumn);
                id = cur.getInt(idColumn);
                data = cur.getString(dataColumn);
                name = cur.getString(nameColumn);
                // Do something with the values.
                Log.i("ListingImages", " name = " + ""
                        + "  date_taken = " + date);
                String [] path = data.split("/");
//                String name = path[path.length-1];
                String [] cleanName = name.split("\\.");
                String imageName = cleanName[0];
                Item item = new Item();
                item.setImageUrl(data);
                item.setName(name);
                item.setOrientation(orientationColumn);
                smileImages.add(item);
            } while (cur.moveToNext());
            cur.close();
        }
        return smileImages;
    }
}
