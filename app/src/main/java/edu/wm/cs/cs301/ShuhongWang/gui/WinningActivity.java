package edu.wm.cs.cs301.ShuhongWang.gui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.ShuhongWang.R;

public class WinningActivity extends AppCompatActivity {
    private Button playAgain;
    private String log = "WinningActivity";
    private int pathLength;
    private int shortestPathLength;
    private TextView txtPathLength;
    private TextView txtIdealPathLength;
    private int energy;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);

        mediaPlayer = MediaPlayer.create(this.getApplicationContext(), R.raw.winning);
        mediaPlayer.start();

        Intent intent = getIntent();
        pathLength = intent.getIntExtra("pathLength", 0);
        shortestPathLength = intent.getIntExtra("shortestPathLength", 0);
        energy = intent.getIntExtra("energyConsumption", 0);
        Log.v(log, "Received path length: " + String.valueOf(pathLength));
        Toast.makeText(this, "Received path length: " + String.valueOf(pathLength), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Received shortest path length: " + String.valueOf(shortestPathLength), Toast.LENGTH_SHORT).show();
        setText();

        setButtonPlayAgain();
    }

    /**
     * set the text to display the path length.
     */
    private void setText() {
        txtPathLength = findViewById(R.id.txtPathLength);
        txtIdealPathLength = findViewById(R.id.txtIdealPathLength);
        txtPathLength.setText("Total path length you walked: " + pathLength);
        txtIdealPathLength.setText("Shortest possible path length: " + shortestPathLength);
        if (energy != 0){
            txtIdealPathLength.setText("Total Energy Consumption: " + energy);
        }
    }

    /**
     * Set the button for going back to the title screen
     */
    private void setButtonPlayAgain(){
        playAgain = (Button) findViewById(R.id.buttonPlayAgain);
        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log, "Clicked button Play Again.");
                goBackToTitle();
            }
        });
    }

    /**
     * Switch back to title.
     */
    private void goBackToTitle(){
        mediaPlayer.stop();
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Set up the back button.
     */
    public void onBackPressed(){
        Intent intent = new Intent(this, AMazeActivity.class);
        Log.v(log, "Go back to title page");
        startActivity(intent);
        finish();
    }
}