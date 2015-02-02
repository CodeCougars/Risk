package io.github.codecougars;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

/**
 * Created by as on 13/01/15.
 */
public class Territory {
    public int[] pos;
    public int width;
    public int height;
    public BufferedImage image;
    public String name;
    public int id; // seems like the best worst way to keep a reference that I know of.
    public Player owner;
    public int troops;

    public ArrayList<Territory> neighbours;

    static Color DEFAULT_COLOR = new Color(133, 148, 133);

    public Territory(int i, String nameInput, int[] position, BufferedImage img) {
        pos = position;
        name = nameInput;
        image = img;
        id = i;
        setColor(DEFAULT_COLOR);

        width = img.getWidth();
        height = img.getHeight();
    }

    public void setColor(Color color) {
        // way faster
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int c = image.getRGB(x, y);

                if (!((c >> 24) == 0x00)) {
                    image.setRGB(x, y, color.getRGB());
                    //System.out.println(Integer.toHexString(c) + " " + Integer.toHexString(color.getRGB()));
                }
                else {
                    //image.setRGB(x, y, new Color(170, 40, 200).getRGB());
                }
            }
        }
    }

    public void resetColor() {
        if (owner != null)
            setColor(owner.color);
        else {
            setColor(DEFAULT_COLOR);
        }
    }

    public void setFocusColor() {
        Color color = null;
        if (owner != null) {
            color = owner.color;
        }
        else {
            color = DEFAULT_COLOR;
        }

        setColor(color.darker());
    }

    public void setOwner(Player player) {
        owner = player;

        setColor(player.color);
    }
}