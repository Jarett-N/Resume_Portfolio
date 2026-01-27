import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Random;



public class RabbitSimulation {
    public static void main(String[] args) {
        //Enter input file
        File file = new File("RabbitSimTest.txt");
        ArrayList<ArrayList<Rabbit>> rabbits = new ArrayList<>();
        ArrayList<ArrayList<Rabbit>> initialRabbits = new ArrayList<>();
        double average = 0;
        double stdDev = 0;
        int i = 0, j = 0, m = 0;

        try {
            Scanner lineReader = new Scanner(file);
            String line;

            //checks if the file is empty
            if (lineReader.hasNextLine()) {

                //turns file into 2D ArrayList rabbits
                while (lineReader.hasNextLine()) {
                    line = lineReader.nextLine();
                    Scanner rabbitReader = new Scanner(line);
                    rabbitReader.useDelimiter("\t");

                    rabbits.add(i, new ArrayList<>());
                    initialRabbits.add(i, new ArrayList<>());

                    int a = rabbitReader.nextInt();
                    //adds females to the list
                    for (j = 0; j < a; j++) {
                        rabbits.get(i).add(new Rabbit(0, 0, 0, 0)); //ask chet?
                    }

                    a = rabbitReader.nextInt();
                    //adds males to the list
                    for (j = 0; j < a; j++) {
                        rabbits.get(i).add(new Rabbit(1, 0, 0, 0));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
        }

        /*Main Trialsets loop first
        then the indv trials with days,
        then the loops for checking and going through each rabbit
        * */
        int ts;
        int day;
        int id = 0;
        int popsize = 1; //= length of rabbits array

        if(rabbits.isEmpty()){
            System.out.println("rabbits array is empty");
            System.exit(0);
        }

        for (ts = 0; ts < 10; ts++){
            for (day = 0; day < 365; day++){
                for (id = 0; id < popsize; id ++) {
                    if (rabbits.get(id).get(j).getSex() == 0) {
                        rabbits.get(id).get(j).setAge(rabbits.get(id).get(j).getAge() + 1);
                        while (rabbits.get(id).get(j).getAge() >= 100)
                            if (rabbits.get(id).get(j).getPreg() > 0) {
                                rabbits.get(id).get(j).setPreg(rabbits.get(id).get(j).getPreg() - 1);
                                if (rabbits.get(id).get(j).getPreg() == 0) {
                                    Random rand = new Random();
                                    int randomlit = rand.nextInt(3,9); //correct way for rand between 3 and 8
                                    for (int li = randomlit; li > 0; li--) {
                                        int upperb = 2;
                                        int lsex = rand.nextInt(upperb);
                                        if (lsex == 0) {
                                            rabbits.get(i).add(new Rabbit(0, 0, 0, 0));
                                        } else {
                                            rabbits.get(i).add(new Rabbit(1, 0, 0, 0));
                                        }
                                    }
                                    rabbits.get(id).get(j).setWait(7);
                                }
                            else { if (rabbits.get(id).get(j).getWait() > 0){
                                rabbits.get(id).get(j).setWait(rabbits.get(id).get(j).getWait() - 1);
                                } else {
                                }
                                int pregmin = 28;
                                int pregmax = 32;
                                int randompreg = (int) Math.floor(Math.random() *(pregmax - pregmin + 1) -+ pregmin);
                                    rabbits.get(id).get(j).setPreg(randompreg);
                                }
                            }
                        }
                    }
                }
            ArrayList<Integer> femaleRabbits = new ArrayList<>();
            ArrayList<Integer> maleRabbits = new ArrayList<>();
            for (i = 0; i < rabbits.size() + 1; i++) {
                if (rabbits.get(id).get(j).getSex() == 0){
                    femaleRabbits.add(i);
                }else {
                    maleRabbits.add(i);
                }
                id++;
            }
            System.out.println("Trial" + ts + ":" + rabbits.size() + "was the final population of rabbits;" +
                    femaleRabbits.size() + "Does," + maleRabbits.size() + "Bucks");
            }
        }
    }