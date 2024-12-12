package Tanks;

public class Projectile {
    
    public int x;
    public int y;
    public int power;
    public Player shooter;
    // public float angle;
    public App app;
    public double gravity = 3.6;
    public double x_speed;
    public double y_speed;
    public boolean initial_launch = true;
    public double constant;

    public int x_initial;
    public int y_initial;
    public double magnitude;
    public double x_velocity;
    public double y_velocity;
    public double angle;
    public int[] colour;


    public Projectile(App app, Player shooter) {
        this.shooter = shooter;
        this.x_initial = shooter.x_pos;
        this.y_initial = shooter.y_pos;

        // minimum of 1, maximum of 9
        this.magnitude = (int)((8*shooter.power)/100 + 1);
        // angle to the positive x-axis. refer to: http://btk.tillnagel.com/tutorials/rotation-translation-matrix.html
        this.angle = ((3*Math.PI)/2) - shooter.turret_angle;
        this.x_velocity = this.magnitude * Math.cos(this.angle);
        this.y_velocity = -(this.magnitude * Math.sin(this.angle));
        this.colour = DrawTerrain.unwrapRGB(shooter.colour);

    }

    public int[] calculatePositions(double t) {
        // assumptions: 
        // - angle is to the positive x-axis
        // - velocity is the magnitude (the hypotenuse) in pixels/frame
        // - t is in frames (not seconds), and so are the other units)
        
        int[] xy_positions = new int[2];
        double gravity = -3.6/App.FPS;

        xy_positions[0] = (int)(this.x_initial + this.x_velocity * t + (int)(0.03 * ((double)DrawTerrain.wind/App.FPS) * (double)Math.pow(t, 2)));
        xy_positions[1] = (int)(this.y_initial + this.y_velocity * t - (0.5 * gravity * (Math.pow(t, 2))));

        return xy_positions;
    }

    // validate + calculate the next move. If not valid, explode and end
    public static boolean toExplode(int x, int y) {
        // checks if projectile above ground and within screen edges. false means keep moving, true means explode the ball
        // ignore if above screen
        boolean toExplode;
        boolean inside;
        boolean projectile_above;

        inside = (x + App.radius < App.WIDTH) && (x - App.radius > 0) && (y - App.radius < App.HEIGHT);
        if (inside) { 
            // consider when centre is out of screen, but radius is in screen -x coordinate
            
            // centre is compared by default
            projectile_above = y <= DrawTerrain.terrain_av2[x];
        } else {
            projectile_above = false;
        }

            
        if (projectile_above && inside) {
            toExplode = false;

        } else {
            toExplode = true;
        }
            return toExplode;
        }
}
