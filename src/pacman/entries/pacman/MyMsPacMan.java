package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import java.util.ArrayList;

public class MyMsPacMan extends Controller<MOVE> {

    private MOVE myMove= MOVE.NEUTRAL;
    private int absolute_depth = 1;

    public MOVE getMove(Game game, long timeDue)
    {
        //Place your game logic here to play the game as Ms Pac-Man
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
            utility = expectiminimax(game, 0, 1, 2, currentNodes);
            System.out.println("Moves: "+ move + "\tUtility: " + utility);
            if (utility > maximum){
                maximum = utility;
                myMove = move;
            }
        }

        return myMove;
    }


    private float expectiminimax(Game game, int depth, int agentType, int num_agents, ArrayList<Integer> index){

        // if this node is a final state, it returns the utility
        if (game.gameOver() || (game.getNumberOfActivePills() + game.getNumberOfActivePowerPills()) == 0 || depth == absolute_depth){
            float eval = (float) Utils.EvaluationFunction(game);
            System.out.println(eval);
            return eval;
        }

        // if agentType is a MAX node
         if (agentType == 0){ // agentType 0 is Pacman while others are Ghosts
             float max = 0;
             int[] neighbouringNodes = game.getNeighbouringNodes(index.get(agentType));
             //System.out.println("neighbourlenght:" + neighbouringNodes.length);
             int correctIndex = 0;
             int i=0;
             for (i=0; i<neighbouringNodes.length; i++) { //for (MOVE move: possibleMoves)
                 index.set(i, neighbouringNodes[i]);
                 float value = expectiminimax(GameCopy(game.copy(), index), depth, 1, num_agents, index);
                 if(value > max){
                     max = value;
                     correctIndex = i;
                 }
             }
             index.set(0, neighbouringNodes[correctIndex]);
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
             //System.out.println("neighbourlenght:" + neighbouringNodes.length);
             for (int i=0; i<neighbouringNodes.length; i++) {
                 index.set(i, neighbouringNodes[i]);
                 sum += expectiminimax(GameCopy(game.copy(), index), depth, nextAgentType, num_agents, index) * (1 / (float)neighbouringNodes.length); // non è fatta bene la chiamata
             }
             return sum;
         }
    }

    private Game GameCopy(Game game, ArrayList<Integer> index){
        //Return a game copy with modified State

        String gameState = "";

        //MAZE STATE
        gameState += String.valueOf(game.getMazeIndex()) + ",";
        gameState += String.valueOf(game.getTotalTime()) + ",";
        gameState += String.valueOf(game.getScore()) + ",";
        gameState += String.valueOf(game.getCurrentLevelTime())+ ",";
        gameState += String.valueOf(game.getCurrentLevel())+ ",";

        //PACMAN STATE
        gameState += String.valueOf(index.get(0))+ ","; // Pacman actual position
        gameState +=String.valueOf(game.getPacmanLastMoveMade())+ ","; //LastMoveMade è la penultima -- DA CAMBIARE!!
        gameState +=String.valueOf(game.getPacmanNumberOfLivesRemaining())+ ",";
        gameState +=String.valueOf(false)+ ",";

        //GHOSTS STATE
        for(int i=0; i<GHOST.values().length; i++) {
            gameState += String.valueOf(index.get(i+1)) + ","; // First ghost position
            gameState += String.valueOf(game.getGhostEdibleTime(GHOST.values()[i])) + ",";
            gameState += String.valueOf(game.getGhostLairTime(GHOST.values()[i])) + ",";
            gameState += String.valueOf(game.getGhostLastMoveMade(GHOST.values()[i])) + ",";
        }

        //PILLS STATE
        int[] pills=game.getPillIndices();
        String binaryPills = "";

        for(int i=0;i<pills.length;i++) {                //check which pills are available
            if (game.isPillStillAvailable(i))
                binaryPills += "1";
            else {
                binaryPills += "0";
            }
        }
        gameState+=binaryPills+",";

        //POWERPILLS STATE
        int[] powerPills=game.getPowerPillIndices();
        String binaryPowerPills = "";

        for(int i=0;i<powerPills.length;i++) {        //check with power pills are available
            if (game.isPowerPillStillAvailable(i))
                binaryPowerPills += "1";
            else {
                binaryPowerPills += "0";
            }
        }
        gameState+=binaryPowerPills + ",";

        // BOOLEAN STATE
        gameState += String.valueOf(game.getTimeOfLastGlobalReversal()) + ",";
        gameState += String.valueOf(game.wasPacManEaten())+ ",";


        for(GHOST ghost:GHOST.values()) {
            gameState+= String.valueOf(game.wasGhostEaten(ghost))+ ",";
        }

        gameState+= String.valueOf(game.wasPillEaten())+ ",";
        gameState+= String.valueOf(game.wasPillEaten());

        //System.out.println(game.getGameState());
        //System.out.println(gameState);
        game.setGameState(gameState);


        return game;
    }
}
