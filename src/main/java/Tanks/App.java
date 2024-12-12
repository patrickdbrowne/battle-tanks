// r -20 points for +20 hp
// r to restart at the end
// f -10 points for +200 fuel
// sound effect wilhelm scream and vine boom ofc
// power - w/s
// arrows for turret and movement

package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import ddf.minim.*;


import java.applet.*; 
import java.net.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class App extends PApplet {

    public Minim minim;
    public AudioPlayer player;
    public Minim minim2;
    public AudioPlayer player2;
    public double t_ani = 0;
    public static boolean animation = false;
    public static int current_index;
    public static int A_score;
    public static int B_score;
    public static int C_score;
    public static int D_score;
    public static boolean A_exists = true;
    public static boolean B_exists = true;
    public static boolean C_exists = true;
    public static boolean D_exists = true;
    public static boolean endgame = false;



    public Player shooter;
    public int remove_at_index = -10;
    public DrawTerrain level;
    public static PFont f;
    public static PImage background_image;
    public static int test_x = 1;
    public int current_level = 0;

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; 
    public static int HEIGHT = 640; 
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

    // keep track of each tank
    public ArrayList<Player> all_players = new ArrayList<Player>();

    public static char[] valid_players = {'A','B','C','D','E','F','G','H','I','0','1','2','3','4','5','6','7','8','9'};
    public static JSONObject colours;

    public Player currentPlayer;
    public int currentIndexPlayer = 0;
    public boolean projectile_fired = false;
    public Projectile new_proj;
    public int proj_x;
    public int proj_y;
    public int stopper_value = 100000;
    public double t;
    public static int radius = 6;
    public boolean explosion = false;
    public double t_explode = 0;
    public boolean explode;
    public int[] positions;
    public double explosion_time = (FPS/5);
    public double float_move = (60/FPS); // every frame, descends by 2 pixels translating to 60p per second.
	
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

        minim = new Minim(this);
        player = minim.loadFile("build/resources/main/Tanks/Vine-boom-sound-effect.mp3");

        minim2 = new Minim(this);
        player2 = minim2.loadFile("build/resources/main/Tanks/scream.wav");
        // font of displayed text // 3rd param is antialiasing, and PFont.list() shows available fonts.
        f = createFont("Arial", 16, true);
        textFont(f, 16);

        //all_levels: is the array of details for each level
        //current_level: the details of one element in the array all_levels (one level)
        //layout, background etc. the specific detail in the current_level starting at level 1

        json_data = loadJSONObject("config.json");        
        all_levels = json_data.getJSONArray("levels");
        current_level_data = all_levels.getJSONObject(current_index);
        
        // ! details of the current level - if empty null is returned
        layout = current_level_data.getString("layout");
        background = current_level_data.getString("background");
        foreground_colour = current_level_data.getString("foreground-colour");
        trees = current_level_data.getString("trees");
        
        // load images //
        size(WIDTH, HEIGHT);
        background_image = loadImage("build/resources/main/Tanks/" + background);
        image(background_image, 0, 0);

        // colour map
        colours = json_data.getJSONObject("player_colours");



        // Setting up from blocky level the first time
        level = new DrawTerrain(layout, background, foreground_colour, trees);
        level.drawForeground(this);

        // for projectile to release from correct initial position 
        for (Player i : all_players) {
            i.y_pos = DrawTerrain.terrain_av2[i.x_pos];
        }

    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        // powerups
        // System.out.println(event);
        if (keyCode == 70) {
            switch (this.currentPlayer.name) {
                case 'A':
                    if (A_score > 10) {
                        A_score = A_score - 10;
                        this.currentPlayer.fuel = this.currentPlayer.fuel + 200;
                    }
                    break;
            
                case 'B':
                    if (B_score > 10) {
                        B_score = B_score - 10;
                        this.currentPlayer.fuel = this.currentPlayer.fuel + 200;
                    }
                    break;

                case 'C':
                    if (C_score > 10) {
                        C_score = C_score - 10;
                        this.currentPlayer.fuel = this.currentPlayer.fuel + 200;
                    }
                    break;

                case 'D':
                    if (D_score > 10) {
                        D_score = D_score - 10;
                        this.currentPlayer.fuel = this.currentPlayer.fuel + 200;
                    }

                    break;
            }
        }
        if (keyCode == 82 && !endgame) {
            // health +20 for -20 points
            switch (this.currentPlayer.name) {
                case 'A':
                    if (A_score > 20 && this.currentPlayer.health < 100) {
                        A_score = A_score - 20;
                        if (this.currentPlayer.health <= 80) {
                            this.currentPlayer.health = this.currentPlayer.health + 20;
                        } else {
                            this.currentPlayer.health = 100;
                        }
                    }
                    break;
            
                case 'B':
                    if (B_score > 20 && this.currentPlayer.health < 100) {
                        B_score = B_score - 20;
                        if (this.currentPlayer.health <= 80) {
                            this.currentPlayer.health = this.currentPlayer.health + 20;
                        } else {
                            this.currentPlayer.health = 100;
                        }
                    }
                    break;

                case 'C':
                    if (C_score > 20 && this.currentPlayer.health < 100) {
                        C_score = C_score - 20;
                        if (this.currentPlayer.health <= 80) {
                            this.currentPlayer.health = this.currentPlayer.health + 20;
                        } else {
                            this.currentPlayer.health = 100;
                        }
                    }
                    break;

                case 'D':
                    if (D_score > 20 && this.currentPlayer.health < 100) {
                        D_score = D_score - 20;
                        if (this.currentPlayer.health <= 80) {
                            this.currentPlayer.health = this.currentPlayer.health + 20;
                        } else {
                            this.currentPlayer.health = 100;
                        }
                    }

                    break;
            }
        }
        if (keyCode == 82 && endgame) {
            // restart
            animation = false;
            current_index = 0;
            A_score = 0;
            B_score = 0;
            C_score = 0;
            D_score = 0;
            A_exists = true;
            B_exists = true;
            C_exists = true;
            D_exists = true;
            endgame = false;

            current_index = 0;
            endgame = false;
            PApplet.main("Tanks.App");


        }
        if (keyCode == 87) {
            // W is hit
            currentPlayer.changeTankPower(true);
        }
        if (keyCode == 83) {
            // S is hit
            currentPlayer.changeTankPower(false);
        }

        if (keyCode == 40) {
            // up arrow- move turret moves left
            currentPlayer.changeTurretAngle((float)3/FPS);

        } if (keyCode == 38) {
            // down arrow - move turret right
            currentPlayer.changeTurretAngle((float)-3/FPS);

        } if (keyCode == 32 && !projectile_fired) {

            projectile_fired = true;
            t = 0;
            new_proj = new Projectile(this, currentPlayer);
            shooter = currentPlayer;
            currentIndexPlayer = (currentIndexPlayer + 1) % all_players.size();
            DrawTerrain.wind = DrawTerrain.wind + ThreadLocalRandom.current().nextInt(-5, 5+1);
            // if (DrawTerrain.wind + wind_add >= -35 && DrawTerrain.wind + wind_add <= 35)
            //     DrawTerrain.wind = DrawTerrain.wind + wind_add;

        } if (keyCode == 39 && !projectile_fired) {
            // move right - right key hit
            currentPlayer.moveTank(false);

        } if (keyCode == 37 && !projectile_fired) {
            // move left - left key hit
            currentPlayer.moveTank(true);

        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        
        // image must be placed here, so the projectile can be displayed and not covered by background image
        // background image placed first.
        if (!animation) {
            image(background_image, 0, 0);
            level.continueDrawing(this);

            
            // float down necessary tanks
            for (Player i : all_players) {
                if (i.float_down == true) {

                    // check if next descent hits ground
                    if (i.y_pos >= DrawTerrain.terrain_av2[i.x_pos]) {
                        i.y_pos = DrawTerrain.terrain_av2[i.x_pos];
                        i.parachutes --;
                        i.float_down = false;
                    } else {

                        // load parachute
                        if (i.parachutes > 0) {
                            PImage para = loadImage("build/resources/main/Tanks/parachute.png");
                            int psize = 26;
                            image(para, i.x_pos - psize/2, i.y_pos - 3*Player.tank_width/2, psize, psize);
                            i.y_pos += float_move;
                        } else {
                            // drop down faster
                            i.y_pos += 2 * float_move;
                            i.health = i.health - (int)(2 * float_move);
                        }
                    }
                }

                // remove players when health removed or below screen
                if (i.health <= 0 || i.y_pos >= HEIGHT) {
                    remove_at_index = all_players.indexOf(i);
                }

            }



            if (projectile_fired) {
                // use units of frames instead of seconds. i.e., each time draw() is called, frame increases by 1.
                this.t ++;
            
                // cycle between predict <--> draw until explosion
                positions = new_proj.calculatePositions(t);

                explode = Projectile.toExplode(positions[0], positions[1]);
                
                if (explode) {
                    // explode the projectile
                    projectile_fired = false;
                    explosion = true;
                    this.t = 0;
                    this.t_explode = 0;
                }

                this.stroke(new_proj.colour[0], new_proj.colour[1], new_proj.colour[2]);
                this.fill(new_proj.colour[0], new_proj.colour[1], new_proj.colour[2]);
                this.ellipse(positions[0], positions[1], radius,radius);
                
            } 
            
            if (explosion) {
                player.rewind();
                player.play();

                int x_plode = positions[0];
                int y_plode = positions[1];

                // radius is percentage of FPS' elapsed
                // make sure fraction has double numerator and denominator
                int radius = (int)((this.t_explode/explosion_time) * 60); // change 60 for different sized explosion

                // the red, orange and yellow
                stroke(255, 0, 0);
                fill(255, 0, 0);
                ellipse(x_plode, y_plode, radius, radius);

                stroke(255, 165, 0);
                fill(255, 165, 0);
                ellipse(x_plode, y_plode, (int)(radius*0.5), (int)(radius*0.5));

                stroke(255, 255, 0);
                fill(255, 255, 0);
                ellipse(x_plode, y_plode, (int)(radius*0.2), (int)(radius*0.2));

                // increment frame
                this.t_explode = this.t_explode + 1;

                if (this.t_explode >= explosion_time) {

                    // remove land

                    // from further pixel to closest pixel in circle of damage (left to right), calculate the 
                    // hieght of the cord from the corresponding x value and the two y-values. Then, the new
                    // height is the sum of the land above and below this chord.

                    // 1. iterate each pixel //x_plode and y_plode represent coords of the circle's centre
                    // 2. calculate chord
                    // 3. find new height
                    // 4. apply new height in terrain_av2

                    for (int pixel=x_plode-radius; pixel<=x_plode+radius; pixel++) {
                        // find height of chord at x=pixel. Traverse left to right the diameter
                        if (pixel < 0 || pixel > App.WIDTH) {
                            continue;
                        }
                        int dist_from_radius = x_plode - pixel;
                        double y = Math.sqrt(Math.pow(radius, 2) - Math.pow(dist_from_radius, 2));// this is the positive solution

                        double height_chord = 2 * y;

                        if (DrawTerrain.terrain_av2[pixel] < y_plode - y) {
                            // land is fully above the circle of interest at the chord x
                            DrawTerrain.terrain_av2[pixel] = DrawTerrain.terrain_av2[pixel] + (int)height_chord;


                        } else if (DrawTerrain.terrain_av2[pixel] > y_plode - y && DrawTerrain.terrain_av2[pixel] < y_plode + y) {
                            // land is inside the circle of interest at chord x
                            int centre_from_pixel = DrawTerrain.terrain_av2[pixel] - y_plode;
                            int remove_part =  (int)y - centre_from_pixel;

                            DrawTerrain.terrain_av2[pixel] = DrawTerrain.terrain_av2[pixel] + remove_part;

                        } else {
                            // land is below chord and can be ignored
                            continue;
                        }

                    }

                    // for projectile to release from correct initial position 
                    for (Player i : all_players) {
                        // check if y of tank is equal to y of land. if not, float down = true and float to there
                        if (i.y_pos != DrawTerrain.terrain_av2[i.x_pos]) {
                            i.float_down = true;
                        }

                        // deal damage from explosion
                        int dist = Math.abs(i.x_pos - x_plode);
                        if (dist <= 30) {
                            i.health = i.health - (-2*dist + 60);
                            
                            // tally points
                            if (shooter.name != i.name) {
                                switch (shooter.name) {
                                    case 'A':
                                        A_score = A_score + (int)(-2*dist + 60);
                                        break;

                                    case 'B':
                                        B_score = B_score + (int)(-2*dist + 60);
                                        break;
                                    
                                    case 'C':
                                        C_score = C_score + (int)(-2*dist + 60);
                                        break;

                                    case 'D':
                                        D_score = D_score + (int)(-2*dist + 60);
                                        break;
                                }
                            }


                        }                        
                    }

                    explosion = false;
                }
            }

            // draw terrain //

            if (remove_at_index != -10) {

                all_players.remove(remove_at_index);
                player2.rewind();
                player2.play();

                // adjust current player
                if (remove_at_index < currentIndexPlayer) {
                    currentIndexPlayer --;
                }
                if (remove_at_index == currentIndexPlayer) {
                    currentIndexPlayer = (currentIndexPlayer) % all_players.size();
                }

                remove_at_index = -10;

                // endgame - move to next level, display winner etc.
                if (all_players.size() == 1) {

                    // display winner or smth, move to next level
                    current_index ++;
                    if (current_index < 3) {
                        PApplet.main("Tanks.App");
                    } else {
                        // TODO: endgame
                        animation = true;
                        t_ani = 0;
                    }
                }
            }

            currentPlayer = all_players.get(currentIndexPlayer);

            this.noStroke();

        } else {
            // roll the credits
            t_ani ++;
            int largest_score = A_score;
            char winner = 'A';
            int[] colour = DrawTerrain.colourA;
            
            if (B_score > largest_score) {
                winner = 'B';
                largest_score = B_score;
                colour = DrawTerrain.colourB;

            }
            if (C_score > largest_score) {
                winner = 'C';
                largest_score = C_score;
                colour = DrawTerrain.colourC;

            }
            if (D_score > largest_score) {
                winner = 'D';
                largest_score = D_score;
                colour = DrawTerrain.colourD;

            }

            f = createFont("Arial", 30, true);
            textFont(f, 30);
            fill(colour[0], colour[1], colour[2]);

            String msg = "Player " + Character.toString(winner) + " wins!";
            text(msg, App.WIDTH/3 - 30, 140);

            stroke(0, 0, 0);
            strokeWeight(6);
            fill(colour[0], colour[1], colour[2], (float)1);

            rect(App.WIDTH/3 - 50, 160, 400, 240);

            line(App.WIDTH/3 - 50, 220, App.WIDTH/3 - 50 + 400, 220);
            fill(0, 0, 0);
            text("Final Scores", App.WIDTH/3 - 30, 200);

            // display first, then second etc. 0.7s delays
            if (t_ani > FPS * 0.7) {
                fill(DrawTerrain.colourA[0], DrawTerrain.colourA[1], DrawTerrain.colourA[2]);
                text("Player A", App.WIDTH/3 - 30, 260);

                    
                fill(0, 0, 0);
                text(String.valueOf(A_score), App.WIDTH/3 + 300, 260);
            }

            if (t_ani > 2 * FPS * 0.7) {
                fill(DrawTerrain.colourB[0], DrawTerrain.colourB[1], DrawTerrain.colourB[2]);
                text("Player B", App.WIDTH/3 - 30, 300);

                fill(0, 0, 0);
                text(String.valueOf(B_score), App.WIDTH/3 + 300, 300);
            }

            if (t_ani > 3 * FPS * 0.7) {
                fill(DrawTerrain.colourC[0], DrawTerrain.colourC[1], DrawTerrain.colourC[2]);
                text("Player C", App.WIDTH/3 - 30, 340);

                fill(0, 0, 0);
                text(String.valueOf(C_score), App.WIDTH/3 + 300, 340);
            }

            if (t_ani > 4 * FPS * 0.7) {
                fill(DrawTerrain.colourD[0], DrawTerrain.colourD[1], DrawTerrain.colourD[2]);
                text("Player D", App.WIDTH/3 - 30, 380);

                fill(0, 0, 0);
                text(String.valueOf(D_score), App.WIDTH/3 + 300, 380);
                endgame = true;
            }

        }

    }

    public static void main(String[] args) {


        current_index = 0;
        PApplet.main("Tanks.App");
    }

}
