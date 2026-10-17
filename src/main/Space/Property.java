package main.Space;


import main.Player;

/**
 * Represents a property space on the game board.
 * Properties can be bought, upgraded, and rented out to other players.
 */
public class Property extends Space {

    /**
     * The cost to purchase the property.
     */
    private int price;

    /**
     * The rent paid by players who land on this property.
     */
    private int rent;

    /**
     * The color group of the property (e.g., red, blue).
     */
    private String color;

    /**
     * Indicates whether the property is owned.
     */
    private boolean owned;

    /**
     * The current owner of the property.
     */
    private Player owner;

    /**
     * The level of upgrades (e.g., houses or hotels) on the property.
     */
    private int upgradeLevel;

    /**
     * Constructs a Property space with the specified name, price, rent, and color group.
     *
     * @param name  the name of the property
     * @param price the cost to purchase the property
     * @param rent  the base rent of the property
     * @param color the color group of the property
     */
    public Property(String name, int price, int rent, String color) {
        super(name); // Initialize space name
        this.price = price;
        this.rent = rent;
        this.color = color;
        this.owned = false;
        this.upgradeLevel = 0;
    }

    /**
     * Checks if the property is owned.
     *
     * @return true if the property is owned, false otherwise
     */
    public boolean IsOwned() {
        return owned;
    }

    /**
     * Gets the current rent of the property.
     *
     * @return the rent amount
     */
    public int getRent() {
        return rent;
    }

    /**
     * Gets the purchase price of the property.
     *
     * @return the property price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Gets the color group of the property.
     *
     * @return the color of the property
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets the current upgrade level of the property.
     *
     * @return the upgrade level
     */
    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    /**
     * Increases the rent by a specified amount.
     *
     * @param amount the amount to add to the rent
     */
    public void increaseRent(int amount) {
        rent += amount;
    }

    /**
     * Buys the property for a specified player, marking it as owned.
     *
     * @param player the player purchasing the property
     */
    public void buy(Player player) {
        this.owner = player;
        this.owned = true;
    }

    /**
     * Executes the action for a player landing on this property.
     * If the property is owned by another player, rent is paid.
     * If the property is not owned, it can be purchased.
     *
     * @param currentPlayer the player who landed on this property
     * @return true if the action was completed, false if the property can be purchased
     */
    @Override
    public boolean action(Player currentPlayer) {
        if (owned && owner.getBalance() <= 0) { owned = false; owner = null; }
        if (owned) {
            // Handle rent payment if property is owned by another player
            if (!owner.equals(currentPlayer)) {
                System.out.println(currentPlayer.getName() + " pays rent to " + owner.getName());
                int paid = Math.min(rent, Math.max(0, currentPlayer.getBalance()));
                currentPlayer.makePayment(rent); // Deduct rent from the current player
                owner.acceptMoney(paid); // Add rent to the owner's balance
            } else {
                // Log message if the current player owns the property
                System.out.println(currentPlayer.getName() + " already owns " + getName());
            }
            return true; // Action completed
        } else {
            return false; // Property can be purchased
        }
    }

    /**
     * Gets the current owner of the property.
     *
     * @return the owner of the property, or null if unowned
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Sets the ownership status of the property.
     *
     * @param b true if the property is owned, false otherwise
     */
    public void setOwned(boolean b) {
        owned = b;
    }

    /**
     * Sets the upgrade level of the property.
     *
     * @param i the new upgrade level
     */
    public void setUpgradeLevel(int i) {
        upgradeLevel = i;
    }
}
