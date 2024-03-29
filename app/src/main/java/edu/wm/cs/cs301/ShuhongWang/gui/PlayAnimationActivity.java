package edu.wm.cs.cs301.ShuhongWang.gui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.ShuhongWang.R;
import edu.wm.cs.cs301.ShuhongWang.generation.DataHolder;
import edu.wm.cs.cs301.ShuhongWang.generation.Maze;

public class PlayAnimationActivity extends AppCompatActivity {
    private String strDriver;
    private String strRobot;
    private ToggleButton pause;
    private ToggleButton map;
    private Button zoomIn;
    private Button zoomOut;
//    private Button go2Winning;
//    private Button go2Losing;
    private SeekBar speed;
    private TextView txtSpeed;
    private ProgressBar energy;
    private TextView txtEnergy;
    private TextView leftSensor;
    private TextView rightSensor;
    private TextView forwardSensor;
    private TextView backSensor;

    private String log = "PlayAnimationActivity";
//    private TextView txtMaze;

    private int energyLeft;
    private int pathLength;

    private Robot robot;
    private RobotDriver driver;
    private StatePlaying statePlaying;
    private Maze maze;
    private Handler handler;
    private int[] sleepTime;
    private MazePanel panel;
    private Animation animation;
    private boolean stopped;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_animation);

        // Start BGM
        mediaPlayer = MediaPlayer.create(this.getApplicationContext(), R.raw.gameplay);
        mediaPlayer.start();

        // get information from generating activity
        Intent intent = getIntent();
        strRobot = intent.getStringExtra("robot");
        strDriver = intent.getStringExtra("driver");
        Log.v(log, "Received robot: " + strRobot);
        Log.v(log, "Received driver: " + strDriver);
        Toast.makeText(this, "Received driver: " + strDriver + "\n Robot: " + strRobot, Toast.LENGTH_SHORT).show();
        handler = new Handler();
        stopped = false;

        // Set up maze panel and statePlaying
        panel = findViewById(R.id.maze_panel);
        statePlaying = new StatePlaying();
        maze = DataHolder.getInstance().getMazeConfig();
        statePlaying.setMazeConfiguration(maze);
        setRobot();
        setDriver();
        statePlaying.setRobotAndDriver(robot, driver);
        statePlaying.setPlayAnimationActivity(this);

        // Set up the buttons
        setTogglePause();
        setToggleMap();
        setButtonZoomIn();
        setButtonZoomOut();
        setSeekBarSpeed();
        setProgressBarEnergy();
        setSensorStatus();
        sleepTime = new int[]{2000, 1800, 1600, 1400, 1200, 1000, 800, 600, 400, 200 };

        // set up energy and path length
        energyLeft = 3500;
        pathLength = 0;

        // start state playing and start the animation
        statePlaying.start(panel);
        map.setChecked(true);
        statePlaying.keyDown(Constants.UserInput.ToggleLocalMap);
//        Handler animationHandler = new Handler();
//        animationHandler.postDelayed(new Animation(), sleepTime[speed.getProgress()]);
        animation = new Animation();
    }

//    public void stopDriver() {
//        handler.removeCallbacks(animation);
//        animation = null;
//        handler = null;
//    }

    /**
     * The private class to handle the animation process.
     */
    private class Animation implements Runnable{
        @Override
        public void run() {
            // update the sensor status
            if (robot instanceof UnreliableRobot){
                updateSensorStatus();
            }
            // move one step forward
            try {
                if (!driver.drive1Step2Exit()){
                    stopped = true;
                    System.out.println("Animation: Failed @ drive1step2exit");
                }
                energyLeft = (int) robot.getBatteryLevel();
                energy.setProgress(energyLeft);
                txtEnergy.setText(energyLeft + "/3500");
            } catch (Exception e) {
                e.printStackTrace();
                stopped = true;
            }
            // if failed, switch to losing activity
            if (stopped){
                pathLength = robot.getOdometerReading();
                startLosingActivity();
                return;
            }
            // if reached the exit, switch to winning activity.
            if (robot.isAtExit()){
                pathLength = robot.getOdometerReading();
                startWinningActivity();
                return;
            }
            // Wait some time before proceeding to the next move. The time is based on the choice of the usr.
            handler.postDelayed(animation, sleepTime[speed.getProgress()]);
        }

        /**
         * Show the sensor status on the top-right of the screen
         */
        private void updateSensorStatus() {
            boolean[] status = ((UnreliableRobot)robot).getSensorStatus();
            if(status[0]){
                leftSensor.setTextColor(Color.GREEN);
            }
            else{
                leftSensor.setTextColor(Color.RED);
            }
            if(status[1]){
                rightSensor.setTextColor(Color.GREEN);
            }
            else{
                rightSensor.setTextColor(Color.RED);
            }
            if(status[2]){
                forwardSensor.setTextColor(Color.GREEN);
            }
            else{
                forwardSensor.setTextColor(Color.RED);
            }
            if(status[3]){
                backSensor.setTextColor(Color.GREEN);
            }
            else{
                backSensor.setTextColor(Color.RED);
            }
        }
    }

    /**
     * Initialize the driver according to the parameter
     */
    private void setDriver() {
        switch (strDriver){
            case "Wall Follower":
                driver = new WallFollower();
                driver.setRobot(robot);
                driver.setMaze(maze);
                break;
            case "Wizard":
                driver = new Wizard();
                driver.setRobot(robot);
                driver.setMaze(maze);
                break;
            default:
                break;
        }
    }

    /**
     * Initialize the robot according to the parameter
     */
    private void setRobot() {
        if (strRobot.equals("Premium")){
            robot = new ReliableRobot();
            robot.setStatePlaying(statePlaying);
            Log.v(log, "set a reliable robot");
            return;
        }
        int[] sensorParam;
        switch (strRobot){
            case "Mediocre":
                sensorParam = new int[]{1,0,0,1};
                break;
            case "Soso":
                sensorParam = new int[]{0,1,1,0};
                break;
            case "Shaky":
                sensorParam = new int[]{0,0,0,0};
                break;
            default:
                sensorParam = new int[]{1,1,1,1};
                break;
        }
        robot = new UnreliableRobot(sensorParam);
        robot.setStatePlaying(statePlaying);
        ((UnreliableRobot) robot).startRobot();
    }

    /**
     * Set up the toggle button to toggle the start and stop of the map.
     */
    private void setTogglePause(){
        pause = (ToggleButton) findViewById(R.id.tbPause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create vibration
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));

                Log.v(log, "Toggled pause.");
                // if paused, stop the animation
                if (pause.isChecked()){
                    handler.post(animation);
                    Log.v(log, "Started animation");
                }
                // if not paused, continue the animation.
                else{
                    handler.removeCallbacks(animation);
                    Log.v(log, "Paused animation");
                }
            }
        });
    }

    /**
     * Set up the toggle button to toggle the map.
     */
    private void setToggleMap() {
        map = (ToggleButton) findViewById(R.id.tbMap);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statePlaying.keyDown(Constants.UserInput.ToggleLocalMap);
                // play sound effect
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.button);
                mediaPlayer.start();
                Log.v(log, "Toggled map.");
            }
        });
    }


    /**
     * Set up the button to zoom in.
     */
    private void setButtonZoomIn() {
        zoomIn = (Button) findViewById(R.id.buttonZoomIn);
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statePlaying.keyDown(Constants.UserInput.ZoomIn);
                Log.v(log, "Zoom in.");
//                txtMaze.setText("Zoom in.");
            }
        });
    }

    /**
     * Set up the button to zoom out.
     */
    private void setButtonZoomOut() {
        zoomOut = (Button) findViewById(R.id.buttonZoomOut);
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statePlaying.keyDown(Constants.UserInput.ZoomOut);
                Log.v(log, "Zoom out.");
//                txtMaze.setText("Zoom out.");
            }
        });
    }

    /**
     * Set up the seek bar for speed adjustment
     */
    private void setSeekBarSpeed(){
        speed = (SeekBar) findViewById(R.id.sbSpeed);
        txtSpeed = (TextView) findViewById(R.id.txtSpeed);
        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtSpeed.setText(String.valueOf(speed.getProgress()) + "/9");
//                txtMaze.setText("Change speed to " + String.valueOf(speed.getProgress()));
                Log.v(log, "Changed speed.");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * Set up the progress bar for energy consumption.
     */
    private void setProgressBarEnergy(){
        energy = (ProgressBar) findViewById(R.id.pbEnergy);
        txtEnergy = (TextView) findViewById(R.id.txtEnergy);
        energy.setMax(3500);
        energy.setProgress(3500);
        txtEnergy.setText("Remaining energy: 3500");
    }

//    /**
//     * set up the button for going to the winning state
//     */
//    private void setButtonGo2Winning(){
//        go2Winning = (Button) findViewById(R.id.Go2Winning);
//        go2Winning.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.v(log, "Clicked Go2Winning");
//                startWinningActivity();
//            }
//        });
//    }
//
//    /**
//     * set up the button for going to the losing state
//     */
//    private void setButtonGo2Losing(){
//        go2Losing = (Button) findViewById(R.id.Go2Losing);
//        go2Losing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.v(log, "Clicked Go2Losing");
//                startLosingActivity();
//            }
//        });
//    }

    /**
     * Switch to winning state.
     */
    public void startWinningActivity(){
        mediaPlayer.stop();
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("energyConsumption", 3500 - energyLeft);
        intent.putExtra("pathLength", pathLength);
        startActivity(intent);
        finish();
    }

    /**
     * Switch to losing state.
     */
    public void startLosingActivity(){
        mediaPlayer.stop();
        Intent intent = new Intent(this, LosingActivity.class);
        intent.putExtra("energyConsumption", 3500 - energyLeft);
        intent.putExtra("pathLength", pathLength);
        startActivity(intent);
        finish();
    }

    /**
     * Set sensor status, i.e., change the color of the text of corresponding sensor.
     */
    private void setSensorStatus(){
        leftSensor = (TextView) findViewById(R.id.txtLeftSensor);
        rightSensor = (TextView) findViewById(R.id.txtRightSensor);
        forwardSensor = (TextView) findViewById(R.id.txtForwardSensor);
        backSensor = (TextView) findViewById(R.id.txtBackSensor);

        leftSensor.setTextColor(Color.GREEN);
        rightSensor.setTextColor(Color.GREEN);
        forwardSensor.setTextColor(Color.GREEN);
        backSensor.setTextColor(Color.GREEN);
    }

    /**
     * Set up the back button.
     */
    public void onBackPressed(){
        mediaPlayer.stop();
        Intent intent = new Intent(this, AMazeActivity.class);
        intent.putExtra("energyConsumption", 3500 - energyLeft);
        intent.putExtra("pathLength", pathLength);
        Log.v(log, "Go back to title page");
        startActivity(intent);
        finish();
    }
}
