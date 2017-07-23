package com.example.mohit.xmohit;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class myupload extends AppCompatActivity {

    private List<ImageUpload> imglist;
    private ListView lv;
    private imagelistadapter adapter;
    AlertDialog.Builder b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myupload);

        imglist = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

/////////////////////show progress dialog box during list image upload

        final ProgressDialog progressdialog = new ProgressDialog(this);
        progressdialog.setMessage("please wait loading list image...");
        progressdialog.show();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        int length = email.length();

        b = new AlertDialog.Builder(this);


        email = email.substring(0,length-4);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(uploadimage.FB_DATABASE_PATH);
        ref = ref.child(email).child("images");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //fetch image data from firebase database
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //image upload class requires default constructor
                    ImageUpload img = snapshot.getValue(ImageUpload.class);
                    imglist.add(img);
                }

                adapter = new imagelistadapter(myupload.this, R.layout.imagedikhega, imglist);
                //set adapter for listview
                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String item = ((TextView) view.findViewById(R.id.descr)).getText().toString();
                        b.setTitle(item);
                        AlertDialog alert = b.create();
                        alert.show();
                        Toast.makeText(getBaseContext(), "hello", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressdialog.dismiss();
            }
        });
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
            FirebaseAuth firebaseAuth;
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
    public void visib(View v){

    }

}
