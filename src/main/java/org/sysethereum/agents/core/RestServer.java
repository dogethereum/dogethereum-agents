package org.sysethereum.agents.core;

import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Manages REST requests for SPV Proofs
 *
 * @author Jag Sidhu
 */
@Service
@Slf4j(topic = "RestServer")
public class RestServer {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("RestServer");
    private final HttpServer httpServer;

    public RestServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        try {
            httpServer.stop(10); // seconds
            logger.debug("stop: HTTP server was stopped");
        } catch (Exception e) {
            logger.error("HTTP server stop method raised an exception", e);
        }
    }
}