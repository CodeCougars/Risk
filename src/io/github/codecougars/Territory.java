package io.github.codecougars;

import java.awt.*;

/**
 * Created by as on 13/01/15.
 */
public class Territory {
    public int[] pos;
    public Image image;
    public String name;

    public Territory(String nameInput, int[] position, Image img) {
        pos = position;
        name = nameInput;
        image = img;
    }
}