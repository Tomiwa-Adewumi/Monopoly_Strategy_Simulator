package main;

import main.Space.Property;
import main.Space.Space;
import main.Strategy.Strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a player in the Monopoly game.
 * Each player has a name, balance, position on the board, list of owned properties, and a strategy.
 */
public class Player {
    private String name;
    private int id;
    private int balance;
    private int position;
    private boolean isInJail;
    private List<Space> spacesOwned;
    private Strategy strategy;
    private final Random random;

    /**
     * Constructor to initialize the player with a name, ID, and strategy.
     * The player starts with a balance of 750 and position at 0.
     *
     * @param name The name of the player
     * @param id The unique ID of the player
     * @param strategy The strategy the player will follow during the game
     */
    public Player(String name, int id, Strategy strategy) {
        this(name, id, strategy, new Random());
    }

    public Player(String name, int id, Strategy strategy, Random random) {
        this.random = random;
        this.name = name;
        this.id = id;
        this.strategy = strategy;
        this.balance = 750; // Initial balance for the player
        this.position = 0; // Starting position on the board
        this.isInJail = false; // Player starts outside jail
        this.spacesOwned = new ArrayList<>(); // No properties owned initially
    }

    /**
     * Returns the player's name.
     *
     * @return The name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the player's unique ID.
     *
     * @return The ID of the player
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the player's current balance.
     *
     * @return The balance of the player
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Returns the player's current position on the board.
     *
     * @return The position of the player
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns the number of properties owned by the player.
     *
     * @return The number of properties owned by the player
     */
    public int getNumberOwned() {
        return spacesOwned.size();
    }

    /**
     * Sets the player's position on the board.
     *
     * @param position The new position of the player
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Sets the player's balance to a new value.
     *
     * @param balance The new balance of the player
     */
    public void setBalance(int balance) {
        this.balance = balance;
    }

    /**
     * Adds a property to the player's list of owned properties.
     *
     * @param space The property to be added
     */
    public void addSpace(Space space) {
        spacesOwned.add(space);
    }

    /**
     * Checks if the player is currently in jail.
     *
     * @return true if the player is in jail, false otherwise
     */
    public boolean isInJail() {
        return isInJail;
    }

    /**
     * Toggles the player's jail status between in and out of jail.
     */
    public void toggleJailStatus() {
        this.isInJail = !this.isInJail;
    }

    /**
     * Resets the player's state to the initial conditions.
     * The player's position, balance, and owned properties are reset.
     */
    public void reset() {
        this.position = 0; // Reset position
        this.balance = 750; // Reset balance
        this.spacesOwned = new ArrayList<>(); // Reset properties owned
        this.isInJail = false; // Player starts outside jail
        strategy.reset();
    }

    /**
     * Attempts to acquire a property if the player has enough balance and the property is not already owned.
     *
     * @param property The property to be acquired
     * @return true if the property is successfully acquired, false otherwise
     */
    public boolean acquireProperty(Property property) {
        if (!property.IsOwned() && balance >= property.getPrice()) {
            spacesOwned.add(property);
            balance -= property.getPrice();
            property.buy(this);
            if (strategy instanceof main.Strategy.DisruptiveStrategy disruptive) {
                disruptive.addOwnedColor(property.getColor());
            }
            return true;
        } else {
            System.out.println("Cannot acquire property. Either insufficient balance or already owned.");
            return false;
        }
    }

    /**
     * Simulates rolling two dice and returns the total sum of the dice rolls.
     *
     * @return The total of the dice rolls
     */
    public int takeTurn() {
        int dice1 = random.nextInt(6) + 1; // Dice roll 1-6
        int dice2 = random.nextInt(6) + 1; // Dice roll 1-6
        return dice1 + dice2;
    }

    /**
     * Attempts to make a payment. If the player has insufficient balance, their balance is set to zero.
     *
     * @param value The amount to be paid
     * @return true if the payment is successful, false if the player has insufficient funds
     */
    public boolean makePayment(int value) {
        if (this.balance >= value) {
            balance -= value;
            return true;
        } else {
            balance = 0; // Player goes bankrupt
            System.out.println(name + " has insufficient funds!");
            return false;
        }
    }

    /**
     * Accepts money and increases the player's balance by the specified amount.
     *
     * @param amount The amount of money to be added to the balance
     */
    public void acceptMoney(int amount) {
        balance += amount;
    }

    /**
     * Considers whether to buy or upgrade a property based on the player's strategy.
     * If the strategy allows, it attempts to acquire the property or upgrade it.
     *
     * @param property The property being considered for purchase or upgrade
     * @return true if the property is purchased or upgraded, false otherwise
     */
    public boolean considerBuyingHouse(Property property) {
        if (strategy.shouldBuyLand(property, balance)) {
            return acquireProperty(property); // Attempt to buy the property
        }
        if (property.getOwner() == this && strategy.shouldBuyProperty(property, balance)) {
            return tryUpgrade(property); // Attempt to upgrade properties
        }
        return false;
    }

    /**
     * Attempts to upgrade all properties of the same color group if the player owns at least 3 properties of that color.
     * The player must also have enough balance to pay for the upgrades.
     *
     * @param property The reference property used to determine the color group
     * @return true if the upgrade is successful, false otherwise
     */
    public boolean tryUpgrade(Property property) {
        List<Property> sameColor = spacesOwned.stream()
                .filter(space -> space instanceof Property) // Ensure the space is a property
                .map(space -> (Property) space)
                .filter(p -> p.getColor().equals(property.getColor())) // Match color group
                .toList();

        if (sameColor.size() >= 3) { // At least 3 properties required for upgrade
            int upgradeCost = 10 * sameColor.stream().mapToInt(p -> p.getUpgradeLevel() + 1).sum();
            if (this.balance >= upgradeCost) {
                sameColor.forEach(p -> { p.increaseRent(30); p.setUpgradeLevel(p.getUpgradeLevel() + 1); }); // Increase rent for all properties in the group
                this.balance -= upgradeCost; // Deduct upgrade cost
                System.out.println("Successfully upgraded properties of color: " + property.getColor());
                return true;
            }
        }
        return false;
    }

    /**
     * Provides a string representation of the player's current state, including name, position, and balance.
     *
     * @return A string representation of the player's state
     */
    @Override
    public String toString() {
        return name + " [Position: " + position + ", Balance: " + balance + "]";
    }

    /**
     * Returns the player's strategy, which determines their behavior in the game.
     *
     * @return The player's strategy
     */
    public Strategy getStrategy() {
        return strategy;
    }
}
