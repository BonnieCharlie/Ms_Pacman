package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

import java.lang.Double;
import java.util.*;

public class ExpectiminimaxPacMan extends Controller<MOVE> {

    private MOVE myMove=MOVE.NEUTRAL;
    private HashMap<Double, MOVE> pacmanNextState = new HashMap<Double, MOVE>();
    ArrayList<ArrayList<MOVE>> ghostsNextState = new ArrayList<ArrayList<MOVE>>();
    int depth = 4;

    public MOVE getMove(Game game, long timeDue)
    {
        //Place your game logic here to play the game as Ms Pac-Man

        myMove = expectiminimaxResearch(game, depth);
        return myMove;
    }


    /*
    * Expectiminimax algorithm based on alfa-beta pruning structure
    */
    private MOVE expectiminimaxResearch(Game game, int depth){
        double v = maxValue(game, depth);
        MOVE move = pacmanNextState.get(v);
        System.out.println(move);
        return move; // best move
    }

    /*
     * Function that returns utility value for max move
     */
    private double maxValue(Game game, int depth){

        // Termination test
        if (game.gameOver() || (game.getNumberOfActivePills() + game.getNumberOfActivePowerPills()) == 0 || depth == 0) {
            //float eval = (float) Utils.InfluenceFunction(game);
            float eval = (float) Utils.EvaluationFunction(game);
            return eval;
        }
        else{
            depth--;
            double v = -1000;
            Game g = null;

            // Pacman possible next state
            MOVE[] movePacman = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
            for(int i=0; i<movePacman.length; i++){
                pacmanNextState.put((double)i, movePacman[i]);
            }
            System.out.println(pacmanNextState);
            for (HashMap.Entry<Double, MOVE> entry : pacmanNextState.entrySet()){
                g = game.copy();
                MOVE move = entry.getValue(); // it is possible to exstract this value only ones
                g.updatePacMan(move);
                System.out.println("Chiamo ricorsione: "+ String.valueOf(depth+1));
                double max = minValue(g, depth);
                pacmanNextState.put(max, move);
                if(max > v){
                    v = max;
                }
            }
            return v;
        }
    }

    /*
     * Function that returns utility value for min move
     */
    private double minValue(Game game, int depth){

        // Termination test
        if (game.gameOver() || (game.getNumberOfActivePills() + game.getNumberOfActivePowerPills()) == 0 || depth == 0) {
            //float eval = (float) Utils.InfluenceFunction(game);
            float eval = (float) Utils.EvaluationFunction(game);
            return eval;
        }
        else{
            double v = 1000;
            Game g = null;

            // Ghosts possible next state
            for(GHOST ghostType : GHOST.values()){
                MOVE[] possibleMoves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghostType));
                ArrayList<MOVE> moves = new ArrayList<MOVE>();
                for(MOVE move: possibleMoves){
                    moves.add(move);
                }
                ghostsNextState.add(moves);
            }
            ArrayList<EnumMap<GHOST, MOVE>> combinations = combination_between_vectors(ghostsNextState.get(0), ghostsNextState.get(1), ghostsNextState.get(2), ghostsNextState.get(3));

            System.out.println("Chiamo ricorsione: "+ String.valueOf(depth+1));
            for(int i = 0; i<combinations.size(); i++) {
                g = game.copy();
                g.updateGhosts(combinations.get(i));
                v += maxValue(g, depth) * (1 / (double) (combinations.size()));
            }
            return v;
        }
    }

    /*
    * Returns all possible combinations between four vectors
    */
    private ArrayList<EnumMap<GHOST, MOVE>> combination_between_vectors(ArrayList<MOVE> vet1, ArrayList<MOVE> vet2, ArrayList<MOVE> vet3, ArrayList<MOVE> vet4){
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
                        combination.add(comb.clone());
                        comb.clear();
                    }
                }
            }
        }
        return combination;
    }
}
