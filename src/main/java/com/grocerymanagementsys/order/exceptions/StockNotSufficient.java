package com.grocerymanagementsys.order.exceptions;

public class StockNotSufficient extends RuntimeException {
    public StockNotSufficient(Long itemId) {
        super("Not enough stock for item " + itemId);
    }

    public StockNotSufficient(Long itemId, String itemName) {
        super("Not enough stock for \"" + itemName + "\" (item " + itemId + ")");
    }
}
