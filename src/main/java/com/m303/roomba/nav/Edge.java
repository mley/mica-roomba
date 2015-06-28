package com.m303.roomba.nav;

import lombok.Data;

/**
 * Created by mley on 28.05.15.
 */
@Data
class Edge {
    private String action;
    private Vertex source;
    private Vertex destination;
    private int weight = 1;

    public Edge(String a, Vertex s, Vertex d) {
        this.action = a;
        this.source = s;
        this.destination = d;
    }
}
