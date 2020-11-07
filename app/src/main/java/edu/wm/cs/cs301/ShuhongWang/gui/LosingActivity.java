package edu.wm.cs.cs301.ShuhongWang.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.ShuhongWang.R;

public class LosingActivity extends AppCompatActivity {
    private Button playAgain;
    private String log = "LosingActivity";
    private int energyConsumed;
    private int pathLength;

    private TextView txtEnergy;
    private TextView txtPathLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_losing);
        setButtonPlayAgain();

        Intent intent = getIntent();
        energyConsumed = intent.getIntExtra("energyConsumed", 0);
        pathLength = intent.getIntExtra("pathLength", 0);
        Log.v(log, "Received energyConsumed: " + String.valueOf(energyConsumed));
        Log.v(log, "Received pathLength: " + String.valueOf(pathLength));
//        Toast.makeText(this, "Received energyConsumed: " + String.valueOf(energyConsumed) + "\n pathLength: " + String.valueOf(pathLength), Toast.LENGTH_SHORT).show();

        // Create vibration
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));

        setText();
    }

    private void setText() {
        txtEnergy = findViewById(R.id.txt_energy_consumption);
        txtPathLength = findViewById(R.id.txt_path_length);
        txtEnergy.setText("Total Energy Consumed: " + energyConsumed);
        txtPathLength.setText("Total Path Length: " + pathLength);
    }

    /**
     * Set the button for going back to the title screen
     */
    private void setButtonPlayAgain(){
        playAgain = (Button) findViewById(R.id.button_play_again);
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