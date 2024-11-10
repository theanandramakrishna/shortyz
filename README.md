Shortyz Crosswords 
==================
[![Build Status](https://travis-ci.org/kebernet/shortyz.svg?branch=master)](https://travis-ci.org/kebernet/shortyz)
[![Waffle.io - Columns and their card count](https://badge.waffle.io/kebernet/shortyz.svg?columns=all)](https://waffle.io/kebernet/shortyz)

Shortyz is a crossword puzzle application for Android that downloads a number of
free puzzles, and with a paid subscription, the New York Times.

Downloading
-----------

Several services host Shortyz APKs:

* [Google Play Store](https://play.google.com/store/apps/details?id=com.totsp.crossword.shortyz&hl=en)
* [F-Droid](https://f-droid.org/repository/browse/?fdid=com.totsp.crossword.shortyz) (out of date)
* [Shortyz home page](http://www.kebernet.net/Home/projects/shortyz)

Compilation
-----------

See [issue #46](https://github.com/kebernet/shortyz/issues/46).


Project Structure
-----------------

  * ./app The Android App.
  * ./puzzlib A platform independent Java library for dealing with Across Lite and other puzzle formats
  * ./web A GWT/AppEngine web app for Shortyz that has fallen into disrepair.
  * ./gfx Art assets related to the Play Store publishing

Testing
-------
Download system images 
```    $ sdkmanager "system-images;android-29;google_apis;x86_64"

Create an avd
```    $ avdmanager create avd -n p6 -k "system-images;android-29;google_apis;x86_64" -d "pixel_6"

Start the emulator 
```    $ emulator @p6

Run automated tests
```    $ ./gradlew connectedCheck

(Optionally) To manually load the apk and test apk into the emulator using adb and run tests
```    $ adb -e install app/build/outputs/apk/debug/app-debug.apk
```    $ adb -e install app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
```    $ adb shell
```    $ am instrument -w com.totsp.crossword.shortyz.test/androidx.test.runner.AndroidJUnitRunner

License
-------

Copyright (C) 2010-2016 Robert Cooper

Licensed under the GNU General Public License, Version 3
