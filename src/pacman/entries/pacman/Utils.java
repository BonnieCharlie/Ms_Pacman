package pacman.entries.pacman;

import org.omg.PortableInterceptor.INACTIVE;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.DM;
import pacman.game.Game;
import pacman.game.internal.Ghost;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static double EvaluationFunction(Game game) {

        if (game.getNumberOfActivePills() + game.getNumberOfActivePowerPills() == 0) {
            return Double.POSITIVE_INFINITY;
        } else if (game.gameOver() || game.wasPacManEaten()) {
            return Double.NEGATIVE_INFINITY;
        }


        int score = game.getScore();
        int posPacman = game.getPacmanCurrentNodeIndex();
        float distance = 10000;
        float oldDistance = distance;
        Constants.GHOST nearestghost;
        nearestghost = null;
        for (Constants.GHOST ghost : Constants.GHOST.values()) {

            distance = game.getShortestPathDistance(posPacman, game.getGhostCurrentNodeIndex(ghost));

            if (distance == -1) {
                distance = 10000;
            }
            if (distance < oldDistance) {
                oldDistance = distance;
                nearestghost = ghost;
            }

        }
        return oldDistance;
    }

    public static double InfluenceFunction(Game game) {
        float influence = 0; // UTILITY

        // Optimize Parameters
        float p_dots = 1 / (float) 5;
        float p_run = 1;
        float p_chase = 30;
        float p_powerDots = 1;

        // Sum Indices
        int n_d = game.getNumberOfActivePills();// number of uneated pills
        int n_pd = game.getNumberOfActivePowerPills(); // number of uneaten powerpills;
        int n_totalPowerPills = game.getNumberOfPowerPills(); //Total number of PowerPills;
        int n_totalPills = game.getNumberOfPills(); // Total number of pills;
        int n_d_signed = n_totalPills - n_d; // number of eated pills
        int n_pd_signed = n_totalPowerPills - n_pd; //number of eated power pills

        int n_r = 0; // number of unedible ghosts
        int n_c = 0; // number of edible ghosts

        int[] ghostUnedibleIndices = new int[4];
        int[] ghostEdibleIndices = new int[4];
        for (GHOST ghostType : GHOST.values()) {
            if (!game.isGhostEdible(ghostType) && game.getGhostLairTime(ghostType) == 0) { //
                ghostUnedibleIndices[n_r] = game.getGhostCurrentNodeIndex(ghostType);
                n_r++;
            } else if (game.isGhostEdible(ghostType)) {
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
        /*
        for (int k = 0; k < (n_d / 4); k++) {
            double den = game.getShortestPathDistance(posPACMAN, game.getClosestNodeIndexFromNodeIndex(posPACMAN, pillIndices, Constants.DM.PATH));
            //double den = game.getManhattanDistance(posPACMAN, game.getClosestNodeIndexFromNodeIndex(posPACMAN, pillIndices, Constants.DM.MANHATTAN));
            //double den = game.getEuclideanDistance(posPACMAN, game.getClosestNodeIndexFromNodeIndex(posPACMAN, pillIndices, Constants.DM.EUCLID));
            if (den == 0) // correzione se +inf
                den = 1;
            pill_influence += (float) p_dots * n_d_signed / den;
        }
        //POWERPILLS INFLUENCE
        for (int k = 0; k < n_pd; k++) {
            double den = game.getShortestPathDistance(posPACMAN, game.getClosestNodeIndexFromNodeIndex(posPACMAN, powerPillIndices, Constants.DM.PATH));
            //double den = game.getManhattanDistance(posPACMAN, game.getClosestNodeIndexFromNodeIndex(posPACMAN, pillIndices, Constants.DM.MANHATTAN));
            //double den = game.getEuclideanDistance(posPACMAN, game.getClosestNodeIndexFromNodeIndex(posPACMAN, pillIndices, Constants.DM.EUCLID));
            if (den == 0) // correzione se +inf
                den = 1;
            powerPill_influence += (float) p_powerDots * n_pd_signed / den;
        }
*/
        //Pill influence total
        for (int k = 0; k < n_d; k++) {
            //double den = game.getEuclideanDistance(game.getPacmanCurrentNodeIndex(), targets.get(k));
            double den = game.getShortestPathDistance(posPACMAN, pillIndices[k]);
            //double den = game.getManhattanDistance(game.getPacmanCurrentNodeIndex(), targets.get(k));

            if (den == 0) // correzione se +inf
                den = 1;

            pill_influence += (float) p_dots * n_d_signed / den;
            //System.out.println("pill_influence " + k + ": " + pill_influence);
        }


        //System.out.println("pill_influence totale" + pill_influence);

        //UNEDIBLE GHOST INFLUENCE
        for (int k = 0; k < n_r; k++) {
            //unedibleGhost_influence += p_run * n_d / game.getManhattanDistance(game.getPacmanCurrentNodeIndex(), ghostUnedibleIndices[k]);
            unedibleGhost_influence += p_run * n_d / game.getEuclideanDistance(posPACMAN, ghostUnedibleIndices[k]);
            //unedibleGhost_influence += p_run * n_d / game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), ghostUnedibleIndices[k]);

        }
        //EDIBLE GHOST INFLUENCE
        for (int k = 0; k < n_c; k++) {
            //edibleGhost_influence += p_chase / game.getEuclideanDistance(game.getPacmanCurrentNodeIndex(), ghostEdibleIndices[k]);
            edibleGhost_influence += p_chase / game.getShortestPathDistance(posPACMAN, ghostEdibleIndices[k]);
            //edibleGhost_influence += p_chase / game.getManhattanDistance(game.getPacmanCurrentNodeIndex(), ghostEdibleIndices[k]);
        }

        influence = pill_influence - unedibleGhost_influence + edibleGhost_influence;
        //influence = pill_influence + powerPill_influence - unedibleGhost_influence + edibleGhost_influence
        //System.out.println("Influence: " + influence + ", " + pill_influence +", " + unedibleGhost_influence+", "+  edibleGhost_influence );
        return influence;
    }


    public static ArrayList<EnumMap<GHOST, MOVE>> legacyMin(Game g) {

        ArrayList<EnumMap<GHOST, MOVE>> listMoves = new ArrayList<EnumMap<GHOST, MOVE>>();
        EnumMap<GHOST, MOVE> ghostMove = new EnumMap<GHOST, MOVE>(GHOST.class);
        int targetNode = g.getPacmanCurrentNodeIndex();
        //Random rnd = new Random();
        MOVE[] moves = MOVE.values();

        for (GHOST ghostType : GHOST.values()) {
            if (ghostType == GHOST.SUE) {

                // If ghost is edible, he doesn't follow pacman
                if (g.isGhostEdible(ghostType)) {
                    ghostMove.put(GHOST.SUE, g.getGhostLastMoveMade(GHOST.SUE));
                } else {
                    MOVE[] possibleMoves = g.getPossibleMoves(g.getGhostCurrentNodeIndex(GHOST.SUE));
                    if (possibleMoves.length<3){
                        possibleMoves = g.getPossibleMoves(g.getGhostCurrentNodeIndex(GHOST.SUE), g.getGhostLastMoveMade(GHOST.SUE));
                    }
                    for (MOVE move : possibleMoves) {
                        ghostMove.put(GHOST.SUE, move);
                        listMoves.add(ghostMove.clone());
                    }
                }
                //ghostMove.put(ghostType, MOVE.NEUTRAL);
            }
            if (ghostType == GHOST.BLINKY) {
                // If ghost is edible, he doesn't follow pacman
                if (g.isGhostEdible(ghostType)) {
                    ghostMove.put(GHOST.BLINKY, g.getApproximateNextMoveAwayFromTarget(g.getGhostCurrentNodeIndex(GHOST.BLINKY), targetNode, g.getGhostLastMoveMade(GHOST.BLINKY), DM.PATH));
                } else {
                    ghostMove.put(GHOST.BLINKY, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(GHOST.BLINKY), targetNode, g.getGhostLastMoveMade(GHOST.BLINKY), DM.PATH));
                }
            } else if (ghostType == GHOST.INKY) {
                // If ghost is edible, he doesn't follow pacman
                if (g.isGhostEdible(ghostType)) {
                    ghostMove.put(GHOST.INKY, g.getApproximateNextMoveAwayFromTarget(g.getGhostCurrentNodeIndex(GHOST.INKY), targetNode, g.getGhostLastMoveMade(GHOST.INKY), DM.MANHATTAN));
                } else {
                    ghostMove.put(GHOST.INKY, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(GHOST.INKY), targetNode, g.getGhostLastMoveMade(GHOST.INKY), DM.MANHATTAN));
                }
            } else if (ghostType == GHOST.PINKY) {
                // If ghost is edible, he doesn't follow pacman
                if (g.isGhostEdible(ghostType)) {
                    ghostMove.put(GHOST.PINKY, g.getApproximateNextMoveAwayFromTarget(g.getGhostCurrentNodeIndex(GHOST.PINKY), targetNode, g.getGhostLastMoveMade(GHOST.PINKY), DM.EUCLID));
                } else {
                    ghostMove.put(GHOST.PINKY, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(GHOST.PINKY), targetNode, g.getGhostLastMoveMade(GHOST.PINKY), DM.EUCLID));

                }
            }
        }
        if (listMoves.size() == 0) {
            ghostMove.put(GHOST.SUE, MOVE.NEUTRAL);
            listMoves.add(ghostMove);
        }
        //System.out.println(ghostMove);
        //System.out.println(listMoves);
        ghostMove.clear();
        //System.out.println("List moves 2: " +listMoves);
        return listMoves;
    }

    public static float rulesUtilityFunction(Game game){

        if (game.getNumberOfActivePills() + game.getNumberOfActivePowerPills() == 0) {
            return Float.MAX_VALUE;
        } else if (game.gameOver() || game.wasPacManEaten()) {
            return Float.MIN_VALUE;
        }

        //GAME ELEMENTS
        int score = game.getScore();
        int posPacman = game.getPacmanCurrentNodeIndex();
        int[] pillIndices = game.getPowerPillIndices();
        int minDistancePill = -1;
        int minDistanceGhost = Integer.MAX_VALUE;
        int activePowerPills= game.getNumberOfActivePowerPills();
        float utility = 0;

        EnumMap<GHOST, Integer> distanceGhosts = new EnumMap<GHOST, Integer>(GHOST.class);

        // Save a map of ghosts distances and the minimum distance
        for (GHOST ghost: GHOST.values()){
            int dist = game.getShortestPathDistance(posPacman,game.getGhostCurrentNodeIndex(ghost));
            //System.out.println(dist);
            if (dist ==-1){
                dist = 10000;
            }
            distanceGhosts.put(ghost,dist);
            if(dist<minDistanceGhost){
                minDistanceGhost=dist;
            }
        }

        //Calculate Distance between pacman and nearest pill
        minDistancePill = game.getShortestPathDistance(posPacman, game.getClosestNodeIndexFromNodeIndex(posPacman, pillIndices, DM.PATH));

        int numFarGhosts = 0;
        // RULES
        for (EnumMap.Entry<GHOST,Integer> entry: distanceGhosts.entrySet()){
            int value = entry.getValue();
            if (value > 40){
                numFarGhosts++;
            }
        }
        utility += 100*numFarGhosts;
        if (minDistanceGhost > 20){
            utility = utility +(1/(float)minDistancePill)*1000 + score;
        }
        //System.out.println(minDistanceGhost);
        if(minDistanceGhost <20){
            utility -= 200;
        }
        //System.out.println(utility);
        return utility;
    }


    public static float utilityFunction(Game game){

        int posPacman = game.getPacmanCurrentNodeIndex();
        int [] powerPillIndices = game.getPowerPillIndices();
        int nearestPowerPillDistance = game.getShortestPathDistance(posPacman, game.getClosestNodeIndexFromNodeIndex(posPacman, powerPillIndices, DM.PATH));
        EnumMap<GHOST, Integer> distanceGhosts = new EnumMap<GHOST, Integer>(GHOST.class);
        int nearestGhostDistance = Integer.MAX_VALUE;
        int nearestEdibleGhostDistance = Integer.MAX_VALUE;
        GHOST nearestGhostEdible = GHOST.BLINKY;
        GHOST nearestGhost = GHOST.SUE;
        float utility = 0;
        int n_r = 0; // number of unedible ghosts
        int n_c = 0; // number of edible ghosts
        int n_d = game.getNumberOfActivePills();// number of uneated pills
        int n_totalPills = game.getNumberOfPills(); // Total number of pills;
        int n_d_signed = n_totalPills - n_d; // number of eated pills

        // Save a map of ghosts distances and the minimum distance
        for (GHOST ghost: GHOST.values()){
            int dist = game.getShortestPathDistance(posPacman,game.getGhostCurrentNodeIndex(ghost));
            //System.out.println(dist);
            if (dist ==-1){
                dist = 1000;
            }
            distanceGhosts.put(ghost,dist);
            if(dist<nearestGhostDistance){
                nearestGhostDistance=dist;
            }
        }

        float overallDistanceEdibleGhost = 0;

        int[] ghostUnedibleIndices = new int[4];
        int[] ghostEdibleIndices = new int[4];
        for (GHOST ghostType : GHOST.values()) {
            if (!game.isGhostEdible(ghostType) && game.getGhostLairTime(ghostType) == 0) { //
                ghostUnedibleIndices[n_r] = game.getGhostCurrentNodeIndex(ghostType);
                n_r++;
            } else if (game.isGhostEdible(ghostType)) {
                int posGhost = game.getGhostCurrentNodeIndex(ghostType);
                ghostEdibleIndices[n_c] = posGhost;
                int distanceBetweenGhostAndPacman = game.getShortestPathDistance(posPacman, posGhost);
                overallDistanceEdibleGhost += (ghostType.initialLairTime/(float)20) * (1/(float)distanceBetweenGhostAndPacman);
                if(distanceBetweenGhostAndPacman < nearestEdibleGhostDistance){
                    nearestEdibleGhostDistance = distanceBetweenGhostAndPacman;
                    nearestGhostEdible = ghostType;
                }
                n_c++;
            }
        }

        //get all active pills
        int[] activePills=game.getActivePillsIndices();

        //get all active power pills
        int[] activePowerPills=game.getActivePowerPillsIndices();

        //create a target array that includes all ACTIVE pills and power pills
        int[] targetNodeIndices=new int[activePills.length+activePowerPills.length];

        for(int i=0;i<activePills.length;i++)
            targetNodeIndices[i]=activePills[i];

        for(int i=0;i<activePowerPills.length;i++)
            targetNodeIndices[activePills.length+i]=activePowerPills[i];

        int nearestPillDistance = 0;
        try{
            if(targetNodeIndices.length == 0){
                nearestPillDistance = 1;
            }else{
                nearestPillDistance = game.getShortestPathDistance(posPacman, game.getClosestNodeIndexFromNodeIndex(posPacman, targetNodeIndices, DM.PATH));
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage() + "  targetNodeIndices: " + Arrays.toString(targetNodeIndices) + "    posPacman: " + posPacman);
        }
        //int nearestPillDistance = game.getShortestPathDistance(posPacman, game.getClosestNodeIndexFromNodeIndex(posPacman, targetNodeIndices, DM.PATH));
        int nearestGhostToTheNearestPowerPillDistance = game.getShortestPathDistance(nearestGhostDistance, game.getClosestNodeIndexFromNodeIndex(posPacman, powerPillIndices, DM.PATH));

        // *********************** RULES *************************
        // WINNING CONDITION
        /*if (n_totalPills + game.getNumberOfActivePowerPills() == 0) {
            utility += 10000;
        }

        // LOST CONDITION
        if (game.gameOver() || game.wasPacManEaten()) {
            utility -= 1000;
        }*/

        // RULE 1: move to the nearest power pill
        if(game.getNumberOfActivePowerPills()>1 && nearestGhostDistance <=8 && nearestPowerPillDistance<=nearestGhostToTheNearestPowerPillDistance){
            utility += nearestPowerPillDistance;
        }

        // RULE 2: move to the nearest edible ghost if exists at least one edible ghost
        if(n_c >= 1 && nearestGhostDistance <= 20 && nearestEdibleGhostDistance <=20 ){
            if(game.isGhostEdible(nearestGhost) && (game.getGhostEdibleTime(nearestGhost)>2)){
                utility = utility + n_c*(overallDistanceEdibleGhost) + game.getScore();
            }
        }

        // RULE 3: move to the nearest pill
        if (nearestGhostDistance>=20 || game.isGhostEdible(nearestGhost)){
            utility = utility +(1/(float)nearestPillDistance) + n_d_signed + game.getScore();
        }

        // RULE 4: move away from ghosts
        if ((nearestGhostDistance<=10 && !game.isGhostEdible(nearestGhost))){
            utility -= 1000;
        }

        // RULE 5: considering overall distance from ghosts


        //System.out.println("nearestEdibleGhostDistance: " + nearestEdibleGhostDistance + ", nearestGhostDistance: " + nearestGhostDistance + ", utility = " + utility);
        return utility;
    }
}
