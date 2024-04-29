package Tanks;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.concurrent.ThreadLocalRandom;

import jogamp.nativewindow.windows.PIXELFORMATDESCRIPTOR;

public class DrawTerrain {
    // public static File terrain_data;
    public String layout_path; 
    public String background_path;
    public String foreground_colour;
    public String trees_path;
    public static final int PIXEL_WIDTH = 32;
    public static final int PIXEL_HEIGHT = 32;
    public static int terrain_width = App.WIDTH;

    // Each index corresponds to x-coordinate, and the y-coordinate is the value in that space.
    // E.g., [0,0,15,20,20,20], at index=2, x=2 and y=15 (in pixels)
    public int[] terrain_av0;
    public int[] terrain_av1;
    public static int[] terrain_av2;
    // private boolean yes = true;

    // Data Structure //
    // Each 2 spaced list represents x and y-coordinate of tree on map
    public int[][] tree_locations;
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
        // if (end - start == 32) {
        float num = 0;
        for (int i=start; i<end; i++){
            num += (float)arr[i];
        }
        return num/32;
        // } // raise exception or smth if gap isn't 32

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
        int[][] tree_locations = new int[27][2];
        // // Actually draw the BLOCKY terrain from the array
        // for (int row=0; row<20; row++) {
        //     System.out.println();
        //     for (int col=0; col<27; col++) {
        //         // CHANGE TO COL<28 IF NEEDED
        //         // size, colour, position
        //         // app.size(PIXEL_WIDTH, PIXEL_HEIGHT);

        //         // Draw the units of land at the top
        //         if (drawing_data[row][col] == 'X') {

        //             // fill land downwards iteratively below where the 'X' appears
        //             for (int i=row; i<20; i++) {
        //                 app.stroke(255, 255, 255);
        //                 app.fill(255, 255, 255);
        //                 int x_coord = col * PIXEL_WIDTH;
        //                 int y_coord = i * PIXEL_HEIGHT;

        //                 app.rect(x_coord, y_coord, PIXEL_WIDTH, PIXEL_HEIGHT);
        //             }
        //         }
        //     }
        // }
        
        // Draw it by pixel by averaging it two times
        terrain_av0 = new int[App.WIDTH + PIXEL_WIDTH];
        terrain_av1 = new int[App.WIDTH + PIXEL_WIDTH];
        terrain_av2 = new int[App.WIDTH + PIXEL_WIDTH];


        // know whether to setup player list or not
        if (App.all_players.size() == 0) {
            // App.all_players = 
            setup_player_arr = true;
            // System.out.println("womp");
        }
        // SCANNING ITEMS LOOPS
        for (int col=0; col<28; col++) {
            // System.out.println(terrain_av0.length);
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

                        // Random numbers - COMMENTED OUT SO FAR.
                        // int randomNum = ThreadLocalRandom.current().nextInt(0, 30+1);
                        // int x_tree = col*PIXEL_WIDTH + PIXEL_WIDTH/2 + randomNum;

                        // born in the centre of the column (32/2=16)
                        int x_tree = col*PIXEL_WIDTH;
                        // int y_tree = row * PIXEL_HEIGHT;

                        tree_locations[numberOfTrees][0] = x_tree; 
                        numberOfTrees++;
                    }//!(String.valueOf(drawing_data[row][col]).isEmpty())
                } else if (checkArrContains(App.valid_players, drawing_data[row][col])) {
                    // must be player
                    if (setup_player_arr) {
                        char player_name = drawing_data[row][col];
                        // find colour of this player

                        App.all_players.add(new Player(col * PIXEL_WIDTH, row * PIXEL_HEIGHT, player_name, App.colours.getString(String.valueOf(player_name))));
                    } else if (!setup_player_arr) {
                        System.out.println("the show must go on.");
                    }

                    // setupPlayers();
                    
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


        // // Draw values onto terrain - DEV DEBUGGING//
        // if (yes) {
        //     for (int i=0; i<terrain_av0.length; i++) {
        //         System.out.println(terrain_av0[i]);
        //     }
        //     yes = false;
        // }

        // Draw the terrain
        int[] rgb_list = unwrapRGB(foreground_colour);
        int R = rgb_list[0];
        int G = rgb_list[1];
        int B = rgb_list[2];
        app.stroke(R, G, B);
        app.fill(R, G, B);
        app.beginShape();
        // start colouring from the bottom left, and end bottom right
        app.vertex(0, app.HEIGHT);
        for (int i=0; i < terrain_av2.length; i++) {
            app.vertex(i, terrain_av2[i]);
        }
        app.vertex(app.WIDTH, app.HEIGHT);
        app.endShape();


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
        }

        // Insert the tanks //

        for (Player player : App.all_players) {
            player.drawTank(app);
        }

        setup_player_arr = false;
        return;        
        // draws the actual ground

        // null check foreground colour
    }
}
