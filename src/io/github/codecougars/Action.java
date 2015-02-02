package io.github.codecougars;

/**
 * Created by as on 02/02/15.
 */
public class Action {
    public static int TYPE_ATTACK = 0;
    public static int TYPE_REINFORCE = 1;

    public int type;
    public Player player;
    public Territory territory; // territory to defend or target of attack
    int troops;

    public Action Action() {
        return this;
    }

    public Action attack(Player p, Territory territory, int troops) {
        Action a = new Action();

        a.type = TYPE_ATTACK;
        a.troops = troops;
        a.player = p;
        a.territory = territory;

        return a;
    }

    public Action reinforce(Territory territory, int troops) {
        Action a = new Action();

        a.type = TYPE_REINFORCE;
        a.territory = territory;
        a.troops = troops;

        return a;
    }
}
