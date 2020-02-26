import optimize.Optimizable;
import optimize.Optimizer;

public class Main {
    public static void main(String[] args) {
        TestFunction x = new TestFunction(new double[]{10,7});
        x.run();
        Optimizer op = new Optimizer(x);
        System.out.println(x.getScore());
        Optimizable optimized = op.optimize(100000, 500000,0.0001);
    }
}
