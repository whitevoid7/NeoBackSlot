# NeoBackSlot Fabric

NeoBackSlot Fabric is the Fabric 1.21.1 port of NeoBackSlot, a standalone Back Slot and Belt Slot equipment mod.

Store weapons and tools on your character's back or belt for quick access. Items are synchronized in multiplayer and visible to other players.

## Requirements

* Minecraft 1.21.1
* Fabric Loader 0.16.0 or newer
* Fabric API

Optional:

* ModMenu, for the in-game config screen

## Features

* Back Slot
* Belt Slot
* Quick slot swapping with keybinds
* HUD indicators
* Inventory GUI integration
* In-game transform editor
* Per-item transform profiles
* Multiplayer synchronization
* Configurable inventory/HUD slot positions
* Modded weapon compatibility helpers

## Controls

| Key | Action         |
| --- | -------------- |
| G   | Swap Back Slot |
| V   | Swap Belt Slot |
| N   | Open Editor    |

## Configuration

Fabric config files are generated under:

```txt
config/neobackslot/
```

Files:

* `common.properties`
* `client.properties`
* `item-transforms.properties`

If ModMenu is installed, NeoBackSlot provides a config screen for common UI options.

## Credits

NeoBackSlot is a standalone implementation and is not a direct port of BackSlot.

This project was originally inspired by the BackSlot mod created by Globox1997, especially the idea of dedicated back and belt equipment slots.

## License

NeoBackSlot is licensed under the GNU General Public License v3.0 (GPLv3).
