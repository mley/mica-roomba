package com.m303.roomba.nav;

import com.m303.roomba.Cell;
import com.m303.roomba.Constants;
import com.m303.roomba.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mley on 28.05.15.
 */
/*
XXXXXXXX
X^     X
X XXXX X
X Z    X
XXXXXXXX



 */
public class Routing implements Constants {


    private Field field;

    private Graph g;
    public Routing(Field f, Graph g) {
        this.field = f;
        this.g = g;
    }

    public Cell bfSearch(Selector selector) {

        Vertex from = g.getVertex(field.getCurrent(), field.getHeading());

        Vertex to = bfSearch(from, selector);
        if(to != null) {
            return to.getCell();
        }
        return null;
    }

    private List<Edge> getEdges(Vertex v) {
        List<Edge> es = new ArrayList<>();
        for(Edge e : g.getEdges()) {
            if(e.getSource() == v) {
                es.add(e);
            }
        }
        return es;
    }

    public interface Selector {
        boolean select(Cell c);
    }

    public Vertex bfSearch( Vertex from, Selector selector) {

        Set<Vertex> visited = new HashSet<>();
        List<Vertex> queue = new ArrayList<>();
        queue.add(from);
        visited.add(from);
        while(!queue.isEmpty() ) {
            List<Vertex> visitedList = new ArrayList<>();
            for(Vertex v : queue) {
                if(!visited.contains(v)) {
                    visitedList.add(v);
                }

                visited.add(v);
                if (selector.select(v.getCell())) {
                    return v;
                }
            }
            List<Vertex> tmp = new ArrayList<>(queue);
            queue.clear();
            for(Vertex v : tmp) {
                for (Edge e : getEdges(v)) {
                    Vertex v2 = e.getDestination();
                    if (!visited.contains(v2)) {
                        queue.add(v2);
                    }
                }
            }
        }

        return null;



    }

    /**
     * Calculate shortest path from current position to goal
     * @param to goal
     * @return list of commands to get to requested cell
     */
    public List<String> route2(Cell to) {
        Vertex from = g.getVertex(field.getCurrent(), field.getHeading());

        Vertex vto = g.getVertex(to, field.getHeading());

        return bfSearch2(from, vto);
    }

    public List<String> bfSearch2(Vertex from, Vertex to) {

        for(Vertex v : g.getVertexes()) {
            v.setParent(null);
        }

        Set<Vertex> visited = new HashSet<>();
        List<Vertex> queue = new ArrayList<>();

        queue.add(from);
        visited.add(from);
        while(!queue.isEmpty() ) {
            List<Vertex> visitedList = new ArrayList<>();
            for(Vertex v : queue) {
                if(!visited.contains(v)) {
                    visitedList.add(v);
                }

                visited.add(v);
                if (to == v) {
                    return calcPath(from, to);
                }
            }
            List<Vertex> tmp = new ArrayList<>(queue);
            queue.clear();
            for(Vertex v : tmp) {
                for (Edge e : getEdges(v)) {
                    Vertex v2 = e.getDestination();
                    if (!visited.contains(v2)) {
                        if(v2.getParent() == null) {
                            v2.setParent(v);
                            queue.add(v2);
                        }
                    }
                }
            }
        }

        return null;



    }

    private List<String> calcPath(Vertex from, Vertex to) {
        List<String> commands = new ArrayList<>();
        while(from != to) {
           commands.add(getCommand(to.getParent(), to));
            to = to.getParent();
        }

        Collections.reverse(commands);

        while(!WALK.equals(commands.get(commands.size()-1)) && commands.size()>0) {
            commands.remove(commands.size()-1);
        }

        return commands;
    }



    private static String getCommand(Vertex from, Vertex to) {
        if(to.getCell() == from.getCell()) {
            if(from.getDirection().left() == to.getDirection()) {
                return LEFT;
            } else {
                return RIGHT;
            }
        } else {
            return WALK;
        }

    }


}
