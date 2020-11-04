package edu.wm.cs.cs301.ShuhongWang.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.ShuhongWang.R;
import edu.wm.cs.cs301.ShuhongWang.generation.DataHolder;
import edu.wm.cs.cs301.ShuhongWang.generation.Maze;

public class PlayManuallyActivity extends AppCompatActivity {
    private Button forward;
    private Button jump;
    private Button left;
    private Button right;
    private ToggleButton showSolution;
    private ToggleButton mapMode;
    private ToggleButton showWalls;
    private Button zoomIn;
    private Button zoomOut;
    private Button shortCut;

    private int pathLength;
    private int shortestPathLength;
    private String log = "PlayManuallyActivity";
//    private TextView txtMaze;

    private MazePanel mazePanel;
    private StatePlaying statePlaying;
    private Maze maze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manually);

        pathLength = 0;

        mazePanel = findViewById(R.id.mazePanel);
        statePlaying = new StatePlaying();
        maze = DataHolder.getInstance().getMazeConfig();
        statePlaying.setMazeConfiguration(maze);
        statePlaying.setPlayManuallyActivity(this);

        int[] start = maze.getStartingPosition();
        shortestPathLength = maze.getDistanceToExit(start[0], start[1]);

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

        Log.v(log, "Start Playing Activity");
        statePlaying.start(mazePanel);

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
                statePlaying.keyDown(Constants.UserInput.Up);
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
                statePlaying.keyDown(Constants.UserInput.Jump);
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
                statePlaying.keyDown(Constants.UserInput.Left);
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
                statePlaying.keyDown(Constants.UserInput.Right);
                Log.v(log, "Clicked right.");
//                txtMaze.setText("Turn right.");
            }
        });
    }

    /**
     * Set up the toggle button to toggle the solution.
     */
    private void setToggleSolution() {
        showSolution = (ToggleButton) findViewById(R.id.tb_solution);
        showSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statePlaying.keyDown(Constants.UserInput.ToggleSolution);
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
        mapMode = (ToggleButton) findViewById(R.id.tb_map);
        mapMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statePlaying.keyDown(Constants.UserInput.ToggleLocalMap);
                Log.v(log, "Toggled map.");
//                if (map.isChecked()){
//                    txtMaze.setText("Map On.");
//                }
//                else
//                    txtMaze.setText("Map Off.");
//                }
            }
        });
    }

    /**
     * Set up the toggle button to toggle the walls.
     */
    private void setToggleWalls() {
        showWalls = (ToggleButton) findViewById(R.id.tb_walls);
        showWalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statePlaying.keyDown(Constants.UserInput.ToggleFullMap);
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
        zoomOut = (Button) findViewById(R.id.button_zoomOut);
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
    public void startWinningActivity(){
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("pathLength", pathLength);
        intent.putExtra("shortestPathLength", shortestPathLength);
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