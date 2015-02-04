package io.github.codecougars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by as on 02/02/15.
 */
public class Action {
    public static int TYPE_ATTACK = 0;
    public static int TYPE_REINFORCE = 1;

    public int round;
    public int type;
    public Player player;
    public Territory territory; // territory to defend or target of attack
    public int troops;

    public Action Action() {
        return this;
    }

    public static Action attack(int round, Player p, Territory territory, int troops) {
        Action a = new Action();

        a.round = round;
        a.type = TYPE_ATTACK;
        a.troops = troops;
        a.player = p;
        a.territory = territory;

        return a;
    }

    public static Action reinforce(int round, Territory territory, int troops) {
        Action a = new Action();

        a.round = round;
        a.type = TYPE_REINFORCE;
        a.territory = territory;
        a.player = territory.owner;
        a.troops = troops;

        return a;
    }

    public String describe() {
        return (type == TYPE_ATTACK ? "attack: " : "reinforce: ") +
                player.name + " -> " + territory.name + " (" + troops + " troops)";
    }

    public static ArrayList<Action> mergeActions(List<Action> actions) {
        ArrayList<Action> mergedActions = new ArrayList<Action>();

        for (Action action : actions) {
            boolean found = false;
            for (Action a : mergedActions) {
                if (a.round == action.round && a.player == action.player && a.territory == action.territory) {
                    a.troops += action.troops;
                    found = true;
                }
            }

            if (!found) {
                mergedActions.add(action);
            }
        }

        return mergedActions;
    }
}
