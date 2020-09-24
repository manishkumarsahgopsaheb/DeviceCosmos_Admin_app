package com.example.addblog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class composeblog extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 1;
    private static final int Camera_Image = 0;
    private static final int Video_Image = 2;
    FirebaseAuth mAuth;
    CircleImageView circleImageView;
    DatabaseReference databaseReference , databaseReferenceforuserblog;
    String uid , email;
    String downloadUrl;
    ImageView postbtn,cancelbtn,galleryimprtbtn,camerabtn,videobtn,uploadedingimage;
    EditText writeheading;
    StorageReference imgstoragereference;
    DatePicker datepi;
    TimePicker timepic;
    TextView dattext,timtxt;
    ProgressDialog loadingbar;

    Spinner taggedin , category;
    EditText link , description;
   // private long counterposts = 0;

    long childcount = 0;
    //private String ccc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composeblog);

        circleImageView = (CircleImageView)findViewById(R.id.prof_id);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("admin");
        databaseReferenceforuserblog = FirebaseDatabase.getInstance().getReference().child("Blogs");
        imgstoragereference = FirebaseStorage.getInstance().getReference().child("blog_images");
        uid = mAuth.getCurrentUser().getUid();
        email = mAuth.getCurrentUser().getEmail();

        postbtn = (ImageView)findViewById(R.id.post_id);
        cancelbtn = (ImageView)findViewById(R.id.cancel_id);
        galleryimprtbtn =(ImageView)findViewById(R.id.choose);
        camerabtn = (ImageView)findViewById(R.id.camera_id);
        videobtn = (ImageView)findViewById(R.id.video_id);
        writeheading = (EditText)findViewById(R.id.editheading_id);
        uploadedingimage = (ImageView)findViewById(R.id.upload);
        datepi = (DatePicker) findViewById(R.id.datepicker);
        timepic = (TimePicker) findViewById(R.id.timepicker);
        dattext = (TextView) findViewById(R.id.datepost_id);
        timtxt = (TextView) findViewById(R.id.timepost_id);

        taggedin = (Spinner)findViewById(R.id.tagspiiner_id);
        category = (Spinner)findViewById(R.id.categoryspinner_id);
        link = (EditText)findViewById(R.id.linktext_id);
        description = (EditText)findViewById(R.id.description_id);

        loadingbar = new ProgressDialog(this);

        dattext.setText(getCurrentDate());
        timtxt.setText(getCurrentTime());


        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postblogtodatabase();
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(composeblog.this,writeblog.class);
                startActivity(intent);
                finish();
            }
        });

        galleryimprtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryimporting();
            }
        });
        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureimage();
            }
        });

        videobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturevideos();
            }
        });

    }

    private void capturevideos()
    {

        Intent videointent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(videointent, Video_Image);
    }

    private void captureimage()
    {
        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraintent, Camera_Image);

    }

    private void galleryimporting()
    {
        Intent galleryintent = new Intent();
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, CHOOSE_IMAGE);

    }

    private void postblogtodatabase() {
         final String heading = writeheading.getText().toString();
         final String descriptiongetting = description.getText().toString();
         final String linkgetting = link.getText().toString();
         final String attitme = timtxt.getText().toString();
         final String ondate = dattext.getText().toString();
         final String postimageurl = downloadUrl;

         final String taggetting = taggedin.getSelectedItem().toString();
         final String categorygetting = category.getSelectedItem().toString();

         final String username = mAuth.getCurrentUser().getDisplayName();
         final String urlofprofileimage = mAuth.getCurrentUser().getPhotoUrl().toString();

        int c = 0;
        final String cc = Integer.toString(c);

        if (TextUtils.isEmpty(heading)) {
            Toast.makeText(composeblog.this, "please write some messages....", Toast.LENGTH_SHORT).show();
        } else {
            final String currentmail = mAuth.getCurrentUser().getEmail();
            //String key = databaseReference.push().getKey();

            databaseReferenceforuserblog.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {


                        childcount = dataSnapshot.getChildrenCount();

                        HashMap blogs  = new HashMap<>();
                        blogs.put("Email", currentmail);
                        blogs.put("heading", heading);
                        blogs.put("description",descriptiongetting);
                        blogs.put("Urllink",linkgetting);
                        blogs.put("tag",taggetting);
                        blogs.put("category",categorygetting);
                        blogs.put("time", attitme);
                        blogs.put("date", ondate);
                        blogs.put("profilephoto", urlofprofileimage);
                        blogs.put("username", username);
                        blogs.put("photo", postimageurl);
                        blogs.put("uid",uid);
                        blogs.put("count",cc);
                        blogs.put("blogcounting",childcount);
                        String key = databaseReferenceforuserblog.push().getKey();

                        databaseReferenceforuserblog.child(key).setValue(blogs).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(composeblog.this, "Blog Uploaded successfully....", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(composeblog.this,writeblog.class);
                                    startActivity(intent);
                                    finish();

                                }
                                else {
                                    String message = task.getException().toString();
                                    Toast.makeText(composeblog.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else
                        {
                            childcount = 0;

                            HashMap blogs  = new HashMap<>();
                            blogs.put("Email", currentmail);
                            blogs.put("heading", heading);
                            blogs.put("description",descriptiongetting);
                            blogs.put("Urllink",linkgetting);
                            blogs.put("tag",taggetting);
                            blogs.put("category",categorygetting);
                            blogs.put("time", attitme);
                            blogs.put("date", ondate);
                            blogs.put("profilephoto", urlofprofileimage);
                            blogs.put("username", username);
                            blogs.put("photo", postimageurl);
                            blogs.put("uid",uid);
                            blogs.put("count",cc);
                            blogs.put("blogcounting",childcount);
                            String key = databaseReferenceforuserblog.push().getKey();

                            databaseReferenceforuserblog.child(key).setValue(blogs).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(composeblog.this, "Blog Uploaded successfully....", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(composeblog.this,writeblog.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                    else {
                                        String message = task.getException().toString();
                                        Toast.makeText(composeblog.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private String getCurrentTime() {
        String currentTime = timepic.getCurrentHour() + ":" + timepic.getCurrentMinute();
        return currentTime;
    }

    private String getCurrentDate() {
        StringBuilder builder = new StringBuilder();
        builder.append((datepi.getMonth() + 1) + "/");//month is 0 based
        builder.append(datepi.getDayOfMonth() + "/");
        builder.append(datepi.getYear());
        return builder.toString();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //.setAspectRatio(1, 1)
                    .start(this);
        }
        else if (requestCode == Camera_Image && resultCode == RESULT_OK && data != null)
        {
            Uri camerauri = data.getData();

            // write the code for crop image
            CropImage.activity(camerauri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                   // .setAspectRatio(2,2)
                    .start(this);

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                // progressBar.setVisibility(View.VISIBLE);
                loadingbar.setTitle("Please wait");
                loadingbar.setMessage("importing....");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
                Uri resultUri = result.getUri();

                String key = databaseReference.push().getKey();

                StorageReference filepath = imgstoragereference.child(key + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            Task<Uri> downresult = task.getResult().getMetadata().getReference().getDownloadUrl();

                            downresult.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = uri.toString();

                                    Picasso.get().load(downloadUrl).into(uploadedingimage);
                                    // progressBar.setVisibility(View.GONE);
                                    loadingbar.dismiss();

                                }
                            });

                        }

                    }
                });

            } else {
                Toast.makeText(composeblog.this, "Error while getting image", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image")))
                {
                    String imgurl = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(imgurl).into(circleImageView);
                }
                else
                {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(composeblog.this);
                    builder.setMessage("It Seems You Are a New User So Kindly Visit The Profile First!")
                            .setCancelable(true);
                    final AlertDialog alert = builder.create();
                    alert.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
