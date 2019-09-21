package org.sysethereum.agents.core;

import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.service.rest.*;

import javax.annotation.PostConstruct;
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
    private final InfoHandler infoHandler;

    public RestServer(
            GetSPVHandler getSPVHandler,
            GetSuperblockBySyscoinHandler getSuperblockBySyscoinHandler,
            GetSuperblockHandler getSuperblockHandler,
            GetSyscoinRPCHandler getSyscoinRPCHandler,
            InfoHandler infoHandler
    ) {
        this.getSPVHandler = getSPVHandler;
        this.getSuperblockBySyscoinHandler = getSuperblockBySyscoinHandler;
        this.getSuperblockHandler = getSuperblockHandler;
        this.getSyscoinRPCHandler = getSyscoinRPCHandler;
        this.infoHandler = infoHandler;
    }

    @PostConstruct
    public void setup() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", infoHandler);
        server.createContext("/spvproof", getSPVHandler);
        server.createContext("/superblockbysyscoinblock", getSuperblockBySyscoinHandler);
        server.createContext("/superblock", getSuperblockHandler);
        server.createContext("/syscoinrpc", getSyscoinRPCHandler);
        server.setExecutor(null); // creates a default executor
        server.start();
    }
}