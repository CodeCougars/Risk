package io.github.codecougars;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class GameFrame extends JFrame {
    static int WINDOW_WIDTH = 1040;
    static int WINDOW_HEIGHT = 600;

    static String REGIONS_FILE = "regions.json";

    JPanel panel;

    ArrayList<Territory> territories = new ArrayList<Territory>();

    public class GamePanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            drawMap(g);
        }
    }

    private void drawMap(Graphics g) {
        for (Territory territory : territories) {
            System.out.println(territory.name);
            g.drawImage(territory.image, territory.pos[0], territory.pos[1], this);
        }
    }

    public void init() {
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setName("Risk");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center
        setVisible(true);

        Container window = getContentPane();

        panel = new GamePanel();
        panel.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        panel.setBackground(Color.BLACK);

        window.add(panel);

        initRegions();
    }

    public void initRegions() {
        InputStream regionsIs = getClass().getResourceAsStream(REGIONS_FILE);

        /* Java Overhead ... */

        BufferedReader streamReader = null;
        try {
            streamReader = new BufferedReader(new InputStreamReader(regionsIs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String regionsFile = "";

        String inputStr;
        try {
            while ((inputStr = streamReader.readLine()) != null)
                regionsFile += inputStr;
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray regionsJson = new JSONArray(regionsFile);

        for (int i = 0; i < regionsJson.length(); i++) {
            JSONObject regionJson = regionsJson.getJSONObject(i);

            JSONArray positionJsonArray = regionJson.getJSONArray("pos");
            int[] position = {positionJsonArray.getInt(0), positionJsonArray.getInt(1)};

            String name = regionJson.getString("name");

            BufferedImage img = null;

            try {
                img = ImageIO.read(getClass().getResource("images/regions/" + name + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Territory territory = new Territory(name, position, img);

            territories.add(territory);
        }

        System.out.println(regionsJson);
    }

    public void start() {
        panel.repaint();
    }
}
