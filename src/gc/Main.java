package gc;

import processing.core.PApplet;

public class Main extends PApplet {

    public static PApplet app;
    private Logic logic;

    public static void main(String[] args) {
        // write your code here
        PApplet.main("gc.Main");
    }

    @Override
    public void setup() {
        app = this;
        logic = new Logic(this);
    }

    public void draw() {
        logic.draw();
    }

    public void mousePressed() {
        logic.mPressed();
    }

    public void keyPressed() {
        logic.kPresed();
    }
}
