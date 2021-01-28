package org.dogethereum.agents.tool;

import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.datatypes.Address;
import org.web3j.codegen.TruffleJsonFunctionWrapperGenerator;

import java.io.File;
import java.io.IOException;

@Slf4j(topic = "RegenerateContractWrappers")
public class RegenerateContractWrappers {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String dogethereumContractsRootDir = "/path-to-code/dogethereum-contracts";
        String dogethereumAgentsRootDir = "/path-to-code/dogethereum-agents";
        String[] contractNames = new String[]{"DogeToken", "DogeClaimManager", "DogeBattleManager", "DogeSuperblocks", "ClaimManager"};
        for (String contractName : contractNames) {
            new TruffleJsonFunctionWrapperGenerator(
                    dogethereumContractsRootDir + "/build/contracts/" + contractName + ".json",
                    dogethereumAgentsRootDir + "/src/main/java/",
                    "org.dogethereum.agents.contract",
                    true)
                    .generate();
        }
    }
}