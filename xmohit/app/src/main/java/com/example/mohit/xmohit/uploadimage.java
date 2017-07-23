package com.example.mohit.xmohit;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.content.Intent.createChooser;


public class uploadimage extends AppCompatActivity implements View.OnClickListener {

    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    private EditText imagename;
    private Button upload;
    private Button show;
    private EditText desc;
    private Uri imageuri;
    FirebaseAuth firebaseAuth;
    private ImageView imageView;
    private Button camera;
    String name;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CODE = 1234;


    public static final String FB_STORAGE_PATH = "";
    public static final String FB_DATABASE_PATH = "images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadimage);


        Bundle x =savedInstanceState;
        firebaseAuth = FirebaseAuth.getInstance();
        name = firebaseAuth.getCurrentUser().getEmail();
        mStorageRef = FirebaseStorage.getInstance().getReference(String.valueOf(name));
        databaseReference = FirebaseDatabase.getInstance().getReference();



        imageView = (ImageView) findViewById(R.id.imageView);
        imagename = (EditText) findViewById(R.id.imagename);
        desc = (EditText) findViewById(R.id.desc);
        show = (Button) findViewById(R.id.button3);
        camera = (Button) findViewById(R.id.camera);
        upload = (Button) findViewById(R.id.brows);

        upload.setOnClickListener(this);
        show.setOnClickListener(this);
        camera.setOnClickListener(this);
    }

        public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            firebaseAuth = FirebaseAuth.getInstance();
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("finish", true);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            firebaseAuth.signOut();
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    @SuppressWarnings("VisibleForTests")
    public void onClick(View v) {
        if (v == upload){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(createChooser(intent, "select Image"), REQUEST_CODE);

        }

        if( v == camera){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
        if (v == show){
            if (imageuri != null){
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("uploading..");
                dialog.show();

                StorageReference ref = mStorageRef.child(FB_STORAGE_PATH +  imagename.getText() + "." + getimagetext(imageuri));

                ref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess( UploadTask.TaskSnapshot taskSnapshot) {


                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "image uploaded", Toast.LENGTH_SHORT).show();
                        ImageUpload imageUpload = new ImageUpload(imagename.getText().toString(), taskSnapshot.getDownloadUrl().toString(),desc.getText().toString());
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String email = user.getEmail();
                        int length = email.length();
                        email = email.substring(0,length-4);
                        databaseReference.child("images").child(email).child("images").push().setValue(imageUpload);
                        databaseReference.child("img").push().setValue(imageUpload);
                        startActivity(new Intent(getIntent()));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog.setMessage("uploaded " + (int)progress+"%");
                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(), "please select image" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestcode, int resultcode, Intent data){
        super.onActivityResult(requestcode,resultcode,data);
        if ((requestcode == REQUEST_CODE)  && (resultcode == RESULT_OK) && (data != null) && (data.getData() != null)){
            imageuri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
                imageView.setImageBitmap(bm);

            } catch (IOException f) {
                f.printStackTrace();
            }
        }

        if((requestcode == REQUEST_IMAGE_CAPTURE) && (resultcode == RESULT_OK)){


        }

    }

    public String getimagetext(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

}
