package io.github.codecougars;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by as on 18/01/15.
 */
public class ClickMap {
    byte[][] map;
    List<Territory> territories;

    public ClickMap(List<Territory> territoryList, int w, int h) {
        territories = territoryList;

        map = new byte[w][h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                map[x][y] = 0;
            }
        }

        // If there are more than 126 countries we're screwed, but there aren't...
        for (byte i = 1; i < territories.size() + 1; i++) { // shift by + 1
            Territory territory = territories.get(i - 1); // 0 == no country
            BufferedImage img = territory.image;

            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    int pixel = img.getRGB(x, y);

                    // check if opaque
                    if (!((pixel>>24) == 0x00)) {
                        map[x + territory.pos[0]][y + territory.pos[1]] = i;
                    }
                }
            }
        }
    }

    public Territory getTerritoryAt(int x, int y) {
        byte number = map[x][y];
        if (number > 0) {
            return territories.get(map[x][y] - 1);
        }
        else {
            return null;
        }
    }
}