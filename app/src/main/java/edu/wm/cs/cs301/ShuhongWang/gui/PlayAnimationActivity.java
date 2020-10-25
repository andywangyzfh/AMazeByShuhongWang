package edu.wm.cs.cs301.ShuhongWang.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import edu.wm.cs.cs301.ShuhongWang.R;

public class PlayAnimationActivity extends AppCompatActivity {
    private ToggleButton pause;
    private ToggleButton map;
    private Button zoomIn;
    private Button zoomOut;
    private Button go2Winning;
    private Button go2Losing;
    private SeekBar speed;
    private TextView txtSpeed;
    private ProgressBar energy;
    private TextView txtEnergy;

    private String log = "PlayAnimationActivity";
    private TextView txtMaze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manually);

        txtMaze = (TextView) findViewById(R.id.txt_maze);
        setTogglePause();
        setToggleMap();
        setButtonZoomIn();
        setButtonZoomOut();
        setSeekBarSpeed();
        setProgressBarEnergy();
        setButtonGo2Winning();
        setButtonGo2Losing();
    }

    /**
     * Set up the toggle button to toggle the start and stop of the map.
     */
    private void setTogglePause(){
        pause = (ToggleButton) findViewById(R.id.tbPause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log, "Toggled pause.");
                if (map.isChecked()){
                    pause.setTextOn("Running");
                    txtMaze.setText("Switch from pause to play.");
                }
                else{
                    pause.setTextOff("Paused");
                    txtMaze.setText("Switched from play to pause");
                }
            }
        });
    }

    /**
     * Set up the toggle button to toggle the map.
     */
    private void setToggleMap() {
        map = (ToggleButton) findViewById(R.id.tb_map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log, "Toggled map.");
                if (map.isChecked()){
                    txtMaze.setText("Map On.");
                }
                else{
                    txtMaze.setText("Map Off.");
                }
            }
        });
    }


    /**
     * Set up the button to zoom in.
     */
    private void setButtonZoomIn() {
        zoomIn = (Button) findViewById(R.id.button_zoomIn);
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log, "Zoom in.");
                txtMaze.setText("Zoom in.");
            }
        });
    }

    /**
     * Set up the button to zoom out.
     */
    private void setButtonZoomOut() {
        zoomOut = (Button) findViewById(R.id.button_zoomOut);
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log, "Zoom out.");
                txtMaze.setText("Zoom out.");
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
                txtMaze.setText("Change speed to " + String.valueOf(speed.getProgress()));
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
        txtEnergy.setText("Remaining energy: 3500");
    }

    /**
     * set up the button for going to the winning state
     */
    private void setButtonGo2Winning(){
        go2Winning = (Button) findViewById(R.id.Go2Winning);
        go2Winning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log, "Clicked Go2Winning");
                startWinningActivity();
            }
        });
    }

    /**
     * set up the button for going to the losing state
     */
    private void setButtonGo2Losing(){
        go2Losing = (Button) findViewById(R.id.Go2Losing);
        go2Losing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log, "Clicked Go2Losing");
                startLosingActivity();
            }
        });
    }

    /**
     * Switch to winning state.
     */
    private void startWinningActivity(){
        Intent intent = new Intent(this, WinningActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Switch to losing state.
     */
    private void startLosingActivity(){
        Intent intent = new Intent(this, LosingActivity.class);
        startActivity(intent);
        finish();
    }
}
