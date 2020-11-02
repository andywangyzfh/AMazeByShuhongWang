package edu.wm.cs.cs301.ShuhongWang.gui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.cs301.ShuhongWang.R;
import edu.wm.cs.cs301.ShuhongWang.generation.DataHolder;
import edu.wm.cs.cs301.ShuhongWang.generation.Maze;
import edu.wm.cs.cs301.ShuhongWang.generation.MazeFactory;
import edu.wm.cs.cs301.ShuhongWang.generation.Order;

public class GeneratingActivity extends AppCompatActivity implements Order {
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
    private Handler handler;
    private MazeGeneration mazeGeneration;

    private Order.Builder mazeBuilder;
    private MazeFactory mazeFactory;
    private int skillLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);

//        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Get settings from the previous state.
        Intent intent = getIntent();
        builder = intent.getStringExtra("algorithm");
        difficulty = intent.getStringExtra("difficulty");
        containRooms = intent.getBooleanExtra("containRooms", false);

        Log.v(log, "Builder: " + builder);
        Log.v(log, "Difficulty: " + difficulty);
        Log.v(log, "ContainRooms: " + String.valueOf(containRooms));

        handler = new Handler();
        mazeFactory = new MazeFactory();
        setMazeBuilder(builder);
        setSkillLevel(difficulty);
        setPerfect(containRooms);
        DataHolder.getInstance().setSeed(13);

        setSpinners();
        setProgressBar();
        setButtonStart();

        mazeGeneration = new MazeGeneration();
        mazeGeneration.execute(100);

        Toast.makeText(this, "Received builder:" + builder + "\n Difficulty: " + difficulty + "\n Contain rooms: " + String.valueOf(containRooms), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getSkillLevel() {
        return DataHolder.getInstance().getSkillLevel();
    }

    @Override
    public Builder getBuilder() {
        return DataHolder.getInstance().getBuilder();
    }

    @Override
    public boolean isPerfect() {
        return DataHolder.getInstance().isPerfect();
    }

    @Override
    public int getSeed() {
        return DataHolder.getInstance().getSeed();
    }

    @Override
    public void deliver(Maze mazeConfig) {
        DataHolder.getInstance().setMazeConfig(mazeConfig);
    }

    @Override
    public void updateProgress(int percentage) {
        // Set current progress to percentage and update the progress bar.
        if (progress < percentage && percentage <= 100){
            progress = percentage;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progress);
                    txtProgress.setText(String.valueOf(progress) + "%");
                }
            });
        }
    }

    public void generateNewMaze(){
        mazeFactory.order(this);
    }

    private class MazeGeneration extends AsyncTask<Integer, Integer, String> {
        @Override
        protected void onPreExecute() {
            progress = 0;
            Log.v(log, "Maze Generation Starts");
        }

        @Override
        protected String doInBackground(Integer... integers) {
//            while (progress <= 100){
//                publishProgress(progress);
//                if (isCancelled()){
//                    break;
//                }
//                progress++;
//            }
            generateNewMaze();
            return "Maze Generation Done";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            txtProgress.setText(String.valueOf(values[0]) + "%");
        }

        @Override
        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            generateNewMaze();
            Log.v(log, "Generated Maze.");
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void setPerfect(boolean containRooms) {
        DataHolder.getInstance().setPerfect(!containRooms);
    }

    private void setSkillLevel(String difficulty) {
        skillLevel = Integer.valueOf(difficulty);
        DataHolder.getInstance().setSkillLevel(skillLevel);
    }

    private void setMazeBuilder(String builder) {
        if (builder.equals("DFS")){
            mazeBuilder = Order.Builder.DFS;
        }
        else if (builder.equals("Prim")){
            mazeBuilder = Order.Builder.Prim;
        }
        else if (builder.equals("Eller")){
            mazeBuilder = Order.Builder.Eller;
        }
        DataHolder.getInstance().setBuilder(mazeBuilder);
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
//
//        // Start a thread for testing in project 6.
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (progress < 100){
//                    progress++;
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    handler.post(new Runnable() {
//                        public void run() {
//                            progressBar.setProgress(progress);
//                            txtProgress.setText(String.valueOf(progress) + "%");
//                        }
//                    });
//                }
//            }
//        }).start();
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

    /**
     * Set up the back button.
     */
    public void onBackPressed(){
        if (!mazeGeneration.isCancelled()){
            mazeGeneration.cancel(true);
        }

        Intent intent = new Intent(this, AMazeActivity.class);
        Log.v(log, "Go back to title page");
        startActivity(intent);
        finish();
    }
}