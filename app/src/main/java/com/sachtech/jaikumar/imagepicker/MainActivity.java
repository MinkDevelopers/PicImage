package com.sachtech.jaikumar.imagepicker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button select_image;
    ImageView selected_image;
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static String IMAGE_DIRECTORY_NAME = "ImagePicker";
    public static final int MEDIA_TYPE_IMAGE = 1;
    RelativeLayout gallery, camera;
    /**
     * Standard activity result: operation canceled.
     */
    public static final int RESULT_CANCELED = 0;
    /**
     * Standard activity result: operation succeeded.
     */
    public static final int RESULT_OK = -1;

    private static int GALLERY_IMAGE_REQUEST_CODE = 25;
     Mpermission mpermission;
    String filepath="";
    Uri fileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mpermission=new Mpermission(this);
        select_image=(Button)findViewById(R.id.image_select);
        selected_image=(ImageView)findViewById(R.id.select_image);
        select_image.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
     if(view.getId()==R.id.image_select)
     {
         if(!mpermission.checkPermissionForCallPhone())
         {
             mpermission.requestPermissionForCallPhone(0);
         }
         else
         {        callOptionDialog();}
     }
    }
    public void callOptionDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Image");
        builder.setMessage("Select source of the image from where you want to pic image.");
        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callGallery();
            }
        });
        builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            callCamera();
            }
        });
        Dialog dialog=builder.create();
        dialog.show();

    }
    public void callGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, GALLERY_IMAGE_REQUEST_CODE);
    }
    // * Creating file uri to store image/video
    // */
    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    public static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }


        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "JaiKumar" + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


    public void callCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (requestCode == GALLERY_IMAGE_REQUEST_CODE) {
                Uri Uri_gallery = data.getData();
                previewGalleryImage(Uri_gallery);
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void previewGalleryImage(Uri Uri_gallery) {
        String[] FILE = {MediaStore.Images.Media.DATA};


        Cursor cursor = getContentResolver().query(Uri_gallery,
                FILE, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(FILE[0]);
        filepath = cursor.getString(columnIndex);
        cursor.close();
        Log.e("ImageFileGALLERY",""+filepath);
        final Bitmap bitmap=compress_bitmap_from_filepath(filepath);
        selected_image.setImageBitmap(bitmap);



    }
    public void previewCapturedImage() {
        try {
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;
            filepath = fileUri.getPath();
            Bitmap bd = compress_bitmap_from_filepath(filepath);
            selected_image.setImageBitmap(bd);
            Log.e("ImageFileCAMERA", filepath);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap compress_bitmap_from_filepath(String filepath) {

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);
        final int REQUIRED_SIZE = 300;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(filepath, options);
        return bm;
    }

}
