package model;

import java.util.HashMap;
import java.util.Map;

import model.enumeration.PipeType;

/**
 * Represents a collection of pipes available in the game's stock.
 * This class manages the quantity of each type of pipe, including rotations.
 *
 * @author hamza-okutucu
 */
public class PipeStock {
    
    private Map<PipeType, Map<Integer, Integer>> stock;

    /**
     * Initializes an empty PipeStock.
     */
    public PipeStock() {
        stock = new HashMap<>();
    }
    
    /**
     * Initializes a new PipeStock by making a deep copy of an existing one.
     *
     * @param pipeStock The PipeStock to copy.
     */
    public PipeStock(PipeStock pipeStock) {
        this.stock = new HashMap<>();
        for (PipeType type : pipeStock.stock.keySet()) {
            Map<Integer, Integer> typeStock = pipeStock.stock.get(type);
            Map<Integer, Integer> copiedTypeStock = new HashMap<>(typeStock);
            this.stock.put(type, copiedTypeStock);
        }
    }

    /**
     * Adds a pipe of the specified type and rotations to the stock.
     *
     * @param type      The type of pipe to add.
     * @param rotations The number of clockwise rotations for the pipe.
     */
    public void addPipe(PipeType type, int rotations) {
        Map<Integer, Integer> typeStock = stock.get(type);
        if (typeStock == null) {
            typeStock = new HashMap<>();
            stock.put(type, typeStock);
        }

        typeStock.put(rotations, typeStock.getOrDefault(rotations, 0) + 1);
    }

    /**
     * Retrieves the quantity of a specific type and rotations of a pipe in the stock.
     *
     * @param type      The type of pipe to query.
     * @param rotations The number of clockwise rotations for the pipe.
     * @return The quantity of the specified pipe type.
     */
    public int getPipeQuantity(PipeType type, int rotations) {
        Map<Integer, Integer> typeStock = stock.get(type);
        if (typeStock != null) {
            return typeStock.getOrDefault(rotations, 0);
        }
        return 0;
    }

    /**
     * Removes a pipe of the specified type and rotations from the stock.
     *
     * @param type      The type of pipe to remove.
     * @param rotations The number of clockwise rotations for the pipe.
     */
    public void removePipe(PipeType type, int rotations) {
        Map<Integer, Integer> typeStock = stock.get(type);
        if (typeStock != null) {
            int quantity = typeStock.getOrDefault(rotations, 0);
            if (quantity > 0) {
                typeStock.put(rotations, quantity - 1);
            }
        }
    }
    
    /**
     * Creates a deep copy of the PipeStock.
     *
     * @return A new PipeStock instance with the same content as the original.
     */
    public PipeStock deepCopy() {
        return new PipeStock(this);
    }

    /**
     * Returns a string representation of the PipeStock, listing available pipe types and quantities.
     *
     * @return A string describing the contents of the PipeStock.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Pipe Stock:\n");
        for (PipeType type : stock.keySet()) {
            Map<Integer, Integer> typeStock = stock.get(type);
            for (int rotations : typeStock.keySet()) {
                int quantity = typeStock.get(rotations);
                builder.append(type).append(" (Rotations: ").append(rotations).append("): ").append(quantity).append("\n");
            }
        }
        return builder.toString();
    }
}