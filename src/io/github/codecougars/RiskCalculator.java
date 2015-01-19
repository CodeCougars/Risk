package io.github.codecougars;
import java.util.*;


public class RiskCalculator
{
    static int ATTACKER = 1;
    static int DEFENDER = 2;

    public static int calculate(int attackSize, int defendSize) {
        if (attackSize <= 1) {
            throw new Error("Can't attack. Need more than 1 attacker.");
        }

        Random rn = new Random();
        Integer[] attackDice = new Integer[attackSize];
        Integer[] defendDice = new Integer[defendSize];

        for (int l = (attackDice.length - 1 ); l >= 0; l--) {
            attackDice[l] = rn.nextInt(6) + 1;
        }
        for (int l = (defendDice.length - 1 ); l >= 0; l--) {
            defendDice[l] = rn.nextInt(6) + 1;
        }

        Arrays.sort(attackDice, Collections.reverseOrder());
        Arrays.sort(defendDice, Collections.reverseOrder());

        int defenderWins = 0;
        int attackerWins = 0;

        for (int i = 0; i < attackDice.length && i < defendDice.length; i++ ) {
            if (defendDice[i] >= attackDice[i]) {
                //System.out.println("Defender wins!");
                defenderWins++;
                //return DEFENDER;
            }
            else {
                //System.out.println("Attacker wins!");
                attackerWins++;
                //return ATTACKER;
            }
        }

        if (attackerWins > defenderWins) {
            return ATTACKER;
        }
        else {
            return DEFENDER;
        }
    }
}