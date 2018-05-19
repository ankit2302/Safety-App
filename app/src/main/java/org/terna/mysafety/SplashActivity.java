package org.terna.mysafety;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (isNetworkAvailable(getBaseContext())) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    currentUser = auth.getInstance().getCurrentUser();

                    if (currentUser != null) {
                        startActivity(new Intent(SplashActivity.this, MySafety.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }

                }
            }, 500);
        }else {
            alert();
        }
    }

    public boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void alert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(SplashActivity.this);
        alert.setTitle("Connection Error");
        alert.setCancelable(false);
        alert.setMessage("You are not currently connected to the internet");
        alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isNetworkAvailable(getBaseContext())) {
                    if (currentUser != null) {
                        startActivity(new Intent(SplashActivity.this, MySafety.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                }else {
                    alert();
                }
            }
        });
        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog abuilder = alert.create();
        abuilder.show();
    }

}

