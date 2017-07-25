package com.sahakarmart.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sahakarmart.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = HomeActivity.class.getSimpleName();
    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/sahakarmart";
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private static final Integer CALL = 0x2;
    private static final Integer READ_EXTERNAL = 0x3;
    private static final Integer CAMERA_ = 0x4;
    LinearLayout llFirstF,llFirstS,llSecondF,llSecondS;
    ArrayList<Uri> arrayUri = new ArrayList<Uri>();
    String  attachmentFile;
    Uri URI = null;

    int columnIndex;

    TextView txtHowToOrder,txtContact;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        toolbar = (Toolbar)findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);

        llFirstF = (LinearLayout)findViewById(R.id.ll_first_first);
        llFirstS = (LinearLayout)findViewById(R.id.ll_first_second);
        llSecondF = (LinearLayout)findViewById(R.id.ll_second_first);
        llSecondS = (LinearLayout)findViewById(R.id.ll_second_second);



        txtHowToOrder = (TextView)findViewById(R.id.txtHowtoOrder);
        txtContact = (TextView)findViewById(R.id.txtContact);



        llFirstF.setOnClickListener(this);
        llFirstS.setOnClickListener(this);
        llSecondF.setOnClickListener(this);
        llSecondS.setOnClickListener(this);


        txtHowToOrder.setOnClickListener(this);
        txtContact.setOnClickListener(this);


    }



    @Override
    public void onClick(View v) {

        int id = v.getId();


        switch (id)
        {
            case R.id.ll_first_first :

             //showPictureDialog();

                Intent upload = new Intent(HomeActivity.this,UploadPrecription.class);
                startActivity(upload);

             break;



            case R.id.ll_first_second :


                call();

                break;


            case R.id.ll_second_first :

                Intent test = new Intent(HomeActivity.this,Web.class);
                test.putExtra("Web","test");
                startActivity(test);


                break;


            case R.id.ll_second_second :
                Intent web = new Intent(HomeActivity.this,Web.class);
                web.putExtra("Web","web");
                startActivity(web);

                break;

            case  R.id.txtContact :

                Intent contact = new Intent(HomeActivity.this,Web.class);
                contact.putExtra("Web","contact");
                startActivity(contact);

                break;

            case  R.id.txtHowtoOrder :

                Intent order = new Intent(HomeActivity.this,Web.class);
                order.putExtra("Web","order");
                startActivity(order);

                break;


        }

    }



    private void call() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            askForPermission(Manifest.permission.CALL_PHONE, CALL);
        } else {


            String phone = "09922845588";
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(intent);
        }
    }


    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };

        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallary() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXTERNAL);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(galleryIntent, GALLERY);
        }


    }


    private void takePhotoFromCamera() {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            askForPermission(Manifest.permission.CAMERA, CAMERA_);
        } else
            {

            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);


        }

    }



    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{permission}, requestCode);
            }
        }

        // permission already granted
        else {


            if(permission.equals(Manifest.permission.CAMERA))
            {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA);


            }

            if(permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE))
            {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, GALLERY);
            }

            if(permission.equals(Manifest.permission.CALL_PHONE))
            {
                String phone = "09922845588";
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                startActivity(intent);
            }

            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {

                    Uri selectedImage = data.getData();
                Log.e(TAG,"Gallery IMAGE :"+selectedImage.toString());
                    arrayUri.add(selectedImage);
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    attachmentFile = cursor.getString(columnIndex);
                    Log.e("Attachment Path:", attachmentFile);
                    URI = Uri.parse("file://" + attachmentFile);
                    cursor.close();
                    Toast.makeText(HomeActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();


                Intent emailIntent = new Intent();
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Sahakar Mart Prescription");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sahakarmartcoop@gmail.com"});

                    emailIntent.setAction(Intent.ACTION_SEND);
                    emailIntent.putExtra(Intent.EXTRA_STREAM, arrayUri.get(0));
                emailIntent.setType("image/* video/*");


                startActivity(Intent.createChooser(emailIntent,
                        "Sending email..."));

            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            saveImage(thumbnail);
           /* Uri selectedImage = data.getData();
            Log.e(TAG,"Camera IMAGE :"+selectedImage.toStr/home/ubuntu/Downloads/Untitledqweqewqe.pnging());
            //imageview.setImageBitmap(thumbnail);*/

            Toast.makeText(HomeActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
            
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
               // GALLERY
                case 1:
                    //  askForGPS();
                    break;
                //Call
                case 2:

                    String phone = "09922845588";
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                    startActivity(intent);

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    }
                    break;

                //Read External Storage
                case 3:


                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(galleryIntent, GALLERY);
                    break;
                //Camera
                case 4:
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA);
                    break;


            }

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }

    }
}
