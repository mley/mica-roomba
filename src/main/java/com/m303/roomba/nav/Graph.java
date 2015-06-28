package com.m303.roomba.nav;

import com.m303.roomba.Cell;
import com.m303.roomba.Constants;
import lombok.Data;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mley on 28.05.15.
 */
@Data
public class Graph implements Constants{
    private List<Vertex> vertexes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private Map<Cell, Map<Constants.Direction, Vertex>> vertexMap = new IdentityHashMap<>();


    public void addCell(Cell c) {
        if(vertexMap.containsKey(c) && vertexMap.get(c).size() == 4) {
            return;
        }

        Vertex e = new Vertex(c, Constants.Direction.EAST);
        Vertex s = new Vertex(c, Constants.Direction.SOUTH);
        Vertex w = new Vertex(c, Constants.Direction.WEST);
        Vertex n = new Vertex(c, Constants.Direction.NORTH);

        addEdge(RIGHT, e, s);
        addEdge(LEFT, s, e);
        addEdge(RIGHT, s, w);
        addEdge(LEFT, w, s);
        addEdge(RIGHT, w, n);
        addEdge(LEFT, n, w);
        addEdge(RIGHT, n, e);
        addEdge(LEFT, e, n);

        addVertex(e);
        addVertex(s);
        addVertex(w);
        addVertex(n);

        createNeighborEdges(e);
        createNeighborEdges(s);
        createNeighborEdges(w);
        createNeighborEdges(n);

    }

    private void createNeighborEdges(Vertex from) {
        Vertex to = getVertex(from.getCell().translate(from.getDirection(), 1), from.getDirection());
        if(to != null) {
            // walking from from to to
            addEdge(WALK, from, to);

            Vertex from2 = getVertex(to.getCell(), from.getDirection().opposite());
            Vertex to2 = getVertex(from.getCell(), from.getDirection().opposite());
            // walk in opposite direction
            if(from2 != null && to2 != null) {
                addEdge(WALK, from2, to2);
            }
        }
    }

    private void addEdge(String a, Vertex s, Vertex d) {
        edges.add(new Edge(a, s, d));
    }

    public void addVertex(Vertex v) {
        vertexes.add(v);

        if(!vertexMap.containsKey(v.getCell())) {
            vertexMap.put(v.getCell(), new IdentityHashMap<Constants.Direction, Vertex>());
        }
        vertexMap.get(v.getCell()).put(v.getDirection(), v);

    }

    public Vertex getVertex(Cell c, Constants.Direction d) {
        if(vertexMap.containsKey(c)) {
            return vertexMap.get(c).get(d);
        }
        return null;
    }

    public Edge getEdge(Vertex s, Vertex d) {
        for(Edge e : edges) {
            if(e.getSource() == s && e.getDestination() == d) {
                return e;
            }
        }

        return null;
    }
}
