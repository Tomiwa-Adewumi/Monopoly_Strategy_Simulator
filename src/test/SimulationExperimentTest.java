package main;
import main.Strategy.CollectorStrategy;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class SimulationExperimentTest {
    @Test void aggregatesByIdEvenWhenFinishingOrderChanges() {
        Player a = new Player("A", 20, new CollectorStrategy("green"));
        Player b = new Player("B", 70, new CollectorStrategy("blue"));
        a.setBalance(100); b.setBalance(900);
        SimulationExperiment experiment = new SimulationExperiment(List.of(a, b), 1, 42);
        experiment.recordPlayers(List.of(b, a));
        assertEquals(100, experiment.results().get(20).averageBalance());
        assertEquals(900, experiment.results().get(70).averageBalance());
    }
    @Test void seedReproducesResultsAndEachTrialHasOneWinner() {
        var players = List.of(new Player("A", 20, new CollectorStrategy("green")),
                new Player("B", 70, new CollectorStrategy("blue")));
        var a = new SimulationExperiment(players, 10, 42);
        var b = new SimulationExperiment(players, 10, 42);
        a.runSimulation(); b.runSimulation();
        assertEquals(a.results(), b.results());
        assertEquals(10, a.results().values().stream().mapToInt(SimulationExperiment.Result::wins).sum());
    }
}
