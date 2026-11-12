# Eternal Winter (Fabric, Minecraft 26.2)

Port of [Eternal Winter](https://www.curseforge.com/minecraft/mc-mods/eternal-winter) by ichttt
(originally Forge 1.20.1) to Fabric for Minecraft 26.2. Original license: GPL v3 — put the license text in `LICENSE`.

Enables snow in every biome: temperature 0, no temperature modifier, precipitation on.

## Build
Requires JDK 25.

    ./gradlew build

The jar is in `build/libs/`. Needs Fabric Loader >= 0.19.3 and Fabric API.

## Config
`config/eternalwinter.json` is created on the first start (same options as the old Forge toml):
- `listMode`: `BLACKLIST` (default) or `WHITELIST`
- `biomeList`: biome ids (`river` or `minecraft:river`)
- `downfall`: `-1` keeps the biome's own value, otherwise `0..1`

Changes need a restart (the biome registry is modified while the world loads).
