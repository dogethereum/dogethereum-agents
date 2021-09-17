/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 */
package org.dogethereum.agents.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.dogethereum.agents.constants.SystemProperties;
import org.libdohj.params.AbstractDogecoinParams;
import org.bouncycastle.util.encoders.Hex;
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
/**
 * Manages operator private key
 */
public class OperatorKeyHandler implements OperatorPublicKeyHandler {

    public static final int KEY_LENGTH = 32;

    AbstractDogecoinParams dogeParams;

    private final String filePath;

    private byte[] privateKey;

    // We ignore txs to the operator before this time
    private long addressCreationTime;

    public OperatorKeyHandler() {
        SystemProperties config = SystemProperties.CONFIG;
        this.dogeParams = config.getAgentConstants().getDogeParams();
        this.filePath = config.operatorPrivateKeyFilePath();
        this.addressCreationTime = config.operatorAddressCreationTime();
        if (config.isDogeTxRelayerEnabled() || config.isOperatorEnabled()) {
            validateOperatorKeyFile();
            log.info("OperatorKeyHandler started. Operator address is {}, created on {}.", getAddress(), getOperatorAddressCreationDate());
        } else {
            log.info("OperatorKeyHandler not started because it is not needed");
        }
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

    @Override
    public Script getOutputScript() {
        return ScriptBuilder.createOutputScript(getAddress());
    }

    @Override
    public Address getAddress() {
        return getPrivateKey().toAddress(dogeParams);
    }

    @Override
    public byte[] getPublicKeyHash() {
        return getPrivateKey().getPubKeyHash();
    }

    @Override
    public long getAddressCreationTime() {
        return addressCreationTime;
    }

    private Date getOperatorAddressCreationDate() {
        return new Date(getAddressCreationTime() * 1000);
    }


    private void validateOperatorKeyFile() {
        if (StringUtils.isBlank(this.filePath))
            throw new RuntimeException ("Invalid Operator Key File Name");

        if (!Paths.get(this.filePath).toFile().exists()) {
            throw new RuntimeException ("Operator Key File '" + this.filePath + "' does not exist");
        }

        SystemProperties config = SystemProperties.CONFIG;
        if (config.isProduction()) {
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
