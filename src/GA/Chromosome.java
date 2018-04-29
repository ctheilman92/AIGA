package GA;

import java.util.ArrayList;
import java.util.Random;



public class Chromosome {

    private ArrayList<Integer> GOALSTATE;
    private int SIZE;                   //board size is [SIZE*SIZE]
    private int NUMTILES;               //number of tiles in a set STATE.   = SIZE * SIZE (since we are designating '0' as the empty tile
    private float FitnessGrade;           //this set when population is created
    private float FitnessProbability;   //this set later when selection occurs -- we divide by the total of all chromosome's fitness grades to get probability of selection
    private ArrayList<Integer> STATE;

    public Chromosome(int n) {
        SIZE = n;
        NUMTILES = (n*n);

        GOALSTATE = new ArrayList<>(NUMTILES);
        for(int i = 1; i < NUMTILES; i++) {
            GOALSTATE.add(i);
        }

        GOALSTATE.add(0);
    }

    //this constructor used when a new child is crossbred
    public Chromosome(int n, ArrayList<Integer> NewState) {
        SIZE = n;
        NUMTILES = (n*n);

        GOALSTATE = new ArrayList<>(NUMTILES);
        for (int i = 1; i < NUMTILES; i++) {
            GOALSTATE.add(i);
        }
        GOALSTATE.add(0);
        STATE = NewState;
        SetFitnessGradeByStepComparitor();
    }


    public void SetState(ArrayList<Integer> NewState) {
        this.STATE = NewState;
        this.SetFitnessGradeByStepComparitor();
    }

    public void SetState() {
        STATE = new ArrayList<>();
        int tile;

        Random gen = new Random();
        for (int i = 0; i < NUMTILES; i++) {

            do {
                tile = gen.nextInt(NUMTILES);

            } while(STATE.contains(tile));


            STATE.add(tile);
        }

        //after this we need to set the fitness grade for this chromosome
        this.SetFitnessGradeByStepComparitor();
    }

    public ArrayList<Integer> GetState() {
        return STATE;
    }

    public ArrayList<Integer> GetGoal() { return GOALSTATE; }

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

    public boolean IsGoal() { return (STATE.equals(GOALSTATE)) ? true : false; }

    private int[] TransposeGridCoordinates(ArrayList<Integer> State, int ValueInState) {
        int StatePos = State.indexOf(ValueInState);
//        System.out.println("TILE (" + ValueInState + ") GRADIENT: -> " + StatePos);
        int Si = 1;
        int Sj = 0;

        //col
        Sj = (StatePos + 1) % SIZE;
        if (Sj == 0) {
            Sj = SIZE;
        }


        //row
        Si = (int)Math.floor((StatePos) / SIZE) + 1;

        int[] coords = new int[2];
        coords[0] = Si;
        coords[1] = Sj;

        return coords;
    }

    //if tile is in intended position -> tile grade = 1;
    //if 1 step away take portion of steps away from 1 (ex. if n=3 => maxsteps = 4...2 is in position 1 (1 step away) -> 2 is 3/4.
    public void SetFitnessGradeByStepComparitor() {
        int MaxMoves = MaxMoves();
        float SigmaTileMoves = 0;

        for (int tile : STATE) {

            int[] StateCoordinates = TransposeGridCoordinates(STATE, tile);
            int[] GoalCoordinates = TransposeGridCoordinates(GOALSTATE, tile);

            int TileMovesToGoal = Math.abs(GoalCoordinates[0] - StateCoordinates[0]) + Math.abs(GoalCoordinates[1] - StateCoordinates[1]);
            int variantTileGrade = MaxMoves - TileMovesToGoal;

            float VariantProportionGrade = ((float)variantTileGrade / (float)MaxMoves);
            SigmaTileMoves += VariantProportionGrade;

        }

        this.FitnessGrade = SigmaTileMoves;
    }


    //this sets the fitness function based on the sum of each tile's grade as how many steps away from the expected position for that tile's value
    //total moves = 2N - 2
    private int MaxMoves() { return ((2*SIZE) -2); }


    //for this we determine sigma(1,n^2) - n*i)
    private void SetFitnessGradeByValueMultiplier() {

        int total = 0;

        for (int tile : STATE) {
            int currentPos = STATE.indexOf(tile);
            total += (currentPos * tile);
        }

        this.FitnessGrade = total;
    }

    //this fitness function compares position relative to Goal State
    //if (S,i == G,i) Fitness += 1
    private void SetFitnessGradeByPositionMatch() {
        int fc = 0;

        for (int i = 0; i < GOALSTATE.size(); i++) {
            int gval = GOALSTATE.get(i);
            int sval = STATE.get(i);

            if (gval == sval)
                fc++;
        }

        this.FitnessGrade = fc;

    }

    public float GetFitnessGrade() {
        return this.FitnessGrade;
    }

    public void SetProbabilityGrade(float SumAll) { this.FitnessProbability = (this.FitnessGrade / SumAll) * 100; }

    public float GetProbabilityGrade() { return FitnessProbability; }
}
