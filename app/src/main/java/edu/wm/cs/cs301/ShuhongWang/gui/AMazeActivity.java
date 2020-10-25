package edu.wm.cs.cs301.ShuhongWang.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.cs301.ShuhongWang.R;

public class AMazeActivity extends AppCompatActivity {
    private SeekBar sbDifficulty;
    private TextView txtDifficulty;
    private Spinner spinnerAlgorithm;
    private Button revisit;
    private Button explore;

    private String difficulty;
    private String algorithm;
    private boolean containRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_maze);
        bindViews();
        setSpinner();
        setButtonExplore();
        setButtonRevisit();
    }

    /**
     * Let the text below the seek bar display the difficulty that the user chose.
     */
    private void bindViews(){
        sbDifficulty = (SeekBar) findViewById(R.id.sb_difficulty);
        txtDifficulty = (TextView) findViewById(R.id.txt_difficulty);
        sbDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               txtDifficulty.setText("Difficulty: " + progress + "/9");
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
     * Initialize and set the spinner for algorithm selection
     */
    private void setSpinner(){
        spinnerAlgorithm = (Spinner) findViewById(R.id.spinner_maze);
        // set up spinner list for generating algorithms
        List<String> algorithmList = new ArrayList<String>();
        algorithmList.add("DFS");
        algorithmList.add("Prim");
        algorithmList.add("Eller");
        ArrayAdapter<String> algorithmAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, algorithmList);
        spinnerAlgorithm.setAdapter(algorithmAdapter);
    }

    /**
     * Set the functions for the explore button.
     */
    private void setButtonExplore(){
        explore = (Button) findViewById(R.id.button_explore);
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load difficulty
                int valDifficulty = sbDifficulty.getProgress();
                difficulty = String.valueOf(valDifficulty);
                Log.v("Difficulty: ", difficulty);

                // Load algorithm
                algorithm = spinnerAlgorithm.getSelectedItem().toString();
                Log.v("Generating algorithm: ", algorithm);

                // Load rooms
                Switch switchRoom = (Switch) findViewById(R.id.switch_rooms);
                containRooms = switchRoom.isChecked();
                Log.v("Contain rooms: ", String.valueOf(containRooms));

                startExploreActivity();
            }
        });
    }

    /**
     * Set the functions for the revisit button.
     */
    private void setButtonRevisit(){
        revisit = (Button) findViewById(R.id.button_revisit);
        // Set the clicking action
        revisit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Load difficulty
                int valDifficulty = sbDifficulty.getProgress();
                difficulty = String.valueOf(valDifficulty);
                Log.v("Difficulty: ", difficulty);

                // Load algorithm
                algorithm = spinnerAlgorithm.getSelectedItem().toString();
                Log.v("Generating algorithm: ", algorithm);

                // Load rooms
                Switch switchRoom = (Switch) findViewById(R.id.switch_rooms);
                containRooms = switchRoom.isChecked();
                Log.v("Contain rooms: ", String.valueOf(containRooms));

                startRevisitActivity();
            }
        });
    }

    /**
     * Switch to generating state and start a new maze.
     */
    private void startExploreActivity(){
        Intent intent = new Intent(this, GeneratingActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("algorithm", algorithm);
        intent.putExtra("containRooms", containRooms);
        Log.v("AMazeActivity", "explore activity started");
        startActivity(intent);
        finish();
    }

    /**
     * Switch to generating state and generate an old maze.
     */
    private void startRevisitActivity(){
        Intent intent = new Intent(this, GeneratingActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("algorithm", algorithm);
        intent.putExtra("containRooms", containRooms);
        Log.v("AMazeActivity", "revisit activity started");
        startActivity(intent);
        finish();
    }
}