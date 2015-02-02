package io.github.codecougars;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Game extends JFrame {
    static int WINDOW_WIDTH = 1200;
    static int WINDOW_HEIGHT = 700;

    static int FRAME_WIDTH = 1200;
    static int FRAME_HEIGHT = 600;

    static int AI_PLAYERS = 3;

    static String REGIONS_FILE = "regions.json";

    /* Java UI */

    JPanel frame;
    JPanel controls;
    JPanel topBar;
    JLabel roundLabel = new JLabel();
    JButton doneButton = new JButton();
    JButton menuButton = new JButton();

    /* Game Round State */

    int round = 0;

    ArrayList<Territory> territories = new ArrayList<Territory>();
    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<Action> actions = new ArrayList<Action>();

    ClickMap clickMap;
    BufferedImage background;

    /* UI State */

    Territory currentlyInFocus = null;

    public void init() {
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setTitle("Risky Risk");
        setName("Risk");
        setDefaultCloseOperation(EXIT_ON_CLOSE); // close when close button pressed
        setLocationRelativeTo(null); // center
        setVisible(true);

        Container window = getContentPane();

        window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));

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

        background = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        initRegions(); // territories
        clickMap = new ClickMap(territories, FRAME_WIDTH, FRAME_HEIGHT);
        initPlayers();
        draw();

        /* Click Action */

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);

                Territory clicked = clickMap.getTerritoryAt(mouseEvent.getX(), mouseEvent.getY());
                if (clicked != null) {
                    for (Territory t : clicked.neighbours) {
                        System.out.print(t.name + " ");
                    }
                    System.out.print("\n");
                }
            }
        });

        /* Hover Animations */

        frame.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
                super.mouseMoved(mouseEvent);

                Territory hoveredOver = clickMap.getTerritoryAt(mouseEvent.getX(), mouseEvent.getY());

                if (hoveredOver != null) {
                    // Change focus or start focusing
                    if (currentlyInFocus != null) {
                        currentlyInFocus.resetColor();
                    }

                    Territory territory = territories.get(hoveredOver.id);
                    currentlyInFocus = territory;
                    territory.setFocusColor();
                }
                else {
                    if (currentlyInFocus != null) {
                        currentlyInFocus.resetColor();
                        currentlyInFocus = null;
                    }
                }

                draw();
                redraw();
            }
        });

        /* Next Round button */

        doneButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                advance();
            }
        });

        redraw();
        refresh();
    }

    // redraws the main view
    public void redraw() {
        frame.repaint();
        frame.revalidate();
    }

    // Updates the other UI elements
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

    // Redraws the main frame
    private void draw() {
        Graphics g = background.getGraphics();
        g.clearRect(0, 0, background.getWidth(), background.getHeight());

        for (Territory territory : territories) {
            g.drawImage(territory.image, territory.pos[0], territory.pos[1], this);
        }

        /* Player list */

        g.setColor(new Color(205, 222, 225));
        g.fillRoundRect(FRAME_WIDTH - 80, Math.round(FRAME_HEIGHT / 2 - 220), 80, players.size() * 20 + 8, 6, 6);

        int offset = 0;
        for (Player player : players) {
            g.setColor(player.color);
            g.fillRect(FRAME_WIDTH - 72, Math.round(FRAME_HEIGHT / 2 - 208) + offset * 20, 8, 8);

            g.setColor(new Color(46, 42, 49));
            g.drawString(player.name, FRAME_WIDTH - 62, Math.round(FRAME_HEIGHT / 2 - 200) + offset * 20);
            offset++;
        }

        /* Name display when hovering */

        if (currentlyInFocus != null) {
            g.setColor(new Color(248, 248, 248));
            g.drawString(currentlyInFocus.name,
                    currentlyInFocus.pos[0] + Math.round(currentlyInFocus.width / 2) - currentlyInFocus.name.length() * 4,
                    currentlyInFocus.pos[1] + Math.round(currentlyInFocus.height / 2));
        }

        /* stats */

        Player humanPlayer = getHumanPlayer();

        g.setColor(new Color(216, 225, 224));
        g.drawString("reinforcements: " + humanPlayer.reinforcements, 10, FRAME_HEIGHT / 2);
    }

    public void advance() {
        for (Player player : players) {
            player.reinforcements += 6;
        }

        round++;

        redraw();
        refresh();
    }

    private Territory getRandomTerritory() {
        return territories.get((new Random()).nextInt(territories.size()));
    }

    private Player getHumanPlayer() {
        for (Player player : players) {
            if (!player.isAI) {
                return player;
            }
        }

        return null;
    }

    private void initPlayers() {
        Player player = new Player("you");
        player.isAI = false;
        player.color = new Color(89, 119, 150);
        player.color = new Color(196, 44, 42);
        players.add(player);

        for (int i = 0; i < AI_PLAYERS; i++) {
            Player ai = new Player("AI " + (i + 1));
            ai.isAI = true;
            ai.reinforcements = 20;
            ai.setRandomColor();
            players.add(ai);
        }

        for (Player p : players) {
            Territory territory = getRandomTerritory();

            while (territory.owner != null) {
                territory = getRandomTerritory();
            }

            territory.setOwner(p);
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
                BufferedImage image = ImageIO.read(getClass().getResource("images/regions/" + name + ".png"));
                img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.drawImage(image, 0, 0, null);
                g.dispose();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Territory territory = new Territory(i, name, position, img);



            territories.add(territory);
        }

        HashMap<String, Territory> territoryHashMap = new HashMap<String, Territory>();

        for (Territory territory : territories) {
            territoryHashMap.put(territory.name, territory);
        }

        for (int i = 0; i < regionsJson.length(); i++) {
            JSONObject regionJson = regionsJson.getJSONObject(i);

            JSONArray neighboursJsonArray = regionJson.getJSONArray("neighbours");
            ArrayList<Territory> neighbours = new ArrayList<Territory>();

            for (int j = 0; j < neighboursJsonArray.length(); j++) {
                String name = neighboursJsonArray.getString(0);

                neighbours.add(territoryHashMap.get(name));
            }

            territories.get(i).neighbours = neighbours;
        }

    }

    public void start() {
        redraw();
        refresh();
    }
}
