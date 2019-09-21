package org.sysethereum.agents.core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.service.rest.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;

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

    private final GetSPVHandler getSPVHandler;
    private final GetSuperblockBySyscoinHandler getSuperblockBySyscoinHandler;
    private final GetSuperblockHandler getSuperblockHandler;
    private final GetSyscoinRPCHandler getSyscoinRPCHandler;

    public RestServer(
            GetSPVHandler getSPVHandler,
            GetSuperblockBySyscoinHandler getSuperblockBySyscoinHandler,
            GetSuperblockHandler getSuperblockHandler,
            GetSyscoinRPCHandler getSyscoinRPCHandler
    ) {
        this.getSPVHandler = getSPVHandler;
        this.getSuperblockBySyscoinHandler = getSuperblockBySyscoinHandler;
        this.getSuperblockHandler = getSuperblockHandler;
        this.getSyscoinRPCHandler = getSyscoinRPCHandler;
    }

    @PostConstruct
    public void setup() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new InfoHandler());
        server.createContext("/spvproof", getSPVHandler);
        server.createContext("/superblockbysyscoinblock", getSuperblockBySyscoinHandler);
        server.createContext("/superblock", getSuperblockHandler);
        server.createContext("/syscoinrpc", getSyscoinRPCHandler);
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    // http://localhost:8000/info
    private static class InfoHandler extends CommonHttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "Valid Superblock calls: " + System.lineSeparator() +
                    "\t/spvproof?hash=<blockhash>" + System.lineSeparator() +
                    "\t/spvproof?height=<blockheight>" + System.lineSeparator() +
                    "\t/superblockbysyscoinblock?hash=<blockhash>" + System.lineSeparator() +
                    "\t/superblockbysyscoinblock?height=<blockheight>" + System.lineSeparator() +
                    "\t/superblock?hash=<superblockid>" + System.lineSeparator() +
                    "\t/superblock?height=<superblockheight>" + System.lineSeparator() + System.lineSeparator() +
                    "Valid Syscoin RPC calls: " + System.lineSeparator() +
                    "\t/syscoinrpc?method=<methodname>&param1name=<param1value>&paramNname=<paramNvalue>...";
            writeResponse(httpExchange, response);
        }
    }

}

