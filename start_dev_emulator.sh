#!/bin/bash
SDK=/home/mmorano/Android/Sdk
EMU="${SDK}/emulator/emulator"
ADB="${SDK}/platform-tools/adb"

echo '*** Starting Emulator ***'
$EMU -avd Pixel_4a_API_30 -no-snapshot -dns-server 192.168.1.1 &

#echo '*** Wait for Emulator to Start ***'
sleep 20

#echo '*** Mount Emulator ***'
#$ADB root
#$ADB remount
#while [ $? -ne 0 ]
#do
#   sleep 1
#   echo "  - retrying adb remount..."
#   $ADB remount
#done

#echo '*** Hosts File ***'
#$ADB push ~/git/maw_photos_android/dev_etc_hosts /system/etc/hosts

#if [ $? -ne 0 ]
#then
# echo "  - failed to push hosts file to android device"
#else
# echo "  - hosts file pushed successfully"
#fi

echo '*** Certificate Authority ***'

echo '  - copying public cert to emulator'
$ADB push ~/git/mikeandwan.us/maw_certs/ca/ca.crt /sdcard/mawdev.public.crt

echo '  - Add certificate to emulator (must be performed manually within the emulator):'
echo '    1. Settings > Security > Credential Storage'
echo '    2. Click Install from SD card'
echo '    3. Select mawdev.public.crt in the resulting file explorer'
