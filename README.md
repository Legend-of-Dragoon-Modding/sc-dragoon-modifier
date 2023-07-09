For more information about modding with the Severed Chains platform view https://legendofdragoon.org/modding/

# About
Dragoon Modifier for [Severed Chains](https://legendofdragoon.org/projects/severed-chains/) is the port of the emulation modding tool [Dragoon Modifier](https://github.com/Legend-of-Dragoon-Modding/Dragoon-Modifier) to the Severed Chains platform. 
Dragoon Modifier is a simple tool that anyone can use with a spreadsheet editor to make their own custom difficulties. 
It comes with my prepackaged difficulty preset with additional mods.

Dragoon Modifier is currently only supported for Severed Chains Recommended Build #2.

### Installation

Drag and drop it into the Servered Chains/mods folder.

### Mods
**Note** Mod folders labeled US + Hard Mode or Hard Mode will also execute mods exclusive to Dragoon Modifier.<br/>
**Note** Japan Demo mod folder will make all repeat items non repeatable except Psyche Bomb X

Dragoon Modifier has several spreadsheets you can edit.
1.  Addition Base Stats Multipliers (scdk-addition-multiplier-stats.csv)<br/>
    Changes additions base stats. These stats are not reflected in game yet.
2.  Addition Stats Per Level (scdk-addition-stats.csv)<br/>
    Changes additions stats per level. These will multiply the stats you give in the base state CSV. These stats are not reflected in game yet.
3.  Addition Level Unlock(scdk-addition-unlock-levels.csv)<br/>
    Changes what level the addition should unlock.
4.  Character Stats (scdk-character-stats.csv)<br/>
    Changes character base stats.
5.  Dragoon EXP Table (scdk-dragoon-exp-table.csv)<br/>
    Changes when Dragoons level up.
6.  Equip Stats (scdk-equip-stats.csv)<br/>
    Changes Equipment stats.
7.  Character EXP Table (scdk-exp-table.csv)<br/>
    Changes the EXP required to level up.
8.  Monster Rewards (scdk-monster-rewards.csv)<br/>
    Changes the monsters EXP/Gold/Item Drop & Chance per Monster
9.  Monster Stats (scdk-monster-stats.csv)<br/>
    Changes the monsters stats.
10. Shop Prices (scdk-shop-prices.csv)<br/>
    Changes the shop prices **Note** this table is using the retail extraction method so these are SELL prices not BUY prices which are 2x sell prices.
11. Spell Stats (scdk-spell-stats.csv)<br/>
    Changes dragoon spell stats.
12. Use Item Stats (scdk-thrown-item-stats.csv)<br/>
    Changes thrown item stats.

**Note** For spell stats and thrown item stats damage is using both retail and not retail methods.
You can set a spell to mostly any percentage you want it to be at. 
However, if you use the following values (0,1,2,4,8,16,32,64,128) they will all mean something else.
This will be removed at a later date.<br/>
0   = 100<br/>
1   = 800<br/>
2   = 600<br/>
4   = 500<br/>
8   = 400<br/>
16  = 300<br/>
32  = 200<br/>
64  = 150<br/>
128 = 50

### Upcoming/TODO
Level Cap unlock<br/>
Dragoon Level cap unlock<br/>
Hell Mode<br/>
Hard + Hell Mode Bosses<br/>
Elemental Bomb<br/>
Damage Tracker<br/>
Monster Names as HP<br/>
Hard and Hell Mode with Dragoon level 6 & 7<br/>
Ultimate Boss Challenge Mode<br/>
A table with what each value means<br/>
Remove retail and DraMod