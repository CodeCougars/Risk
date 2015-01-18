package io.github.codecougars;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by as on 13/01/15.
 */
public class Territory {
    public int[] pos;
    public BufferedImage image;
    public String name;

    public Territory(String nameInput, int[] position, BufferedImage img) {
        pos = position;
        name = nameInput;
        image = img;
    }
}