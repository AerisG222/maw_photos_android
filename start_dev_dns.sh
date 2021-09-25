#!/bin/bash

echo '*** Starting Dev DNS ***'
dnsmasq --no-hosts \
        --listen-address 127.0.0.1 \
        --port 5300 \
        --keep-in-foreground \
        --log-queries \
        --address /mikeandwan.us/10.0.2.2
