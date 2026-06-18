# NeoBackSlot Pre-release Testing Checklist

Use this checklist before publishing an editor/customization update. Test with a fresh client config when possible, then repeat with an existing config to catch migration issues.

## Build

- [ ] `./gradlew build` completes successfully.
- [ ] Client starts with `./gradlew runClient`.
- [ ] Dedicated server starts with the mod installed.
- [ ] A client can join the dedicated server with the mod installed.

## Basic Gameplay

- [ ] Back Slot accepts valid weapons/tools.
- [ ] Belt Slot accepts valid weapons/tools.
- [ ] Invalid items cannot be placed into Back/Belt slots.
- [ ] `G` swaps the Back Slot item with the held item.
- [ ] `V` swaps the Belt Slot item with the held item.
- [ ] Swap sound plays once and at the correct location.
- [ ] HUD icons update after slot changes.
- [ ] Inventory overlay updates after slot changes.

## Multiplayer Sync

- [ ] Player A can see Player B's Back Slot item.
- [ ] Player A can see Player B's Belt Slot item.
- [ ] Swapping Back Slot updates for nearby players.
- [ ] Swapping Belt Slot updates for nearby players.
- [ ] Joining late syncs existing Back/Belt items correctly.
- [ ] Leaving and rejoining preserves server-side slot state.

## Rendering

- [ ] Vanilla sword renders correctly in Back Slot.
- [ ] Vanilla pickaxe renders correctly in Back Slot.
- [ ] Vanilla shield renders with correct colors.
- [ ] Vanilla trident renders with correct colors in Back Slot.
- [ ] Vanilla trident renders with correct colors in Belt Slot.
- [ ] Modded weapon renders without wrong tinting.
- [ ] Modded shield renders without wrong tinting.
- [ ] Rendering looks correct with chest armor equipped.
- [ ] Rendering looks correct with no chest armor equipped.
- [ ] Rendering remains stable while walking, sprinting, jumping, sneaking, swimming, and riding.

## Editor

- [ ] Editor opens from the configured keybind or command.
- [ ] Player preview renders correctly.
- [ ] Player preview does not follow the mouse.
- [ ] Preview direction buttons work: `Left`, `Right`, `Front`, `Back`.
- [ ] Clicking the preview box does not play a click sound.
- [ ] `Save` persists changes.
- [ ] `Cancel` restores unsaved changes.
- [ ] `Reset Parameter` resets only the selected parameter.
- [ ] `Reset Back Defaults` resets global Back Slot values.
- [ ] `Reset Belt Defaults` resets global Belt Slot values.
- [ ] Text does not overlap at common resolutions.

## Transform Profiles

- [ ] `All Items` Back Slot changes affect all Back Slot items without custom Back profiles.
- [ ] `All Items` Belt Slot changes affect all Belt Slot items without custom Belt profiles.
- [ ] `This Item` Back Slot changes affect only that item ID in Back Slot.
- [ ] `This Item` Belt Slot changes affect only that item ID in Belt Slot.
- [ ] The same item ID can have different Back Slot and Belt Slot profiles.
- [ ] An item with a custom profile does not get overwritten by `All Items`.
- [ ] `This item uses a custom profile` appears in `All Items` scope when relevant.
- [ ] `Custom Profile Active` appears in `This Item` scope when relevant.
- [ ] `No Custom Profile` appears in `This Item` scope when relevant.
- [ ] `Remove Item Profile` is disabled when no profile exists.
- [ ] `Remove Item Profile` removes the custom profile and returns the item to `All Items` behavior.
- [ ] Long item IDs scroll inside the scope label area.
- [ ] Profile changes persist after restarting the client.
- [ ] Profile changes persist after reconnecting to a dedicated server.

## Config Files

- [ ] NeoForge creates `config/neobackslot/common.toml`.
- [ ] NeoForge creates `config/neobackslot/client.toml`.
- [ ] NeoForge creates `config/neobackslot/item-transforms.toml`.
- [ ] Fabric creates `config/neobackslot/common.properties`.
- [ ] Fabric creates `config/neobackslot/client.properties`.
- [ ] Fabric creates `config/neobackslot/item-transforms.properties`.
- [ ] Item transform profiles save under `itemTransforms.back`.
- [ ] Item transform profiles save under `itemTransforms.belt`.
- [ ] Existing old item profile entries are still read as Back Slot profiles.
- [ ] A malformed item transform file does not crash the client.

## Death And Persistence

- [ ] Back Slot item drops on death when keep inventory is off.
- [ ] Belt Slot item drops on death when keep inventory is off.
- [ ] Back/Belt items are kept when vanilla keep inventory is on.
- [ ] Back/Belt items are kept when `keepItemsOnDeath` is enabled.
- [ ] Respawn sync is correct after death.

## Compatibility Smoke Tests

- [ ] Test with at least one weapon mod.
- [ ] Test with at least one shield/item model mod.
- [ ] Test with a grave mod if available.
- [ ] Test with a large modpack if available.

## Known Environment Notes

- SSL/PKIX handshake failures during Gradle downloads are environment/tooling issues, not NeoBackSlot runtime bugs.
- `TransformProfileManager` currently emits a NightConfig deprecation warning during compilation. Track this as cleanup, not a release blocker.

## Bug Report Template

```txt
Minecraft:
NeoForge:
NeoBackSlot build/version:
Client or dedicated server:
Singleplayer or multiplayer:
Other relevant mods:

What happened:

Expected result:

Steps to reproduce:
1.
2.
3.

Screenshots/video:

Relevant config snippets:
```
