# PaperCraft

This is the public repository for [PaperCraft](https://play.google.com/store/apps/details?id=cordproject.lol.papercraft), a Material Design inspired space shooter for your Android Wear watch.
Now officially an Android Experiment :)

## Overview

The app consists of three main modules:

**wear**
- The core game module for the wear app. 
- Controls the main game logic and periodically sends achievement/high score data
over to the phone app. If you're curious on how to set up a game loop with dynamic drawing on Android, this is a good resource.
The [Canvas API](http://developer.android.com/reference/android/graphics/Canvas.html) is your friend :)

**mobile**
- The companion phone app. 
- This app communicates with any connected watches that are running papercraft, and it is
  the main interface between you and Google Play Games services. It sends and receives data to/from the wear app
  via the [Wearable.DataApi layer](http://developer.android.com/training/wearables/data-layer/data-items.html). Through this app, one can view the leaderboards and the in-game achievements.
- This app, in general, attempts to connect to Google Play Games services and asks the player to log in. *However,
  out of the box, this will not happen because we've removed our proprietary Google Play Games app IDs, leaderboard IDs, 
  achievement IDs, etc from the public repository. If you'd like to see how this works, we encourage you to replace 
  those strings with your own Google Play Games project ID, leaderboard IDs, and achievement IDs*.

**shared**
- A utility library that is shared between the mobile and the wear modules. 
- It is mostly responsible for tracking 
  achievements and generating papery bitmap elements. 

## License

    Copyright 2015 Cord Project Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
