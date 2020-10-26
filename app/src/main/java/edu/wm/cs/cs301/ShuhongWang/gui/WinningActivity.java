package edu.wm.cs.cs301.ShuhongWang.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import edu.wm.cs.cs301.ShuhongWang.R;

public class WinningActivity extends AppCompatActivity {
    private Button playAgain;
    private String log = "WinningActivity";
    private String pathLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);

        Intent intent = getIntent();
        pathLength = intent.getStringExtra("pathLength");
        Log.v(log, "Received path length: " + pathLength);

        setButtonPlayAgain();
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