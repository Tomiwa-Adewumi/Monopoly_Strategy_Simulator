package main;

import main.Strategy.AggresivveBuyerStrategy;
import main.Strategy.CautiousSaverStrategy;
import main.Strategy.CollectorStrategy;
import main.Strategy.DisruptiveStrategy;

import java.util.ArrayList;


/**
 * Main class to initiate and start a Monopoly game simulation.
 * It creates players with different strategies, initializes the game, and runs the simulation.
 */
public class Main {
    /**
     * Main method that serves as the entry point to start the Monopoly game simulation.
     * It sets up the players, assigns strategies to each player, and starts the game.
     *
     * @param args command-line arguments (not used in this simulation)
     */
    public static void main(String[] args) {
        System.out.println("Welcome to Monopoly!");

        // Create a list of players with different strategies
        ArrayList<Player> players = new ArrayList<>();

        // Add players with various strategies to the list
        players.add(new Player("Player 1", 1, new AggresivveBuyerStrategy())); // Aggressive buyer strategy
        players.add(new Player("Player 2", 2, new CautiousSaverStrategy()));   // Cautious saver strategy
        players.add(new Player("Player 3", 3, new CollectorStrategy("green"))); // Collector strategy focusing on green properties
        players.add(new Player("Player 4", 4, new DisruptiveStrategy()));      // Disruptive strategy to block others

        // Initialize the game simulation with the players and a set number of turns (60 in this case)
        SimulationExperiment game = new SimulationExperiment(players, args.length > 0 ? Integer.parseInt(args[0]) : 1000,
                args.length > 1 ? Long.parseLong(args[1]) : 42L);

        // Start the simulation
        game.runSimulation();
    }
}
