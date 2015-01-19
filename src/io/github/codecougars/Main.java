package io.github.codecougars;

public class Main {

    public static void main(String[] args) {
        final Game frame = new Game();

        frame.init();
        frame.start();

        //calcTest();
    }

    static void calcTest() {
        System.out.print("\t\t");
        for (int i = 2; i < 40; i++) {
            System.out.print(i + "\t\t");
        }
        System.out.println();
        for (int i = 2; i < 40; i++) {
            System.out.print("--------");
        }
        System.out.println();

        for (int x = 2; x < 40; x++) {
            System.out.print(x + "\t|\t");
            for (int y = 2; y < 40; y++) {
                int attackerWins = 0;
                int defenderWins = 0;
                for (int i = 0; i < 400; i++) {
                    if (RiskCalculator.calculate(x, y) == RiskCalculator.ATTACKER) {
                        attackerWins++;
                    }
                    else {
                        defenderWins++;
                    }
                }

                if (defenderWins != 0) {
                    float ratio = ((float) attackerWins / defenderWins);
                    System.out.printf("%.2f\t", ratio);
                }
                else {
                    System.out.print("na\t\t");
                }
            }
            System.out.println();
        }
    }
}
