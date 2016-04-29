package com.example.hasna2.movieapp.Data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hasna2 on 17-Apr-16.
 */


public class SaveAndGetImages {
    final String LOG_TAG=SaveAndGetImages.class.getSimpleName();
    String image_path ;
    ImageView imageView;
    Context context;

    public SaveAndGetImages(Context context, String image_path,ImageView imageView ){
        this.context=context;
        this.image_path=image_path;
        this.imageView=imageView;
    }

    // try to get the image from internal storage if not found  download it from picasso
    public void getImage (final String id){
        Bitmap bitmap= getThumbnail(id);
        if(bitmap==null) {
            Log.v(LOG_TAG,"the image not saved");
            Picasso.with(context).load(image_path).into(imageView, new Callback() {

                @Override
                public void onSuccess() {
                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bitmap1 = drawable.getBitmap();
                    if (saveImageToInternalStorage(bitmap1, id)) Log.v(LOG_TAG, "the image saved successfully");
                }
                @Override
                public void onError() {
                    Log.v(LOG_TAG, "Error downloading image");
                }
            });
        }else {
            imageView.setImageBitmap(bitmap);
            Log.v(LOG_TAG, "the image loaded successfully");
        }
    }

    private boolean saveImageToInternalStorage(Bitmap image,String name) {

        File pictureFile =getOutputMediaFile(name);
        if (pictureFile == null) {
            Log.d(LOG_TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return false;
        }
        try {
            Log.v(LOG_TAG,""+name+".png1");
            FileOutputStream fos = new FileOutputStream(pictureFile);
            if(fos!=null)Log.v(LOG_TAG,""+name+".png2");
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG, "File not found: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error accessing file: " + e.getMessage());
            return false;
        }
    }

    private Bitmap getThumbnail(String name) {
        Bitmap thumbnail = null;
        try {
            File filePath = getOutputMediaFile(name);
            if(filePath==null)Log.v(LOG_TAG,"file is null cant get it");
            FileInputStream fi = new FileInputStream(filePath);
            thumbnail = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {
            Log.e("getThumbnail() on internal storage", ex.getMessage());
        }
        return thumbnail;
    }

    //get directory for the image
    public File getOutputMediaFile(String name){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/Files");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String Stamp =name;
        File mediaFile;
        String mImageName="MI_"+ Stamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

}




