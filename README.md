# NeoBackSlot

NeoBackSlot is a lightweight Back Slot and Belt Slot equipment mod for NeoForge 1.21.1.

Store weapons and tools on your character's back or belt for quick access. Items are fully synchronized in multiplayer and visible to other players.

## Features

* Back Slot
* Belt Slot
* Quick weapon swapping with keybinds
* Multiplayer synchronization
* Dedicated server support
* HUD indicators
* Inventory GUI integration
* Custom equip and unequip sounds
* Death drop support
* Basic grave mod compatibility (YIGD tested)
* Config support
* Lightweight and dependency-free

## Installation

Install NeoBackSlot on both the server and all clients.

Requirements:

* NeoForge 1.21.1

No additional dependencies are required.

## Controls

| Key | Action         |
| --- | -------------- |
| G   | Swap Back Slot |
| V   | Swap Belt Slot |

## Configuration

A configuration file will be generated automatically:

```txt
config/neobackslot-common.toml
```

Available options:

```toml
[death]
keepItemsOnDeath = false
```

When enabled, Back Slot and Belt Slot items will remain equipped after respawn.

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
* Dedicated Server
* 300+ mod environment
* YIGD

## Known Issues

* Modded shields may require additional transform adjustments.
* Grave restoration behavior may vary between grave mods.
* Back Slot and Belt Slot items may be lost when claiming a grave with a completely full inventory.

## Credits

Original BackSlot:
- Globox1997

NeoBackSlot:
- whitevoid7

## License

NeoBackSlot is licensed under the GNU General Public License v3.0 (GPLv3).

This project is heavily inspired by and derived from the original BackSlot mod by Globox1997.
