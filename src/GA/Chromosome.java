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
                tile = gen.nextInt(NUMTILES);

            } while(STATE.contains(tile));

            System.out.println(tile);
            STATE.add(tile);

        }
    }

    public ArrayList<Integer> GetState() {
        return STATE;
    }

    public boolean IsStateSolvable() {

        /*solution is solvable in following cases:
            : IF SIZE = EVEN => Total Inversion Count is (EVEN|ODD) && the 0 tile is on an (ODD|EVEN) space
            : IF SIZE = ODD => TOTAL Inversion Count is EVEN
        */

        int InversionCount = 0;
        int row = 0;
        int blankrow = 0;

        //count pairs(i, j) such that i appears before j, and i > j
        for (int tile : STATE) {
            int positionOfCurrent = STATE.indexOf(tile);

            if (row % 2 == 0)
                row++;

            //keep track of the row where the blank is.
            if (STATE.get(positionOfCurrent) == 0) {
                blankrow = row;
                continue;
            }

            //start at 1 -- find position of tile 1. if position is higher than position of the current tile we are checking -- then INVERSION -- move to next number
            for (int previousTile = 1; previousTile < tile; previousTile++) {
                int positionOfPrevious = STATE.indexOf(previousTile);

                if (positionOfPrevious > positionOfCurrent)
                    InversionCount++;
            }
        }

        //if even
        if (SIZE % 2 == 0) {
            if (blankrow % 2 == 0) {
                return InversionCount % 2 == 0;
            }
            else {
                return InversionCount % 2 != 0;
            }
        }
        else {
            //else false (odd size)
            return InversionCount % 2 == 0;
        }


    }

    private void SetFitness() {

    }


}
