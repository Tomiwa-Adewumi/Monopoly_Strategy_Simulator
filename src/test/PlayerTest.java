import main.Player;
import main.Space.Property;
import main.Space.Space;
import main.Strategy.CollectorStrategy;
import main.Strategy.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Player class. These tests verify the correct functionality
 * of methods related to player attributes, actions, and interactions with properties
 * in the game.
 */
public class PlayerTest {

    private Player player; // Player instance to test
    private Strategy strategy; // Strategy for the player

    /**
     * Sets up the test environment by initializing a player and assigning a strategy.
     * This method is executed before each test to ensure tests run with a clean state.
     */
    @BeforeEach
    public void setUp() {
        strategy = new CollectorStrategy("green");
        player = new Player("Test Player", 1, strategy);
    }

    /**
     * Tests the initialization of a Player object.
     * Verifies that the player's name, ID, balance, position, jail status, and number of properties owned are set correctly.
     */
    @Test
    public void testPlayerInitialization() {
        assertEquals("Test Player", player.getName());
        assertEquals(1, player.getId());
        assertEquals(750, player.getBalance()); // Initial balance is 750
        assertEquals(0, player.getPosition()); // Initial position is 0
        assertFalse(player.isInJail()); // Player should not be in jail initially
        assertEquals(0, player.getNumberOwned()); // Player should own no properties initially
    }

    /**
     * Tests the setPosition method of the Player class.
     * Verifies that the player's position can be updated correctly.
     */
    @Test
    public void testSetPosition() {
        player.setPosition(5);
        assertEquals(5, player.getPosition()); // Position should be updated to 5
    }

    /**
     * Tests the setBalance method of the Player class.
     * Verifies that the player's balance can be updated correctly.
     */
    @Test
    public void testSetBalance() {
        player.setBalance(1000);
        assertEquals(1000, player.getBalance()); // Balance should be updated to 1000
    }

    /**
     * Tests the addSpace method, which adds a space (property) to the player's portfolio.
     * Verifies that the player's number of owned properties increases correctly.
     */
    @Test
    public void testAddSpace() {
        Space space = new Property("Boardwalk", 400, 500, "blue");
        player.addSpace(space);
        assertEquals(1, player.getNumberOwned()); // Player should now own 1 property
    }

    /**
     * Tests the toggleJailStatus method of the Player class.
     * Verifies that the player's jail status can be toggled correctly.
     */
    @Test
    public void testToggleJailStatus() {
        player.toggleJailStatus();
        assertTrue(player.isInJail()); // Player should be in jail after toggle
        player.toggleJailStatus();
        assertFalse(player.isInJail()); // Player should not be in jail after second toggle
    }

    /**
     * Tests the acquireProperty method.
     * Verifies that a player can acquire a property if they have enough balance,
     * and the property ownership is updated correctly.
     */
    @Test
    public void testAcquireProperty() {
        Property property = new Property("Boardwalk", 400, 500, "blue");
        assertTrue(player.acquireProperty(property));
        assertEquals(350, player.getBalance()); // Balance should decrease after buying property
        assertTrue(property.IsOwned()); // Property should be owned now
        assertEquals(player, property.getOwner()); // Player should be the owner of the property
    }

    /**
     * Tests the acquireProperty method when the player does not have enough balance.
     * Verifies that the property cannot be acquired if the player has insufficient funds.
     */
    @Test
    public void testAcquirePropertyInsufficientBalance() {
        Property property = new Property("Park Place", 800, 500, "");
        assertFalse(player.acquireProperty(property)); // Acquisition should fail due to insufficient balance
        assertEquals(750, player.getBalance()); // Balance should remain the same
        assertFalse(property.IsOwned()); // Property should not be owned
    }

    /**
     * Tests the takeTurn method, which simulates a player's turn by rolling the dice.
     * Verifies that the roll result is within the valid range (2-12).
     */
    @Test
    public void testTakeTurn() {
        int roll = player.takeTurn();
        assertTrue(roll >= 2 && roll <= 12); // Roll result should be between 2 and 12 (inclusive)
    }

    /**
     * Tests the makePayment method, which deducts a payment from the player's balance.
     * Verifies that the balance is reduced correctly after making a payment.
     */
    @Test
    public void testMakePayment() {
        assertTrue(player.makePayment(200));
        assertEquals(550, player.getBalance()); // Balance should decrease by 200
    }

    /**
     * Tests the makePayment method when the player has insufficient funds.
     * Verifies that the payment is not processed if the player does not have enough balance.
     */
    @Test
    public void testMakePaymentInsufficientFunds() {
        assertFalse(player.makePayment(800)); // Payment should fail due to insufficient funds
        assertEquals(0, player.getBalance()); // Balance should remain the same
    }

    /**
     * Tests the acceptMoney method, which increases the player's balance.
     * Verifies that the balance is updated correctly after receiving money.
     */
    @Test
    public void testAcceptMoney() {
        player.acceptMoney(300);
        assertEquals(1050, player.getBalance()); // Balance should increase by 300
    }

    /**
     * Tests the considerBuyingHouse method, which checks if the player can buy a house on a property.
     * Verifies that the player can buy a house if they have enough funds and the property is available for upgrading.
     */
    @Test
    public void testConsiderBuyingHouse() {
        Property property = new Property("Boardwalk", 400, 200, "green");
        assertTrue(player.considerBuyingHouse(property)); // Should be able to buy a house
        assertEquals(350, player.getBalance()); // Balance should decrease by the cost of the house
        assertTrue(property.IsOwned()); // Property should still be owned
    }

    /**
     * Tests the tryUpgrade method, which upgrades a player's property if they meet the requirements.
     * Verifies that the player can upgrade a property if they own all the required properties.
     */
    @Test
    public void testTryUpgrade() {
        Property property1 = new Property("Boardwalk", 100, 200, "Blue");
        Property property2 = new Property("Park Place", 100, 200, "Blue");
        Property property3 = new Property("Baltic Avenue", 100, 200, "Blue");
        player.acquireProperty(property1);
        player.acquireProperty(property2);
        player.acquireProperty(property3);
        assertTrue(player.tryUpgrade(property1)); // Should be able to upgrade
        assertEquals(420, player.getBalance()); // Three first-level upgrades cost $10 each
        assertEquals(1, property1.getUpgradeLevel());
        assertEquals(230, property1.getRent()); // Rent should increase after upgrading the property
    }
}
