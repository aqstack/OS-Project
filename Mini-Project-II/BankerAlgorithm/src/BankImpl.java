import java.util.Timer;
import java.util.TimerTask;

public class BankImpl implements Bank {
    private Customer[] customers = new Customer[Customer.COUNT];
    private int[] resources;

    private int[][] allocated;
    private int[][] maxDemand;

    public BankImpl(int[] resources){
        this.resources = resources;
        this.maxDemand = new int[Customer.COUNT][resources.length];
        this.allocated = new int[Customer.COUNT][resources.length];
    }

    /**
     * Add a customer to the bank.
     * @param threadNum The number of the customer being added.
     * @param maxDemand The maximum demand for this customer.
     */
    public void addCustomer(int threadNum, int[] maxDemand){
        this.customers[threadNum] = new Customer(threadNum, maxDemand, this);
        this.maxDemand[threadNum] = maxDemand;
    }

    /**
     * Outputs the available, allocation, max, and need matrices.
     */
    public void getState(){
        StringBuilder sb = new StringBuilder();

        // Append available
        sb.append("\n\nAvailable:\n----------\n");
        for(int i = 0; i < resources.length; i++){
            sb.append("Resource " + i + ": " + resources[i] + "\n");
        }

        // Append allocated
        sb.append("\n\nAllocated:\n----------\n");
        for(int i = 0; i < allocated[0].length; i++){
            // Sum of all allocated resources
            int sum = 0;

            for(int j = 0; j < allocated.length; j++){
                sum += allocated[j][i];
            }

            sb.append("Resource " + i + ": " + sum + "\n");
        }

        // Append max
        sb.append("\n\nMax demand:\n----------");
        for(int i = 0; i < maxDemand.length; i++){
            sb.append("\nCustomer " + i + ": ");

            // Append individual resources to line.
            for(int j = 0; j < maxDemand[i].length; j++){
                sb.append(" Resource " + j + ": " + maxDemand[i][j]);
            }
        }

        // Append need
        sb.append("\n\nNeed:\n----------");
        for(int i = 0; i < maxDemand.length; i++){
            sb.append("\nCustomer " + i + ": ");

            for(int j = 0; j < maxDemand[i].length; j++){
                sb.append(" Resource " + j + ": " + (maxDemand[i][j] - allocated[i][j]));
            }
        }

        // Print everything in one go.
        System.out.println(sb);
    }

    /**
     * Make a request for resources.
     * @param threadNum The number of the customer being added.
     * @param request The request for this customer.
     * @return  true The request is granted.
     * @return  false The request is not granted.
     */
    public synchronized boolean requestResources(int threadNum, int[] request){
        // Check if we allow this request to be processed.
        for(int i = 0; i < request.length; i++){
            // Decline request if it does not leave the system in safe state.
            if(request[i] > resources[i]){
                return false;
            }

            // Decline request if request is more than the max demand for that customer
            if(request[i] > maxDemand[threadNum][i]){
                return false;
            }
        }

        // Check if it leaves the system in a safe state
        if(!isSafeState(threadNum, request)){
            return false;
        }

        // Process request and return true
        for(int i = 0; i < request.length; i++){
            resources[i] -= request[i];
            allocated[threadNum][i] += request[i];
        }

        return true;
    }

    /**
     * Release resources.
     * @param threadNum The number of the customer being added.
     * @param release The resources to be released.
     */
    public synchronized void releaseResources(int threadNum, int[] release){
        // Release resources
        for(int i = 0; i < release.length; i++){
            resources[i] += release[i];
            allocated[threadNum][i] -= release[i];
        }
        getState();
    }

    private boolean isSafeState( int customerNum, int[] request ) {
        // Determines if a state is safe by trying to find a hypothetical set of requests by the processes that would allow each to acquire its
        // maximum resources and then terminate (returning its resources to the system).
        // Any state where no such set exists is an unsafe state.
        int[] clonedResources = resources.clone();
        int[][] clonedAllocated = allocated.clone();

        // First check if any part of the request requires more resources than are available (unsafe state)
        for(int i = 0; i < clonedResources.length; i++){
            if(request[i] > clonedResources[i]){
                return false;
            }
        }

        // If we reach this point, the first request was valid so we execute it on the simulated resources
        for(int i = 0; i < clonedResources.length; i++){
            clonedResources[i] -= request[i];
            clonedAllocated[customerNum][i] += request[i];
        }

        // Create new boolean array and set all to false
        boolean[] canFinish = new boolean[customers.length];

        for(int i = 0; i < canFinish.length; i++){
            canFinish[i] = false;
        }

        // Now check if there is an order wherein other customers can still finish after us
        for(int i = 0; i < customers.length; i++){
            // Find a customer that can finish a request. Loop through all resources per customer
            for(int j = 0; j < customers.length; j++){
                if(!canFinish[j]){
                    boolean temp = true;
                    for(int k = 0; k < clonedResources.length; k++){
                        // If the need (maxdemand - allocated = need) is noot bigger than the amount of available resources, thread can finish
                        if(!((maxDemand[j][k] - clonedAllocated[j][k]) > clonedResources[k])){
                            canFinish[j] = true;
                            for(int l = 0; l < clonedResources.length; l++){
                                clonedResources[l] += clonedAllocated[j][l];
                            }
                        }
                    }
                }
            }
        }

        // restore the value of need and allocation for this thread
        for (int i = 0; i < resources.length; i++) {
            clonedAllocated[customerNum][i] -= request[i];
        }

        // After all the previous calculations. Loop through the array and see if every customer can complete the transaction for their maximum demand
        for(int i = 0; i < canFinish.length; i++){
            if(canFinish[i] == false){
                return false;
            }
        }

        return true;
    }
}