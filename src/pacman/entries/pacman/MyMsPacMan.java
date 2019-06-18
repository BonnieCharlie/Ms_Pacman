package pacman.entries.pacman;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.PacMan;

public class MyMsPacMan {

    private MOVE myMove= MOVE.NEUTRAL;

    public MOVE getMove(Game game, long timeDue)
    {
        //Place your game logic here to play the game as Ms Pac-Man

        return myMove;
    }

    private float evaluationFunction(Game game){
        int utility = 1;
        return utility;
    }

    private float expectiminimax(Game game, int depth, int agentType, int num_agents){
        int absolute_depth = 4;
        int currentNode = game.getPacmanCurrentNodeIndex();

        // if currentNode is a final state, it returns the utility
        if (game.gameOver() || depth == absolute_depth){
            return evaluationFunction(game);
        }

        // if currentNode is a MAX node
         if (agentType == 0){ // agentType 0 is Pacman while others are Ghosts
             float max = 0;
             MOVE[] possibleMoves=game.getPossibleMoves(game.getPacmanCurrentNodeIndex(),game.getPacmanLastMoveMade());
             for (MOVE newState: possibleMoves) {
                 float value = expectiminimax(game, depth, 1, num_agents)*(1/possibleMoves.length); // non è fatta bene la chiamata
                 if(value > max)
                     max = value;
             }
             return max;
         }

        // if currentNode is a MIN node with possibility
        // else {
        int nextAgentType = agentType + 1;
        if (num_agents == nextAgentType)
            nextAgentType = 0;
        if (nextAgentType == 0)
            depth += 1;

        float sum = 0;
        GHOST[] ghosts = GHOST.values();
        MOVE[] possibleMoves=game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghosts[nextAgentType+1]),game.getGhostLastMoveMade(ghosts[nextAgentType+1]));
        for (MOVE newState: possibleMoves) {
            sum += expectiminimax(game, depth, nextAgentType, num_agents)*(1/possibleMoves.length); // non è fatta bene la chiamata
        }
        return sum;
    }
}
