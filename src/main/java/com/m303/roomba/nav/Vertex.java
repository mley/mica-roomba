package com.m303.roomba.nav;

import com.m303.roomba.Cell;
import com.m303.roomba.Constants;
import lombok.Data;

/**
 * Created by mley on 28.05.15.
 */
@Data
public class Vertex {

    private Vertex parent;
    private Cell cell;
    private Constants.Direction direction;

    public Vertex(Cell c, Constants.Direction d) {
        this.cell = c;
        this.direction = d;
    }

    public String toString() {
        return "("+cell.getX()+","+cell.getY()+")"+direction;
    }
}
