#!/bin/bash

echo '*** Starting Dev DNS ***'
sudo dnsmasq \
    --no-hosts \
    --listen-address 127.0.0.1 \
    --keep-in-foreground \
    --log-queries \
    --address /mikeandwan.us/10.0.2.2
