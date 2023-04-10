package lod.csvstat;

import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import legend.game.combat.environment.BattlePreloadedEntities_18cb0;
import legend.game.combat.types.CombatantStruct1a8;
import legend.game.inventory.ItemRegistryEvent;
import legend.game.modding.Mod;
import legend.game.modding.events.EventListener;
import legend.game.modding.events.characters.*;
import legend.game.modding.events.combat.EnemyRewardsEvent;
import legend.game.modding.events.combat.MonsterStatsEvent;
import legend.game.modding.events.combat.SpellStatsEvent;
import legend.game.modding.events.inventory.EquipmentStatsEvent;
import legend.game.modding.events.inventory.RepeatItemReturnEvent;
import legend.game.modding.events.inventory.ShopItemEvent;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Mod(id = CsvStatMod.MOD_ID)
@EventListener
public class CsvStatMod {
    public static final String MOD_ID = "CSV Stat Mod";
    public static String modDirectory = "";
    public static List<String[]> monsterStats;
    public static List<String[]> monstersRewardsStats;
    public static List<String[]> additionStats;
    public static List<String[]> additionMultiStats;
    public static List<String[]> additionUnlockStats;
    public static List<String[]> characterStats;
    public static List<String[]> dragoonStats;
    public static List<String[]> xpNextStats;
    public static List<String[]> spellStats;
    public static List<String[]> equipStats;
    public static List<String[]> itemStats;
    public static List<String[]> shopItems;

    public CsvStatMod() {
        try {
            modDirectory = Files.readAllLines(Path.of("./mods/csvstat/csvmoddirectory.txt")).get(0);

            List<String[]> monstersCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-MONSTER-STATS.CSV")).build().readAll();
            List<String[]> monstersRewardsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-MONSTER-REWARDS.CSV")).build().readAll();
            List<String[]> additionCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-ADDITION-STATS.CSV")).build().readAll();
            List<String[]> additionUnlockCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-ADDITION-UNLOCK-LEVELS.CSV")).build().readAll();
            List<String[]> additionMultiCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-ADDITION-MULTIPLIER-STATS.CSV")).build().readAll();
            List<String[]> characterStatsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-CHARACTER-STATS.CSV")).build().readAll();
            List<String[]> dragoonStatsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-DRAGOON-STATS.CSV")).build().readAll();
            List<String[]> xpNextStatsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-EXP-TABLE.CSV")).build().readAll();
            List<String[]> spellStatsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-SPELL-STATS.CSV")).build().readAll();
            List<String[]> equipStatsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-EQUIP-STATS.CSV")).build().readAll();
            List<String[]> itemStatsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-THROWN-ITEM-STATS.CSV")).build().readAll();
            List<String[]> shopItemsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-SHOP-ITEMS.CSV")).build().readAll();

            monstersCsv.remove(0);
            monstersRewardsCsv.remove(0);
            additionCsv.remove(0);
            additionMultiCsv.remove(0);
            additionUnlockCsv.remove(0);
            characterStatsCsv.remove(0);
            dragoonStatsCsv.remove(0);
            xpNextStatsCsv.remove(0);
            spellStatsCsv.remove(0);
            equipStatsCsv.remove(0);
            itemStatsCsv.remove(0);
            shopItemsCsv.remove(0);

            monsterStats = monstersCsv;
            monstersRewardsStats = monstersRewardsCsv;
            additionStats = additionCsv;
            additionMultiStats = additionMultiCsv;
            additionUnlockStats = additionUnlockCsv;
            characterStats = characterStatsCsv;
            dragoonStats = dragoonStatsCsv;
            xpNextStats = xpNextStatsCsv;
            spellStats = spellStatsCsv;
            equipStats = equipStatsCsv;
            itemStats = itemStatsCsv;
            shopItems = shopItemsCsv;

            System.out.println("CSV Mod loaded using directory: " + modDirectory);
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        } catch (CsvException e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }

    }

    @EventListener
    public static void registerItems(final ItemRegistryEvent item) {

    }

    @EventListener
    public static void registerEquips(final EquipmentStatsEvent equip) {
        int equipId = equip.equipmentId;
        int special1 = Integer.parseInt(equipStats.get(equipId)[11]);
        int special2 = Integer.parseInt(equipStats.get(equipId)[12]);

        equip.flags = Integer.parseInt(equipStats.get(equipId)[0]);
        equip.type = Integer.parseInt(equipStats.get(equipId)[1]);
        equip._02 = Integer.parseInt(equipStats.get(equipId)[2]);
        equip.equipableFlags = Integer.parseInt(equipStats.get(equipId)[3]);
        equip.element = Integer.parseInt(equipStats.get(equipId)[4]);
        equip._05 = Integer.parseInt(equipStats.get(equipId)[5]);
        equip.elementalResistance = Integer.parseInt(equipStats.get(equipId)[6]);
        equip.elementalImmunity = Integer.parseInt(equipStats.get(equipId)[7]);
        equip.statusResist = Integer.parseInt(equipStats.get(equipId)[8]);
        equip._09 = Integer.parseInt(equipStats.get(equipId)[9]);
        equip.special1 = Integer.parseInt(equipStats.get(equipId)[11]);
        equip.special2 = Integer.parseInt(equipStats.get(equipId)[12]);
        equip.specialAmount = Integer.parseInt(equipStats.get(equipId)[13]);
        equip.icon = Integer.parseInt(equipStats.get(equipId)[14]);
        equip.speed = Integer.parseInt(equipStats.get(equipId)[15]);
        equip.attack = Integer.parseInt(equipStats.get(equipId)[16]) + Integer.parseInt(equipStats.get(equipId)[10]);
        equip.magicAttack = Integer.parseInt(equipStats.get(equipId)[17]);
        equip.defence = Integer.parseInt(equipStats.get(equipId)[18]);
        equip.magicDefence = Integer.parseInt(equipStats.get(equipId)[19]);
        equip.attackHit = Integer.parseInt(equipStats.get(equipId)[20]);
        equip.magicHit = Integer.parseInt(equipStats.get(equipId)[21]);
        equip.attackAvoid = Integer.parseInt(equipStats.get(equipId)[22]);
        equip.magicAvoid = Integer.parseInt(equipStats.get(equipId)[23]);
        equip.statusChance = Integer.parseInt(equipStats.get(equipId)[24]);
        equip._19 = Integer.parseInt(equipStats.get(equipId)[25]);
        equip._1a = Integer.parseInt(equipStats.get(equipId)[26]);
        equip.onHitStatus = Integer.parseInt(equipStats.get(equipId)[27]);
    }

    @EventListener
    public static void registerEnemyRewards(final EnemyRewardsEvent enemyRewards) {
        int enemyId = enemyRewards.enemyId;
        enemyRewards.clear();
        enemyRewards.xp = Integer.parseInt(monstersRewardsStats.get(enemyId)[0]);
        enemyRewards.gold = Integer.parseInt(monstersRewardsStats.get(enemyId)[1]);
        enemyRewards.add(new CombatantStruct1a8.ItemDrop(Integer.parseInt(monstersRewardsStats.get(enemyId)[2]), Integer.parseInt(monstersRewardsStats.get(enemyId)[3])));
    }

    @EventListener
    public static void registerEnemyStats(final MonsterStatsEvent enemyStats) {
        int ovrId = enemyStats.enemyId;

        enemyStats.hp = Integer.parseInt(monsterStats.get(ovrId)[1]);
        enemyStats.maxHp = Integer.parseInt(monsterStats.get(ovrId)[1]);
        enemyStats.attack = Integer.parseInt(monsterStats.get(ovrId)[3]);
        enemyStats.magicAttack = Integer.parseInt(monsterStats.get(ovrId)[4]);
        enemyStats.speed = Integer.parseInt(monsterStats.get(ovrId)[5]);
        enemyStats.defence = Integer.parseInt(monsterStats.get(ovrId)[6]);
        enemyStats.magicDefence = Integer.parseInt(monsterStats.get(ovrId)[7]);
        enemyStats.attackAvoid = Integer.parseInt(monsterStats.get(ovrId)[8]);
        enemyStats.magicAvoid = Integer.parseInt(monsterStats.get(ovrId)[9]);
        enemyStats.specialEffectFlag = Integer.parseInt(monsterStats.get(ovrId)[10]);
        enemyStats.elementFlag = Integer.parseInt(monsterStats.get(ovrId)[12]);
        enemyStats.elementalImmunityFlag = Integer.parseInt(monsterStats.get(ovrId)[13]);
        enemyStats.statusResistFlag = Integer.parseInt(monsterStats.get(ovrId)[14]);
    }

    @EventListener
    public static void registerAdditionStats(final AdditionHitEvent addition) {
        int additionId = addition.addition.hits_00[0].hitProperty_00[6];
        for (int i = 0; i < 8; i++) {
            final BattlePreloadedEntities_18cb0.AdditionHitProperties_20 hit = addition.addition.hits_00[i];
            hit.hitProperty_00[0] = Short.parseShort(additionStats.get(additionId * 8 + i)[0]);
            hit.hitProperty_00[1] = Short.parseShort(additionStats.get(additionId * 8 + i)[1]);
            hit.hitProperty_00[2] = Short.parseShort(additionStats.get(additionId * 8 + i)[2]);
            hit.hitProperty_00[3] = Short.parseShort(additionStats.get(additionId * 8 + i)[3]);
            hit.hitProperty_00[4] = Short.parseShort(additionStats.get(additionId * 8 + i)[4]);
            hit.hitProperty_00[5] = Short.parseShort(additionStats.get(additionId * 8 + i)[5]);
            hit.hitProperty_00[6] = Short.parseShort(additionStats.get(additionId * 8 + i)[6]);
            hit.hitProperty_00[7] = Short.parseShort(additionStats.get(additionId * 8 + i)[7]);
            hit.hitProperty_00[8] = Short.parseShort(additionStats.get(additionId * 8 + i)[8]);
            hit.hitProperty_00[9] = Short.parseShort(additionStats.get(additionId * 8 + i)[9]);
            hit.hitProperty_00[10] = Short.parseShort(additionStats.get(additionId * 8 + i)[10]);
            hit.hitProperty_00[11] = Short.parseShort(additionStats.get(additionId * 8 + i)[11]);
            hit.hitProperty_00[12] = Short.parseShort(additionStats.get(additionId * 8 + i)[12]);
            hit.hitProperty_00[13] = Short.parseShort(additionStats.get(additionId * 8 + i)[13]);
            hit.hitProperty_00[14] = Short.parseShort(additionStats.get(additionId * 8 + i)[14]);
            hit.hitProperty_00[15] = Short.parseShort(additionStats.get(additionId * 8 + i)[15]);
        }
    }

    @EventListener
    public static void registerAdditionMulti(final AdditionHitMultiplierEvent multiplier) {
        multiplier.additionSpMulti = Integer.parseInt(additionMultiStats.get(multiplier.additionId)[(multiplier.additionLevel - 1) * 4]);
        multiplier.additionDmgMulti = Integer.parseInt(additionMultiStats.get(multiplier.additionId)[(multiplier.additionLevel - 1) * 4 + 1]);
    }

    @EventListener
    public static void registerAdditionUnlock(final AdditionUnlockEvent unlock) {
        unlock.additionLevel = Integer.parseInt(additionUnlockStats.get(unlock.additionId)[0]);
    }

    @EventListener
    public static void registerCharacterStats(final CharacterStatsEvent character) {
        character.maxHp = Short.parseShort(characterStats.get(character.characterId * 61 + character.level)[5]);
        character.bodySpeed = Short.parseShort(characterStats.get(character.characterId * 61 + character.level)[0]);
        character.bodyAttack = Short.parseShort(characterStats.get(character.characterId * 61 + character.level)[1]);
        character.bodyMagicAttack = Short.parseShort(characterStats.get(character.characterId * 61 + character.level)[2]);
        character.bodyDefence = Short.parseShort(characterStats.get(character.characterId * 61 + character.level)[3]);
        character.bodyMagicDefence = Short.parseShort(characterStats.get(character.characterId * 61 + character.level)[4]);

        if (character.dlevel > 0) {
            character.maxMp = Integer.parseInt(dragoonStats.get(character.characterId * 6 + character.dlevel)[0]);
            character.dragoonAttack = Integer.parseInt(dragoonStats.get(character.characterId * 6 + character.dlevel)[3]);
            character.dragoonMagicAttack = Integer.parseInt(dragoonStats.get(character.characterId * 6 + character.dlevel)[4]);
            character.dragoonDefence = Integer.parseInt(dragoonStats.get(character.characterId * 6 + character.dlevel)[5]);
            character.dragoonMagicDefence = Integer.parseInt(dragoonStats.get(character.characterId * 6 + character.dlevel)[6]);
        }
    }

    @EventListener
    public static void registerXpNext(final XpToLevelEvent exp) {
        exp.xp = Integer.parseInt(xpNextStats.get(exp.charId * 61 + exp.level)[0]);
    }

    @EventListener
    public static void registerSpellStats(final SpellStatsEvent spell) {
        int spellId = spell.spellId;
        spell.targetType = Integer.parseInt(spellStats.get(spellId)[0]);
        spell._01 = Integer.parseInt(spellStats.get(spellId)[1]);
        spell.specialEffect = Integer.parseInt(spellStats.get(spellId)[2]);
        spell.damageFlag = Integer.parseInt(spellStats.get(spellId)[3]);
        spell.healingPercent = Integer.parseInt(spellStats.get(spellId)[4]);
        spell.accuracy = Integer.parseInt(spellStats.get(spellId)[5]);
        spell.mpUsage = Integer.parseInt(spellStats.get(spellId)[6]);
        spell.statusChance = Integer.parseInt(spellStats.get(spellId)[7]);
        spell.element = Integer.parseInt(spellStats.get(spellId)[8]);
        spell.statusType = Integer.parseInt(spellStats.get(spellId)[9]);
        spell.buffType = Integer.parseInt(spellStats.get(spellId)[10]);
        spell._0b = Integer.parseInt(spellStats.get(spellId)[1]);
    }

    @EventListener
    public static void registerShopItem(final ShopItemEvent shopItem) {
        shopItem.itemId = Integer.parseInt(shopItems.get(shopItem.shopId)[shopItem.slotId]);
    }

    @EventListener
    public static void repeatItems(final RepeatItemReturnEvent item) {
        if (modDirectory.equals("JapanDemo")) {
            item.returnItem = item.itemId == 250 ? true : false;
        }
    }
}