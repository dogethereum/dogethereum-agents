# Dogethereum Agents

A set of agents:
- [Doge superblock submitter](src/main/java/org/dogethereum/agents/core/SuperblockSubmitterAgent.java): Sends doge superblocks.
- [Superblockchain updater](src/main/java/org/dogethereum/agents/core/SuperblockchainUpdaterAgent.java): Keeps local copy of superblockchain up to date as new doge blocks are created.
  - There's a JSON-RPC over HTTP API to query information about a specific superblock.
- [Superblock challenger](src/main/java/org/dogethereum/agents/core/SuperblockChallengerAgent.java): Challenges invalid superblocks sent by rogue submitters.
- [Superblock defender](src/main/java/org/dogethereum/agents/core/SuperblockDefenderAgent.java): Replies to challenges made by rogue challengers.
- [Doge tx relay](src/main/java/org/dogethereum/agents/core/DogeTxRelayAgent.java): Sends doge lock and unlock txs, so they are validated and relayed to DogeToken ERC20 contract.
- [Sign & Broadcast doge unlock tx](src/main/java/org/dogethereum/agents/core/SignBroadcastDogeUnlockTxAgent.java): Signs and broadcasts doge unlock txs

If you are new to the Dogecoin <-> Ethereum bridge, please check the [docs](https://github.com/dogethereum/docs) repository first.

## Development

### Requirements
- IntelliJ IDEA 2017.2 or superior
- JDK 1.11 or superior
- Dogecoin
- Ganache

### Run ganache
> ganache -l GAS_LIMIT -p 8545
- Replace GAS_LIMIT with the value used in the configuration file
- deploy https://github.com/dogethereum/dogethereum-contracts contracts to ganache
- run scripts/init_contracts_regtest.js


### Run dogecoin
-  Start the dogecoin node in regtest mode
> dogecoind -rpcport=22220 -regtest -rpcuser=RPCUSER -rpcpassword=RPCPASS -datadir=DATADIR
- Mine 1 doge block to "wake up" the dogecoin node in regtest mode
- To verify it is working
> dogecoind -rpcport=22220 -regtest -rpcuser=RPCUSER -rpcpassword=RPCPASS -datadir=DATADIR getinfo


### Java project setup
- Clone this repository
- Open IntelliJ IDEA
- Import project as Maven
- Configuration file
  - Create a custom configuration file by making a copy of the sample configuration file `dogethereum-agents/src/main/resources/dogethereum-agents.sample.conf` and place it anywhere you want, e.g. `/home/yourUser/dogethereum-agents.conf`
  - Edit these entries to point to your computer paths
    - `deployment.path`
    - `data.directory`
    - `operator.private.key.file.path`
  - Note: On windows paths have to be between "" and with a double backslash `\\` as separator. E.g. `data.directory = "D:\\dogethereum-agents\\storage\\data"`
- Create Run configuration
  - In Run/Edit Configurations... add a new "Application" configuration
  - Set parameters like this
    - Name: Main local
    - Main class: `org.dogethereum.agents.Main`
    - VM options: `-Ddogethereum.agents.conf.file=path_to_configuration_file_copy`
  - Note: On windows paths have to use the double backslash as separator.




### Run the agents
- Delete agent data dir (`data.directory` config variable) before each restart just to make sure you are on the safe side.
- On IntelliJ IDEA go to Run/Run...
- Select "Main local" run configuration


## License

MIT License<br/>
Copyright (c) 2021 Coinfabrik & Oscar Guindzberg<br/>
[License](LICENSE)