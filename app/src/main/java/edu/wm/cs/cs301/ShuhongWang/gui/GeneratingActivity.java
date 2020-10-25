package edu.wm.cs.cs301.ShuhongWang.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.cs301.ShuhongWang.R;

public class GeneratingActivity extends AppCompatActivity {
    private Spinner spinnerDriver;
    private Spinner spinnerRobot;
    private ProgressBar progressBar;
    private int progress = 0;
    private String builder;
    private String difficulty;
    private boolean containRooms;
    private String log = "GeneratingActivity";
    private TextView txtProgress;
    private Button buttonStart;
    private String driver;
    private String robot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);

        // Get settings from the previous state.
        Intent intent = getIntent();
        builder = intent.getStringExtra("algorithm");
        difficulty = intent.getStringExtra("difficulty");
        containRooms = intent.getBooleanExtra("containRooms", false);
        Log.v(log, "Builder: " + builder);
        Log.v(log, "Difficulty: " + difficulty);
        Log.v(log, "ContainRooms: " + String.valueOf(containRooms));

        setSpinners();
        setProgressBar();
        setButtonStart();
    }

    /**
     * Set the spinners for driver/robot selection.
     */
    private void setSpinners(){
        spinnerDriver = (Spinner) findViewById(R.id.spinner_driver);
        spinnerRobot = (Spinner) findViewById(R.id.spinner_robot);

        // set up spinner list for drivers
        List<String> driverList = new ArrayList<String>();
        driverList.add("Manual");
        driverList.add("Wall Follower");
        driverList.add("Wizard");
        ArrayAdapter<String> driverAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, driverList);
        spinnerDriver.setAdapter(driverAdapter);

        // set up spinner list for robot
        List<String> robotList = new ArrayList<String>();
        robotList.add("Premium");
        robotList.add("Mediocre");
        robotList.add("Soso");
        robotList.add("Shaky");
        ArrayAdapter<String> robotAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, robotList);
        spinnerRobot.setAdapter(robotAdapter);
    }

    /**
     * Initialize the progress bar and start a thread to show the progress change.
     */
    private void setProgressBar(){
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtProgress = (TextView) findViewById(R.id.txt_progress);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        // Start a thread for testing in project 6.
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progress < 100){
                    progress++;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBar.setProgress(progress);
                    txtProgress.setText(String.valueOf(progress) + "%");
                }
            }
        }).start();
    }

    /**
     * Initialize the button for start playing. Pops up a message if generating is not done. Switch to playing state if generating is finished.
     */
    private void setButtonStart(){
        buttonStart = (Button) findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progress < 100) {
                    Toast.makeText(GeneratingActivity.this, "Please wait until the generating process finish!", Toast.LENGTH_SHORT).show();
                }
                else {
                    robot = spinnerRobot.getSelectedItem().toString();
                    driver = spinnerDriver.getSelectedItem().toString();

                    Log.v(log, "robot: " + robot);
                    Log.v(log, "Driver: " + driver);

                    if (driver.equals("Manual")){
                        startPlayManuallyActivity();
                    }
                    else{
                        startPlayAnimationActivity();
                    }
                }
            }
        });
    }

    /**
     * Switch to manual playing mode.
     */
    private void startPlayManuallyActivity(){
        Intent intent = new Intent(this, PlayManuallyActivity.class);
//        intent.putExtra("driver", driver);
//        intent.putExtra("robot", robot);
        Log.v(log, "Started PlayManuallyActivity. ");
        startActivity(intent);
        finish();
    }

    /**
     * Switch to automatic playing mode.
     */
    private void startPlayAnimationActivity(){
        Intent intent = new Intent(this, PlayAnimationActivity.class);
        intent.putExtra("driver", driver);
        intent.putExtra("robot", robot);
        Log.v(log, "Started PlayAnimationActivity. ");
        startActivity(intent);
        finish();
    }
}