package Tanks;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

import java.util.concurrent.ThreadLocalRandom;

import jogamp.nativewindow.windows.PIXELFORMATDESCRIPTOR;

public class DrawTerrain {
    public int[][] tree_locations = new int[27][2];

    public static int[] colourA;
    public static int[] colourB;
    public static int[] colourC;
    public static int[] colourD;
    public static boolean first_pass = true;

    public String layout_path; 
    public String background_path;
    public String foreground_colour;
    public String trees_path;
    public static final int PIXEL_WIDTH = 32;
    public static final int PIXEL_HEIGHT = 32;
    public static int terrain_width = App.WIDTH;
    public int top_height = 24;
    public static int wind = ThreadLocalRandom.current().nextInt(-35, 35+1);

    // Each index corresponds to x-coordinate, and the y-coordinate is the value in that space.
    // E.g., [0,0,15,20,20,20], at index=2, x=2 and y=15 (in pixels)
    public int[] terrain_av0;
    public int[] terrain_av1;
    public static int[] terrain_av2;
    // private boolean yes = true;

    // keeps track of how many position have been filled for calculation purposes
    public int numberOfTrees = 0;

    //know whether to setup players in list
    public boolean setup_player_arr = false;



    public DrawTerrain(String layout_path, String background_path, String foreground_colour, String trees_path) {
        this.layout_path = layout_path;
        this.background_path = background_path;
        this.foreground_colour = foreground_colour;
        this.trees_path = trees_path;
    }

    public boolean checkArrContains(char[] arr, char target) {
        boolean contains = false;
        for (char i : arr) {
            if (i == target)
                contains = true;
        }
        return contains;
        
    }

    public char[][] readLevel() {
        // maps positions of items from text file to array, which is later used
        char[][] drawing_data = new char[20][28];
        
        // Read terrain
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.layout_path));
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null && row < 20) {
                for (int col = 0; col < Math.min(28, line.length()); col++) {
                    drawing_data[row][col] = line.charAt(col);
                }
                row++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return drawing_data;
    }

    public static float average32(int start, int end, int[] arr) {
        // start is inclusive, end is exclusive
        float num = 0;
        for (int i=start; i<end; i++){
            num += (float)arr[i];
        }
        return num/32;
    }

    public static int[] unwrapRGB(String rgbString) {
        // Turns "255,0,0" to [255, 0, 0]
        String[] parts = rgbString.split(",");
        int[] rgb = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            rgb[i] = Integer.parseInt(parts[i].trim()); // trim to remove any extra spaces
        }

        return rgb;
    }
    public void drawForeground(App app) {

        char[][] drawing_data = readLevel();
        
        // Draw it by pixel by averaging it two times
        terrain_av0 = new int[App.WIDTH + PIXEL_WIDTH];
        terrain_av1 = new int[App.WIDTH + PIXEL_WIDTH];
        terrain_av2 = new int[App.WIDTH + PIXEL_WIDTH];


        // know whether to setup player list or not
        if (app.all_players.size() == 0) {
            setup_player_arr = true;
        }
        // SCANNING ITEMS LOOPS
        for (int col=0; col<28; col++) {
            // rintln(terrain_av0.length);
            for (int row=0; row<20; row++) {

                // Draw the units of land at the top
                if (drawing_data[row][col] == 'X') {

                    // record pixels in list to average later.
                    for (int i=0; i<PIXEL_WIDTH; i++) {
                        // Record the placement of 32 values (pixels) in terrain_av0

                        // This shifts the y-position according to its correct column
                        int index = i + (col*PIXEL_WIDTH);

                        // calculation to find pixel height based on row increasing top to down screen
                        terrain_av0[index] = row * PIXEL_HEIGHT;
                    }
                } else if (drawing_data[row][col] == 'T') {
                    if (this.trees_path != null) {
                        // if null, just ignore the T

                        // born in the centre of the column (32/2=16)
                        int x_tree = col*PIXEL_WIDTH;
                        // int y_tree = row * PIXEL_HEIGHT;

                        tree_locations[numberOfTrees][0] = x_tree; 
                        numberOfTrees++;
                    }

                } else if (checkArrContains(App.valid_players, drawing_data[row][col])) {
                    // must be player
                    if (setup_player_arr) {
                        char player_name = drawing_data[row][col];
                        // find colour of this player

                        app.all_players.add(new Player(col * PIXEL_WIDTH, row * PIXEL_HEIGHT, player_name, App.colours.getString(String.valueOf(player_name))));
                    } else if (!setup_player_arr) {}                    
                }
            }
        }

        // Average values one time //
        for (int pixel=0; pixel<terrain_av1.length - PIXEL_WIDTH; pixel++) {
            terrain_av1[pixel] = (int)average32(pixel, pixel+PIXEL_WIDTH, terrain_av0);
        }
        // for simplicity, match the last portion of pixels not displayed
        for (int pixel=terrain_av1.length - PIXEL_WIDTH; pixel<terrain_av1.length; pixel++) {
            terrain_av1[pixel] = terrain_av0[pixel];
        }

        // Average values a second time //
        for (int pixel=0; pixel<terrain_av2.length - PIXEL_WIDTH; pixel++) {
            terrain_av2[pixel] = (int)average32(pixel, pixel+PIXEL_WIDTH, terrain_av1);
        }
        for (int pixel=terrain_av2.length - PIXEL_WIDTH; pixel<terrain_av2.length; pixel++) {
            terrain_av2[pixel] = terrain_av1[pixel];
        }

        continueDrawing(app);
        setup_player_arr = false;

        
        return;        

    }

    public void continueDrawing(App app) {
        // terrain_av2 cannot be recalculated each time from the file. allow explosions etc.

        // Draw the terrain
        int[] rgb_list = unwrapRGB(foreground_colour);
        int R = rgb_list[0];
        int G = rgb_list[1];
        int B = rgb_list[2];
        app.stroke(R, G, B);
        app.fill(R, G, B);
        app.beginShape();
        // start colouring from the bottom left, and end bottom right
        app.vertex(0, App.HEIGHT);
        for (int i=0; i < terrain_av2.length; i++) {
            app.vertex(i, terrain_av2[i]);
        }
        app.vertex(App.WIDTH, App.HEIGHT);
        app.endShape();

        // Insert the tanks //
        for (Player player : app.all_players) {
            player.drawTank(app);
        }

        // assign current player //
        app.currentPlayer = app.all_players.get(app.currentIndexPlayer);

        //////// Insert the logos/symbols on the screen /////////
        // Player turn //
        app.fill(0,0,0);
        // params are content, x-coord of text, y-coord
        String display_player = "Player " + app.currentPlayer.name + "'s turn";
        app.text(display_player, 20, top_height);

        // fuel //
        PImage fuel = app.loadImage("build/resources/main/Tanks/fuel.png");
        int fuel_logo_size = 25;
        int fuel_x = 150;
        app.image(fuel, fuel_x, 5, fuel_logo_size, fuel_logo_size);
        app.text(Integer.toString(app.currentPlayer.fuel), fuel_x + 25, fuel_logo_size);

        // health //
        int x_health = 380;
        String display_health = "Health:";
        app.text(display_health, x_health, top_height);

        int[] colour = unwrapRGB(app.currentPlayer.colour);

        app.fill(colour[0], colour[1], colour[2]);
        app.stroke(0, 0, 0);
        // make sure all constants in expression are of same type, cast if needed
        int current_h = (int)((8.0/5.0) * (double)app.currentPlayer.health);
        app.rect(x_health + 55, 8, current_h, 20);

        // check power is below new health
        if (app.currentPlayer.health < app.currentPlayer.power) {
            app.currentPlayer.power = app.currentPlayer.health;
        }

        // wind
        int wsize = 50;
        if (wind >= 0) {
            PImage wind_img = app.loadImage("build/resources/main/Tanks/wind.png");
            app.image(wind_img, App.WIDTH - 2 * wsize, 5, wsize, wsize);
        }
        if (wind < 0) {
            PImage wind_img = app.loadImage("build/resources/main/Tanks/wind-1.png");
            app.image(wind_img, App.WIDTH - 2 * wsize, 5, wsize, wsize);
        }

        String display_wind = String.valueOf(Math.abs(wind));

        app.stroke(0, 0, 0);
        app.fill(0, 0, 0);
        app.text(display_wind, App.WIDTH - wsize + 10, top_height * 3/2);

        app.stroke(0, 0, 0);
        app.fill(0, 0, 0);
        String health_num = Integer.toString(app.currentPlayer.health);
        app.text(health_num, x_health + 55 + 160 + 8, top_height);



        // use floating point literal constants i.e., 5.0/8.0 not 5/8 --> 0. something and the something is removed because it is declared as an 'int'
        double line_x = (8.0 / 5.0) * (app.currentPlayer.power);

        // grey box
        app.stroke(211, 211, 211);
        app.fill(211, 211, 211);
        app.rect(x_health + 55, 8, (int)line_x, 20);

        // grey box
        int diff = 3;
        app.stroke(colour[0], colour[1], colour[2]);
        app.fill(colour[0], colour[1], colour[2]);
        app.rect(x_health + 55 + diff, 8 + diff, (int)line_x - 2*diff, 20 - 2*diff);

        app.stroke(255, 0, 0);
        app.fill(255, 0, 0);
        app.line((int)line_x + x_health + 55, 5, (int)line_x + x_health + 55, 31);


        // Power
        app.fill(0, 0, 0);
        String display_power = "Power: " + app.currentPlayer.power;
        app.text(display_power, x_health, 2 * top_height);


        // scoreboard //
        app.stroke(0, 0, 0);

        app.strokeWeight(6);
        app.noFill();
        app.rect(App.WIDTH - 3 * wsize, 5 + wsize, 135, 112);
        app.fill(0, 0, 0);
        app.text("Scores", App.WIDTH - 3 * wsize + 5, 5 + wsize + 20);
        app.line(App.WIDTH - 3 * wsize, 5 + wsize + 25, App.WIDTH - 3 * wsize + 135, 5 + wsize + 25);
        // 25 y - coords difference

        if (first_pass) {
            DrawTerrain.colourA = DrawTerrain.unwrapRGB(app.all_players.get(0).colour);
            DrawTerrain.colourB = DrawTerrain.unwrapRGB(app.all_players.get(1).colour);
            DrawTerrain.colourC = DrawTerrain.unwrapRGB(app.all_players.get(2).colour);
            DrawTerrain.colourD = DrawTerrain.unwrapRGB(app.all_players.get(3).colour);

            first_pass = false;
        }
        
        app.fill(colourA[0], colourA[1], colourA[2]);
        app.text("Player A", App.WIDTH - 3 * wsize + 5, 5 + wsize + 45);

        app.fill(colourB[0], colourB[1], colourB[2]);
        app.text("Player B", App.WIDTH - 3 * wsize + 5, 5 + wsize + 65);

        app.fill(colourC[0], colourC[1], colourC[2]);
        app.text("Player C", App.WIDTH - 3 * wsize + 5, 5 + wsize + 85);

        app.fill(colourD[0], colourD[1], colourD[2]);
        app.text("Player D", App.WIDTH - 3 * wsize + 5, 5 + wsize + 105);

        // player scores
        app.fill(0, 0, 0);
        app.text(App.A_score, App.WIDTH - 3 * wsize + 100, 5 + wsize + 45);
        app.text(App.B_score, App.WIDTH - 3 * wsize + 100, 5 + wsize + 65);
        app.text(App.C_score, App.WIDTH - 3 * wsize + 100, 5 + wsize + 85);
        app.text(App.D_score, App.WIDTH - 3 * wsize + 100, 5 + wsize + 105);


        app.strokeWeight(1);

        // Insert the trees //
        if (this.trees_path != null) {
            PImage tree = app.loadImage("build/resources/main/Tanks/" + this.trees_path);
            for (int treeCount=0; treeCount<=numberOfTrees; treeCount++) {
                // index 0 --> x-coordinate
                // index 1 --> calculate so it sits on the slope rather than floating
                int x_tree_dimensions = tree_locations[treeCount][0];
                // the middle touches the slope and not left edge
                int y_tree_dimensions = terrain_av2[x_tree_dimensions];
                app.image(tree, x_tree_dimensions- PIXEL_WIDTH/2, y_tree_dimensions - PIXEL_HEIGHT, PIXEL_WIDTH, PIXEL_HEIGHT);
            }
            numberOfTrees = 0;
        }
    }
}
