package io.github.codecougars;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Game extends JFrame {
    static int WINDOW_WIDTH = 1200;
    static int WINDOW_HEIGHT = 700;

    static int FRAME_WIDTH = 1200;
    static int FRAME_HEIGHT = 600;

    static String REGIONS_FILE = "regions.json";

    JPanel frame;
    JPanel controls;
    JLabel roundLabel = new JLabel();
    JButton doneButton = new JButton();

    ArrayList<Territory> territories = new ArrayList<Territory>();
    int round = 0;
    ArrayList<Player> players = new ArrayList<Player>();

    BufferedImage background;

    public void init() {
        setTitle("Risky Risk");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setName("Risk");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center
        setVisible(true);

        Container window = getContentPane();

        JPanel container = new JPanel();

        window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));

        background = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        frame = new GamePanel();
        frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        frame.setBackground(Color.BLACK);

        controls = new JPanel();
        controls.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT - FRAME_HEIGHT));
        controls.setBackground(new Color(47, 171, 65));

        controls.add(roundLabel);

        doneButton.setText("done");
        controls.add(doneButton);

        window.add(frame);
        window.add(controls);

        initRegions();
        drawMap();

        redraw();
        refresh();
    }

    public void redraw() {
        frame.invalidate();
    }

    public void refresh() {
        roundLabel.setText("round " + round);
    }

    public class GamePanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(background, 0, 0, this);
        }
    }

    private Territory getRandomTerritory() {
        return territories.get((new Random()).nextInt(territories.size()));
    }

    // Redraws the map background each time needed
    private void drawMap() {
        Graphics g = background.getGraphics();

        for (Territory territory : territories) {
            g.drawImage(territory.image, territory.pos[0], territory.pos[1], this);
        }
    }

    private void initRegions() {
        /*
        * Read the regions definition file and the images.
        * Then add them to the `regions` list.
        */

        InputStream regionsIs = getClass().getResourceAsStream(REGIONS_FILE);

        // InputStream to text String to JSON object.

        BufferedReader streamReader = null;
        try {
            streamReader = new BufferedReader(new InputStreamReader(regionsIs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String regionsFile = "";

        String inputStr = null;
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
        frame.repaint();
    }
}
