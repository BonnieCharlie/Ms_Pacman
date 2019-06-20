package pacman.game;

import java.util.ArrayList;

public class main {
    public static void main(String args[]){
        ArrayList<Integer[]> movesGhosts = new ArrayList<Integer[]>();
        for(int i=0; i<5; i++){
            Integer[] ghosts = {1,2,3,4};
            movesGhosts.add(ghosts);
        }
        System.out.println(movesGhosts.size());
    }
}
