package Tanks;

import java.util.concurrent.ThreadLocalRandom;
import java.lang.Math;

public class Player {
    public int x_pos;
    public boolean float_down = false;
    public int y_pos;
    public char name;
    public int fuel = 250;
    public int PIXEL_WIDTH = 32;
    public int PIXEL_HEIGHT = 32;
    public static int tank_width = 20;
    public float turret_angle = App.PI;
    public static float[] angle_limits = {App.PI/2, 3*App.PI/2};
    public String colour;
    public int power = 50;
    public int health = 100;
    public int parachutes = 3;

    public Player(int x, int y, char name, String colour) {
        // position are by the pixel -bottom left of the col,row access
        this.x_pos = x;
        this.y_pos = y;
        this.name = name;
        this.colour = colour;
    }

    public void drawTank(App app) {
        // Draw tank onto the map

        if (!(this.colour.equals("random"))) {
            int[] rgb = DrawTerrain.unwrapRGB(this.colour);
            app.stroke(rgb[0],rgb[1],rgb[2]);
            app.fill(rgb[0],rgb[1],rgb[2]);
        } else if (this.colour.equals("random")) {
            int[] rgb = {ThreadLocalRandom.current().nextInt(0, 256), ThreadLocalRandom.current().nextInt(0, 256), ThreadLocalRandom.current().nextInt(0, 256)};
            app.stroke(rgb[0],rgb[1],rgb[2]);
            app.fill(rgb[0],rgb[1],rgb[2]);
        }
       
        int tank_y = this.y_pos;
        int tank_width = 20;
        int tank_x = this.x_pos - tank_width/2;

        // base
        app.rect(tank_x, tank_y, tank_width, tank_width*1/6);

        // 2nd base on top - the most slightly not aligned,,,... hardcode the +1
        int second_width = tank_width*2/3 + 1; 
        app.rect(tank_x + tank_width*1/6, tank_y-tank_width*1/6, second_width, tank_width*1/6);

        // turret
        app.stroke(0,0,0);
        app.fill(0,0,0);
        int turret_width = tank_width/5;
        // translate to rotate around the specified coordinate (centre bottom of turret)

        // PROCESS TO ROTATE AN INDIVIDUAL SHAPE WITHIN A COORDINATE ROTATING SYSTEM - CONSIDER TRAFFIC SIMULATOR
        // rotates around origin coordinates, so move the centre to the tank body and rotate turret around that
        app.translate(tank_x + tank_width/2 + turret_width/2, tank_y);
        app.rotate(this.turret_angle);

        // translated position is now the origin
        app.rect(0, 0, turret_width, tank_width*2/3);
        

        // important to draw the other shapes normally - finished rotating your piece
        app.resetMatrix();
      
    }
    public void changeTurretAngle(float rad_change) {
        if (this.turret_angle+rad_change <= angle_limits[1] && this.turret_angle+rad_change >= angle_limits[0]) {
            this.turret_angle = (float)(this.turret_angle + rad_change);
        }
    }
    
    public void moveTank(boolean left) {
        // change x position of tank (and y) depending if there's fuel left
        if (fuel > 0) {
            if (left) {
                this.x_pos = this.x_pos - 2;
            } else {
                this.x_pos = this.x_pos + 2;
            }
            this.y_pos = DrawTerrain.terrain_av2[this.x_pos];
            fuel = fuel - 2;
        }
    }
    public void changeTankPower(boolean increase) {
        if (increase) {
            // increase power by 1 if less than health - health always <100
            if (this.power < this.health) {
                this.power++;
            }
        } else {
            // decrease power by 1
            if (this.power > 0) {
                this.power --;
            }

        }
    }
}
