package main.Strategy;



import main.Space.Property;

import java.util.HashSet;
import java.util.Set;

/**
 * DisruptiveStrategy
 * This strategy represents a player who aims to block other players by owning at least one property of each color.
 */
public class DisruptiveStrategy implements Strategy {

    /**
     * A set that tracks the colors of properties the player already owns.
     * This helps the player avoid buying properties of colors they already own.
     */
    private final Set<String> ownedColors = new HashSet<>();

    /**
     * Determines whether the player should buy the land they land on.
     * The player buys the property if it is unowned, affordable, and if it is a color they do not yet own.
     *
     * @param property the property the player landed on
     * @param balance  the player's current balance
     * @return true if the property should be bought, false otherwise
     */
    @Override
    public boolean shouldBuyLand(Property property, int balance) {
        // Buy property if it's unowned, affordable, and of a color not yet owned
        return !property.IsOwned() && balance >= property.getPrice() && !ownedColors.contains(property.getColor());
    }

    /**
     * Determines whether the player should upgrade a property they own.
     * The player following this strategy does not upgrade properties.
     *
     * @param property the property to be upgraded
     * @param balance  the player's current balance
     * @return false, as the strategy does not allow upgrading properties
     */
    @Override
    public boolean shouldBuyProperty(Property property, int balance) {
        // This strategy ignores upgrading properties
        return false;
    }

    /**
     * Adds a color to the set of owned property colors.
     * This helps track which colors the player has already acquired.
     *
     * @param color the color of the property to add to the owned set
     */
    @Override
    public void reset() { ownedColors.clear(); }

    public void addOwnedColor(String color) {
        // Adds a new color to the set of owned property colors
        ownedColors.add(color);
    }

}
