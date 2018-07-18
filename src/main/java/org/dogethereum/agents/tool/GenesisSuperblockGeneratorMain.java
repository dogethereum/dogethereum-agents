package org.dogethereum.agents.tool;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.agents.constants.AgentConstants;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.core.dogecoin.DogecoinWrapper;
import org.dogethereum.agents.core.dogecoin.Superblock;
import org.dogethereum.agents.core.dogecoin.SuperblockUtils;
import org.dogethereum.agents.util.OperatorKeyHandler;
import org.spongycastle.util.encoders.Hex;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan
@Slf4j(topic = "GenesisSuperblockGeneratorMain")
/**
 * Tool to create a genesis superblock
 * @author Catalina Juarros
 * @author Oscar Guindzberg
 */
public class GenesisSuperblockGeneratorMain {

    private static String baseDir = "/yourPath/dogethereum-agents";
    private static String subDir = "/src/main/java/org/dogethereum/agents/tool";

    public static void main(String[] args) throws Exception {
        SystemProperties config = SystemProperties.CONFIG;
        log.info("Running GenesisSuperblockGeneratorMain version: {}-{}", config.projectVersion(), config.projectVersionModifier());
        // Instantiate the spring context
        AnnotationConfigApplicationContext c = new AnnotationConfigApplicationContext();
        c.register(OperatorKeyHandler.class);
        c.register(DogecoinWrapper.class);
        c.refresh();
        DogecoinWrapper dogecoinWrapper = c.getBean(DogecoinWrapper.class);
        Superblock s = getGenesisSuperblock(dogecoinWrapper);
        s.getSuperblockId();
        System.out.println(s);
    }

    private static Superblock getGenesisSuperblock(DogecoinWrapper dogecoinWrapper) throws IOException, BlockStoreException {
        SystemProperties config = SystemProperties.CONFIG;
        AgentConstants agentConstants = config.getAgentConstants();
        NetworkParameters params = agentConstants.getDogeParams();

        BufferedReader reader = new BufferedReader(
                new FileReader(baseDir + subDir + "/dogemain-2309215-to-2309216"));
        List<Sha256Hash> dogeBlockHashes = parseBlockHashes(reader);
        byte[] genesisParentHash = new byte[32]; // initialised with 0s
        StoredBlock lastDogeBlock = dogecoinWrapper.getBlock(dogeBlockHashes.get(dogeBlockHashes.size() - 1));
        StoredBlock previousToLastDogeBlock = dogecoinWrapper.getBlock(lastDogeBlock.getHeader().getPrevBlockHash());

        return new Superblock(params,
                dogeBlockHashes,
                lastDogeBlock.getChainWork(),
                lastDogeBlock.getHeader().getTimeSeconds(),
                previousToLastDogeBlock.getHeader().getTimeSeconds(),
                lastDogeBlock.getHeader().getDifficultyTarget(),
                genesisParentHash,
                0, SuperblockUtils.STATUS_APPROVED, 0);
    }

    private static List<Sha256Hash> parseBlockHashes(BufferedReader reader) throws IOException {
        List<Sha256Hash> result = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            byte[] rawBytes = Hex.decode(line);
            result.add(Sha256Hash.wrap(rawBytes));
            line = reader.readLine();
        }
        return result;
    }
}