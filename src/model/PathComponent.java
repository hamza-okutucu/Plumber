package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.enumeration.PathComponentColor;
import model.enumeration.Direction;
import model.enumeration.PipeType;
import model.enumeration.Position;

/**
 * The `PathComponent` class represents a component of a pipe with specific directions and color.
 *
 * @author hamza-okutucu
 */
public class PathComponent {
    
    private Set<Direction> directions;
    private PathComponentColor color;

    /**
     * Private constructor to create a new `PathComponent` with given directions and color.
     *
     * @param directions The set of directions for this component.
     * @param color The color of this component.
     */
    private PathComponent(Set<Direction> directions, PathComponentColor color) {
      this.directions = directions;
      this.color = color;
    }
    
    /**
     * Constructs a new `PathComponent` by copying another `PathComponent`.
     *
     * @param pathComponent The `PathComponent` to copy.
     */
    public PathComponent(PathComponent pathComponent) {
        this.directions = new HashSet<>(pathComponent.directions);
        this.color = pathComponent.color;
    }

    /**
     * Gets the directions of this path component.
     *
     * @return The set of directions for this path component.
     */
    public Set<Direction> getDirections() {
      return directions;
    }

    /**
     * Gets the color of this path component.
     *
     * @return The color of this path component.
     */
    public PathComponentColor getColor() {
      return color;
    }

    /**
     * Sets the color of this path component.
     *
     * @param color The new color for this path component.
     */
    public void setColor(PathComponentColor color) {
      this.color = color;
    }
    
    /**
     * Retrieves a list of path components based on the pipe type, color, and number of rotations.
     *
     * @param type The type of the pipe.
     * @param color The color of the path component.
     * @param rotations The number of clockwise rotations.
     * @return A list of path components.
     */
    public static List<PathComponent> getPathComponents(PipeType type, PathComponentColor color, int rotations) {
        rotations = (rotations % 4 + 4) % 4;
    
        List<PathComponent> pathComponents = new ArrayList<>();
        Set<Direction> componentDirections = new HashSet<>();

        if (type.equals(PipeType.LINE)) {
            if (rotations % 2 == 0) {
                componentDirections.add(Direction.TOP);
                componentDirections.add(Direction.BOTTOM);
            } else {
                componentDirections.add(Direction.LEFT);
                componentDirections.add(Direction.RIGHT);
            }
            pathComponents.add(new PathComponent(componentDirections, color));
        } else if (type.equals(PipeType.OVER)) {
            Set<Direction> componentDirections2 = new HashSet<>();
            if (rotations % 2 == 0) {
                componentDirections.add(Direction.TOP);
                componentDirections.add(Direction.BOTTOM);
                componentDirections2.add(Direction.LEFT);
                componentDirections2.add(Direction.RIGHT);
            } else {
                componentDirections.add(Direction.LEFT);
                componentDirections.add(Direction.RIGHT);
                componentDirections2.add(Direction.TOP);
                componentDirections2.add(Direction.BOTTOM);
            }
            pathComponents.add(new PathComponent(componentDirections, color));
            pathComponents.add(new PathComponent(componentDirections2, color));
        } else if (type.equals(PipeType.FORK)) {
            if (rotations == 0) {
                componentDirections.add(Direction.TOP);
                componentDirections.add(Direction.RIGHT);
                componentDirections.add(Direction.BOTTOM);
            } else if (rotations == 1) {
                componentDirections.add(Direction.RIGHT);
                componentDirections.add(Direction.BOTTOM);
                componentDirections.add(Direction.LEFT);
            } else if (rotations == 2) {
                componentDirections.add(Direction.BOTTOM);
                componentDirections.add(Direction.LEFT);
                componentDirections.add(Direction.TOP);
            } else if (rotations == 3) {
                componentDirections.add(Direction.LEFT);
                componentDirections.add(Direction.TOP);
                componentDirections.add(Direction.RIGHT);
            }
            pathComponents.add(new PathComponent(componentDirections, color));
        } else if (type.equals(PipeType.CROSS)) {
            componentDirections.add(Direction.TOP);
            componentDirections.add(Direction.RIGHT);
            componentDirections.add(Direction.BOTTOM);
            componentDirections.add(Direction.LEFT);
            pathComponents.add(new PathComponent(componentDirections, color));
        } else if (type.equals(PipeType.TURN)) {
            if (rotations == 0) {
                componentDirections.add(Direction.TOP);
                componentDirections.add(Direction.RIGHT);
            } else if (rotations == 1) {
                componentDirections.add(Direction.RIGHT);
                componentDirections.add(Direction.BOTTOM);
            } else if (rotations == 2) {
                componentDirections.add(Direction.BOTTOM);
                componentDirections.add(Direction.LEFT);
            } else if (rotations == 3) {
                componentDirections.add(Direction.LEFT);
                componentDirections.add(Direction.TOP);
            }
            pathComponents.add(new PathComponent(componentDirections, color));
        } else if (type.equals(PipeType.EMPTY)) {
            return pathComponents;
        } else if (type.equals(PipeType.SOURCE)) {
            if (rotations == 0) {
                componentDirections.add(Direction.TOP);
            } else if (rotations == 1) {
                componentDirections.add(Direction.RIGHT);
            } else if (rotations == 2) {
                componentDirections.add(Direction.BOTTOM);
            } else if (rotations == 3) {
                componentDirections.add(Direction.LEFT);
            }
            pathComponents.add(new PathComponent(componentDirections, color));
        } else {
            handleInvalidPipeType(type);
        }
    
        return pathComponents;
    }
    
    /**
     * Handles an exception when an invalid pipe type is encountered.
     *
     * @param type The invalid pipe type.
     */
    private static void handleInvalidPipeType(PipeType type) {
        try {
            throw new Exception("Le tuyau n'existe pas : " + type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if this path component is connected to another path component at a specific position.
     *
     * @param pathComponent The other path component to check for a connection.
     * @param position The relative position to check for a connection.
     * @return `true` if there is a connection, otherwise `false`.
     */
    public boolean isConnectedTo(PathComponent pathComponent, Position position) {
        switch(position) {
            case TOP:
                return pathComponent.getDirections().contains(Direction.BOTTOM) && this.directions.contains(Direction.TOP);
            case RIGHT:
                return pathComponent.getDirections().contains(Direction.LEFT) && this.directions.contains(Direction.RIGHT);
            case BOTTOM:
                return pathComponent.getDirections().contains(Direction.TOP) && this.directions.contains(Direction.BOTTOM);
            case LEFT:
                return pathComponent.getDirections().contains(Direction.RIGHT) && this.directions.contains(Direction.LEFT);
            default:
                handleInvalidPosition(position);
        }

        return false;
    }
    
    /**
     * Handles an exception when an invalid position is encountered.
     *
     * @param position The invalid position.
     */
    private static void handleInvalidPosition(Position position) {
        try {
            throw new Exception("La position n'existe pas : " + position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a deep copy of this path component.
     *
     * @return A new `PathComponent` instance with the same properties.
     */
    public PathComponent deepCopy() {
        return new PathComponent(this);
    }
}
