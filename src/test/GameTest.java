import main.Game;
import main.Player;
import main.Strategy.CollectorStrategy;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    Player player(int id, int balance) {
        Player p = new Player("Player " + id, id, new CollectorStrategy("green")) {
            @Override public int takeTurn() { return 18; }
        };
        p.setBalance(balance);
        return p;
    }
    @Test void thresholdAlwaysSetsWinner() {
        Player p = player(1, 1500);
        Game game = new Game(List.of(p, player(2, 750)));
        game.startGame();
        assertSame(p, game.getWinner());
    }
    @Test void firstBankruptcyDoesNotEndThreePlayerGame() {
        Game game = new Game(List.of(player(1, -1), player(2, 750), player(3, 750)));
        game.startGame();
        assertEquals(100, game.getTurnCounter());
        assertNotEquals(1, game.getWinner().getId());
    }
    @Test void winnerSelectionDoesNotReorderPlayers() {
        List<Player> players = List.of(player(9, 500), player(2, 900));
        Game game = new Game(players);
        game.determineWinner();
        assertSame(players.get(1), game.getWinner());
        assertEquals(9, players.get(0).getId());
    }
    @Test void lastSolventPlayerWins() {
        Game game = new Game(List.of(player(1, 0), player(2, 100)));
        game.startGame();
        assertEquals(2, game.getWinner().getId());
    }
    @Test void jailTurnsConsumeBudget() {
        Player p = new Player("Jailed", 1, new CollectorStrategy("green")) {
            @Override public int takeTurn() { return getPosition() == 0 ? 5 : 14; }
        };
        Game game = new Game(List.of(p, player(2, 750)));
        game.startGame();
        assertEquals(100, game.getTurnCounter());
        assertNotNull(game.getWinner());
    }
}
