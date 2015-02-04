package io.github.codecougars;

import java.awt.*;
import java.util.Random;

/**
 * Created by as on 16/01/15.
 */
public class Player {
    public String name;
    public boolean isAI = false;
    public Color color;
    public int reinforcements = 4;

    public Player(String n) {
        name = n;

        setRandomColor();
    }

    public void setRandomColor() {
        Random r = new Random();

        color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }
}
