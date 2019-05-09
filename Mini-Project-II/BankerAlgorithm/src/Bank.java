public interface Bank {
    /**
     * Add a customer to the bank.
     *
     * @param threadNum The number of the customer being added.
     * @param maxDemand The maximum demand for this customer.
     */
    public void addCustomer(int threadNum, int[] maxDemand);

    /**
     * Outputs the available, allocation, max, and need matrices.
     */
    public void getState();

    /**
     * Make a request for resources.
     *
     * @param threadNum The number of the customer being added.
     * @param request   The request for this customer.
     * @return false The request is not granted.
     */
    public boolean requestResources(int threadNum, int[] request);

    /**
     * Release resources.
     *
     * @param threadNum The number of the customer being added.
     * @param release   The resources to be released.
     */
    public void releaseResources(int threadNum, int[] release);
}