import optimize.Optimizable;
import optimize.Optimizer;

public class Main {
    public static void main(String[] args) {
        TestFunction x = new TestFunction(new double[]{100});
        x.run();
        Optimizer op = new Optimizer(x);
        op.verbose = true;
        System.out.println(x.getScore());
        long start = System.currentTimeMillis();
        Optimizable optimized = op.optimize(1000, 500000,0.0001);
        System.out.println(optimized);
        System.out.println(System.currentTimeMillis()-start);
    }
}
