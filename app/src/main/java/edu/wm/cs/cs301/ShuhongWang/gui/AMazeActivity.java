package edu.wm.cs.cs301.ShuhongWang.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.cs301.ShuhongWang.R;

public class AMazeActivity extends AppCompatActivity {
    private SeekBar sbDifficulty;
    private TextView txtDifficulty;
    private Spinner algorithmSpinner;
    private Button revisit;
    private Button explore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_maze);
        bindViews();
        setSpinner();
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
        algorithmSpinner = (Spinner) findViewById(R.id.spinner_maze);
        // set up spinner list for generating algorithms
        List<String> algorithmList = new ArrayList<String>();
        algorithmList.add("DFS");
        algorithmList.add("Prim");
        algorithmList.add("Eller");
        ArrayAdapter<String> algorithmAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, algorithmList);
        algorithmSpinner.setAdapter(algorithmAdapter);
    }
}