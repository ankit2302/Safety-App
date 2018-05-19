package org.terna.mysafety;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.HashMap;


public class MySafety extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    GPSTracker gps;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    String currentUser;
    ImageView sendButton;
    DrawerLayout drawer;
    ArrayList<String>list1=new ArrayList<>();
    TextView userName,userEmail,abcd;
    String a,b;

    ArrayList<String>list2=new ArrayList<>();

    ArrayAdapter<String> arrayAdapter;
    DatabaseReference databaseReference1,reference;
    String currentUser1,mpath;
    FirebaseAuth auth1;
    int count=0;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_safety);

        abcd=(TextView) findViewById(R.id.abcd);
        abcd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MySafety.this,HelpActivity.class));
                finish();
            }
        });

        progressDialog=new ProgressDialog(MySafety.this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header=navigationView.getHeaderView(0);

        reference=FirebaseDatabase.getInstance().getReference();

        currentUser1= auth1.getInstance().getCurrentUser().getUid();
        arrayAdapter=new ArrayAdapter<>(MySafety.this,android.R.layout.simple_list_item_1,list2);
        databaseReference1= FirebaseDatabase.getInstance().getReference("Users").child(currentUser1).child("contacts");
        mpath=FirebaseDatabase.getInstance().getReference("Users").child(currentUser1).child("contacts").getKey();

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list2.clear();
                if (count==0) {
                    for (DataSnapshot ds1 : dataSnapshot.getChildren()) {

                        HashMap<String, String> value = (HashMap<String, String>) ds1.getValue();
                        String a = value.get("contactNumber");
                        list2.add(a);
                        //arrayAdapter.notifyDataSetChanged();
                        //Log.e("list",""+list2);

                    }
                }
                count++;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userName= (TextView)header.findViewById(R.id.name1);
        userEmail=(TextView)header.findViewById(R.id.email1);

        currentUser= auth.getInstance().getCurrentUser().getUid();
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    a = dataSnapshot.child("Users").child(currentUser).child("name").getValue().toString();
                    b = dataSnapshot.child("Users").child(currentUser).child("email").getValue().toString();
//                    Toast.makeText(MySafety.this, "" + a+"\n"+b, Toast.LENGTH_SHORT).show();

                    String ab=a.toUpperCase();

                    userName.setText(ab);
                    userEmail.setText(b);

                    progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
            }


        });

        //ContactActivity.list=list1;

        gps = new GPSTracker(MySafety.this);

        sendButton=(ImageView)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    String msg="Please help me, I'm in a danger. My location is\nLatitude - "+latitude+"\nLongitude - "+longitude;
                    if (list2.size()!=0){
                        try {
                            for (int i = 0; i < list2.size(); i++) {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(list2.get(i), null, msg, null, null);
                            }
                            Toast.makeText(getApplicationContext(), "SMS Sent!", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    "SMS failed, please try again later!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(MySafety.this, "No contacts saved", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    gps.showSettingsAlert();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        drawer.closeDrawers();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addContacts) {
            Intent i = new Intent(MySafety.this,AddContactActivity.class);
            startActivity(i);
            finish();
            item.setChecked(true);
            drawer.closeDrawers();
        } else if (id == R.id.allContacts) {
            Intent i = new Intent(MySafety.this,ContactActivity.class);
            startActivity(i);
            finish();
            item.setChecked(true);
            drawer.closeDrawers();
        }else if (id == R.id.logout) {
            FirebaseUser currentUser= auth.getInstance().getCurrentUser();
            if(currentUser!=null) {
                auth.getInstance().signOut();
                Intent i = new Intent(MySafety.this, LoginActivity.class);
                startActivity(i);
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_LONG).show();
                finish();
            }else {
                Toast.makeText(MySafety.this,"Error occurred", Toast.LENGTH_SHORT).show();
            }
            item.setChecked(true);
            drawer.closeDrawers();
        }else if (id==R.id.help){
            startActivity(new Intent(MySafety.this,HelpActivity.class));
            item.setChecked(true);
            drawer.closeDrawers();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
