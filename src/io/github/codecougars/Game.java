package io.github.codecougars;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Game extends JFrame {
    static int WINDOW_WIDTH = 1200;
    static int WINDOW_HEIGHT = 700;

    static int FRAME_WIDTH = 1200;
    static int FRAME_HEIGHT = 600;

    static String REGIONS_FILE = "regions.json";

    ClickMap clickMap;

    JPanel frame;
    JPanel topBar;
    JPanel controls;
    JLabel roundLabel = new JLabel();
    JButton doneButton = new JButton();
    JButton menuButton = new JButton();

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
        JPanel controls = new JPanel();

        window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));

        background = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        /*

        --------------------------------------------------
        | roundLabel                                menu | <- topBar
        --------------------------------------------------
        |                                                |
        |                                                |
        |                                                | <- frame
        |                                                |
        |                                                |
        --------------------------------------------------
        |                                                | <- controls
        |                                                |
        --------------------------------------------------

         */

        topBar = new JPanel();
        topBar.setPreferredSize(new Dimension(WINDOW_WIDTH, 26));
        topBar.setBackground(new Color(46, 60, 99));
        topBar.setLayout(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

        frame = new GamePanel();
        frame.setPreferredSize(new Dimension(FRAME_WIDTH, WINDOW_HEIGHT));
        frame.setBackground(Color.BLACK);

        controls = new JPanel();
        controls.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT - FRAME_HEIGHT));
        controls.setBackground(new Color(34, 161, 62));

        topBar.add(roundLabel, BorderLayout.LINE_START);
        roundLabel.setForeground(new Color(238, 238, 238));

        topBar.add(menuButton, BorderLayout.LINE_END);
        menuButton.setText("menu");
        menuButton.setSize(80, 20);
        menuButton.setForeground(new Color(234, 234, 234));
        menuButton.setBackground(new Color(55, 48, 101));
        menuButton.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        menuButton.setFocusPainted(false);

        doneButton.setText("done");
        doneButton.setSize(200, 400);
        doneButton.setForeground(new Color(234, 234, 234));
        doneButton.setBackground(new Color(55, 48, 101));
        doneButton.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        doneButton.setFocusPainted(false);
        controls.add(doneButton);

        window.add(topBar);
        window.add(frame);
        window.add(controls);

        initRegions();
        clickMap = new ClickMap(territories, FRAME_WIDTH, FRAME_HEIGHT);
        drawMap();

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);

                Territory clicked = clickMap.getTerritoryAt(mouseEvent.getX(), mouseEvent.getY());
                if (clicked != null) {
                    System.out.println(clicked.name);
                }
            }
        });

        redraw();
        refresh();
    }

    public void redraw() {
        frame.repaint();
    }

    public void refresh() {
        roundLabel.setText("round " + round);
    }

    public class GamePanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(background, 0, 0, this);

            /*
            // fuck this
            for (int x = 0; x < clickMap.map.length; x++) {
                for (int y = 0; y < clickMap.map[x].length; y++) {
                    g.setColor(new Color(45, 138, 22));
                    if (clickMap.map[x][y] != 0)
                        g.drawRect(x, y, 1, 1);
                }
            }
            */
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
        redraw();
        refresh();
    }
}
