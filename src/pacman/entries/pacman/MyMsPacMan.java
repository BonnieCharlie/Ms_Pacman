package pacman.entries.pacman;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import java.util.ArrayList;

public class MyMsPacMan {

    private MOVE myMove= MOVE.NEUTRAL;

    public MOVE getMove(Game game, long timeDue)
    {
        //Place your game logic here to play the game as Ms Pac-Man
        int depth = 4;
        int currentNode = game.getPacmanCurrentNodeIndex();
        ArrayList currentNodes = new ArrayList<Integer>();
        currentNodes.add(currentNode);
        for (GHOST ghost: GHOST.values()){
            currentNodes.add(game.getGhostCurrentNodeIndex(ghost));
        }
        float maximum = -1000;
        float utility = maximum;
        MOVE[] possibleMoves=game.getPossibleMoves(game.getPacmanCurrentNodeIndex(),game.getPacmanLastMoveMade());
        for (MOVE move : possibleMoves){
            utility = expectiminimax(game, depth, 0, 2, currentNodes);
            if (utility > maximum){
                maximum = utility;
                myMove = move;
            }
        }
        
        return myMove;
    }

    private float evaluationFunction(Game game){
        int utility = 1;
        return utility;
    }

    private float expectiminimax(Game game, int depth, int agentType, int num_agents, ArrayList<Integer> index){
        int absolute_depth = 4;

        // if currentNode is a final state, it returns the utility
        if (game.gameOver() || depth == absolute_depth){
            return evaluationFunction(game);
        }

        // if agentType is a MAX node
         if (agentType == 0){ // agentType 0 is Pacman while others are Ghosts
             float max = 0;
             int[] neighbouringNodes = game.getNeighbouringNodes(index.get(agentType));
             for (int i=0; i<neighbouringNodes.length; i++) { //for (MOVE move: possibleMoves)
                 index.set(i, neighbouringNodes[i]);
                 float value = expectiminimax(game, depth, 1, num_agents, index)*(1/neighbouringNodes.length); // non è fatta bene la chiamata
                 if(value > max)
                     max = value;
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
             int[] neighbouringNodes = game.getNeighbouringNodes(index.get(nextAgentType));
             for (int i=0; i<neighbouringNodes.length; i++) {
                 index.set(i, neighbouringNodes[i]);
                 sum += expectiminimax(game, depth, nextAgentType, num_agents, index) * (1 / neighbouringNodes.length); // non è fatta bene la chiamata
             }
             return sum;
         }
    }
}
