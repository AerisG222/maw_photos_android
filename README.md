[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/AerisG222/maw_photos_android/blob/master/LICENSE.md)

# maw_photos_android

Android version of the photos section of mikeandwan.us

# Running in Emulator

emulator can be found here: `/home/mmorano/Android/Sdk/emulator`
adb can be found here: `/home/mmorano/Android/Sdk/platform-tools`

1 Start emulator from command line

- `./emulator -avd Nexus_5X_API_23 -writable-system`

2 Mount storage in the emulator

- `./adb remount`

3 Copy the dev hosts file and cert to emulator

- `./adb push ~/git/maw_photos_android/dev_etc_hosts /system/etc/hosts`
- `./adb push ~/git/mikeandwan.us/maw_certs/ca/ca.crt /sdcard`

4 Install cert

- Go to Settings / Security in emulator
- If not enabled, setup a lock screen PIN, which seems to be a requirement to load custom certs
- Under `Credential Storage`, click on `Install from SD card`
- Point at the file uploaded above (ca.crt), and give it a name

Once the above steps are complete, you should now be able to debug the application while all
services are running on a single machine.

## License

MIT
