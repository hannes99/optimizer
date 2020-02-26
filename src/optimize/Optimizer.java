package optimize;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Optimizer {
    final private int MAX_SEARCH_RADIUS = 100;
    public boolean verbose = true;
    private Optimizable problem;
    private int parameters;

    public Optimizer(Optimizable problem) {
        this.problem = problem;
        this.parameters = problem.getParameterCount();
    }

    public Optimizable optimize(int batchSize, int batches, double stepSize) {
        Optimizable best = problem;
        final LinkedList<Integer> winnerHistory = new LinkedList<>();
        for (int batchNr = 0; batchNr < batches; batchNr++) {
            Optional<Integer> sumOfLastTen = winnerHistory.stream().limit(MAX_SEARCH_RADIUS).reduce(Integer::sum);
            if (sumOfLastTen.isPresent() && sumOfLastTen.get() == 0) break;
            if (verbose && !winnerHistory.isEmpty())
                System.err.println(batchNr + " => " + best + " based on " + winnerHistory.getFirst() + " winners");
            long countOfBatchesWithNotEnoughWinner = winnerHistory.stream().limit(MAX_SEARCH_RADIUS).filter(x -> x < 10).count();
            List<Optimizable> batch = new ArrayList<>();
            IntStream.range(0, batchSize).forEach(x -> batch.add(best.copy()));
            IntStream.range(0, parameters)
                    .forEach(id -> batch
                            .forEach(batchEntry -> batchEntry
                                    .setParameter(id, batchEntry.getParameter(id)
                                            + ThreadLocalRandom.current().nextGaussian()
                                            * (countOfBatchesWithNotEnoughWinner + 1))));
            batch.parallelStream().forEach(Optimizable::run);
            List<Optimizable> winners = batch.stream().filter(o -> o.isBetter(best)).sorted().collect(Collectors.toList());
            winnerHistory.push(winners.size());
            IntStream.range(0, parameters).parallel().forEach(id -> {
                OptionalDouble winnerValuesAverage = winners.stream()
                        .mapToDouble(winner -> winner.getParameter(id))
                        .average();
                int direction = (winnerValuesAverage.isPresent() ? winnerValuesAverage.getAsDouble() : best.getParameter(id)) - best.getParameter(id) < 0 ? -1 : 1;
                best.setParameter(id, best.getParameter(id) + (direction * (stepSize + 1.0 * winnerHistory.getFirst() / batchSize)));
            });
            best.run();
        }
        return best;
    }
}
