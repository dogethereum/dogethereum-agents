curl -X POST \
     -H 'Content-Type: application/json' \
     -d '{"jsonrpc":"2.0","id":"anid","method":"getSuperblock","params":{"superblockId":"0x3e519ab3095c3f23742d3fcfe92351d78b2859b502985a963010999466237588"}}' \
     http://localhost:9000 | jq .
