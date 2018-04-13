package org.dogethereum.dogesubmitter.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.libdohj.params.AbstractDogecoinParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@Component
@Slf4j(topic = "OperatorKeyHandler")
public class OperatorKeyHandler {

    private static final Logger logger = LoggerFactory.getLogger("OperatorKeyHandler");

    AbstractDogecoinParams dogeParams;

    // We ignore txs to the federation before this time
    protected long federationAddressCreationTime;

    private final String filePath;

    private byte[] privateKey;

    private long operatorAddressCreationTime;

    public OperatorKeyHandler() {
        SystemProperties config = SystemProperties.CONFIG;
        this.dogeParams = config.getBridgeConstants().getDogeParams();
        this.filePath = config.operatorPrivateKeyFilePath();
        this.operatorAddressCreationTime = config.operatorAddressCreationTime();
    }

    public byte[] getPrivateKeyBytes() {
        if (privateKey == null) {
            if (StringUtils.isNotBlank(this.filePath) && Paths.get(this.filePath).toFile().exists()) {
                try (FileReader fr = new FileReader(this.filePath); BufferedReader br = new BufferedReader(fr)) {
                    privateKey = Hex.decode(StringUtils.trim(br.readLine()).getBytes(StandardCharsets.UTF_8));
                } catch (Exception ex) {
                    logger.error("Error while reading getting operator secret");
                    throw new RuntimeException("Error while reading getting operator secret");
                }
            } else {
                throw new RuntimeException("Error accessing operator key.");
            }
        }
        return privateKey;
    }

    public ECKey getPrivateKey() {
        return ECKey.fromPrivate(getPrivateKeyBytes());
    }

    public Script getOutputScript() {
        return ScriptBuilder.createOutputScript(getAddress());
    }

    public Address getAddress() {
        return getPrivateKey().toAddress(dogeParams);
    }


    public long getOperatorAddressCreationTime() {
        return operatorAddressCreationTime; }

}
