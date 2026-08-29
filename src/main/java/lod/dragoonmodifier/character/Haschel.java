package lod.dragoonmodifier.character;

import legend.game.additions.AdditionHits80;
import legend.game.characters.AdditionLevelUnlockCriterion;
import legend.game.characters.AdditionMasteryUnlockCriterion;
import legend.game.characters.CharacterAdditionInfo;
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
import legend.lodmod.LodAdditions;
import legend.lodmod.characters.RetailCharacterTemplate;
import lod.dragoonmodifier.DragoonModifier;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static legend.game.DrgnFiles.loadDrgnDir;
import static legend.lodmod.LodGoods.VIOLET_DRAGOON_SPIRIT;
import static legend.lodmod.LodMod.THUNDER_ELEMENT;

public class Haschel extends RetailCharacterTemplate {
  private static final String template = "Haschel";
  private static Map<String, List<Integer>> baseStats = new HashMap<>();
  private static Map<String, List<Integer>> dragoonStats = new HashMap<>();
  private static final List<AdditionHits80> dragoonAdditions = new ArrayList<>();

  @Override
  public CharacterData2c make(final GameState52c gameState) {
    baseStats = TemplateCommon.getBaseStats(template);
    dragoonStats = TemplateCommon.getDragoonStats(template);

    final CharacterData2c character = super.make(gameState);
    final List<String[]> additionList = TemplateCommon.getAdditionList();
    final Map<Integer, String> spellUnlock = TemplateCommon.getSpellUnlocks(template);
    RegistryId firstAddition = LodAdditions.DOUBLE_PUNCH.getId();

    //TODO Allow non dragoon_modifier registry IDs
    for(final String[] line : additionList) {
      if(line[0].equals(template)) {
        final int[] additionUnlock = TemplateCommon.getAdditionUnlockCriteria(template, line[1]);
        if(additionUnlock[0] == 1) {
          if(additionUnlock[1] == 1) {
            character.addAddition(DragoonModifier.id(line[1]), new CharacterAdditionInfo(List.of()));
            firstAddition = DragoonModifier.id(line[1]);
          } else {
            character.addAddition(DragoonModifier.id(line[1]), new CharacterAdditionInfo(List.of(new AdditionLevelUnlockCriterion(additionUnlock[1]))));
          }
        } else {
          character.addAddition(DragoonModifier.id(line[1]), new CharacterAdditionInfo(List.of(new AdditionMasteryUnlockCriterion())));
        }
      }
    }

    for(final Map.Entry<Integer, String> entry : spellUnlock.entrySet()) {
      final int unlockLevel = entry.getKey();
      final String registryId = entry.getValue();

      if(unlockLevel == 1) {
        character.addSpell(DragoonModifier.id(registryId.split(":")[1]), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion()))).unlock(gameState.timestamp_a0);
      } else {
        character.addSpell(DragoonModifier.id(registryId.split(":")[1]), new CharacterSpellInfo(List.of(new SpellDragoonLevelUnlockCriterion(unlockLevel))));
      }
    }

    character.selectedAddition_19 = firstAddition;

    dragoonAdditions.add(TemplateCommon.getDragoonAddition(template, "dragoon_addition"));

    return character;
  }

  @Override
  public void loadWorldMapModel(final CharacterData2c character, final Consumer<List<FileData>> onLoad) {
    Loader
      .loadFiles("SECT/DRGN22.BIN/836/165", "SECT/DRGN22.BIN/836/textures/5", "SECT/DRGN22.BIN/836/166", "SECT/DRGN22.BIN/836/167", "SECT/DRGN22.BIN/836/168")
      .thenAccept(onLoad)
    ;
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
  public AdditionHits80 getDragoonAddition(final CharacterData2c character) {
    return dragoonAdditions.get(0);
  }

  @Override
  public Element getElement(final CharacterData2c character) {
    return THUNDER_ELEMENT.get();
  }

  @Override
  protected Good getDragoonSpirit() {
    return VIOLET_DRAGOON_SPIRIT.get();
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
  public Path getAttackSoundsPath(final CharacterData2c character, final PlayerBattleEntity bent) {
    return Loader.resolve(Path.of("SECT/DRGN0.BIN/1302"));
  }

  @Override
  public Path getDragoonAttackSoundsPath(final CharacterData2c character, final PlayerBattleEntity bent) {
    return Loader.resolve(Path.of("SECT/DRGN0.BIN/1311"));
  }

  @Override
  public Path getDragoonTransformSoundsPath(final CharacterData2c character, final PlayerBattleEntity bent) {
    return Loader.resolve(Path.of("SECT/DRGN0.BIN/1321"));
  }

  @Override
  public CompletableFuture<List<FileData>> loadDragoonAttackAnimations(final CharacterData2c character, final PlayerBattleEntity bent) {
    return loadDrgnDir(0, 4107);
  }

  @Override
  public boolean hasWeaponTrail(final CharacterData2c character, final PlayerBattleEntity bent) {
    return false;
  }

  @Override
  public int getWeaponTrailColour(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 0x88d4d8;
  }

  @Override
  public int getSpellRingColour(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 0x6c306c;
  }

  @Override
  public int getParticleColour(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 0xd888d4;
  }

  @Override
  public int getLeftHandModelPart(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 6;
  }

  @Override
  public int getRightHandModelPart(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 7;
  }

  @Override
  public int getFootModelPart(final CharacterData2c character, final PlayerBattleEntity bent) {
    if(bent.isDragoon()) {
      return 8;
    }

    return 10;
  }

  @Override
  public int getWeaponModelPart(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 5;
  }

  @Override
  public int getWeaponTrailVertexComponent(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 2;
  }

  @Override
  public int getShadowSize(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 0x1600;
  }

  @Override
  public int getSpecialTransformStage(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 77;
  }

  @Override
  public int getDragoonTransformDeff(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 40;
  }

  @Override
  public int getDragoonAttackDeff(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 52;
  }

  @Override
  public int getDragoonAttackSounds(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 108;
  }
}
