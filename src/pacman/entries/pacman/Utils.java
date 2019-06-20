package pacman.entries.pacman;

import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.DM;
import pacman.game.Game;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.stream.Collectors;

public class Utils {

    public static double EvaluationFunction(Game game){

        if(game.getNumberOfActivePills()+game.getNumberOfActivePowerPills() == 0){
            return Double.POSITIVE_INFINITY;
        }else if(game.gameOver() || game.wasPacManEaten()){
            return Double.NEGATIVE_INFINITY;
        }


        int score = game.getScore();
        int posPacman = game.getPacmanCurrentNodeIndex();
        float distance = 10000;
        float oldDistance = distance;
        Constants.GHOST nearestghost;
        nearestghost = null;
        for (Constants.GHOST ghost: Constants.GHOST.values()) {
            if(game.getGhostLairTime(ghost)==0){
                distance = game.getShortestPathDistance(posPacman, game.getGhostCurrentNodeIndex(ghost));
                //distance = game.getManhattanDistance(posPacman,game.getGhostCurrentNodeIndex(ghost));
                //distance = (float) game.getEuclideanDistance(posPacman, game.getGhostCurrentNodeIndex(ghost));
                if(distance < oldDistance){
                    oldDistance = distance;
                    nearestghost = ghost;
                }
            }
        }
        /*
        int[] pills=game.getPillIndices();
        int[] powerPills=game.getPowerPillIndices();

        ArrayList<Integer> targets=new ArrayList<Integer>();

        for(int i=0;i<pills.length;i++)					//check which pills are available
            if(game.isPillStillAvailable(i))
                targets.add(pills[i]);

        for(int i=0;i<powerPills.length;i++)			//check with power pills are available
            if(game.isPowerPillStillAvailable(i))
                targets.add(powerPills[i]);

        int[] targetsArray=new int[targets.size()];		//convert from ArrayList to array

        for(int i=0;i<targetsArray.length;i++)
            targetsArray[i]=targets.get(i);

        float minDistanceToNextPill =  game.getShortestPathDistance(posPacman, game.getClosestNodeIndexFromNodeIndex(posPacman,targetsArray, Constants.DM.MANHATTAN));
        */
        return oldDistance;
    }

    public static double InfluenceFunction(Game game){
        float influence = 0; // UTILITY

        // Optimize Parameters
        float p_dots = 1/(float)10;
        float p_run = 1;
        float p_chase = 30;
        float p_powerDots =1;

        // Sum Indices
        int n_d = game.getNumberOfActivePills();// number of uneated pills
        int n_pd = game.getNumberOfActivePowerPills(); // number of uneaten powerpills;
        int n_totalPowerPills = game.getNumberOfPowerPills(); //Total number of PowerPills;
        int n_totalPills = game.getNumberOfPills(); // Total number of pills;
        int n_d_signed = n_totalPills - n_d; // number of eated pills
        int n_pd_signed = n_totalPowerPills-n_pd; //number of eated power pills

        int n_r = 0; // number of unedible ghosts
        int n_c = 0; // number of edible ghosts

        int[] ghostUnedibleIndices = new int[4];
        int[] ghostEdibleIndices = new int[4];
        for (GHOST ghostType: GHOST.values()){
            if (!game.isGhostEdible(ghostType) && game.getGhostLairTime(ghostType)==0) { //
                ghostUnedibleIndices[n_r] = game.getGhostCurrentNodeIndex(ghostType);
                n_r++;
            }else if(game.isGhostEdible(ghostType)){
                ghostEdibleIndices[n_c] = game.getGhostCurrentNodeIndex(ghostType);
                n_c++;
            }
        }

        //p_dots = (p_dots+n_d_signed)/n_totalPills;
        float pill_influence = 0;
        float powerPill_influence = 0;
        float unedibleGhost_influence = 0;
        float edibleGhost_influence = 0;

        int[] pillIndices = game.getPillIndices();
        int[] powerPillIndices = game.getPowerPillIndices();

        //ArrayList<Integer> targets=new ArrayList<Integer>(Arrays.stream(pillIndices).boxed().collect(Collectors.toList()));
        //targets.addAll(Arrays.stream(powerPillIndices).boxed().collect(Collectors.toList()));

        //System.out.println(targets.size());
        int posPACMAN = game.getPacmanCurrentNodeIndex(); //Pacman Position

        //PILLS INFLUENCE
        for(int k = 0; k<n_d/2; k++){
            double den = game.getShortestPathDistance(posPACMAN, game.getClosestNodeIndexFromNodeIndex(posPACMAN, pillIndices, Constants.DM.PATH));
            //double den = game.getManhattanDistance(posPACMAN, game.getClosestNodeIndexFromNodeIndex(posPACMAN, pillIndices, Constants.DM.MANHATTAN));
            //double den = game.getEuclideanDistance(posPACMAN, game.getClosestNodeIndexFromNodeIndex(posPACMAN, pillIndices, Constants.DM.EUCLID));
            if (den == 0) // correzione se +inf
                den = 1;
            pill_influence += (float)p_dots*n_d_signed/den;
        }
        //POWERPILLS INFLUENCE
        for(int k=0; k<n_pd; k++){
            double den = game.getShortestPathDistance(posPACMAN, game.getClosestNodeIndexFromNodeIndex(posPACMAN, powerPillIndices, Constants.DM.PATH));
            //double den = game.getManhattanDistance(posPACMAN, game.getClosestNodeIndexFromNodeIndex(posPACMAN, pillIndices, Constants.DM.MANHATTAN));
            //double den = game.getEuclideanDistance(posPACMAN, game.getClosestNodeIndexFromNodeIndex(posPACMAN, pillIndices, Constants.DM.EUCLID));
            if (den == 0) // correzione se +inf
                den = 1;
            powerPill_influence += (float)p_powerDots*n_pd_signed/den;
        }

        //Pill influence total
/*        for(int k=0; k<n_d; k++){
            //double den = game.getEuclideanDistance(game.getPacmanCurrentNodeIndex(), targets.get(k));
            double den = game.getShortestPathDistance(posPACMAN, pillIndices[k]);
            //double den = game.getManhattanDistance(game.getPacmanCurrentNodeIndex(), targets.get(k));

            if (den == 0) // correzione se +inf
                den = 1;

            pill_influence += (float)p_dots*n_d_signed/den;
            //System.out.println("pill_influence " + k + ": " + pill_influence);
        }*/


        //System.out.println("pill_influence totale" + pill_influence);

        //UNEDIBLE GHOST INFLUENCE
        for(int k=0; k<n_r; k++) {
            //unedibleGhost_influence += p_run * n_d / game.getManhattanDistance(game.getPacmanCurrentNodeIndex(), ghostUnedibleIndices[k]);
            unedibleGhost_influence += p_run * n_d / game.getEuclideanDistance(posPACMAN, ghostUnedibleIndices[k]);
            //unedibleGhost_influence += p_run * n_d / game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), ghostUnedibleIndices[k]);

        }
        //EDIBLE GHOST INFLUENCE
        for(int k=0; k<n_c; k++) {
            //edibleGhost_influence += p_chase / game.getEuclideanDistance(game.getPacmanCurrentNodeIndex(), ghostEdibleIndices[k]);
            edibleGhost_influence += p_chase / game.getShortestPathDistance(posPACMAN, ghostEdibleIndices[k]);
            //edibleGhost_influence += p_chase / game.getManhattanDistance(game.getPacmanCurrentNodeIndex(), ghostEdibleIndices[k]);
        }

        influence = pill_influence  - unedibleGhost_influence + edibleGhost_influence;
        //influence = pill_influence + powerPill_influence - unedibleGhost_influence + edibleGhost_influence
        //System.out.println("Influence: " + influence + ", " + pill_influence +", " + unedibleGhost_influence+", "+  edibleGhost_influence );
        return influence;
    }


    public static void legacyMin(Game g){
        EnumMap<GHOST, MOVE> ghostMove= new EnumMap<GHOST, MOVE>(GHOST.class);
        int count = 0;
        for(GHOST ghostType : GHOST.values()) {
            if (ghostType == GHOST.SUE){

            }else{
                int posGhost = g.getGhostCurrentNodeIndex(ghostType);
                int posPacMan = g.getPacmanCurrentNodeIndex();
                int node = (int) g.getDistance(posGhost, posPacMan, DM.values()[count]);
                MOVE move = g.getMoveToMakeToReachDirectNeighbour(posGhost, node);
                ghostMove.put(ghostType, move);
                count ++;
            }
        }
    }
}
