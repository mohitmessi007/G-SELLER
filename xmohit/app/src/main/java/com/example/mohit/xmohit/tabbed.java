package com.example.mohit.xmohit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import layout.fhome;

public class tabbed extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FirebaseAuth firebaseauth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }

    public void call(View v){
        startActivity(new Intent(this, uploadimage.class));
    }

    public void shows(View v){

        startActivity(new Intent(this, myupload.class));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            firebaseauth = FirebaseAuth.getInstance();
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("finish", true);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            firebaseauth.signOut();
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                final View rootView = inflater.inflate(R.layout.fragment_fhome, container, false);
                //        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                //        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));


                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String email = user.getEmail();
                int length = email.length();
                email = email.substring(0, length - 4);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref = ref.child("img");


                final List<ImageUpload> imglist;
                final ListView lv;
                final AlertDialog.Builder b;
                final imagelistadapter[] adapter = new imagelistadapter[1];

                imglist = new ArrayList<>();
                lv = (ListView) rootView.findViewById(R.id.list1);

                final ProgressDialog progressdialog = new ProgressDialog(getActivity());
                progressdialog.setMessage("please wait loading list image...");
                progressdialog.show();

                b = new AlertDialog.Builder(getContext());

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //fetch image data from firebase database
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //image upload class requires default constructor
                            System.out.println("hex");
                            ImageUpload img = snapshot.getValue(ImageUpload.class);
                            imglist.add(img);
                        }

                        adapter[0] = new imagelistadapter(getActivity(), R.layout.imagedikhega, imglist);
                        //set adapter for listview
                        lv.setAdapter(adapter[0]);

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String item2 = ((TextView) view.findViewById(R.id.descr)).getText().toString();
                                final String item1 = ((TextView) view.findViewById(R.id.imgname)).getText().toString();
                                final CharSequence[] c = {"name : "+item1, "Description : "+item2};
                                b.setItems(c, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getContext(),item1, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                AlertDialog alert = b.create();
                                alert.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressdialog.dismiss();
                    }
                });


                return rootView;

            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2){
                View rootView = inflater.inflate(R.layout.fragment_fupload, container, false);
                //        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                //        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                return rootView;
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==3){
                View rootView = inflater.inflate(R.layout.fragment_myuploads, container, false);
                //        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                //        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                return rootView;
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==4){
                View rootView = inflater.inflate(R.layout.fragment_shows, container, false);
                //        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                //        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));



                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String email = user.getEmail();
                int length = email.length();
                email = email.substring(0, length - 4);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(uploadimage.FB_DATABASE_PATH);
                ref = ref.child(email).child("images");

                final List<ImageUpload> imglist;
                final ListView lv;
                final AlertDialog.Builder b;
                final imagelistadapter[] adapter = new imagelistadapter[1];

                imglist = new ArrayList<>();
                lv = (ListView) rootView.findViewById(R.id.list2);

                final ProgressDialog progressdialog = new ProgressDialog(getActivity());
                progressdialog.setMessage("please wait loading list image...");
                progressdialog.show();

                b = new AlertDialog.Builder(getContext());

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //fetch image data from firebase database
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //image upload class requires default constructor
                            System.out.println("hex");
                            ImageUpload img = snapshot.getValue(ImageUpload.class);
                            imglist.add(img);
                        }

                        adapter[0] = new imagelistadapter(getActivity(), R.layout.imagedikhega, imglist);
                        //set adapter for listview
                        lv.setAdapter(adapter[0]);

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String item2 = ((TextView) view.findViewById(R.id.descr)).getText().toString();
                                final String item1 = ((TextView) view.findViewById(R.id.imgname)).getText().toString();
                                final CharSequence[] c = {"name : "+item1, "Description : "+item2};
                                b.setItems(c, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getContext(),item1, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                AlertDialog alert = b.create();
                                alert.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressdialog.dismiss();
                    }
                });


















                return rootView;
            }
            else {
                View rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);
                //        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                //        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                return rootView;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "HOME";
                case 1:
                    return "UPLOAD";
                case 2:
                    return "MY UPLOADS";
                case 3:
                    return "SHOW";
            }
            return null;
        }
    }
}
