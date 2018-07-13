# Dogethereum Agents

A set of agents:
- Doge superblock submitter: Sends doge superblocks to DogeRelay.
- Superblock challenger: Challenges invalid superblocks sent by rogue submitters.
- Superblock defender: Replies to challenges made by rogue challengers.
- Doge tx submitter: Sends doge lock and unlock txs to DogeRelay so they are validated and relayed to DogeToken ERC20 contract.
- Operator agent: Signs and broadcasts doge unlock txs
- Oracle agent: Informs the doge/eth price to DogeToken
