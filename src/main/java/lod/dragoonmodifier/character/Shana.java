package lod.dragoonmodifier.character;

import legend.game.additions.AdditionHits80;
import legend.game.characters.CharacterData2c;
import legend.game.characters.CharacterSpellInfo;
import legend.game.characters.Element;
import legend.game.characters.SpellDragoonLevelUnlockCriterion;
import legend.game.characters.SpellDragoonSpiritUnlockCriterion;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.inventory.CanEquip;
import legend.game.inventory.Equipment;
import legend.game.inventory.Good;
import legend.game.types.EquipmentSlot;
import legend.game.types.GameState52c;
import legend.game.unpacker.FileData;
import legend.game.unpacker.Loader;
import legend.lodmod.characters.RetailCharacterTemplate;
import lod.dragoonmodifier.DragoonModifier;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static legend.game.DrgnFiles.loadDrgnDir;
import static legend.lodmod.LodGoods.SILVER_DRAGOON_SPIRIT;
import static legend.lodmod.LodMod.LIGHT_ELEMENT;

public class Shana extends RetailCharacterTemplate {
  private static final String template = "Shana";
  private static Map<String, List<Integer>> baseStats = new HashMap<>();
  private static Map<String, List<Integer>> dragoonStats = new HashMap<>();

  @Override
  public CharacterData2c make(final GameState52c gameState) {
    baseStats = TemplateCommon.getBaseStats(template);
    dragoonStats = TemplateCommon.getDragoonStats(template);

    final CharacterData2c character = super.make(gameState);
    final Map<Integer, String> spellUnlock = TemplateCommon.getSpellUnlocks(template);

    for(final Map.Entry<Integer, String> entry : spellUnlock.entrySet()) {
      final int unlockLevel = entry.getKey();
      final String registryId = entry.getValue();

      if(unlockLevel == 1) {
        character.addSpell(DragoonModifier.id(registryId.split(":")[1]), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion()))).unlock(gameState.timestamp_a0);
      } else {
        character.addSpell(DragoonModifier.id(registryId.split(":")[1]), new CharacterSpellInfo(List.of(new SpellDragoonLevelUnlockCriterion(unlockLevel))));
      }
    }

    return character;
  }

  @Override
  public void loadWorldMapModel(final CharacterData2c character, final Consumer<List<FileData>> onLoad) {
    Loader
      .loadFiles("SECT/DRGN22.BIN/836/33", "SECT/DRGN22.BIN/836/textures/1", "SECT/DRGN22.BIN/836/34", "SECT/DRGN22.BIN/836/35", "SECT/DRGN22.BIN/836/36")
      .thenAccept(onLoad)
    ;
  }

  @Override
  public int prepareAttack(final CharacterData2c character, final PlayerBattleEntity bent) {
    bent.battle.playBentSound(1, bent, 0, 0, 0, 44, 0);
    bent.battle.playBentSound(1, bent, 4, 0, 0, 16, 0);
    bent.battle.playBentSound(1, bent, 5, 0, 0, 46, 0);
    return 44;
  }

  @Override
  public CanEquip canEquip(final CharacterData2c character, final EquipmentSlot slot, final Equipment equipment) {
    return CanEquip.NORMAL;
  }

  @Override
  public int getXpToNextLevel(final CharacterData2c character) {
    return baseStats.get("EXP").get(character.level_12 - 1);
  }

  @Override
  public int getDxpToNextLevel(final CharacterData2c character) {
    return dragoonStats.get("DEXP").get(character.dlevel_13 - 1);
  }

  @Override
  public Element getElement(final CharacterData2c character) {
    return LIGHT_ELEMENT.get();
  }

  @Override
  protected Good getDragoonSpirit() {
    return SILVER_DRAGOON_SPIRIT.get();
  }

  @Override
  public AdditionHits80 getDragoonAddition(final CharacterData2c character) {
    return null;
  }

  @Override
  protected int getHpToAdd(final int level) {
    return baseStats.get("HP").get(level);
  }

  @Override
  protected int getSpeedToAdd(final int level) {
    return baseStats.get("Speed").get(level);
  }

  @Override
  protected int getAttackToAdd(final int level) {
    return baseStats.get("AT").get(level);
  }

  @Override
  protected int getDefenseToAdd(final int level) {
    return baseStats.get("DF").get(level);
  }

  @Override
  protected int getMagicAttackToAdd(final int level) {
    return baseStats.get("MAT").get(level);
  }

  @Override
  protected int getMagicDefenseToAdd(final int level) {
    return baseStats.get("MDF").get(level);
  }

  @Override
  protected int getDragoonAttackToAdd(final int dlevel) {
    return dragoonStats.get("DAT").get(dlevel);
  }

  @Override
  protected int getDragoonMagicAttackToAdd(final int dlevel) {
    return dragoonStats.get("DMAT").get(dlevel);
  }

  @Override
  protected int getDragoonDefenseToAdd(final int dlevel) {
    return dragoonStats.get("DDF").get(dlevel);
  }

  @Override
  protected int getDragoonMagicDefenseToAdd(final int dlevel) {
    return dragoonStats.get("DMDF").get(dlevel);
  }

  @Override
  protected int getMpToAdd(final int dlevel) {
    return dragoonStats.get("MaxMP").get(dlevel);
  }

  @Override
  public boolean isArcher(final CharacterData2c character) {
    return true;
  }

  @Override
  public Path getAttackSoundsPath(final CharacterData2c character, final PlayerBattleEntity bent) {
    return Loader.resolve(Path.of("SECT/DRGN0.BIN/1300"));
  }

  @Override
  public Path getDragoonAttackSoundsPath(final CharacterData2c character, final PlayerBattleEntity bent) {
    return Loader.resolve(Path.of("SECT/DRGN0.BIN/1309"));
  }

  @Override
  public Path getDragoonTransformSoundsPath(final CharacterData2c character, final PlayerBattleEntity bent) {
    return Loader.resolve(Path.of("SECT/DRGN0.BIN/1319"));
  }

  @Override
  public CompletableFuture<List<FileData>> loadHumanAttackAnimations(final CharacterData2c character, final PlayerBattleEntity bent) {
    return loadDrgnDir(0, 4047);
  }

  @Override
  public CompletableFuture<List<FileData>> loadDragoonAttackAnimations(final CharacterData2c character, final PlayerBattleEntity bent) {
    return loadDrgnDir(0, 4105);
  }

  @Override
  public int getWeaponTrailColour(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 0x808080;
  }

  @Override
  public int getSpellRingColour(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 0x6c8283;
  }

  @Override
  public int getParticleColour(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 0x747044;
  }

  @Override
  public int getLeftHandModelPart(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 11;
  }

  @Override
  public int getRightHandModelPart(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 12;
  }

  @Override
  public int getFootModelPart(final CharacterData2c character, final PlayerBattleEntity bent) {
    if(bent.isDragoon()) {
      return 11;
    }

    return 13;
  }

  @Override
  public int getWeaponModelPart(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 0;
  }

  @Override
  public int getWeaponTrailVertexComponent(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 0;
  }

  @Override
  public int getShadowSize(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 0x1000;
  }

  @Override
  public int getSpecialTransformStage(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 75;
  }

  @Override
  public int getDragoonTransformDeff(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 36;
  }

  @Override
  public int getDragoonAttackDeff(final CharacterData2c character, final PlayerBattleEntity bent) {
    return -1;
  }

  @Override
  public int getDragoonAttackSounds(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 106;
  }
}
