# DNDSync
This App was developed to enable synchronization between the smartphone and the **Galaxy Watch 4** of the
**Do Not Disturb** (*DND*) and **Bedtime mode** from [Digital Wellbeing](https://play.google.com/store/apps/details?id=com.google.android.apps.wellbeing&hl=en_US).
*DND* synchronization is only supported officially if using a Samsung phone with this smartwatch, Bedtime mode synchronization
is only newly available for the newest Pixel Watch 2:
* With this repo you get ***both***!

**Functionalities:**
* *1-way sync* or a *2-way sync* of **DND**, depending on the preferences
* Automatically toggle **Bedtime** mode for the watch whenever it is activated on the phone
    * At night, when I charge my phone, bedtime mode on the phone is enabled and I wanted to sync and enable same mode on the watch
* Automatically toggle **Power Saver** mode in combo with bedtime mode on the watch, whenever bedtime mode is synced from the phone

Majority of the credits goes to [@rhaeus](https://github.com/rhaeus) for the initial developing of this app
and to [@DreadedLama](https://github.com/DreadedLama) for the initial developing of a better bedtime mode implementation!

**Tested on Nothing Phone (1) (*Android 13*) paired with a Galaxy Watch 4 (*40mm*, *Wear OS 4.0*)**

## Setup

***Manual installation is required. The use of ADB is required. (*Don't worry, it's very easy!*)***

* Download the latest `.apk` files from the ['Releases' section](https://github.com/Silleellie/dnd-bedtime-sync/releases) (`dndsync-mobile.apk` and `dndsync-wear.apk`)
* Be sure to enable notifications for Bedtime mode of the Digital Wellbeing app on your phone (*They are by default*)
    * This app knows that bedtime mode is activated when its notification pops up (since there's no public API for the *Digital Wellbeing* app)

### Phone

<img src="/images/mobile.png" width="300">

1. Install the app `dndsync-mobile.apk` on the phone via *adb*
    * Enable `USB Debugging` in the *Developer Options* of your phone and the connect it to the PC
    * Run `adb install dndsync-mobile.apk`
2. Disconnect the phone from the PC
    * Disable `USB Debugging` from the *Developer Options* of your phone
3. Open the app and grant the permission for *DND Access* and *Bedtime Access* by clicking on the menu entry *DND-Bedtime Permission*. This will open the permission screen.
    * This Permission is required so that the app can *read/write* DND state and *read* Bedtime mode. Without this permission, the sync will not work.
4. Go back on the app and check that `DND-Bedtime Permission` say **DND-Bedtime access granted** (*you may need to tap on the menu entry for it to update*)
5. Turn on the switch for the mode that you'd like to sync (*you can enable both, of course*):
    * With the ***Sync DND state to watch*** switch you can enable and disable the sync for *DND* mode.
      If enabled, a *DND* change on the phone will lead to *DND* change on the watch.
    * With the ***Sync Bedtime mode to watch*** switch you can enable and disable the sync for bedtime mode.
      If enabled, when *Bedtime mode* is *enabled/disabled/paused* on the phone, it will be *enabled/disabled/paused* on the watch

### Watch
<p float="left">
  <img src="/images/wear_1.png" width="200" />
  <img src="/images/wear_2.png" width="200" /> 
  <img src="/images/wear_3.png" width="200" />
  <img src="/images/wear_4.png" width="200" />
</p>

Setting up the watch is a bit more *tricky* since the watch OS lacks the permission screen for DND access,
but the permission needed can be **easily set via ADB***!

Note: This is only tested on my **Galaxy Watch 4** and it might not work on other devices!
1. Connect the watch to your computer via **adb** (watch and computer have to be in the *same network!*)
    * Enable Developer Options: Go to `Settings -> About watch -> Software -> tap the Software version 5 times -> developer mode is on (you can disable it in the same way)`
    * Enable `ADB debugging` and `Debug over WIFI` (in `Settings -> Developer Options`)
    * Click on `Pair new device`
    * Note the watch IP address and port, something like `192.168.0.100:5555` 
    * Note also the pair key, something like `123456`
    * Pair the watch with `adb pair 192.168.0.100:5555 123456` (***insert your value!***)
    * Now you will be brought back to the *Debug over WIFI* menu, note the **new port**, something like `:6666`
    * Connect to the watch with `adb connect 192.168.0.100:6666` (The ip address stays the same, the port not!)
2. Install the app `dndsync-wear.apk` on the watch
    * Run `adb install dndsync-wear.apk`
3. Grant permission for **DND access** (*This allows the app to listen to DND changes and to change the DND setting*)
    * Run `adb shell cmd notification allow_listener de.rhaeus.dndsync/de.rhaeus.dndsync.DNDNotificationService`  
4. Grant permission for **Secure Setting access** (*This allows the app to change BedTime mode setting on the watch*)
    * Run `adb shell pm grant de.rhaeus.dndsync android.permission.WRITE_SECURE_SETTINGS`
5. Open the app on the watch, scroll to the permission section and check if both `DND Permission` 
   and `Secure Settings Permission` say ***Granted*** (*you may need to tap on the menu entries for them to update*)
6. ***IMPORTANT: Disable `ADB debugging` and `Debug over WIFI`, because these options drain the battery!***
7. Turn on the switch for the mode that you'd like to sync (*you can enable all of them, of course*)
    * If you enable the setting ***Sync DND*** in the App, a DND change on the watch will lead to a DND change on the phone
    * If you enable the setting ***Bedtime Mode*** in the App, the watch will copy the bedtime mode status of the phone, it's a 1-way sync
    * If you enable the setting ***Power Saver Mode*** in the App, the watch will turn on power saver mode whenever the *Bedtime Mode* is synced from the phone
    * If you enable the setting ***Vibration*** in the App, the watch will vibrate whenever it receives a sync request from the phone

# Note
If you are unable to "Allow Notification Access" to the mobile app and it is faded, go the apps section in Settings, open DNDSync app, click 3 dots on top right and grant "Allow Restricted Settings" access.
Now you'll be able to grant the Notification access to mobile app.
