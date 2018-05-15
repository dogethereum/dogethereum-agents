package org.dogethereum.dogesubmitter.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.libdohj.params.AbstractDogecoinParams;
import org.spongycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j(topic = "OperatorKeyHandler")
public class OperatorKeyHandler {

    public static final int KEY_LENGTH = 32;

    AbstractDogecoinParams dogeParams;

    private final String filePath;

    private byte[] privateKey;

    // We ignore txs to the operator before this time
    private long operatorAddressCreationTime;

    public OperatorKeyHandler() {
        SystemProperties config = SystemProperties.CONFIG;
        this.dogeParams = config.getAgentConstants().getDogeParams();
        this.filePath = config.operatorPrivateKeyFilePath();
        this.operatorAddressCreationTime = config.operatorAddressCreationTime();
        validateOperatorKeyFile();
        log.info("OperatorKeyHandler started. Operator address is {}, created on {}.", getAddress(), getOperatorAddressCreationDate());
    }

    public byte[] getPrivateKeyBytes() {
        if (privateKey == null) {
            if (StringUtils.isNotBlank(this.filePath) && Paths.get(this.filePath).toFile().exists()) {
                try (FileReader fr = new FileReader(this.filePath); BufferedReader br = new BufferedReader(fr)) {
                    privateKey = Hex.decode(StringUtils.trim(br.readLine()).getBytes(StandardCharsets.UTF_8));
                } catch (Exception ex) {
                    log.error("Error while reading getting operator secret");
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
        return operatorAddressCreationTime;
    }

    private Date getOperatorAddressCreationDate() {
        return new Date(getOperatorAddressCreationTime() * 1000);
    }


    private void validateOperatorKeyFile() {
        if (StringUtils.isBlank(this.filePath))
            throw new RuntimeException ("Invalid Operator Key File Name");

        if (!Paths.get(this.filePath).toFile().exists()) {
            throw new RuntimeException ("Operator Key File '" + this.filePath + "' does not exist");
        }

        try {
            List<PosixFilePermission> permissions = new ArrayList<>(Files.getPosixFilePermissions(Paths.get(this.filePath)));
            if (permissions.size() == 1 && permissions.get(0).equals(PosixFilePermission.OWNER_READ)) {
                //do-nothing, everything ok so far.
            } else {
                throw new RuntimeException("Error validating Operator file permissions.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error validating Operator file permissions.");
        }

        try {
            byte[] var = getPrivateKeyBytes();
            boolean sizeOk = this.validateKeyLength(var);
            var = null;
            if (!sizeOk) {
                throw new RuntimeException ("Invalid Key Size");
            }
        } catch (Exception ex) {
            throw new RuntimeException ("Error Reading Operator Key File '" + this.filePath + "'");
        }
    }

    private boolean validateKeyLength(byte[] var) {
        return !(var == null || var.length != KEY_LENGTH);
    }

}
