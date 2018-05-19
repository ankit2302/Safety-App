package org.terna.mysafety;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.INTERNET;

public class LoginActivity extends AppCompatActivity {

    TextView noAcc;
    EditText loginEmail, loginPass;
    Button signin;
    String LoginEmail,LoginPass;
    FirebaseAuth auth;
    ProgressDialog progressDialog;


    private static final int PERMISSION_REQUEST_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Logging in....");

        boolean isFirstRun=getSharedPreferences("PREFERNCE",MODE_PRIVATE).getBoolean("isFirstRun",true);
        if (isFirstRun){

            requestPermission();
            getSharedPreferences("PREFERNCE",MODE_PRIVATE).edit().putBoolean("isFirstRun",false).commit();

        }

        noAcc=(TextView)findViewById(R.id.noAcc);
        loginEmail=(EditText)findViewById(R.id.loginEmail);
        loginPass=(EditText)findViewById(R.id.loginPass);
        signin=(Button)findViewById(R.id.signin);




        auth=FirebaseAuth.getInstance();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidate()){
                    Login();

                }
            }
        });

        noAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                finish();
            }
        });


    }

    private void Login() {
        progressDialog.show();
        auth.signInWithEmailAndPassword(LoginEmail,LoginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    startActivity(new Intent(LoginActivity.this,MySafety.class));
                    Toast.makeText(LoginActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
                        finish();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean isValidate(){

        LoginEmail=loginEmail.getText().toString();
        LoginPass=loginPass.getText().toString();

        boolean isValidate = true;

        if (LoginEmail.length()==0){
            loginEmail.setError("Required");
            isValidate=false;
        }
        if (LoginPass.length()==0){
            loginPass.setError("Required");
            isValidate=false;
        }
        return isValidate;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION,SEND_SMS,INTERNET}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean smsAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean internetAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && smsAccepted && internetAccepted)
                        Toast.makeText(this, "Permission Granted, Now you can access location and send sms.", Toast.LENGTH_SHORT).show();
                    else {

                        Toast.makeText(this, "Permission Denied, You cannot access location and send sms.", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION,SEND_SMS,INTERNET},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String s, DialogInterface.OnClickListener onClickListener) {

        new AlertDialog.Builder(LoginActivity.this)
                .setMessage("message")
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create()
                .show();

    }

}
