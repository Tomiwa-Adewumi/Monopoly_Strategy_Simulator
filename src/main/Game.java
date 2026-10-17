package main;

import main.Space.GoToJail;
import main.Space.Jail;
import main.Space.Property;
import main.Space.Space;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Simplified Monopoly variant: last solvent player, $1500, or 100 player turns. */
public class Game {
    private final Board board;
    private final List<Player> players;
    private Player winner;
    private int turnCounter;
    private static final int MAX_TURNS = 100;

    public Game(List<Player> players) {
        if (players.isEmpty()) throw new IllegalArgumentException("At least one player is required");
        this.players = new ArrayList<>(players);
        this.board = new Board(this.players);
    }

    public Player getWinner() { return winner; }
    public int getTurnCounter() { return turnCounter; }
    public void resetPlayerStates() { players.forEach(Player::reset); }

    public void startGame() {
        Jail jail = (Jail) board.getSpaceAt(9);
        int index = 0;
        while (turnCounter < MAX_TURNS) {
            long solvent = players.stream().filter(p -> p.getBalance() > 0).count();
            if (solvent <= 1) break;
            Player player = players.get(index);
            index = (index + 1) % players.size();
            if (player.getBalance() <= 0) continue;
            if (player.getBalance() >= 1500) { winner = player; return; }
            turnCounter++; // Jail and go-to-jail turns consume the same budget.
            if (jail.isPlayerInJail(player)) {
                jail.action(player);
            } else {
                Space space = board.getSpaceAt(board.movePlayer(player, player.takeTurn()));
                if (space instanceof GoToJail) {
                    player.setPosition(9);
                    jail.addPlayerToJail(player);
                } else {
                    boolean handled = space.action(player);
                    if (space instanceof Property property && (!handled || property.getOwner() == player)) {
                        player.considerBuyingHouse(property);
                    }
                }
            }
            for (Player candidate : players) {
                if (candidate.getBalance() >= 1500) { winner = candidate; return; }
            }
        }
        determineWinner();
    }

    /** Does not reorder the caller's list; ID is the deterministic final tie-break. */
    public void determineWinner() {
        winner = players.stream().max(Comparator.comparingInt(Player::getBalance)
                .thenComparingInt(Player::getNumberOwned)
                .thenComparing(Comparator.comparingInt(Player::getId).reversed())).orElseThrow();
    }
}
