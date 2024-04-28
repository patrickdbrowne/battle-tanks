package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();
    public static JSONObject json_data;
    public String layout;
    public String background;
    public String foreground_colour;
    public String trees;
    public static JSONArray all_levels;
    // below is not HashMap<String, String> type
    public JSONObject current_level_data;
    public boolean runonce = true;
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);
		//See PApplet javadoc:
		//loadJSONObject(configPath)
		//loadImage(this.getClass().getResource(filename).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));

        //all_levels: is the array of details for each level
        //current_level: the details of one element in the array all_levels (one level)
        //layout, background etc. the specific detail in the current_level starting at level 1

        json_data = loadJSONObject("config.json");
        all_levels = json_data.getJSONArray("levels");
        current_level_data = all_levels.getJSONObject(0);
        
        // ! details of the current level - if empty null is returned
        layout = current_level_data.getString("layout");
        background = current_level_data.getString("background");
        foreground_colour = current_level_data.getString("foreground-colour");
        trees = current_level_data.getString("trees");
        
        // load images //
        size(WIDTH, HEIGHT);
        PImage background_image = loadImage("build/resources/main/Tanks/" + background);
        image(background_image, 0, 0);




        // Setting up from blocky level the first time
        DrawTerrain level = new DrawTerrain(layout, background, foreground_colour, trees);
        level.drawForeground(this);


        // System.out.println(current_level_data);

    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //TODO - powerups, like repair and extra fuel and teleport


    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        

        //----------------------------------
        //display HUD:
        //----------------------------------
        // draw terrain //

        
        // Don't display last column, 28, only used for moving average

        // if (runonce) {
        // for (int row=0; row<20; row++) {
        //     System.out.println();
        //     for (int col=0; col<27; col++) {
        //         // CHANGE TO COL<28 IF NEEDED
        //         // System.out.print(drawing_map[row][col]);
                
            
            
        //     }
        // }
        //     runonce = false;
        // }



        // System.out.print

        //----------------------------------
        //display scoreboard:
        //----------------------------------
        //TODO
        
		//----------------------------------
        //----------------------------------

        //TODO: Check user action
    }


    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
