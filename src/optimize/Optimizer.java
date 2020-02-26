package optimize;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Optimizer {
    public boolean verbose = true;
    private Optimizable problem;

    public Optimizer(Optimizable problem) {
        this.problem = problem;
    }

    public Optimizable optimize(int batchSize, int batches, double stepSize) {
        Optimizable best = problem;
        int parameters = best.getParameterCount();
        LinkedList<Integer> winnerHistory = new LinkedList<>();
        List<Optimizable> winners = new ArrayList<>();
        for (int batchNr = 0; batchNr < batches; batchNr++) {
            Optional<Integer> sumOfLastTen = winnerHistory.stream().limit(10).reduce(Integer::sum);
            if (sumOfLastTen.isPresent() && sumOfLastTen.get() == 0) {
                if (verbose)
                    System.err.println(batchNr + " => " + best + " based on " + winners.size() + " winners --- STOPPED DUE TO NO PROGRESS");
                break;
            }
            best.run();
            if (verbose)
                System.err.println(batchNr + " => " + best + " based on " + winners.size() + " winners");
            long countOfBatchesWithNotEnoughWinner = winnerHistory.stream().limit(10).filter(x -> x < 10).count();
            List<Optimizable> batch = IntStream.range(0, batchSize)
                    .mapToObj(x -> best.copy())
                    .collect(Collectors.toCollection(ArrayList::new));
            IntStream.range(0, parameters)
                    .forEach(id -> batch
                            .forEach(batchEntry -> {
                                batchEntry.setParameter(id,
                                        batchEntry.getParameter(id) + ThreadLocalRandom.current().nextGaussian() * (countOfBatchesWithNotEnoughWinner + 1));
                            }));
            batch.parallelStream().forEach(Optimizable::run);
            winners = batch.stream()
                    .filter(o -> o.isBetter(best))
                    .sorted()
                    .collect(Collectors.toList());
            winnerHistory.push(winners.size());
            List<Optimizable> finalWinners = winners;
            IntStream.range(0, parameters).forEach(id -> {
                OptionalDouble winnerValuesAverage = finalWinners.stream().mapToDouble(winner -> winner.getParameter(id)).average();
                int direction = (winnerValuesAverage.isPresent() ? winnerValuesAverage.getAsDouble() : best.getParameter(id)) - best.getParameter(id) < 0 ? -1 : 1;
                best.setParameter(id, best.getParameter(id) + (direction * (stepSize + 1.0 * winnerHistory.getFirst() / batchSize)));
            });
        }
        best.run();
        return best;
    }
}
