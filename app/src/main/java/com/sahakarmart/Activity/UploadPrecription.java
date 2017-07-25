package com.sahakarmart.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sahakarmart.Service.ConnectionDetector;
import com.sahakarmart.R;
import com.sahakarmart.SmtpMail.GMailSender;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class UploadPrecription extends AppCompatActivity {

    private EditText inputName, inputAddress, inputCity, inputPincode, inputContact, inputEmail, inputUpload,inputCoupon,inputComment;
    private Button btnUpload;
    private ProgressDialog progressdialog;
    private ArrayList<String> attachments;
    private Uri URI = null;
    private int GALLERY = 1,CAMERA = 2;
    private static final Integer CAMERA_ = 0x4;
    private static final Integer READ_EXTERNAL = 0x3;
    int columnIndex;
    String  attachmentFile,selectedimage,name,address,city,contact,email,pincode,coupon,comment,tempip,ip;

    ConnectionDetector cd;
    AlertDialog alertDialog = null;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_precription);

        toolbar = (Toolbar)findViewById(R.id.UploadToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputName = (EditText) findViewById(R.id.input_name);
        inputAddress = (EditText) findViewById(R.id.input_address);
        inputCity = (EditText) findViewById(R.id.input_city);
        inputPincode = (EditText) findViewById(R.id.input_pincode);
        inputContact = (EditText) findViewById(R.id.input_contact);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputUpload = (EditText) findViewById(R.id.input_upload);
        inputCoupon= (EditText)findViewById(R.id.input_coupon);
        inputComment = (EditText)findViewById(R.id.input_comment);



        btnUpload = (Button) findViewById(R.id.btn_upload);


        progressdialog = new ProgressDialog(UploadPrecription.this);
        progressdialog.setMessage("Please Wait....");
        progressdialog.setCancelable(false);


        attachments = new ArrayList<String>();


        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        tempip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        // if Wifi connection is off and Cellular Network connection is on then get that Ip address
        if(tempip.equals("0.0.0.0"))
        {
           // Log.e("Net Ip","IPPPPPP : "+getDeviceIPAddress(true));

            ip = getDeviceIPAddress(true);

        }

        // else get Wifi connection Ip Address
        else
        {
         //   Log.e("Wifi Ip","IPPPPPP : "+ip);

            ip = tempip;
        }


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                name = inputName.getText().toString();
                address =inputAddress.getText().toString();
                city = inputCity.getText().toString();
                pincode = inputPincode.getText().toString();
                email = inputEmail.getText().toString();
                contact = inputContact.getText().toString();
                coupon = inputCoupon.getText().toString();
                comment = inputComment.getText().toString();





                if(cd.isConnectingToInternet(getApplicationContext()))
                {





                    if((!TextUtils.isEmpty(inputName.getText().toString())) && (!TextUtils.isEmpty(inputAddress.getText().toString()))   && (!TextUtils.isEmpty(inputCity.getText().toString()))  && (!TextUtils.isEmpty(inputPincode.getText().toString()))   && (!TextUtils.isEmpty(inputContact.getText().toString())))
                    {

                        if(!TextUtils.isEmpty(inputUpload.getText().toString()))
                        {

                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String strDate = sdf.format(c.getTime());

                            String devid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);



                            final StringBuilder body = new StringBuilder();
                            body.append("Dear Admin,");
                            body.append(System.getProperty("line.separator"));
                            body.append("Please find the mobile prescription from -");
                            body.append(System.getProperty("line.separator"));
                            body.append("Name :" + name);
                            body.append(System.getProperty("line.separator"));
                            body.append("Address :" + address);
                            body.append(System.getProperty("line.separator"));
                            body.append("City :" + city);
                            body.append(System.getProperty("line.separator"));
                            body.append("Pincode :" + pincode);
                            body.append(System.getProperty("line.separator"));
                            body.append("Email :" + email);
                            body.append(System.getProperty("line.separator"));
                            body.append("Mobile Number  :" + contact);
                            body.append(System.getProperty("line.separator"));
                            body.append("Coupon Code :"+coupon);
                            body.append(System.getProperty("line.separator"));
                            body.append("Comment :"+comment);
                            body.append(System.getProperty("line.separator"));
                            body.append("Date & Time : "+strDate);
                            body.append(System.getProperty("line.separator"));
                            body.append("Device Id :"+devid);
                            body.append(System.getProperty("line.separator"));
                            body.append("IP Address :"+ip);



                            new SendMailAsync(body.toString(),attachments).execute();

                        }

                        else
                        {
                            Toast.makeText(UploadPrecription.this, "Please upload prescription", Toast.LENGTH_SHORT).show();
                        }

                    }


                    else
                    {
                        Toast.makeText(UploadPrecription.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                    }




                }

                else
                {
                    Toast.makeText(UploadPrecription.this, "Please turn on mobile data or wifi", Toast.LENGTH_SHORT).show();
                }




            }
        });



        inputUpload.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(MotionEvent.ACTION_UP == event.getAction()) {
                   // choosePhotoFromGallary();

                    //takePhotoFromCamera();

                    showPictureDialog();
                }

                return true;
            }
        });



    }


    private class SendMailAsync extends AsyncTask<Void, Void, String> {


        private String mailBody;
        private ArrayList<String> mailAttach;


        private SendMailAsync(String body, ArrayList<String> attachList) {

            this.mailBody = body;
            this.mailAttach = attachList;

        }


        @Override
        protected void onPreExecute() {

            progressdialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {



            // sahakarmartcoop@gmail.com
            // smart@2017
            GMailSender sender = new GMailSender("sahakarmartcoop@gmail.com",
                    "smart@2017");


            try {
                sender.sendMailWithAttach("Mobile Prescription"+" - "+name+" "+"-"+" "+contact, mailBody,
                        "sahakarmartcoop@gmail.com", "sahakarmartcoop@gmail.com", mailAttach);

                return "success";
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;

        }


        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if (result.equals("success")) {


                attachments.clear();
                inputName.setText("");
                inputContact.setText("");
                inputEmail.setText("");
                inputPincode.setText("");
                inputAddress.setText("");
                inputCity.setText("");
                inputUpload.setText("");
                inputCoupon.setText("");
                inputComment.setText("");
                inputUpload.setEnabled(true);

                progressdialog.dismiss();


                Toast.makeText(UploadPrecription.this,"Prescription Uploaded Successfully \n  Sahakar Mart Team Will Contact You Soon.\n Thanks!",Toast.LENGTH_LONG).show();




            } else {


                attachments.clear();
                inputUpload.setEnabled(true);

                progressdialog.dismiss();
                Toast.makeText(UploadPrecription.this,"Sorry,Please Try Later",Toast.LENGTH_LONG).show();


            }


        }


    }

    private void showPictureDialog(){


        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);
        //final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEditText);
        //final ImageView subImageView = (ImageView)subView.findViewById(R.id.image);


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(subView);
        builder.setNegativeButton("Cancel", null);


        LinearLayout LinearContactGallery, LinearContactCamera;

        LinearContactGallery = (LinearLayout) subView.findViewById(R.id.GalleryLayout);
        LinearContactCamera = (LinearLayout) subView.findViewById(R.id.CameraLayout);


        alertDialog = builder.create();


        //alertDialog.setButton();
        alertDialog.show();
        LinearContactGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                choosePhotoFromGallary();
            }
        });

        LinearContactCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                takePhotoFromCamera();

            }
        });

       /* alertDialog = builder.create();


        //alertDialog.setButton();
        alertDialog.show();*/


        /*AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
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
        pictureDialog.show();*/
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



    public void choosePhotoFromGallary() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(galleryIntent, GALLERY);
        }


    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(UploadPrecription.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(UploadPrecription.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(UploadPrecription.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(UploadPrecription.this, new String[]{permission}, requestCode);
            }
        }

        // permission already granted
        else {


            if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE))
            {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, GALLERY);
            }

            if(permission.equals(Manifest.permission.CAMERA))
            {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA);


            }


            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY)
        {
            if (data != null) {

                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};


                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                attachmentFile = cursor.getString(columnIndex);
                Log.e("Attachment Path:", attachmentFile);

                selectedimage = attachmentFile;

                // attachList.add(selectedimage);


                URI = Uri.parse("file://" + attachmentFile);
                cursor.close();


                 attachments.add(selectedimage);


                inputUpload.setText("Prescription Attached");
                inputUpload.setEnabled(false);



            }
        }

        if (requestCode == CAMERA) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");



            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), imageBitmap);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
           // Toast.makeText(UploadPrecription.this,"Here "+ getRealPathFromURI(tempUri),Toast.LENGTH_LONG).show();

            selectedimage = getRealPathFromURI(tempUri);

            attachments.add(selectedimage);
            inputUpload.setText("Prescription Attached");
            inputUpload.setEnabled(false);

           // Log.e("Camera","Upload :"+getRealPathFromURI(tempUri));
        }



    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    // get Ip Address if mobile data is on
    public static String getDeviceIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaces) {
                List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress inetAddress : inetAddresses) {
                    if (!inetAddress.isLoopbackAddress()) {
                        String sAddr = inetAddress.getHostAddress().toUpperCase();
                        boolean isIPv4 = sAddr.indexOf(':')<0;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                // drop ip6 port suffix
                                int delim = sAddr.indexOf('%');
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
