import optimize.Optimizable;

import java.util.Arrays;

public class TestFunction implements Optimizable {
    private double[] parameters;
    private double value;

    TestFunction(double[] parametes) {
        this.parameters = Arrays.copyOf(parametes, parametes.length);
    }

    @Override
    public double getScore() {
        return value;
    }

    @Override
    public void setParameter(int id, double value) {
        parameters[id] = value;
    }

    @Override
    public int getParameterCount() {
        return parameters.length;
    }

    @Override
    public double getParameter(int id) {
        return parameters[id];
    }

    @Override
    public Optimizable copy() {
        return new TestFunction(parameters);
    }

    @Override
    public boolean isBetter(Optimizable other) {
        return other.getScore() > value;
    }

    @Override
    public String status() {
        StringBuilder status = new StringBuilder(value + "\t(");
        for(int i = 0;i<parameters.length;i++) {
            status.append(String.format( "%.3f", parameters[i]));
            if(i!=parameters.length-1)
                status.append(',').append(' ');
        }
        status.append(')');
        return status.toString();
    }

    @Override
    public void run() {
        // -5*x*y*e^(-x^2-y^2)
        this.value = Math.pow(parameters[0],2)+Math.pow(parameters[1],2);
    }

    @Override
    public String toString() {
        return status();
    }

    @Override
    public int compareTo(Optimizable optimizable) {
        return Double.compare(value, optimizable.getScore());
    }
}
