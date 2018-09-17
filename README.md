[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/AerisG222/maw_photos_android/blob/master/LICENSE.md)

# maw_photos_android

Android version of the photos section of mikeandwan.us

# Running in Emulator

emulator can be found here: `/home/mmorano/Android/Sdk/emulator`
adb can be found here: `/home/mmorano/Android/Sdk/platform-tools`

1 Start emulator from command line

- `./emulator -avd Nexus_5X_API_23 -writable-system`

2 Mount storage in the emulator

- `./adb root`
- `./adb remount`

3 Copy the dev hosts file to the emulator

- `./adb push ~/git/maw_photos_android/dev_etc_hosts /system/etc/hosts`

4 Add dev CA public key to `res/raw/debug_cas`

- This is referenced in network_security_config to be applied for debug builds

Once the above steps are complete, you should now be able to debug the application while all
services are running on a single machine.

## License

MIT
