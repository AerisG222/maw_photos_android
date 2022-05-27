#!/bin/bash

#    --keep-in-foreground \

echo '*** Starting Dev DNS ***'
sudo dnsmasq \
    --no-hosts \
    --listen-address 127.0.0.1 \
    --no-daemon \
    --log-queries \
    --log-facility - \
    --address /mikeandwan.us/10.0.2.2
