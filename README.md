# GlassTube
### My attempt to introduce a Youtube application to glasses
Works on both XE and EE
## Features

- search video with basic voice recognition
- chose between 5 search results
- properly display videos
- ability to start,pause and move 30 seconds with swipes
## todo
- custom video controls
- ~~decent looking search results~~
- put something on the main page

## Installation
Google APIs need an [original installation of youtube](https://www.apkmirror.com/apk/google-inc/youtube/youtube-14-07-59-release/youtube-14-07-59-17-android-apk-download/) to work properly, install the last one available for your firmware (4.4 on XE and 8.1 on EE2)
```sh
adb install com.google.android.youtube_14.07.59-1407592300_minAPI19.armeabi-v7a.nodpi._apkmirror.com.apk
adb install youtube_debug.apk
```

used code from
https://github.com/tujson/GlassEcho
https://github.com/MatthewHallberg/GoogleGlassTest
