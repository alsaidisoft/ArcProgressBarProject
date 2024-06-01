package com.ArcprogressBarProject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private ArcProgressBar arcProgressBar;
    Button btn;
    TextView speedTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arcProgressBar = (ArcProgressBar) findViewById(R.id.progressBar);
        speedTxt = (TextView) findViewById(R.id.txtProgress);
        btn = (Button) findViewById(R.id.calculate_speed);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int random = (int)(Math.random()*100);
                arcProgressBar.setProgressWithAnimation(random); // Set the progress with animation to 75%
                speedTxt.setText("Speed: "+random);
            }
        });
    }
}