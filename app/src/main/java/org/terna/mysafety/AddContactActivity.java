package org.terna.mysafety;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddContactActivity extends AppCompatActivity {

    EditText contactNumber;
    Button addButton;
    FirebaseUser currentUser;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        contactNumber=(EditText)findViewById(R.id.contactNumber);
        addButton=(Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String contactnumber=contactNumber.getText().toString();

                currentUser= auth.getInstance().getCurrentUser();

                if (currentUser!=null){

                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
                databaseReference=databaseReference.child(currentUser.getUid()).child("contacts").push();

                UserModel userModel=new UserModel();
                userModel.setContactNumber(contactnumber);


                    if (contactnumber.equals("")){
                        contactNumber.setError("Cannot be empty");
                        //Toast.makeText(AddContactActivity.this, "Number cannot be empty", Toast.LENGTH_SHORT).show();
                    }else{
                        databaseReference.setValue(userModel);
                        Toast.makeText(AddContactActivity.this, "Contact Added", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(AddContactActivity.this, "Error adding contact", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddContactActivity.this,MySafety.class));
        finish();
    }
}
