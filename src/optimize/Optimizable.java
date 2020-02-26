package optimize;

/**
 * "Better" solutions have to be before "Worst" ones, implement compareTo accordingly
 */
public interface Optimizable extends Runnable, Comparable<Optimizable> {
    double getScore();
    void setParameter(int id, double value);
    int getParameterCount();
    double getParameter(int id);

    /**
     * Make a copy of this and return it,
     * @return the copy
     */
    Optimizable copy();

    /**
     * Is this parameter assignment better than an other
     * @param other the other assignment
     * @return true if this is better, otherwise false
     */
    boolean isBetter(Optimizable other);

    /**
     * Return a string representing the current status(parameters and score)
     * @return the status-string
     */
    String status();
}