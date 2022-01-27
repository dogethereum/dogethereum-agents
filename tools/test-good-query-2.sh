curl -X POST \
     -H 'Content-Type: application/json' \
     -d '{"jsonrpc":"2.0","id":"anid","method":"getSuperblock","params":["0x5b3a8b68e3b511a4beda25535f6c26431f24cc6e379e0d631b18c0491afc309c"]}' \
     http://localhost:9000 | jq .
