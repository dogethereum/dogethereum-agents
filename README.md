# Sysethereum Agents

A set of agents:
- Syscoin superblock submitter (SyscoinToEthClient.java): Sends syscoin superblocks.
- Superblock challenger (SuperblockChainClient.java): Challenges invalid superblocks sent by rogue submitters.
- Superblock defender  (SuperblockDefenderClient.java): Replies to challenges made by rogue challengers.

If you are new to the Syscoin <-> Ethereum bridge, please check the [docs](https://github.com/syscoin/sysethereum-docs) repository first.

## Development

### Requirements
- IntelliJ IDEA 2017.2 or superior
- JDK 1.8  or superior
- Ganache
- Truffle

### Run ganache (For testing only)
> ganache -l GAS_LIMIT -p 8545
- Replace GAS_LIMIT with the value used in the configuration file
- deploy https://github.com/syscoin/sysethereum-contracts contracts to ganache
- run scripts/initialiseForAgent.sh 


### Run syscoin
-  Start the syscoin node in mode
> syscoind -datadir=DATADIR
- Mine 1 syscoin block to "wake up" the syscoin node in regtest mode
- To verify it is working 
> syscoin-cli -datadir=DATADIR getblockchaininfo


### Java project setup
- Clone this repository
- Open IntelliJ IDEA
- Import project as Maven
- Configuration file
  - Create a custom configuration file by making a copy of the sample configuration file sysethereum-agents/src/main/resources/sysethereum-agents.sample.conf and place it anywhere you want, e.g. /home/yourUser/sysethereum-agents.conf
  - Edit these entries related to your accounts and credentials for sending/defending Superblocks
    - general.purpose.and.send.superblocks.address
    - general.purpose.and.send.superblocks.unlockpw
  - Edit these entries related to your accounts and credentials for challenging invalid Superblocks
    - syscoin.superblock.challenger.address 
    - syscoin.superblock.challenger.unlockpw
  - Edit these entries to point to your syscoin data directory path
    - data.directory
  - Note: On windows paths have to be between "" and with a double backslash \\ as separator. E.g. data.directory = "D:\\sysethereum-agents\\storage\\data"  
- Create Run configuration
  - In Run/Edit Configurations... add a new "Application" configuration
  - Set parameters like this
    - Name: Main local
    - Main class: "org.sysethereum.agents.Main"
    - VM options: -Dsysethereum.agents.conf.file=path_to_configuration_file_copy
  - Note: On windows paths have to use the double backslash as separator.




### Run the agents
- Delete agent data dir (data.directory config variable) before each restart just to make sure you are on the safe side.
- On IntelliJ IDEA go to Run/Run... 
- Select "Main local" run configuration


## License

MIT License<br/>
Copyright (c) 2019 Jagdeep Sidhu<br/>
Copyright (c) 2018 Coinfabrik & Oscar Guindzberg<br/>
[License](LICENSE)
