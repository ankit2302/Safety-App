package org.terna.mysafety;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText name,email,password,confpassword;
    Button signup;
    TextView already;
    String Name,Email,Password,Confpassword;
    FirebaseAuth auth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressDialog=new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Creating Account....");

        name=(EditText)findViewById(R.id.name);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        confpassword=(EditText)findViewById(R.id.confpassword);
        signup=(Button)findViewById(R.id.signup);
        already=(TextView)findViewById(R.id.already);

        auth=FirebaseAuth.getInstance();



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidate()){
                    SignUp();

                }

            }
        });

        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    private void SignUp(){
        progressDialog.show();

        auth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    final FirebaseUser user=task.getResult().getUser();

                    if (user != null){
                        auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){
                                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
                                    databaseReference=databaseReference.child(user.getUid());

                                    UserModel userModel=new UserModel();
                                    userModel.setName(Name);
                                    userModel.setEmail(Email);
                                    userModel.setPassword(Password);

                                    databaseReference.setValue(userModel);
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this,HelpActivity.class));
                                    finish();
                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                    }

                }

            }
        });

    }

    private boolean isValidate(){

        Name=name.getText().toString();
        Email=email.getText().toString();
        Password=password.getText().toString();
        Confpassword=confpassword.getText().toString();

        boolean isValidate=true;

        if (Name.length()==0){
            name.setError("Enter Name");
            isValidate=false;
        }
        if (Email.length()==0){
            email.setError("Enter email");
            isValidate=false;
        }
        if (!Email.contains("@")){
            email.setError("Email id doesn't contains '@' sign");
            isValidate=false;
        }
        if (Email.startsWith("@")){
            email.setError("Email cannot start with '@' sign");
            isValidate=false;
        }
        if (!Email.endsWith(".com")){
            email.setError("Email should end with '.com'");
            isValidate=false;
        }
        if (Password.equals("")){
            password.setError("Enter Password");
            isValidate=false;
        }else if (Password.length() < 8){
            confpassword.setError("Password must be at least 8 character");
            isValidate=false;
        }
        if (Confpassword.equals("")){
            confpassword.setError("Re-Enter Password");
            isValidate=false;
        }else if (!Password.equals(Confpassword)){
            confpassword.setError("Passwords don't match");
            isValidate=false;
        }
        return isValidate;
    }

}
