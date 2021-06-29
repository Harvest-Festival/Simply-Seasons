<img src="https://harvestfestivalwiki.com/images/7/7a/Simply-Seasons-Logo.svg" width="70%">

[![Discord](https://img.shields.io/discord/227497118498029569?style=plastic&colorB=7289DA&logo=discord&logoColor=white)](http://discord.gg/0vVjLvWg5kyQwnHG) &nbsp; ![GitHub](https://img.shields.io/github/license/Harvest-Festival/Simply-Seasons?color=%23990000&style=plastic) &nbsp; ![Jenkins](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fjenkins.joshiejack.uk%2Fjob%2FSimply-Seasons%2F&style=plastic) &nbsp; ![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmaven.joshiejack.uk%2Fuk%2Fjoshiejack%2Fsimplyseasons%2FSimply-Seasons%2Fmaven-metadata.xml&style=plastic) &nbsp; [![Curseforge](http://cf.way2muchnoise.eu/full_497468_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/simply-seasons)

Simply Seasons is the season element of Harvest Festival stripped out in to its own mod. It adds seasons, blizzards and fog. The mod is compatible with Serene Seasons or Better Weather. You'll still get the parts of this mod like the HUD, seasonal music option or varying day length based on season. But the other mod will control the seasons/colours etc or the weather where applicable.

More information about Simply Seasons can be found at https://harvestfestivalwiki.com/Simply_Seasons

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