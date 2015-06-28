package com.m303.roomba;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mley on 24.04.15.
 */

@WebSocket(maxTextMessageSize = 64 * 1024)
public class WSHandler {

    TypeReference<HashMap<String, Object>> typeRef;
    ObjectMapper mapper;
    private Session session;
    private volatile boolean closed;

    private String name = "Roomba";

    private volatile String lastAction;

    private String maze;
    private Brain brain;

    public WSHandler(String name, String maze) {
        this.name = name;
        this.maze = maze;

        JsonFactory factory = new JsonFactory();
        mapper = new ObjectMapper(factory);
        typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };
    }


    public synchronized boolean waitForClose() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return closed;
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        closed = true;
        synchronized (this) {
            this.notifyAll();
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;


    }

    public void act(String action) {
        lastAction = action;
        send("{ \"action\" : \"" + action + "\"}");
    }

    public void send(String s) {
        System.out.println("> "+s);
        try {
            session.getRemote().sendString(s);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @OnWebSocketMessage
    public synchronized void onMessage(String msg) {
        try {
            System.out.println("< " + msg);
            Map<String, Object> response = mapper.readValue(msg, typeRef);
            if("new game".equals(response.get("message")) && Brain.START.equals(lastAction) ) {
                brain = new MappingBrain();
                lastAction = Brain.START;
                act(brain.think(lastAction, response));
            }
            if ("ok".equals(response.get("result"))) {
                String message = (String)response.get("message");
                if (message != null && message.startsWith("Hello robot!")) {
                    send("{ \"name\" : \"" + name + "\", \"maze\" : \"" + maze + "\" }");
                    lastAction = Brain.START;
                } else if(message != null && message.contains("please wait")) {
                    //act("debug");
                    //act(brain.think(lastAction, response));
                } else if(message != null && message.startsWith("Hurray")) {
                    System.out.println("FTW");
                    lastAction = Brain.START;
                    // game won, just wait
                } else {
                    act( brain.think(lastAction, response));
                }
            } else if("fail".equals(response.get("result"))){
                act( brain.think(lastAction, response));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
