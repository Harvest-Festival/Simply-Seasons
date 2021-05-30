![](src/main/resources/assets/harvestfestival/logo.png)

Simply Seasons is the season element of Harvest Festival stripped out in to its own mod. It adds seasons, blizzards and light rain. The mod is compatible with Serene Seasons or Better Weather. You'll still get the parts of this mod like the HUD or varying day length based on season. But the other mod will control the seasons/colours etc.

More information about Simply Seasons and downloads can be found on //TODO

If you have any questions, feel free to join the [Harvest Festival Discord](https://discord.gg/MRZAyze)

Adding Simply Seasons to your buildscript
---
Add to your build.gradle:
```gradle
repositories {
  maven {
    url 'https://maven.joshiejack.uk/'
  }
}

dependencies {
    compile fg.deobf("uk.joshiejack.penguinlib:Penguin-Lib:${minecraft_version}-${penguinlib_version}")
    compile fg.deobf("uk.joshiejack.simplyseasons:Simply-Seasons:${minecraft_version}-${simplyseasons_version}")
}
```

`${$penguinlib_version}` can be found [here](https://maven.joshiejack.uk/uk/joshiejack/penguinlib/Penguin-Lib/)
`${simplyseasons_version}` can be found [here](https://maven.joshiejack.uk/uk/joshiejack/simplyseasons/Simply-Seasons/)