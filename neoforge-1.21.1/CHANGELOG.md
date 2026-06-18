# Changelog

## [1.1.0] - Unreleased

### Added

- Added the NeoBackSlot in-game transform editor.
- Added player preview controls for front, back, left, and right viewing angles.
- Added global and per-item transform profiles.
- Added separate Back Slot and Belt Slot item transform profiles.
- Added transform sync so other players can see configured item positions in multiplayer.
- Added configurable UI offsets for inventory slots and HUD slots.
- Added Fabric 1.20.1 port.
- Added optional Fabric ModMenu config screen without requiring ModMenu as a dependency.
- Added config folder grouping under `config/neobackslot/`.
- Added migration/cleanup for old root-level config files.
- Added item tags for custom Back Slot and Belt Slot compatibility.
- Added broader modded melee and ranged weapon detection, including staffs, wands, spears, guns, blunderbusses, rifles, and similar item IDs.
- Added `soulsweapons:blunderbuss` as a default Back Slot-compatible item.

### Changed

- Updated mod version to `1.1.0`.
- Updated mod metadata and description to describe NeoBackSlot as a standalone implementation.
- Replaced the old icon with the NeoBackSlot icon.
- Inventory Back/Belt slot frames now use vanilla inventory textures so resource packs can style them.
- HUD slot frames now use vanilla hotbar textures so resource packs can style them.
- Renamed and clarified config labels for inventory and HUD slot position offsets.
- Cleaned up unused GUI textures.
- Moved item compatibility tags from the old `backslot` namespace to `neobackslot`.

### Fixed

- Fixed shield and trident rendering tint issues in Back Slot/Belt Slot rendering.
- Fixed the editor player preview following the cursor.
- Fixed missing direct 180-degree back preview angle in the editor.
- Fixed preview panel clicks playing an unnecessary click sound.
- Fixed per-item transform profiles applying across Back Slot and Belt Slot instead of staying slot-specific.
- Fixed All Items transforms not applying when an item had a custom profile.
- Fixed config files being created outside the intended config folder.
- Fixed Fabric HUD and inventory overlay missing from the initial port.
- Fixed shields being accepted by the Fabric Belt Slot.
- Fixed shield unsheath sound using the weapon sheath sound.
- Fixed missing sprite warnings caused by the old `backslot` atlas namespace.

## [1.0.0] - Stable Release

### Added

- First stable NeoBackSlot release for NeoForge 1.21.1.
- Added Back Slot and Belt Slot equipment storage.
- Added keybind swapping for Back Slot and Belt Slot.
- Added multiplayer synchronization for equipped slot items.
- Added third-person rendering for Back Slot and Belt Slot items.
- Added HUD indicators.
- Added inventory screen integration.
- Added custom equip and unequip sounds.
- Added death drop handling and keep-inventory support.
- Added basic grave mod compatibility testing.

### Fixed

- Stabilized dedicated server support.
- Fixed several synchronization and persistence issues found during pre-release testing.
- Fixed slot item loss risks around normal death/drop behavior.

## [0.9.0-alpha] - Alpha

### Added

- Initial alpha implementation of Back Slot equipment storage.
- Added early Belt Slot support.
- Added early keybind swapping.
- Added initial client rendering and HUD experiments.
- Added first pass at inventory persistence.

### Known Issues

- Rendering and compatibility were still experimental.
- Multiplayer synchronization required additional testing.
- Item compatibility rules were limited.
