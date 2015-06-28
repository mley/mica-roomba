package com.m303.roomba;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;

public class Main {

    public static final String EMPTY_MAZE =
            "##############################\n" + // empty maze
                    "#*         *                 #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#           *               *#\n" +
                    "#            *               #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "# *                          #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#    *                       #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#                  *         #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#                            #\n" +
                    "#                           *#\n" +
                    "#                           *#\n" +
                    "##############################\n";


    public static void main(String[] args) throws Exception {
        if(args.length != 1 || !args[0].startsWith("ws://")) {
            System.out.println("Usage: java -jar roomba.jar <Websocket Endpoint>");
            System.out.println("E.g.: java -jar roomba.jar ws://localhost:30000");
            System.exit(1);
        }
        WSHandler handler = createClient(args[0]);

        while (!handler.waitForClose()) {

        }

    }


    private static WSHandler createClient(String destUri) throws Exception {
        WebSocketClient client = new WebSocketClient();
        client.setAsyncWriteTimeout(5*60*1000);
        client.setMaxIdleTimeout(5*60*1000);
        WSHandler socket = new WSHandler("Roomba", "");

        client.start();
        URI echoUri = new URI(destUri);
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        client.connect(socket, echoUri, request);


        return socket;
    }
}
