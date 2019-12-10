package org.sysethereum.agents.core.bridge.battle;
import java.io.Serializable;

public class NewTokenFreezeEvent implements Serializable {
    public String ethTXID;
    public Long ethBlockNumber;
    public Integer bridgeTransferId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewTokenFreezeEvent event = (NewTokenFreezeEvent) o;
        return bridgeTransferId.equals(event.bridgeTransferId);
    }

    @Override
    public int hashCode() {
        return bridgeTransferId;
    }
}
