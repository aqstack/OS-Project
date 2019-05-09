import java.io.*;
import java.util.*;

public class Factory
{
    public static void main(String[] args) {
        int numOfResources = args.length;
        int[] resources = new int[numOfResources];
        for (int i = 0; i < numOfResources; i++) {
            resources[i] = Integer.parseInt(args[i].trim());
            System.out.println(resources[i]);
        }

        Bank theBank = new BankImpl(resources);
        int[] maxDemand = new int[numOfResources];

        // the customers
        Thread[] workers = new Thread[Customer.COUNT];

        // read initial values for maximum array
        String line;
        try {
            BufferedReader inFile = new BufferedReader(new FileReader("src/infile.txt"));

            int threadNum = 0;
            int resourceNum = 0;

            for (int i = 0; i < Customer.COUNT; i++) {
                line = inFile.readLine();
                StringTokenizer tokens = new StringTokenizer(line,",");

                while (tokens.hasMoreTokens()) {
                    int amt = Integer.parseInt(tokens.nextToken().trim());
                    maxDemand[resourceNum++] = amt;
                }
                workers[threadNum] = new Thread(new Customer(threadNum, maxDemand, theBank));
                theBank.addCustomer(threadNum,maxDemand);
                ++threadNum;
                resourceNum = 0;
            }
        }
        catch (FileNotFoundException fnfe) {
            throw new Error("Unable to find file \"infile.txt\"");
        }
        catch (IOException ioe) {
            throw new Error("Error processing \"infile.txt\"");
        }

        // start all the customers
        System.out.println("FACTORY: created threads");

        for (int i = 0; i < Customer.COUNT; i++)
            workers[i].start();

        System.out.println("FACTORY: started threads");

    }
}