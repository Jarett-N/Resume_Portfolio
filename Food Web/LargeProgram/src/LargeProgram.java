import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashSet;

public class LargeProgram {
    public static void displayMenu() {
        System.out.println("Menu:");
        System.out.println("\t(1) The Herbivores, Carnivores, and Omnivores");
        System.out.println("\t(2) Producers");
        System.out.println("\t(3) What each predator eats");
        System.out.println("\t(4) Apex Predators");
        System.out.println("\t(5) The most flexible consumer");
        System.out.println("\t(6) The tastiest treat");
        System.out.println("\t(7) Exit");
        System.out.print("Selection number: ");
    }

    //input: FW, output: what each predator eats
    //creating a string using each row from FW and forming it in a grammatical sentence
    public static void eats(ArrayList<ArrayList<String>> FW){
        int i = 0, j = 1;
        String predEats = "";

        while (i < FW.size()) {
            predEats = FW.get(i).get(0) + " eats ";
            for (j = 1; j < FW.get(i).size();){
                if (FW.get(i).size() == 2)
                    predEats += FW.get(i).get(j);
                else if (j == FW.get(i).size() - 1)
                    predEats += "and " + FW.get(i).get(j);
                else
                    predEats += FW.get(i).get(j) + ", ";
                j++;
            }
            System.out.println(predEats);
            i++;
        }
    }

    //In: Pred/Prey Lists, Out: Apex
    /*
     * declare variables
     * check if each Predator appears in PreyList
     */
    public static ArrayList<String> apexPredators(ArrayList<String> PredList, ArrayList<String> PreyList){
        int i = 0, j = 0;
        ArrayList<String> Apex = new ArrayList<>();
        boolean isApex = false;

        while (i < PredList.size()) {
            while (j < PreyList.size()) {
                if (!PredList.get(i).equals(PreyList.get(j)))
                    isApex = true;
                else {
                    isApex = false;
                    break;
                }
                j++;
            }
            if (isApex)
                Apex.add(PredList.get(i));
            i++;
            j = 0;
            isApex = false;
        }

        return Apex;
    }

    //In: Pred/Prey Lists, Out: Producer
    /*
     * declare variables
     * check if each Prey appears in PredList
     * convert Producer into a HashSet
     */
    public static ArrayList<String> producerlist(ArrayList<String> PredList, ArrayList<String> PreyList) {
        int i = 0, j = 0;
        ArrayList<String> Producer = new ArrayList<>();
        boolean isProducer = false;

        while (i < PreyList.size()) {
            while (j < PredList.size()) {
                if (!PreyList.get(i).equals(PredList.get(j))) {
                    isProducer = true;
                } else {
                    isProducer = false;
                    break;
                }
                j++;
            }
            if (isProducer)
                Producer.add(PreyList.get(i));
            i++;
            j = 0;
            isProducer = false;
        }

        return Producer;
    }

    //In: FW, Out: flex
    /*
     *check if FW is empty
     *declare variables
     *assign first maxCount
     *determine subsequent counts
     *compare individually to determine the most flexible
     */
    public static String flexibleConsumer(ArrayList<ArrayList<String>> FW) {
        int count = 0, maxCount = FW.get(0).size(), i = 1;
        String flex = FW.get(0).get(0);

        while (i < FW.size()) {
            count = FW.get(i).size();
            if (count == maxCount)
                flex += " " + FW.get(i).get(0);
            if (count > maxCount) {
                flex = FW.get(i).get(0);
                maxCount = count;
            }
            i++;
        }

        return flex;
    }

    //In: PreyList, Out: tastiest
    /*
     *declare variables
     *determine first maxCount
     *determine subsequent counts
     *compare individually to determine the tastiest
     */
    public static HashSet<String> tastiestTreat(ArrayList<String> PreyList) {
        int count = 0, maxCount = 0, i = 0, j = 1;
        HashSet<String> Tastiest = new HashSet<>();
        Tastiest.add(PreyList.get(0));

        for (i = 0; i < PreyList.size(); ) {
            if (PreyList.get(0).equals(PreyList.get(i)))
                maxCount++;
            i++;
        }

        for (i = 1; i < PreyList.size(); ) {
            for (j = 0; j < PreyList.size(); ) {
                if (PreyList.get(i).equals(PreyList.get(j)))
                    count++;
                j++;
            }
            if (count == maxCount) {
                Tastiest.add(PreyList.get(i));
            }
            if (count > maxCount) {
                Tastiest.clear();
                Tastiest.add(PreyList.get(i));
                maxCount = count;
            }
            i++;
            count = 0;
        }

        return Tastiest;
    }

    //In: FW, Producer, Out: Herbs
    /*
     *declare variables
     *compare each predators' prey to the list of producers to determine which are herbivores
     */
    public static ArrayList<String> herbivorelist(ArrayList<ArrayList<String>> FW, ArrayList<String> PredList,
                                               ArrayList<String> PreyList) {
        ArrayList<String> Herbs = new ArrayList<>();
        ArrayList<String> Producer2 = producerlist(PredList, PreyList);
        int i = 0, j = 1, l = 0;
        boolean isHerb = false;

        while (i < FW.size()) {
            for (j = 1; j < FW.get(i).size(); ) {
                for (l = 0; l < Producer2.size(); ) {
                    if (FW.get(i).get(j).equals(Producer2.get(l))) {
                        isHerb = true;
                    }
                    else {
                        isHerb = false;
                        break;
                    }
                    l++;
                }
                if (!isHerb){
                    break;
                }
                j++;
            }
            if (isHerb) {
                Herbs.add(FW.get(i).get(0));
            }
            isHerb = false;
            i++;
        }

        return Herbs;
    }

    //In: FW, Producer, Out: Carns
     /*
     declare variables and Lists
     *compare each predators' prey to the list of producers to determine which are carnivores
     */
    public static ArrayList<String> carnivorelist(ArrayList<ArrayList<String>> FW, ArrayList<String> PredList,
                                               ArrayList<String> PreyList){
        int i = 0, j = 1, l = 0;
        boolean isCarn = false;
        ArrayList<String> Carns = new ArrayList<>();
        ArrayList<String> Producer2 = producerlist( PredList, PreyList);

        for (; i < FW.size(); ) {
            for (j = 1; j < FW.get(i).size(); ) {
                for (l = 0; l < Producer2.size(); ) {
                    if (!FW.get(i).get(j).equals(Producer2.get(l)))
                        isCarn = true;
                    else{
                        isCarn = false;
                        break;
                    }
                    l++;
                }
                if (!isCarn)
                    break;
                j++;
            }
            if (isCarn)
                Carns.add(FW.get(i).get(0));
            i++;
        }

        return Carns;
    }

    //In: FW, Producer, Out: Omnivores
    /*
     * declare variables and Lists
     * check if each predator is a herbivore
     * check if each predator is a carnivore
     * if neither are true add predator to Omnivores
     */
    public static HashSet<String> omnivorelist(ArrayList<ArrayList<String>> FW,
                                            ArrayList<String> PredList, ArrayList<String> PreyList){
        int i = 0, j = 0;
        boolean isOmni = false;
        HashSet<String> Omnivores = new HashSet<>();
        ArrayList<String> Herbs2 = herbivorelist(FW, PredList, PreyList);
        ArrayList<String> Carns2 = carnivorelist(FW, PredList, PreyList);

        for (; i < FW.size(); ){
            for (j = 0; j < Herbs2.size(); ){
                if (FW.get(i).get(0).equals(Herbs2.get(j))){
                    isOmni = false;
                    break;
                }
                else
                    isOmni = true;
                j++;
            }
            if (isOmni){
                for (j = 0; j < Carns2.size(); ){
                    if (FW.get(i).get(0).equals(Carns2.get(j))){
                        isOmni = false;
                        break;
                    }
                    else
                        isOmni = true;
                    j++;
                }
            }
            if (isOmni)
                Omnivores.add(FW.get(i).get(0));
            i++;
        }

        return Omnivores;
    }

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("HCOTestCSV.txt");
        ArrayList<ArrayList<String>> FW = new ArrayList<>();
        int i = 0, j = 0;

        try {
            //initializing variables and scanners
            Scanner lineReader = new Scanner(file);
            String line = "0";

            while (lineReader.hasNextLine()) {
                line = lineReader.nextLine();
                Scanner animalReader = new Scanner(line);
                animalReader.useDelimiter(",");

                //Unpacking file and turning into an ArrayList(FW)
                FW.add(i, new ArrayList<>());
                while (animalReader.hasNext()) {
                    FW.get(i).add(animalReader.next());
                }
                i++;
            }
        }
        catch (FileNotFoundException e){
            System.out.println("FileNotFoundException");
        }

        //predator/prey lists
        ArrayList<String> PredList = new ArrayList<>();
        ArrayList<String> PreyList = new ArrayList<>();
        //adding each animal to their correct lists
        if (FW.isEmpty()){
            System.out.println("Food Web is empty");
        }
        else {
            for (i = 0; i < FW.size(); ) {
                PredList.add(FW.get(i).get(0));
                i++;
            }
            for (i = 0; i < FW.size(); ) {
                for (j = 1; j < FW.get(i).size(); ) {
                    PreyList.add(FW.get(i).get(j));
                    j++;
                }
                i++;
            }

            displayMenu();
            Scanner selection = new Scanner(System.in);
            int sel = selection.nextInt();

            switch (sel) {
                case 1:
                    System.out.println("The herbivores are: " + herbivorelist(FW, PredList, PreyList));
                    System.out.println("The carnivores are: " + carnivorelist(FW, PredList, PreyList));
                    System.out.println("The omnivores are: " + omnivorelist(FW, PredList, PreyList));
                    break;
                case 2:
                    //print Producer without duplicates
                    ArrayList<String> Producer2 = producerlist(PredList, PreyList);
                    HashSet<String> SOPproducerlist = new HashSet<String>();
                    for (i = 0; i < Producer2.size(); ) {
                        SOPproducerlist.add(Producer2.get(i));
                        i++;
                    }
                    System.out.println(SOPproducerlist);
                    break;
                case 3://
                    eats(FW);
                    break;
                case 4:
                    System.out.println("The Apex Predators are: " + apexPredators(PredList, PreyList));break;
                case 5:
                    System.out.println("The most flexible consumer is: " + flexibleConsumer(FW));
                    break;
                case 6:
                    System.out.println("The tastiest treat is: " + tastiestTreat(PreyList));
                    break;
                case 7:
                    System.out.println("Exit");
                    break;

            }
        }
    }
}