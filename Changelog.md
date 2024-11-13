# Version 0.6.0 Changelog
## New Features
- Added custom cutting/mining speed setting to fine-tune tree falling mechanics.
- Restructured block and general rendering system for better performance and visual quality.

## Improvements
- Improved tree entity handling and client-side particle system for better visualization.
- Enhanced leaves gathering algorithm for more accurate tree detection.
  > This should make tree detection more reliable and prevent unwanted blocks from being included.
- Restructured rendering components for smoother animations and better performance.

## Bug Fixes
- Fixed "maxTreeDistance" setting not working properly
  > Now the setting correctly limits the distance for tree block detection.
- Fixed "Required tool" setting functionality
  > Previously trees wouldn't fall at all. Now they correctly fall only when cut with appropriate tools.
- Fixed wrong/inverted leaf color displaying in certain scenarios.
- Fixed satiety consumption calculation based on tree height instead of total block count.
  > Now hunger cost is calculated based on the total number of tree blocks, making manual tree cutting more resource-efficient compared to using tools (trades durability cost for hunger points).
- Fixed late loading of particles causing errors on Forge.
- Fixed Fabric server crash on startup related to particle systems.

# Version 0.5.0 Changelog

## New Features
- Added a setting for the tree block count limit. Now you can set a limit on the number of blocks in the X or Z axis if a tree is too big.
  > This may be helpful if a tree is accidentally connected to a large building made of logs. (This doesn't save the whole building, but most of it.)

## Improvements
- Enhanced the method for defining leaf color. Leaf particles now have the correct color.
- Improved the bounce animation. The bounce height now depends on the length (angle) of the falling tree.
- Improved tree and leaf detection algorithms.

## Bug Fixes
- Fixed new tree sounds that were incorrectly assembled.
- Fixed a client crash issue that occurred when a tree fell on water-logged blocks (leaves, mangrove roots, etc.).
- Fixed a bug where item drop and leaf particles appeared in the wrong location if the tree was moved.

# Version 0.2.4 Changelog
## New Features
- Added advanced animation configuration for all falling objects
  > Now you can customize animation settings separately for trees, cacti, bamboo, and chorus plants.
- Added Russian language support for configuration menu.
- Added enhanced tree physics simulation for more realistic behavior.
- Added disappearing particles for fallen trees.
- Added unique sound effects for cactus and bamboo falling.

## Improvements
- Enhanced physics system for more natural tree falling mechanics.
  > Trees now react more realistically to terrain and obstacles.
