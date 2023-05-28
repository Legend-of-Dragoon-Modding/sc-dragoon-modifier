package lod.dragoonmodifier;

import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import legend.core.GameEngine;
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
import legend.game.modding.registries.Registrar;
import legend.game.modding.registries.RegistryDelegate;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigRegistryEvent;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Mod(id = CsvStatMod.MOD_ID)
@EventListener
public class CsvStatMod {
    public static boolean loaded = false;
    public static final String MOD_ID = "dragoon-modifier";
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
        loaded = false;
    }

    @EventListener
    public static void registerConfig(final ConfigRegistryEvent event) {

    }

    public static void changeModDirectory(String newDirectory) {

    }
}