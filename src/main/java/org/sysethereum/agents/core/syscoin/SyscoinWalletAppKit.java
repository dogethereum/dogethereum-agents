package org.sysethereum.agents.core.syscoin;

import org.bitcoinj.core.Context;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.wallet.KeyChainGroupStructure;

import javax.annotation.Nullable;
import java.io.File;

public class SyscoinWalletAppKit extends WalletAppKit {

    public SyscoinWalletAppKit(
            Context context,
            Script.ScriptType preferredOutputScriptType,
            @Nullable KeyChainGroupStructure structure,
            File directory,
            String filePrefix
    ) {
        super(context, preferredOutputScriptType, structure, directory, filePrefix);
    }

    @Override
    protected void onSetupCompleted() {
        Context.propagate(context);
        vPeerGroup.setDownloadTxDependencies(0);
    }

    @Override
    public void shutDown() throws Exception {
        super.shutDown();
    }

    @Override
    protected BlockStore provideBlockStore(File file) throws BlockStoreException {
        return new AltcoinLevelDBBlockStore(context, getChainFile());
    }

    protected File getChainFile() {
        return new File(directory, "chain");
    }
}
