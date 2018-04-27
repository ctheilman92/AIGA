package GA;

import java.util.ArrayList;
import java.util.Random;

/*



 */

public class GA {

    int SIZE;                           //The length of columns in rows in the board states such that the board is [ SIZE * SIZE ]
    int POPULATION_SIZE;                //population size (BOARD_SIZE^2)^3 to gaurentee a large enough set of solvable states are in population
    ArrayList<Chromosome> POPULATION;   //a set of state configurations possible solvable configurations

    public GA() { }

    public GA(int N) {
        this.SIZE = N;
        this.POPULATION_SIZE = 50;

        System.out.println(SIZE + " ======== " + POPULATION_SIZE);
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
            while (!c.IsStateSolvable() || !IsUniqueChromosome(c)) {
                c.SetState();
            }

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

    public String PrintProbabilities() {
        StringBuilder sb = new StringBuilder();

        for(Chromosome c : POPULATION) {
            String p = String.valueOf(c.GetProbabilityGrade());
            sb.append(p);
            sb.append('\n');
        }

        return sb.toString();
    }

    public float GetSumFitness() {
        float SumAll = 0.0f;
        for (Chromosome c : this.POPULATION) {
            SumAll += c.GetFitnessGrade();
        }

        return SumAll;
    }

    public ArrayList<Chromosome> RoulletteSelect() {

        ArrayList<Chromosome> ParentsChosen = new ArrayList<Chromosome>();
        float SumFitness = GetSumFitness();

        //for every chromosome set a fitness-proportionate to the population
        for (Chromosome c : POPULATION) {
            c.SetProbabilityGrade(SumFitness);
        }


        Random gen = new Random();
        int getIndex1 = gen.nextInt(POPULATION_SIZE);
        int getIndex2 = gen.nextInt(POPULATION_SIZE);

        ParentsChosen.add(POPULATION.get(getIndex1));
        ParentsChosen.add(POPULATION.get(getIndex2));


        return ParentsChosen;

    }


}
