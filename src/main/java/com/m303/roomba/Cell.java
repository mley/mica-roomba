package com.m303.roomba;

import lombok.Data;

/**
 * Created by mley on 24.04.15.
 */
@Data
public class Cell implements Constants {


    public boolean isUnknown() {
        return state == State.Unknown;
    }

    public Direction isInDirection(Cell to) {
        for (Direction d : Direction.values()) {
            if (translate(d, 1) == to) {
                return d;
            }
        }

        return null;
    }

    public boolean hasShadow() {
        for(Direction d : Direction.values()) {
            if(translate(d, 1).isUnknown()) {
                return true;
            }
        }

        return false;
    }

    public enum State {
        Unknown,
        Occupied,
        Free
    }

    Field field;

    int x;
    int y;

    State state;
    State buttonState = State.Unknown;
    int button = -1;

    public Cell(Field f, State s) {
        this.field = f;
        this.state = s;


    }

    public boolean isWall() {
        return state == State.Occupied;
    }

    public void setButton(int b) {
        this.button = b;
        if (b >= 0) {
            buttonState = State.Occupied;
        } else {
            buttonState = State.Free;
        }
    }

    public boolean hasButton() {
        return button >= 0;
    }

    /**
     * free in the meaning of: can be walked on. No info about a button here
     *
     * @return
     */
    public boolean isFree() {
        return state == State.Free;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isOnEdge(Direction dir) {
        return translate(dir, 1) == null;
    }


    public Cell translate(Constants.Direction d, int steps) {
        int x = this.x;
        int y = this.y;
        switch (d) {
            case NORTH:
                y -= steps;
                break;
            case EAST:
                x += steps;
                break;
            case SOUTH:
                y += steps;
                break;
            case WEST:
                x -= steps;
                break;
        }


        return field.getCell(x, y);
    }

    public String toString() {
        String s = "  ";
        switch (state) {
            case Occupied:
                s = "█▉";
                break;
            case Unknown:
                s = "▒▒";
                break;
            case Free:
                s = " ";
                if (field.getCurrent() == this) {
                    s = field.getHeading().toSymbol();
                }
                switch (buttonState) {
                    case Occupied:
                        s += Integer.toString(button);
                        break;
                    case Free:
                        s += " ";
                        break;
                    case Unknown:
                        s += "?";
                        break;
                }
        }
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Cell) {
            return hashCode() == o.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return y * field.width() + x;
    }
}
