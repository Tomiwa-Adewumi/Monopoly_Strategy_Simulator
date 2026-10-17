package main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Seeded trials, with results keyed by stable player ID rather than finishing order. */
public class SimulationExperiment {
    public record Result(int wins, double averageBalance, double averageProperties) {}
    private final List<Player> players;
    private final int trials;
    private final long seed;
    private final Map<Integer, long[]> totals = new LinkedHashMap<>();

    public SimulationExperiment(ArrayList<Player> players, int trials) { this(players, trials, 42L); }
    public SimulationExperiment(List<Player> players, int trials, long seed) {
        if (trials <= 0 || players.isEmpty()) throw new IllegalArgumentException("Players and positive trial count required");
        this.players = List.copyOf(players);
        this.trials = trials;
        this.seed = seed;
        for (Player p : players) {
            if (totals.put(p.getId(), new long[3]) != null) throw new IllegalArgumentException("Duplicate player ID");
        }
    }
    public void runSimulation() {
        totals.values().forEach(t -> java.util.Arrays.fill(t, 0));
        Random seeds = new Random(seed);
        for (int i = 0; i < trials; i++) {
            List<Player> trial = new ArrayList<>();
            for (Player p : players) {
                Player copy = new Player(p.getName(), p.getId(), p.getStrategy(), new Random(seeds.nextLong()));
                copy.reset();
                trial.add(copy);
            }
            // Rotate starting position to avoid giving one strategy every first move.
            java.util.Collections.rotate(trial, i % trial.size());
            Game game = new Game(trial);
            game.startGame();
            totals.get(game.getWinner().getId())[0]++;
            recordPlayers(trial);
        }
        displayResults();
    }
    void recordPlayers(List<Player> trial) {
        for (Player p : trial) {
            long[] total = totals.get(p.getId());
            total[1] += Math.max(0, p.getBalance());
            total[2] += p.getBalance() > 0 ? p.getNumberOwned() : 0;
        }
    }
    public Map<Integer, Result> results() {
        Map<Integer, Result> result = new LinkedHashMap<>();
        totals.forEach((id, t) -> result.put(id, new Result((int) t[0], (double)t[1] / trials, (double)t[2] / trials)));
        return result;
    }
    public void displayResults() {
        System.out.printf("Seed: %d; trials: %d%n", seed, trials);
        results().forEach((id, r) -> System.out.printf(java.util.Locale.ROOT,
                "Player %d: wins=%d, average balance=%.2f, average properties=%.2f%n",
                id, r.wins(), r.averageBalance(), r.averageProperties()));
    }
}
