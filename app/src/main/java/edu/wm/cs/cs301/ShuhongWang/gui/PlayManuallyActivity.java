package edu.wm.cs.cs301.ShuhongWang.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import edu.wm.cs.cs301.ShuhongWang.R;

public class PlayManuallyActivity extends AppCompatActivity {
    private Button forward;
    private Button jump;
    private Button left;
    private Button right;
    private ToggleButton solution;
    private ToggleButton map;
    private ToggleButton walls;
    private Button zoomIn;
    private Button zoomOut;
    private Button shortCut;

    private int pathLength;
    private String log = "PlayManuallyActivity";
//    private TextView txtMaze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manually);

        pathLength = 0;
//        txtMaze = (TextView) findViewById(R.id.txt_maze);
        setButtonForward();
        setButtonJump();
        setButtonLeft();
        setButtonRight();
        setToggleSolution();
        setToggleMap();
        setToggleWalls();
        setButtonZoomIn();
        setButtonZoomOut();
        setButtonShortCut();
    }

    /**
     * Set up the button for step forward
     */
    private void setButtonForward(){
        forward = (Button) findViewById(R.id.button_forward);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathLength++;
                Log.v(log, "Clicked Forward.");
//                txtMaze.setText("Move forward.");
            }
        });
    }

    /**
     * Set up the button for jump
     */
    private void setButtonJump(){
        jump = (Button) findViewById(R.id.button_jump);
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathLength++;
                Log.v(log, "Clicked Jump.");
//                txtMaze.setText("Jump forward.");
            }
        });
    }

    /**
     * Set up the button for turn left
     */
    private void setButtonLeft(){
        left = (Button) findViewById(R.id.button_left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log, "Clicked Left.");
//                txtMaze.setText("Turn left.");
            }
        });
    }

    /**
     * Set up the button for turn right
     */
    private void setButtonRight() {
        right = (Button) findViewById(R.id.button_right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log, "Clicked right.");
//                txtMaze.setText("Turn right.");
            }
        });
    }

    /**
     * Set up the toggle button to toggle the solution.
     */
    private void setToggleSolution() {
        solution = (ToggleButton) findViewById(R.id.tb_solution);
        solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log, "Toggled solution.");
//                if (solution.isChecked()){
//                    txtMaze.setText("Solution On.");
//                }
//                else{
//                    txtMaze.setText("Solution Off.");
//                }
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
//                if (map.isChecked()){
//                    txtMaze.setText("Map On.");
//                }
//                else{
//                    txtMaze.setText("Map Off.");
//                }
            }
        });
    }

    /**
     * Set up the toggle button to toggle the walls.
     */
    private void setToggleWalls() {
        walls = (ToggleButton) findViewById(R.id.tb_walls);
        walls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log, "Toggled walls.");
//                if (map.isChecked()){
//                    txtMaze.setText("Toggled Walls.");
//                }
//                else{
//                    txtMaze.setText("Toggled Walls.");
//                }
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
//                txtMaze.setText("Zoom in.");
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
//                txtMaze.setText("Zoom out.");
            }
        });
    }

    /**
     * Set up the short cut button to go to winning state.
     */
    private void setButtonShortCut() {
        shortCut = (Button) findViewById(R.id.ShortCut);
        shortCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log, "Switch to state winning. ");
                startWinningActivity();
            }
        });
    }

    /**
     * Switch to state winning.
     */
    private void startWinningActivity(){
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("pathLength", pathLength);
        Log.v(log, "start WinningActivity. ");
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