package edu.wm.cs.cs301.ShuhongWang.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.wm.cs.cs301.ShuhongWang.R;

public class WinningActivity extends AppCompatActivity {
    private Button playAgain;
    private String log = "WinningActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);
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
}