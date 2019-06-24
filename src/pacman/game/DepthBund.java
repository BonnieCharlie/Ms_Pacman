package pacman.game;

import pacman.controllers.Controller;
import pacman.controllers.examples.RandomGhosts;
import pacman.entries.pacman.MyPacMan;
import pacman.game.internal.PacMan;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

import static pacman.game.Constants.DELAY;

public class DepthBund {

    public static void main(String args[]) {

        int numTrials = 50;
        //myRunExperiment(new RandomPacMan(), new RandomGhosts(), numTrials);
        //myRunExperiment(new RandomPacMan(), new AggressiveGhosts(), numTrials);
        //myRunExperiment(new RandomPacMan(), new Legacy(), numTrials);
        //myRunExperiment(new RandomNonRevPacMan(), new RandomGhosts(), numTrials);
        //myRunExperiment(new RandomNonRevPacMan(), new AggressiveGhosts(), numTrials);
        //myRunExperiment(new RandomNonRevPacMan(), new Legacy(), numTrials);
        //myRunExperiment(new NearestPillPacMan(), new RandomGhosts(), numTrials);
        //myRunExperiment(new NearestPillPacMan(), new AggressiveGhosts(), numTrials);
        //myRunExperiment(new NearestPillPacMan(), new Legacy(), numTrials);
        //myRunExperiment(new LegacyPacMan(), new Legacy(), numTrials);
        long start = System.currentTimeMillis();
        myRunExperiment(RandomGhosts.class.getSimpleName(), new RandomGhosts(), numTrials);
        long tempo = System.currentTimeMillis() - start;
        System.out.println("Tempo totale 50 partite: " + tempo + " , Media tempo singola partita: " + tempo / (long) 50);

    }

    public static void myRunExperiment(String enemyController, Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController, int trials) {

        Random rnd = new Random(0);
        Game game;
        ArrayList<Double[]> performances = new ArrayList<>();

        for (int d = 1; d <= 3; d++) {
            double avgScore = 0;
            double score = 0;
            double maxScore = 0;
            double minScore = 10000000;
            Controller<Constants.MOVE> pacmanController = new MyPacMan(enemyController, d);
            System.out.println("Pacman Controller with depth  " + d );
            for (int i = 0; i < trials; i++) {
                game = new Game(rnd.nextLong());

                while (!game.gameOver()) {
                    game.advanceGame(pacmanController.getMove(game.copy(), System.currentTimeMillis() + DELAY),
                            ghostController.getMove(game.copy(), System.currentTimeMillis() + DELAY));
                }
                score = game.getScore();
                avgScore += score;
                if (score > maxScore) {
                    maxScore = score;
                }
                if (score < minScore) {
                    minScore = score;
                }
                //System.out.println(i + "\t" + game.getScore());
            }

            Double[] scores = {avgScore/trials,maxScore,minScore};
            System.out.println("AvgScore: " + avgScore/trials + "\t" + "MaxScore: " + maxScore + "\t" + "MinScore: " + minScore);
            performances.add(scores);
        }

        try
        {

            PrintWriter pr = new PrintWriter("performance.txt");

            for (int i=0; i<performances.size() ; i++) {
                pr.println("Depth " + (i+1) + ":");
                for(int j=0; j<performances.get(i).length; j++){
                    pr.print(performances.get(i)[j] + "\t");
                }
                pr.println();
            }
            pr.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("No such file exists.");
        }

    }
}
