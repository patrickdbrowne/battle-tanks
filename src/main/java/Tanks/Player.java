package Tanks;

import java.util.concurrent.ThreadLocalRandom;

public class Player {
    public int x_pos;
    public int y_pos;
    public char name;
    public int PIXEL_WIDTH = 32;
    public int PIXEL_HEIGHT = 32;
    public int tank_width = 20;
    public String colour;


    public Player(int x, int y, char name, String colour) {
        // position are by the pixel -bottom left of the col,row access
        this.x_pos = x;
        this.y_pos = y;
        this.name = name;
        this.colour = colour;
        // this.tank_width = 5/8 * 32;
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
        // app.fill(0,0,0);


        // app.rectMode(CENTER);
        // System.out.println("hi");
        // given the x-coordinate, see which y-coordinate on the smooth plane it corresponds to
        int tank_y = DrawTerrain.terrain_av2[this.x_pos];
        int tank_width = 20;
        // System.out.println();
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
        app.rect(tank_x + tank_width/2 - turret_width/2, tank_y-tank_width*5/6, turret_width, tank_width*2/3);



        // // turret
        // app.ellipse(this.x_pos, this.y_pos - 15, 40, 40);

        // // gun barrel
        // app.ellipse(this.x_pos + 30, this.y_pos - 15, 40, 10);

        // // tracks
        // app.fill(50); // Darker shade for the tracks
        // app.rect(this.x_pos, this.y_pos + 25, 110, 15);
        // app.rect(this.x_pos, this.y_pos - 25, 110, 15);

        // // Wheels
        // app.fill(0); // Wheels are black
        // app.ellipse(this.x_pos - 40, this.y_pos + 25, 20, 20);
        // app.ellipse(this.x_pos, this.y_pos + 25, 20, 20);
        // app.ellipse(this.x_pos + 40, this.y_pos + 25, 20, 20);
        // app.ellipse(this.x_pos - 40, this.y_pos - 25, 20, 20);
        // app.ellipse(this.x_pos, this.y_pos - 25, 20, 20);
        // app.ellipse(this.x_pos + 40, this.y_pos - 25, 20, 20);

        
    }
}
