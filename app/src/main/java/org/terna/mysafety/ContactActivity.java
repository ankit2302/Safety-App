package org.terna.mysafety;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.security.AccessController.getContext;

public class ContactActivity extends AppCompatActivity {


    ListView listView;
   public static ArrayList<String> list=new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    DatabaseReference databaseReference,reference;
    String currentUser,mpath;
    FirebaseAuth auth;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        reference=FirebaseDatabase.getInstance().getReference();

        currentUser= auth.getInstance().getCurrentUser().getUid();
        arrayAdapter=new ArrayAdapter<>(ContactActivity.this,android.R.layout.simple_list_item_1,list);
        listView=(ListView)findViewById(R.id.listView);
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(currentUser).child("contacts");
        listView.setAdapter(arrayAdapter);
        mpath=FirebaseDatabase.getInstance().getReference("Users").child(currentUser).child("contacts").getKey();

        Toast.makeText(this,currentUser , Toast.LENGTH_SHORT).show();

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list.clear();
                        if (count==0) {
                            for (DataSnapshot ds1 : dataSnapshot.getChildren()) {

                                HashMap<String, String> value = (HashMap<String, String>) ds1.getValue();
                                String a = value.get("contactNumber");
                                list.add(a);
                                arrayAdapter.notifyDataSetChanged();


                            }
                        }
                        count++;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                final String x =adapterView.getItemAtPosition(pos).toString();
                Toast.makeText(ContactActivity.this, ""+x, Toast.LENGTH_SHORT).show();

                new AlertDialog.Builder(ContactActivity.this)
                        .setMessage("Delete "+x+" ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String i= String.valueOf(dataSnapshot.child("Users").child(currentUser).child("contacts").child(x));
                                        Toast.makeText(ContactActivity.this, ""+i, Toast.LENGTH_SHORT).show();
                                        Log.e("no",""+i);


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }


                        })
                        .show();


            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ContactActivity.this,MySafety.class));
        finish();
    }

}
