package io.github.codecougars;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by as on 13/01/15.
 */
public class Territory {
    public int[] pos;
    public BufferedImage image;
    public String name;
    public int id; // seems like the best worst way to keep a reference that I know of.

    public Territory(int i, String nameInput, int[] position, BufferedImage img) {
        pos = position;
        name = nameInput;
        image = img;
        id = i;
    }

    public void setColor(Color color) {
        // way faster
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int c = image.getRGB(x, y);

                if (!((c >> 24) == 0x00)) {
                    image.setRGB(x, y, color.getRGB());
                }
            }
        }
    }
}