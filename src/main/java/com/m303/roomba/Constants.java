package com.m303.roomba;

/**
 * Created by mley on 24.04.15.
 */
public interface Constants {
    String WALK = "walk";
    String LEFT = "left";
    String RIGHT = "right";
    String PUSH = "push";
    String SWAP = "swap";
    String LOOK = "look";
    String START = "start";


    enum Direction {


        NORTH("^"),
        EAST(">"),
        SOUTH("v"),
        WEST("<");

        private final String s;

        Direction(String s) {
            this.s = s;
        }

        public Direction left() {
            switch (this) {
                case NORTH:
                    return WEST;
                case WEST:
                    return SOUTH;
                case SOUTH:
                    return EAST;
                case EAST:
                    return NORTH;
            }
            throw new IllegalArgumentException(" (╯°□°）╯︵ ┻━┻)");
        }

        public Direction right() {
            switch (this) {
                case NORTH:
                    return EAST;
                case WEST:
                    return NORTH;
                case SOUTH:
                    return WEST;
                case EAST:
                    return SOUTH;
            }
            throw new IllegalArgumentException(" (╯°□°）╯︵ ┻━┻)");
        }

        public Direction opposite() {
            return left().left();
        }

        public String toSymbol() {
            return s;
        }
    }

}
