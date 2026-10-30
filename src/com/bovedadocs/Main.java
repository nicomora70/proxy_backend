package com.bovedadocs;

import com.bovedadocs.api.ApiServer;
import com.bovedadocs.store.AppState;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        String environmentPort = System.getenv("PORT");
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else if (environmentPort != null && !environmentPort.isEmpty()) {
            port = Integer.parseInt(environmentPort);
        }
        AppState state = new AppState();
        ApiServer server = new ApiServer(port, state);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        System.out.println("BovedaDocs backend listening on http://localhost:" + port);
    }
}
