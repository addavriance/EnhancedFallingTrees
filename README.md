# Enhanced Falling Trees Mod for Fabric, Forge and NeoForge

![preview](https://github.com/addavriance/EnhancedFallingTrees/assets/61160742/7f47785d-4630-4468-9c86-3751233ad5a7)

[![Cloth Config](https://img.shields.io/badge/Cloth%20Config-REQUIRED-1?style=for-the-badge&labelColor=black&color=gold)](https://www.curseforge.com/minecraft/mc-mods/cloth-config)
[![Fabric API](https://img.shields.io/badge/Fabric%20API-REQUIRED%20for%20Fabric-1?style=for-the-badge&labelColor=black&color=gold)](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
---

## Description

Enhanced Falling Trees allows trees to fall realistically when cut down, however, the mod only slightly adds realism and has nothing to do with physics as such, it only simulates a fall for the sake of performance, simulating a falling tree.

Additionally, enjoy enhanced visual and auditory features with particle effects as the tree disappears and a wide range of impact and falling sounds. (inspired by The Forset)

## Showcase

>Default tree fall
>
> ![preview](gifs%2Fpreview.gif)

>Correct response to terrain changes
> 
> ![smooth.gif](gifs%2Fsmooth.gif)

>Cactus fall
> 
> ![cactus_preview.gif](gifs%2Fcactus_preview.gif)

## Features
- **Realistic Tree Falling:** (also bamboo and cactus are supported) Trees no longer simply disappear; they fall over realistically when cut.
- **Particle Effects:** (for trees) Witness a stunning display of particles as trees disappear after falling.
- **Dynamic Sounds:** A wide variety of sounds accompany the falling and impact of trees, enhancing the immersive experience.

---

## Compatibility
### Versions
- Minecraft 1.20, 1.20.6, 1.21.1, 1.21.4–1.21.11, and 26.1.2–26.2.
### Loaders
- Fabric, Forge and NeoForge.
### Mods
- Compatible with other mods that add trees natively.

---


## Known issues
- Sometimes when a tree falls, some part of it may fall into the ground, or physics doesn't work as expected. Why?

  -     This is normal behavior and will probably be fixed soon. 
  **Reminder:** _This is just an imitation of physics using simple functions and formulas, so sometimes its behavior may not be entirely correct._

---

>Please note this mod is a work in progress and features are subject to change.

## Development

The repository builds every supported Minecraft/loader combination from one checkout:

```sh
./gradlew build
./gradlew dist
```

Use -Ptargets to build only selected targets. Selectors accept Minecraft's version number; 26.1 also works as an alias for 26.1.2:

```sh
./gradlew -Ptargets=1.21.9-fabric,26.1.2-fabric build
```

Shared sources live in common/, fabric/, forge/ and neoforge/. Minecraft API differences are in compat/mc/<source-family>/. Resources are in compat/resources/shared/<group>/ and compat/resources/<version>/; each target selects its shared groups in versions/<version>/version.properties, alongside its dependency and loader versions. Minecraft 26.x uses the separate no-remap build in modern/.

