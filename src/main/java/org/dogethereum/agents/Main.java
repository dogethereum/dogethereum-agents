package org.dogethereum.agents;

import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.dogethereum.agents.api.SuperblocksService;
import org.dogethereum.agents.constants.SystemProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Configuration
@ComponentScan
@Slf4j(topic = "Main")
public class Main {
    public static void main(String[] args) {
        SystemProperties config = SystemProperties.CONFIG;
        log.info("Running Dogethereum agents version: {}-{}", config.projectVersion(), config.projectVersionModifier());
        // Instantiate the spring context
        AnnotationConfigApplicationContext c = new AnnotationConfigApplicationContext(Main.class);
        SuperblocksService service = c.getBean(SuperblocksService.class);

        if (!config.isHttpApiEnabled()) return;

        int port = config.getHttpApiPort();
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new DogethereumApi(service));
            // Creates a default executor
            server.setExecutor(null);
            server.start();
        } catch (IOException error) {
            log.error("Failed to start HTTP server.", error);
            return;
        }

        log.info("HTTP API listening on port {}", port);
    }
}

/**
 * Class used to provide an HTTP transport for JSON-RPC commmands
 */
@Slf4j(topic = "HttpApi")
class DogethereumApi implements HttpHandler {
    private final JsonRpcServer api;

    DogethereumApi(SuperblocksService service) {
        api = new JsonRpcServer(service, SuperblocksService.class);
    }

    public void handle(HttpExchange t) throws IOException {
        InputStream is = t.getRequestBody();
        OutputStream os = t.getResponseBody();
        ByteArrayOutputStream jsonResponse = new ByteArrayOutputStream();
        int code = api.handleRequest(is, jsonResponse);
        if (code != 0) {
            t.sendResponseHeaders(500, 0);
            log.error("JSON RPC request handling error: {}", code);
        } else {
            t.sendResponseHeaders(200, jsonResponse.size());
            log.debug("Http response: {}; length: {}", jsonResponse.toString(StandardCharsets.UTF_8), jsonResponse.size());
        }
        os.write(jsonResponse.toByteArray());
        os.close();
    }
}