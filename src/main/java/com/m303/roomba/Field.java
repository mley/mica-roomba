package com.m303.roomba;

import com.m303.roomba.nav.Graph;
import com.m303.roomba.nav.Routing;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by mley on 24.04.15.
 */
@Data
public class Field implements Constants {


    Cell current;
    Direction heading = Direction.NORTH;
    List<List<Cell>> field;

    Routing routing;
    Graph graph;

    public Field() {

        field = new Vector<>();
        List<Cell> a = new Vector<>();
        graph = new Graph();
        current = new Cell(this, Cell.State.Free);
        current.setPos(0, 0);
        a.add(current);
        field.add(a);

        for (Direction d : Direction.values()) {
            extendField(current, d);
        }
        graph.addCell(current);
        routing = new Routing(this, graph);
    }

    public void discover(Map<String, Object> result) {
        Direction dir = heading;

        for (int i = 1; i <= 5; i++) {
            String cell = (String) result.get(Integer.toString(i));
            if (cell != null && !cell.isEmpty()) {
                Cell c = current.translate(dir, i);

                if ("#".equals(cell)) {
                    c.setState(Cell.State.Occupied);
                } else {
                    extendField(c, dir);
                    c = current.translate(dir, i);
                    c.setState(Cell.State.Free);
                    c.setButtonState(Cell.State.Free);
                    Cell left = c.translate(dir.left(), 1);
                    Cell right = c.translate(dir.right(), 1);

                    graph.addCell(c);

                    if (cell.contains("l")) {
                        // left of cell is free
                        left.setState(Cell.State.Free);
                        graph.addCell(left);
                        cell = cell.replace("l", "");
                        if (left.isOnEdge(dir.left())) {
                            extendField(c, dir.left());
                        }
                    } else {
                        left.setState(Cell.State.Occupied);
                    }

                    if (cell.contains("r")) {
                        // right of cell is free
                        right.setState(Cell.State.Free);
                        graph.addCell(right);
                        cell = cell.replace("r", "");
                        if (right.isOnEdge(dir.right())) {
                            extendField(c, dir.right());
                        }
                    } else {
                        right.setState(Cell.State.Occupied);
                    }

                    if (cell.matches("\\d")) {
                        //digit left, cell has a button
                        c.setButton(Integer.parseInt(cell));
                    }
                }
            }
        }

    }

    Cell getCell(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            return null;
        }
        return field.get(y).get(x);
    }

    int width() {
        return field.get(0).size();
    }

    int height() {
        return field.size();
    }

    void extendField(Cell from, Direction d) {
        System.out.println(this);
        Cell to = from.translate(d, 1);
        if (to != null) {
            return;
        }

        switch (d) {
            case SOUTH:
                field.add(unknownLine());
                break;
            case NORTH:
                field.add(0, unknownLine());
                break;
            case WEST:
            case EAST:
                for (List<Cell> line : field) {
                    if (d == Direction.WEST) {
                        line.add(0, new Cell(this, Cell.State.Unknown));
                    } else {
                        line.add(new Cell(this, Cell.State.Unknown));
                    }
                }

        }

        // update all positions
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                Cell c = field.get(y).get(x);
                c.setPos(x, y);
            }
        }


    }

    List<Cell> unknownLine() {
        List<Cell> l = new Vector<>();
        for (int i = 0; i < width(); i++) {
            l.add(new Cell(this, Cell.State.Unknown));
        }
        return l;

    }

    public Cell findNearestUnknownButtonG() {


        final Cell to = routing.bfSearch(new Routing.Selector() {
            @Override
            public boolean select(Cell c) {
                return c.getButtonState() == Cell.State.Unknown || c.hasShadow();
            }
        });
        return to;
    }


    public void allCells(CellVisitor cp) {
        for (List<Cell> line : field) {
            for (Cell c : line) {
                cp.process(c);
            }
            cp.eol();
        }
    }

    private List<String> rotateTo(Direction d) {
        List<String> cmdsl = new ArrayList<>();
        List<String> cmdsr = new ArrayList<>();
        Direction tmp = heading;
        while (tmp != d) {
            cmdsl.add(LEFT);
            tmp = tmp.left();
        }
        tmp = heading;
        while (tmp != d) {
            cmdsr.add(RIGHT);
            tmp = tmp.right();
        }

        return cmdsl.size() < cmdsr.size() ? cmdsl : cmdsr;
    }

    public void headRight() {
        heading = heading.right();
    }

    public void headLeft() {
        heading = heading.left();
    }

    public void walk() {
        current = current.translate(heading, 1);
    }

    public Cell findButton(final int nextButton) {
        Cell b = null;
        for (List<Cell> line : field) {
            for (Cell c : line) {
                if (c.getButton() == nextButton) {
                    b = c;
                    break;
                }
            }
        }
        return b;
    }

    static class CellVisitor {
        void process(Cell c) {
        }

        void eol() {
        }
    }


    public List<String> navigate(Cell to, Direction d, boolean ignoreDestDir) {
        return routing.route2(to);
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();

        allCells(new CellVisitor() {
            @Override
            public void process(Cell c) {
                sb.append(c.toString());
            }

            @Override
            public void eol() {
                sb.append("\n");
            }
        });

        return sb.toString();
    }
}
