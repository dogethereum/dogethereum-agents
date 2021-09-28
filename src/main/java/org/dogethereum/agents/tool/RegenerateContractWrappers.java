package org.dogethereum.agents.tool;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.web3j.protocol.core.methods.response.AbiDefinition;
import org.web3j.codegen.SolidityFunctionWrapper;
import org.web3j.tx.Contract;
import org.web3j.protocol.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

@Slf4j(topic = "RegenerateContractWrappers")
public class RegenerateContractWrappers {
    public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException {
        if (args.length < 1) {
            log.error("Missing deployment file argument.");
            return;
        }

        String dogethereumDeploymentJson = args[0];
        String basePackageName = "org.dogethereum.agents.contract";
        String dogethereumAgentsJavaWrapperDir = "/home/scn/Repos/Ethereum/dogethereum/dogethereum-agents/src/main/java/";
        DogethereumSmartContract[] targetContracts = new DogethereumSmartContract[]{
            new DogethereumSmartContract("dogeToken", "DogeToken"),
            new DogethereumSmartContract("superblockClaims", "SuperblockClaims"),
            new DogethereumSmartContract("battleManager", "DogeBattleManager"),
            new DogethereumSmartContract("superblocks", "DogeSuperblocks"),
            new DogethereumSmartContract("scryptChecker", "ScryptClaims")
        };

        SolidityFunctionWrapper wrapperGenerator = new SolidityFunctionWrapper(true);
        FileReader deploymentJson = new FileReader(dogethereumDeploymentJson);
        JSONParser parser = new JSONParser();
        JSONObject deployment = (JSONObject) parser.parse(deploymentJson);
        JSONObject contracts = (JSONObject) deployment.get("contracts");
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        for (DogethereumSmartContract targetContract : targetContracts) {
            JSONObject contract = (JSONObject) contracts.get(targetContract.getComponentName());
            String abi = ((JSONArray) contract.get("abi")).toJSONString();
            AbiDefinition[] abiDefinition = objectMapper.readValue(abi, AbiDefinition[].class);
            // HACK: This lets us workaround this issue in web3j codegen: https://github.com/web3j/web3j/issues/1268
            // It also works around the fact that web3j does not support function types in function parameters
            for (AbiDefinition functionAbi : abiDefinition) {
                String stateMutability = functionAbi.getStateMutability();
                if (stateMutability != null && stateMutability.equals("payable")) {
                    functionAbi.setPayable(true);
                }
                String type = functionAbi.getType();
                if (type.equals("function")) {
                    java.util.List<AbiDefinition.NamedType> inputs = functionAbi.getInputs();
                    for (AbiDefinition.NamedType inputTypeNode : inputs) {
                        String inputType = inputTypeNode.getType();
                        if (inputType.startsWith("function")) {
                            inputTypeNode.setType("bytes24");
                        }
                    }
                }
            }
            wrapperGenerator.generateJavaFiles(
                    targetContract.getContractClassName(),
                    Contract.BIN_NOT_PROVIDED,
                    Arrays.asList(abiDefinition),
                    dogethereumAgentsJavaWrapperDir,
                    basePackageName,
                    null
            );
        }
    }


    @Value
    private static class DogethereumSmartContract {
        /**
         * The name of the Dogethereum smart contract component in the deployment artifact.
         */
        String componentName;
        /**
         * The name of the generated class that represents this particular smart contract component.
         */
        String contractClassName;
    }

}