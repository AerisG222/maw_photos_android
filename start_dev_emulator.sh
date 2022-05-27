#!/bin/bash
SDK=/home/mmorano/Android/Sdk
EMU="${SDK}/emulator/emulator"
ADB="${SDK}/platform-tools/adb"

echo '*** Starting Emulator ***'
$EMU -avd Pixel_4a_API_30 -no-snapshot -dns-server 127.0.0.1 &

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
