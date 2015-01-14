package io.github.codecougars;
import java.util.*;
import javax.swing.*;
import java.awt.*;


public class RiskCalculator
{
    int attackSize = 50;
    int defendSize = 34;

    Integer[] attackDice;
    Integer[] defendDice;
    int[] diceNum = {0, 0};

    Random rn = new Random();

    public void attackCheck() {
        if (attackSize <= 1) {
            //attackVerOne();
            attackVerTwo();
        }
        else {
            JOptionPane.showMessageDialog(null, "Can't attack. Need more than 1 attacker!");
        }
    }
    //public void attackVerOne() {
    //  if (defendSize = 1)
    //}
    private void getWinner(){
        Arrays.sort(attackDice, Collections.reverseOrder());
        System.out.println(Arrays.toString(attackDice));
        Arrays.sort(defendDice, Collections.reverseOrder());
        System.out.println(Arrays.toString(defendDice));
        for (int i = 0; i < attackDice.length && i < defendDice.length; i++ ) {
            if (defendDice[i] >= attackDice[i]) {
                System.out.println("Defender wins!");
            }
            else {
                System.out.println("Attacker wins!");
            }
        }
    }

    public void attackVerTwo() {
        attackDice = new Integer[attackSize];
        defendDice = new Integer[defendSize];
        for (int l = (attackDice.length - 1 ); l >= 0; l--) {
            attackDice[l] = rn.nextInt(6) + 1;
        }
        for (int l = (defendDice.length - 1 ); l >= 0; l--) {
            defendDice[l] = rn.nextInt(6) + 1;
        }


        getWinner();
    }
}