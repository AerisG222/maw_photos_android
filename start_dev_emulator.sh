#!/bin/bash
SDK=/home/mmorano/Android/Sdk
EMU="${SDK}/emulator/emulator"
ADB="${SDK}/platform-tools/adb"
#DNS_SERVER_IP=$(hostname -I | cut -d ' ' -f 1)
DNS_SERVER_IP=127.0.0.1

echo "*** Starting Emulator with DNS server: ${DNS_SERVER_IP} ***"
$EMU -avd Pixel_6_API_33 -no-snapshot -dns-server "${DNS_SERVER_IP}" &

echo '*** Wait for Emulator to Start ***'
sleep 20

echo '*** Certificate Authority ***'
echo '  - copying public cert to emulator'
#$ADB push ~/git/mikeandwan.us/maw_certs/ca/ca.crt /sdcard/mawdev.public.crt
$ADB push ~/maw_dev/certs/ca/ca.crt /sdcard/mawdev.public.crt

echo '  - Add certificate to emulator (must be performed manually within the emulator):'
echo '    1. Settings > Security > Credential Storage'
echo '    2. Click Install from SD card'
echo '    3. Select mawdev.public.crt in the resulting file explorer'
