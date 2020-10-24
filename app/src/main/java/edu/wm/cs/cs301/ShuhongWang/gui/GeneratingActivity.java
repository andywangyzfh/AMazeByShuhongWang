package edu.wm.cs.cs301.ShuhongWang.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.cs301.ShuhongWang.R;

public class GeneratingActivity extends AppCompatActivity {
    private Spinner spinnerDriver;
    private Spinner spinnerRobot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);

        setSpinners();
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
}