# Sysethereum Agents

A set of agents:
- Syscoin superblock submitter (`SyscoinToEthClient.java`): Sends syscoin superblocks.
- Superblock challenger (`SuperblockChainClient.java`): Challenges invalid superblocks sent by rogue submitters.
- Superblock defender  (`SuperblockDefenderClient.java`): Replies to challenges made by rogue challengers.

If you are new to the Syscoin <-> Ethereum bridge, please check the [docs](https://github.com/syscoin/sysethereum-docs) repository first.

## Development

### Requirements

- JDK 11 or superior
- Ganache
- Truffle

### Run Ganache (For testing only)

```bash
ganache -l GAS_LIMIT -p 8545
```

1. Replace GAS_LIMIT with the value used in the configuration file
1. Deploy [https://github.com/syscoin/sysethereum-contracts](https://github.com/syscoin/sysethereum-contracts) contracts to Ganache
1. Run `scripts/initialiseForAgent.sh` 


### Run Syscoin daemon

1.  Start the syscoin node in mode:
    ```bash
    syscoind -datadir=DATADIR
    ```
1. Mine 1 syscoin block to "wake up" the syscoin node in regtest mode
1. Verify it is working 
    ```bash
    syscoin-cli -datadir=DATADIR getblockchaininfo
    ```    

### How to setup project in IntelliJ IDEA
 
- Clone this repository
- Open IntelliJ IDEA
- Import project as Maven
- Modify configuration file `data/sysethereum-agents.conf` (see section below)   
- Create Run configuration
  - In Run/Edit Configurations... add a new "Application" configuration
  - Set parameters like this
    - Name: `Main local`
    - Main class: `org.sysethereum.agents.Main`
    - VM options: `-Dsysethereum.agents.conf.file=path_to_configuration_file -Dhttps.protocols=TLSv1.2,TLSv1.1,TLSv1`
  - Note: Use double backslash as separator on Windows

### Configuration

Sample configuration file is located in `data/sysethereum-agents.conf`. We recommend to make a copy of the file and use that copy when running agents.

The following properties need to be properly set:

- Edit `data.directory` to point to your syscoin data directory path
- Edit these entries related to your accounts and credentials for sending/defending superblocks:
  - `general.purpose.and.send.superblocks.address`
  - `general.purpose.and.send.superblocks.unlockpw`
- Edit these entries related to your accounts and credentials for challenging invalid superblocks:
  - `syscoin.superblock.challenger.address` 
  - `syscoin.superblock.challenger.unlockpw`

**Note:** Windows paths have to be enclosed by quote signs (`"value"`) and with double backslash `\\` as separator - e.g. `data.directory = "D:\\sysethereum-agents\\storage\\data"`

#### Email alert

Agent application is able to send a notification email when your superblock is challenged or when any superblock is challenged. 
The feature is disabled by default. However, you can enable it in [data/sysethereum-agents.conf](data/sysethereum-agents.conf) in `agent.mailer` section.

Warning: Predefined Google settings in the configuration file may not work out of box if you do not have allowed less secure apps access 
(please refer to https://myaccount.google.com/lesssecureapps and https://support.google.com/a/answer/6260879?hl=en). Ideally, you may use some other 
SMTP server where you whitelist Sysethereum Agents application to make sure any email notification is always delivered. 

### Run the agents via IDEA

- Delete agent data directory (`data.directory` config variable) before each restart just to make sure you are on the safe side
- In IntelliJ IDEA go to Run/Run... 
- Select "Main local" run configuration

### Run the agents in terminal

```bash
mvn compile && mvn exec:java -Dsysethereum.agents.conf.file=data/sysethereum-agents.conf -Dhttps.protocols=TLSv1.2,TLSv1.1,TLSv1 
```

## License

[MIT License](LICENSE)<br/>
Copyright (c) 2019 Jagdeep Sidhu<br/>
Copyright (c) 2018 Coinfabrik & Oscar Guindzberg<br/>