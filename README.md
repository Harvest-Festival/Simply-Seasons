![](src/main/resources/assets/harvestfestival/logo.png)

Simply Seasons is the season element of Harvest Festival stripped out in to its own mod. It adds seasons, blizzards and light rain. The mod is compatible with Serene Seasons. You can use their seasons if you prefer and just use this mod for the weather or hud!

More information about Simply Seasons and downloads can be found on //TODO

If you have any questions, feel free to join the [Harvest Festival Discord](https://discord.gg/MRZAyze)

Adding Simply Seasons to your buildscript
---
Add to your build.gradle:
```gradle
repositories {
  maven {
    // url of the maven that hosts simplyseasons files
    url //TODO
  }
}

dependencies {
  // compile against Simply Seasons
  deobfCompile "uk.joshiejack.simplyseasons:Simply-Seasons:${minecraft_version}-${simplyseasons_version}"
}
```

`${minecraft_version}` & `${simplyseasons_version}` can be found //TODO, check the file name of the version you want.