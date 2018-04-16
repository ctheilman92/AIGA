package GA;

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

    public String PrintGeneration() {

        //this is just a formatted output
        //3 states per line
        int lineCount = 0;

        StringBuilder sb = new StringBuilder();

        for (Chromosome c : POPULATION) {
            sb.append(c.GetState().toString() + " , ");
            lineCount++;

            if (lineCount % 3 == 0)
                sb.append("\n-----------------------------------------------------------------------------------------\n");
        }

        return sb.toString();
    }


    public void RandomPopulation() {

        POPULATION = new ArrayList<Chromosome>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            Chromosome c = new Chromosome(SIZE);

            //generate each chromosome configuration such that each solution is solvable AND unique to our population
            c.SetState();
            while (!c.ISStateSolvable() || !IsUniqueChromosome(c)) {
                c.SetState();
            }

//            System.out.println(c.GetState().toString());
//            System.out.println("-------------------------------");
            POPULATION.add(c);

        }


    }

    public boolean IsUniqueChromosome(Chromosome c) {

        String cstate = c.GetState().toString();
        for (Chromosome compc : POPULATION) {

            String compstate = compc.GetState().toString();

            if (cstate.equals(compstate))
                return false;
        }

        return true;
    }

    public ArrayList<Chromosome> GetPopulation() {
        return POPULATION;
    }

}
