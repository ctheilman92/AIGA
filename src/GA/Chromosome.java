package GA;

import java.util.ArrayList;
import java.util.Random;

public class Chromosome {

    private int SIZE;           //board size is [SIZE*SIZE]
    private int NUMTILES;       //number of tiles in a set STATE.   = SIZE * SIZE (since we are designating '0' as the empty tile
    private float fitness;
    private ArrayList<Integer> STATE;

    public Chromosome(int n) {
        SIZE = n;
        NUMTILES = (n*n);
    }

    public void SetState() {
        STATE = new ArrayList<>();
        int tile;

        Random gen = new Random();
        for (int i = 0; i < NUMTILES; i++) {

            do {
                tile = gen.nextInt() + 1;
            }
            while(STATE.contains(tile));
            STATE.add(tile);
        }
    }

    public ArrayList<Integer> GetState() {
        return STATE;
    }

    public boolean ISStateSolvable() {

        /*solution is solvable in following cases:
            : IF SIZE = EVEN => Total Inversion Count is ODD
            : IF SIZE = ODD => TOTAL Inversion Count is EVEN
        */
        boolean Solvable = false;
        int InversionCount = 0;

        //count pairs(i, j) such that i appears before j, and i > j
        for (int tile : STATE) {
            int positionOfCurrent = STATE.indexOf(tile);

            //start at 1 -- find position of tile 1. if position is higher than position of the current tile we are checking -- then INVERSION -- move to next number
            for (int previousTile = 1; previousTile < tile; previousTile++) {
                int positionOfPrevious = STATE.indexOf(previousTile);

                if (positionOfPrevious > positionOfCurrent)
                    InversionCount++;
            }
        }

        //if even
        if (SIZE % 2 == 0) {
            Solvable = (InversionCount % 2 == 0) ? false : true;
        }
        else {
            //else false
            Solvable = (InversionCount % 2 == 0) ? true : false;
        }

        return Solvable;
    }

    private void SetFitness() {

    }


}
