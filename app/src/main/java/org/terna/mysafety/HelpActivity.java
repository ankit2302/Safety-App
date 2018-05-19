package org.terna.mysafety;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HelpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(HelpActivity.this,MySafety.class));
        finish();
        super.onBackPressed();
    }
}
