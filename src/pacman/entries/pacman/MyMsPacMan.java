package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

public class MyMsPacMan extends Controller<MOVE> {

    private MOVE myMove = MOVE.NEUTRAL;
    private int absolute_depth = 4;

    public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man
        Long start = System.currentTimeMillis();
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
        System.out.println("Moves: " + myMove + "\tUtility: " + utility);
        System.out.println("Time Execution: " + (System.currentTimeMillis() - start));
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
            float max = 0;
            MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
            for (MOVE move : possibleMoves) { //for (MOVE move: possibleMoves)
                Game g = game.copy();
                g.updatePacMan(move);
                float value = expectiminimax(g, depth, 1);
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }

        // if agentType is a MIN node with possibility
        else {
            int nextAgentType = agentType + 1;
            depth += 1;

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
            ArrayList<ArrayList<MOVE>>  combinations;

            for(int i = 0; i< movesGhosts.size(); i++){

                if(movesGhosts.get(i).size()==0){
                    movesGhosts.get(i).add(MOVE.NEUTRAL);
                }
            }
            //System.out.println(movesGhosts.get(0) +", "+ movesGhosts.get(1)+", "+ movesGhosts.get(2)+", "+ movesGhosts.get(3));
            combinations = combination_between_two_vectors(movesGhosts.get(0), movesGhosts.get(1), movesGhosts.get(2), movesGhosts.get(3));

            for(int i = 0; i<combinations.size(); i++){
                Game g = game.copy();
                EnumMap<GHOST, MOVE> map = new EnumMap<GHOST, MOVE>(GHOST.class);
                for(MOVE move: combinations.get(i)){
                    for (GHOST ghostType : GHOST.values()) {
                        map.put(ghostType, move);
                    }
                }
                g.updateGhosts(map);
                sum += expectiminimax(g, depth, nextAgentType) * (1 / (float) (combinations.size()));
            }
            return sum;
        }
    }

    private ArrayList<ArrayList<MOVE>> combination_between_two_vectors(ArrayList<MOVE> vet1, ArrayList<MOVE> vet2, ArrayList<MOVE> vet3, ArrayList<MOVE> vet4){
        ArrayList<ArrayList<MOVE>> combination = new ArrayList<ArrayList<MOVE>>();
        for(int i=0; i<vet1.size(); i++){
            ArrayList<MOVE> comb = new ArrayList<MOVE>();
            for(int j=0; j<vet2.size(); j++){
                for(int k=0; k<vet3.size(); k++){
                    for(int z=0; z<vet4.size(); z++){
                        comb.add(vet1.get(i));
                        comb.add(vet2.get(j));
                        comb.add(vet3.get(k));
                        comb.add(vet4.get(z));
                        //System.out.println(comb);
                        combination.add(comb);
                        comb.clear();
                    }
                }
            }
        }
        return combination;
    }
}
