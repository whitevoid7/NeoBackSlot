# NeoBackSlot

[![Modrinth](https://img.shields.io/badge/Download-Modrinth-00AF5C?logo=modrinth&logoColor=white)](https://modrinth.com/mod/neobackslot)
[![Report Issues](https://img.shields.io/badge/Issues-GitHub-red?logo=github)](https://github.com/whitevoid7/NeoBackSlot/issues)

Download the latest release on Modrinth:
https://modrinth.com/mod/neobackslot

Report bugs or compatibility issues:
https://github.com/whitevoid7/NeoBackSlot/issues

NeoBackSlot is a standalone Back Slot and Belt Slot equipment mod.

Store weapons and tools on your character's back or belt for quick access. Items are synchronized in multiplayer and visible to other players.

## Features

* Back Slot
* Belt Slot
* Quick weapon swapping with keybinds
* Multiplayer synchronization
* Dedicated server support
* HUD indicators
* Inventory GUI integration
* In-game transform editor
* Per-item transform profiles
* Custom equip and unequip sounds
* Death drop support
* Basic grave mod compatibility
* Config support

## Installation

Install NeoBackSlot on both the server and all clients.

Supported loaders:

* NeoForge 1.21.1
* Fabric 1.20.1

## Default Controls

NeoForge:

| Key | Action         |
| --- | -------------- |
| G   | Swap Back Slot |
| V   | Swap Belt Slot |
| N   | Open Editor    |

Fabric:

| Key | Action         |
| --- | -------------- |
| G   | Swap Back Slot |
| V   | Swap Belt Slot |
| N   | Open Editor    |

## Configuration

Config files are generated under:

```txt
config/neobackslot/
```

NeoForge uses `.toml` files. Fabric uses `.properties` files.

Important NeoForge options include:

```toml
[death]
keepItemsOnDeath = false
```

When enabled, Back Slot and Belt Slot items remain equipped after respawn.

## Multiplayer

NeoBackSlot supports:

* Dedicated servers
* Multiplayer synchronization
* Player-to-player rendering
* Shared equip and unequip sounds

Both server and client must have the mod installed.

## Compatibility

Tested with:

* NeoForge 1.21.1
* Fabric 1.20.1
* Dedicated server
* Large modpack environments
* YIGD

## Known Issues

* Some modded items may require transform adjustments.
* Grave restoration behavior may vary between grave mods.
* Back Slot and Belt Slot items may be lost when claiming a grave with a completely full inventory.

## Credits

NeoBackSlot is a standalone implementation and is not a direct port of BackSlot.

This project was originally inspired by the BackSlot mod created by Globox1997, especially the idea of dedicated back and belt equipment slots.

## License

NeoBackSlot is licensed under the GNU General Public License v3.0 (GPLv3).
