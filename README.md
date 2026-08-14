# Blocks to Stacks

![Minecraft Version](https://img.shields.io/badge/Minecraft-26.2-brightgreen)
![Mod Loader](https://img.shields.io/badge/Fabric-0.19.3+-orange)
![Environment](https://img.shields.io/badge/Environment-Client_only-blueviolet)
![License](https://img.shields.io/badge/License-CC0_1.0-blue)

**Blocks to Stacks** is a lightweight, client-side utility mod for Minecraft that handles block math directly in-game. It eliminates the need to tab out to a calculator when planning large builds or organizing storage rooms by instantly converting raw block counts into manageable Minecraft stacks.

<img width="659" height="269" alt="image" src="https://github.com/user-attachments/assets/07253843-da76-4152-bd72-0816bfb7ae4b" />

## 🚀 Usage

The mod registers a single client-side command:

```
/b2s <blocks> [<stack-size>]
```

* **Default calculation:** `/b2s 1234` splits the count into 64-block stacks.
* **Custom stack sizes:** `/b2s 150 16` handles items like ender pearls, snowballs or signs. Tab completion suggests `64`, `16` and `1` with a reminder of what stacks that way.
* **Just `/b2s`** prints the usage, and the lines are clickable to drop the command into your chat box.

The result comes back as a compact block:

```
│ Blocks to Stacks
│ 1,234 blocks  ·  64 per stack
│ 19 stacks + 18 blocks  [copy]
│ 20 slots  ·  fits in one shulker box
```

The `[copy]` button puts the plain result on your clipboard, and the storage line tells you how many shulker boxes or double chests it takes to actually hold the pile.

Because the command logic and math are processed locally and printed strictly to your personal chat, this mod is **100% client-side**. It works on any multiplayer server, and the server does not need the mod installed.

> Upgrading from 1.x? The command was `/calc stacks <blocks> [<stack-size>]` and is now `/b2s <blocks> [<stack-size>]`.

## 📥 Installation (for players)

1. Download the latest `.jar` file from [Modrinth](https://modrinth.com/mod/blocks-to-stacks).
2. Install the [Fabric Loader](https://fabricmc.net/) for Minecraft 26.2.
3. Place the downloaded `.jar` and the required [Fabric API](https://modrinth.com/mod/fabric-api) into your `.minecraft/mods` folder.
4. Launch the game!

## 🛠️ Building from source (for developers)

```bash
git clone https://github.com/junaidsultanxyz/blocks-to-stacks.git
cd blocks-to-stacks
./gradlew build          # gradlew build on Windows
```

The compiled mod lands in `build/libs/`.

Minecraft 26.2 runs on **Java 25**. You do not need it installed — the project ships
a daemon JVM criteria file, so Gradle downloads a matching JDK on the first build.

### Project layout

The Minecraft-facing code is kept small and separate from the logic, so that game
updates touch as few files as possible:

```
src/client/java/com/junaidsultan/blockstostacks/
├── BlocksToStacksClient.java        # entrypoint, registers the command
├── command/BlocksToStacksCommand.java   # Brigadier wiring
├── math/StackBreakdown.java         # the arithmetic — no Minecraft imports
└── text/ResultRenderer.java         # chat formatting
```

Every version number lives in `gradle.properties` and is templated into
`fabric.mod.json` at build time. See [PORTING.md](PORTING.md) for the checklist
when a new Minecraft version lands.

## 📄 License

This project is dedicated to the public domain under the CC0-1.0 License. You are free to use, modify, and distribute this code without restriction.
