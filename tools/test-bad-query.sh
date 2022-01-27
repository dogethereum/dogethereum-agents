curl -X POST \
     -H 'Content-Type: application/json' \
     -d '{"jsonrpc":"2.0","id":"anid","method":"getSuperblock","params":["rawrrrr"]}' \
     http://localhost:9000 | jq .
