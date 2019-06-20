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
    private int absolute_depth = 5;

    public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man
        Long start = System.currentTimeMillis();
        float maximum = -1000;
        float utility = maximum;

        MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
        Arrays.stream(possibleMoves).forEach(v -> System.out.println(v));
        for (MOVE move : possibleMoves) {
            Game g = game.copy();
            g.updatePacMan(move);
            utility = expectiminimax(g, 0, 1, 3);

            if (utility > maximum) {
                maximum = utility;
                myMove = move;
            }
        }
        System.out.println("Moves: " + myMove + "\tUtility: " + utility);
        System.out.println("Time Execution: " + (System.currentTimeMillis()-start));
        return myMove;
    }


    private float expectiminimax(Game game, int depth, int agentType, int num_agents) {

        // if this node is a final state, it returns the utility
        if (game.gameOver() || (game.getNumberOfActivePills() + game.getNumberOfActivePowerPills()) == 0 || depth == absolute_depth) {
            //float eval = (float) Utils.InfluenceFunction(game);
            float eval = (float) Utils.EvaluationFunction(game);
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
                float value = expectiminimax(g, depth, 1, num_agents);
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }

        // if agentType is a MIN node with possibility
        else {
            int nextAgentType = agentType + 1;
            if (num_agents == nextAgentType)
                nextAgentType = 0;
            if (nextAgentType == 0)
                depth += 1;

            float sum = 0;
            MOVE[] possibleMoves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(GHOST.values()[agentType - 1]));
            //System.out.println(possibleMoves.length);
            if(possibleMoves.length==0){
                possibleMoves= new MOVE[]{MOVE.UP};
            }
            for (MOVE move : possibleMoves) {
                Game g = game.copy();
                EnumMap<GHOST, MOVE> map = new EnumMap<GHOST, MOVE>(GHOST.class);
                for (GHOST ghostType : GHOST.values()) {
                    if (ghostType == GHOST.values()[agentType - 1])
                        map.put(ghostType, move);
                    else
                        map.put(ghostType, g.getGhostLastMoveMade(ghostType));
                }
                g.updateGhosts(map);
                sum += expectiminimax(g, depth, nextAgentType, num_agents) * (1 / (float) possibleMoves.length);
            }
            return sum;
        }
    }
}
