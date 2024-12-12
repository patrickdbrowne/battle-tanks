package Tanks;


import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SampleTest {

    @Test
    public void setupTest() {
        App app = new App();

        app.setup();

        assertEquals(app.configPath, "config.json");
    }
    @Test
    public void pressTest() {
        App app = new App();

        // app.setup();
        // app.draw();

        // // ASCII characters
        // app.key = 'r'; // Detect r
        // app.keyPressed(null);

        //Arrow keys
        app.projectile_fired = false;
        app.keyCode = 37; // Detect left arrow key
        app.keyPressed(null);

        app.keyCode = 70; 
        app.keyPressed(null);

        App.endgame = true;
        app.keyCode = 82; 
        app.keyPressed(null);

        App.endgame = false;
        app.keyCode = 82; 
        app.keyPressed(null);
        
        app.keyCode = 87; 
        app.keyPressed(null);
        
        app.keyCode = 83; 
        app.keyPressed(null);
        
        app.keyCode = 40; 
        app.keyPressed(null);
        
        app.keyCode = 38; 
        app.keyPressed(null);
        
        app.keyCode = 32; 
        app.keyPressed(null);
        
        app.keyCode = 39; 
        app.keyPressed(null);
        
        }
    @Test
    public void drawTest() {
        App app = new App();
        App.current_index = 0;
        PApplet.main("Tanks.App");

        char[] arr = {'A', 'B', 'C', 'D'};
        char c = 'C';
        DrawTerrain.checkArrContains(arr, c);
    }
        

}
