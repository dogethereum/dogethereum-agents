package org.sysethereum.agents.service.rest;

import com.sun.net.httpserver.HttpExchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j(topic = "InfoHandler")
public class InfoHandler extends CommonHttpHandler {

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