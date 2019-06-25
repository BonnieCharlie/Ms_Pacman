package pacman.game;

import pacman.controllers.Controller;
import pacman.controllers.examples.Legacy;
import pacman.controllers.examples.RandomGhosts;
import pacman.entries.pacman.MyPacMan;

import java.io.PrintWriter;
import java.util.EnumMap;
import java.util.Random;

import static pacman.game.Constants.DELAY;

public class TTest {

    public static void main(String args[]) {

        int numTrials = 2;
        long start = System.currentTimeMillis();

        myRunExperiment(new String[]{RandomGhosts.class.getSimpleName(), Legacy.class.getSimpleName()}, new RandomGhosts(), numTrials);
        long tempo = System.currentTimeMillis() - start;
        System.out.println("Tempo totale " + numTrials + " partite: " + tempo + " , Media tempo singola partita: " + tempo / numTrials);

    }

    public static void myRunExperiment(String[] enemyController, Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController, int trials) {

        Random rnd = new Random(0);
        Game game;
        Game game2;
        double avgScore1 = 0;
        int maxScore1= 0;
        int minScore1 = 10000000;
        double avgScore2 = 0;
        int maxScore2 = 0;
        int minScore2 = 10000000;
        int score1 = 0;
        int score2 = 0;


        Controller<Constants.MOVE> pacManController1 = new MyPacMan(enemyController[0]);
        Controller<Constants.MOVE> pacManController2 = new MyPacMan(enemyController[1]);
        System.out.println("EC1: " + enemyController[0] + " EC2: " + enemyController[1]);


        try (PrintWriter pr = new PrintWriter("TTestRandomEnemy.txt")) {
            pr.println("Datasets,"+enemyController[0]+","+enemyController[1]);
            for (int i = 0; i < trials; i++) {

                long t = System.currentTimeMillis();
                game = new Game(rnd.nextLong());
                game2 = new Game(rnd.nextLong());
                while (!game.gameOver()) {
                    game.advanceGame(pacManController1.getMove(game.copy(), System.currentTimeMillis() + DELAY),
                            ghostController.getMove(game.copy(), System.currentTimeMillis() + DELAY));
                }
                System.out.println("Fine partita EC1");
                while (!game2.gameOver()) {
                    game.advanceGame(pacManController2.getMove(game.copy(), System.currentTimeMillis() + DELAY),
                            ghostController.getMove(game.copy(), System.currentTimeMillis() + DELAY));
                }
                System.out.println("Fine partita EC2");

                score1 = game.getScore();
                avgScore1 += score1;
                if (score1 > maxScore1) {
                    maxScore1 = score1;
                }
                if (score1 < minScore1) {
                    minScore1 = score1;
                }

                score2 = game2.getScore();
                avgScore2 += score2;
                if (score2 > maxScore2) {
                    maxScore2 = score2;
                }
                if (score2 < minScore2) {
                    minScore2 = score2;
                }
                System.out.println(i + "\tGame EC1: " + game.getScore() + "\t Game EC2: " + game2.getScore());
                pr.println((i+1)+","+score1+","+score2);
                //System.out.println("Tempo singola partita : " + (System.currentTimeMillis() - t));
            }

            System.out.println("AvgScore: " + avgScore1 / trials + "\t" + "MaxScore: " + maxScore1 + "\t" + "MinScore: " + minScore1);
            System.out.println("AvgScore: " + avgScore2 / trials + "\t" + "MaxScore: " + maxScore2 + "\t" + "MinScore: " + minScore2);



        } catch (Exception e) {
            System.out.println("Something was wrong");
            e.printStackTrace();
        }

    }

}
