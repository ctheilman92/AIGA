package GA;

import com.sun.org.apache.bcel.internal.generic.NEW;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GA {

    int SIZE;                           //The length of columns in rows in the baord states such that the board is [ SIZE * SIZE ]
    int POPULATION_SIZE;                //population size (BOARD_SIZE^2)^3 to guarentee a large enough set of solvable states are in population
    ArrayList<Chromosome> POPULATION;   //a set of state configurations possible solvable configurations

    public GA() { }

    public GA(int N) {
        this.SIZE = N;
        this.POPULATION_SIZE = (int)Math.pow(N, 3);
    }


    public void RandomPopulation() {

        POPULATION = new ArrayList<Chromosome>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            Chromosome c = new Chromosome(SIZE);

            //generate each chromosome configuration such that each solution is solvable AND unique to our population
            c.SetState();
            while (!c.ISStateSolvable() && ) {
                c.SetState();
            }



        }


    }

    public boolean IsUniqueChromosome(Chromosome c) {

    }

    public ArrayList<Chromosome> GetPopulation() {
        return POPULATION;
    }

}
