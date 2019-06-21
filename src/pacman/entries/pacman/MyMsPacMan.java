package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.EnumMap;

public class MyMsPacMan extends Controller<MOVE> {

    private MOVE myMove = MOVE.NEUTRAL;
    private int absolute_depth = 2;

    public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man
        //Long start1 = System.currentTimeMillis();
        float maximum = -1000;
        float utility = maximum;

        MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
        //Arrays.stream(possibleMoves).forEach(v -> System.out.println(v));
        for (MOVE move : possibleMoves) {
            Game g = game.copy();
            g.updatePacMan(move);
            utility = expectiminimax(g, 0, 1);

            if (utility > maximum) {
                maximum = utility;
                myMove = move;
            }
        }
        //System.out.println("Moves: " + myMove + "\tUtility: " + utility);
        //System.out.println("Time Execution: " + (System.currentTimeMillis() - start1));
        return myMove;
    }


    private float expectiminimax(Game game, int depth, int agentType) {

        // if this node is a final state, it returns the utility
        if (game.gameOver() || (game.getNumberOfActivePills() + game.getNumberOfActivePowerPills()) == 0 || depth == absolute_depth) {
            float eval = (float) Utils.InfluenceFunction(game);
            //float eval = (float) Utils.EvaluationFunction(game);
            //System.out.println("EVAL  "+eval);
            return eval;
        }


        // if agentType is a MAX node
        else if (agentType == 0) { // agentType 0 is Pacman while others are Ghosts
            depth += 1;
            float max = 0;
            MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
            Game g = null;
            for (MOVE move : possibleMoves) { //for (MOVE move: possibleMoves)
                g = game.copy();
                g.updatePacMan(move);
                float value = expectiminimax(g, depth, 1);
                //System.out.println("Mossa Pacman " + move +" valore_max: " + value);
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }

        // if agentType is a MIN node with possibility
        else {
            float sum=0;
            ArrayList<ArrayList<MOVE>> movesGhosts = new ArrayList<ArrayList<MOVE>>();
            for(GHOST ghostType : GHOST.values()){
                MOVE[] possibleMoves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghostType));
                ArrayList<MOVE> moves = new ArrayList<MOVE>();
                for(MOVE move: possibleMoves){
                    moves.add(move);
                }
                movesGhosts.add(moves);
            }
            //System.out.println(movesGhosts);
            ArrayList<EnumMap<GHOST, MOVE>> combinations = combination_between_vectors(movesGhosts.get(0), movesGhosts.get(1), movesGhosts.get(2), movesGhosts.get(3));
            //System.out.println(combinations);
            //System.out.println(movesGhosts.get(0) +", "+ movesGhosts.get(1)+", "+ movesGhosts.get(2)+", "+ movesGhosts.get(3));
            //System.out.println("tempo combinations: " + (System.currentTimeMillis()-start) + "    num_combinations: " + combinations.size() + "  =   " + movesGhosts.get(0).size()* movesGhosts.get(1).size()* movesGhosts.get(2).size()* movesGhosts.get(3).size());
            //System.out.println("number of combinations" + combinations.size());
            Game g = null;
            for(int i = 0; i<combinations.size(); i++){
                g = game.copy();
                //System.out.println("index"+ i + "Before update ghost"+ combinations.get(i));

                g.updateGhosts(combinations.get(i));

                long start = System.currentTimeMillis();
                //System.out.println("Combinations prima di expect "+ combinations.get(i) + "Somma intermedia "+ sum);
                sum += expectiminimax(g, depth, 0) * (1 / (float) (combinations.size()));

                //System.out.println("Combinations "+ combinations.get(i) + "Somma intermedia "+ sum);
                long tempo_sum = System.currentTimeMillis()-start;
                if(tempo_sum > 40){
                    System.out.println("AIUTOOOO");
                    System.out.println("tempo sum: " + tempo_sum + ",    " + movesGhosts.get(0).size()* movesGhosts.get(1).size()* movesGhosts.get(2).size()* movesGhosts.get(3).size());
                }
            }
            return sum;
        }
    }

    private ArrayList<EnumMap<GHOST, MOVE>> combination_between_vectors(ArrayList<MOVE> vet1, ArrayList<MOVE> vet2, ArrayList<MOVE> vet3, ArrayList<MOVE> vet4){
        //System.out.println("vettori: " + vet1 + ", " +  vet2 +", " + vet3 + ", "+ vet4);
        ArrayList<EnumMap<GHOST, MOVE>> combination = new ArrayList<EnumMap<GHOST, MOVE>>();
        for(int i=0; i<vet1.size(); i++){
            EnumMap<GHOST, MOVE> comb = new EnumMap<GHOST, MOVE>(GHOST.class);
            for(int j=0; j<vet2.size(); j++){
                for(int k=0; k<vet3.size(); k++){
                    for(int z=0; z<vet4.size(); z++){
                        comb.put(GHOST.values()[0], vet1.get(i));
                        comb.put(GHOST.values()[1], vet2.get(j));
                        comb.put(GHOST.values()[2], vet3.get(k));
                        comb.put(GHOST.values()[3], vet4.get(z));

                        //System.out.println(comb);
                        //System.out.println(comb);
                        combination.add(comb.clone());
                        //System.out.println(combination);
                        comb.clear();
                        //System.out.println(combination);
                    }
                }
            }
        }

        //combination.forEach(System.out::println);
        //System.out.println("combinazioni restituite: " + combination);
        return combination;
    }
}
