package org.sysethereum.agents.service;

import org.sysethereum.agents.core.syscoin.Keccak256Hash;

import java.util.List;

public class ChallengeReport {

    public final boolean isAtLeastOneMine;
    public final List<Keccak256Hash> challenged;

    public ChallengeReport(boolean isAtLeastOneMine, List<Keccak256Hash> challenged) {

        this.isAtLeastOneMine = isAtLeastOneMine;
        this.challenged = challenged;
    }
}