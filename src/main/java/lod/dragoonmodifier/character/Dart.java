package lod.dragoonmodifier.character;

import legend.core.Latch;
import legend.core.gte.MV;
import legend.core.renderer.Translucency;
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
import legend.game.textures.Image;
import legend.game.textures.TextureAtlasIcon;
import legend.game.types.EquipmentSlot;
import legend.game.types.GameState52c;
import legend.game.unpacker.FileData;
import legend.game.unpacker.Loader;
import legend.lodmod.LodAdditions;
import legend.lodmod.LodMod;
import legend.lodmod.characters.DartCharacterData;
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

import static legend.core.GameEngine.getTextureAtlas;
import static legend.game.DrgnFiles.loadDrgnDir;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.lodmod.LodGoods.DIVINE_DRAGOON_SPIRIT;
import static legend.lodmod.LodGoods.RED_DRAGOON_SPIRIT;
import static legend.lodmod.LodMod.DIVINE_ELEMENT;
import static legend.lodmod.LodMod.FIRE_ELEMENT;

public class Dart extends RetailCharacterTemplate {
  private static final String template = "Dart";
  private static Map<String, List<Integer>> baseStats = new HashMap<>();
  private static Map<String, List<Integer>> dragoonStats = new HashMap<>();
  private static final List<AdditionHits80> dragoonAdditions = new ArrayList<>();

  @Override
  public CharacterData2c make(final GameState52c gameState) {
    baseStats = TemplateCommon.getBaseStats(template);
    dragoonStats = TemplateCommon.getDragoonStats(template);

    final CharacterData2c original = super.make(gameState);
    final CharacterData2c character = new DartCharacterData(gameState, this, original.stats);
    final List<String[]> additionList = TemplateCommon.getAdditionList();
    final Map<Integer, String> spellUnlock = TemplateCommon.getSpellUnlocks(template);
    RegistryId firstAddition = LodAdditions.DOUBLE_SLASH.getId();

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
    dragoonAdditions.add(TemplateCommon.getDragoonAddition(template, "divine_dragoon_addition"));

    return character;
  }

  @Override
  public void loadWorldMapModel(final CharacterData2c character, final Consumer<List<FileData>> onLoad) {
    Loader
      .loadFiles("SECT/DRGN22.BIN/836/264", "SECT/DRGN22.BIN/836/textures/8", "SECT/DRGN22.BIN/836/265", "SECT/DRGN22.BIN/836/266", "SECT/DRGN22.BIN/836/267")
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
    if(character.gameState.goods_19c.has(DIVINE_DRAGOON_SPIRIT.get())) {
      return dragoonAdditions.get(1);
    }

    return dragoonAdditions.get(0);
  }

  @Override
  public void renderTransformIcon(final CharacterData2c character, final PlayerBattleEntity bent, final MV transforms, final int frame) {
    final Element element = character.gameState.goods_19c.has(DIVINE_DRAGOON_SPIRIT.get()) ? DIVINE_ELEMENT.get() : FIRE_ELEMENT.get();
    final TextureAtlasIcon icon = getTextureAtlas().getIcon(LodMod.id(element.getRegistryId().entryId() + '_' + frame));
    icon.render(transforms);

    if(element == DIVINE_ELEMENT.get()) {
      if(frame != 0) {
        transforms.transfer.x += 4.0f;
        transforms.scale(0.5f, 1.0f, 1.0f);

        final TextureAtlasIcon icon2 = getTextureAtlas().getIcon(LodMod.id(DIVINE_ELEMENT.getId().entryId() + "_overlay_" + (frame - 1)));
        icon2.render(transforms)
          .translucency(Translucency.B_PLUS_F)
        ;
      }
    }
  }

  @Override
  public Element getElement(final CharacterData2c character) {
    return FIRE_ELEMENT.get();
  }

  @Override
  public Element getElement(final CharacterData2c character, final PlayerBattleEntity bent) {
    return bent.isDragoon() && character.gameState.goods_19c.has(DIVINE_DRAGOON_SPIRIT.get()) ? DIVINE_ELEMENT.get() : FIRE_ELEMENT.get();
  }

  @Override
  public boolean hasDragoon(final CharacterData2c character) {
    return super.hasDragoon(character) || character.gameState.goods_19c.has(DIVINE_DRAGOON_SPIRIT.get());
  }

  @Override
  protected Good getDragoonSpirit() {
    return RED_DRAGOON_SPIRIT.get();
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
    return Loader.resolve(Path.of("SECT/DRGN0.BIN/1298"));
  }

  @Override
  public Path getDragoonAttackSoundsPath(final CharacterData2c character, final PlayerBattleEntity bent) {
    return Loader.resolve(Path.of("SECT/DRGN0.BIN/1307"));
  }

  @Override
  public Path getDragoonTransformSoundsPath(final CharacterData2c character, final PlayerBattleEntity bent) {
    if(bent.charId_272 == 0 && gameState_800babc8.goods_19c.has(DIVINE_DRAGOON_SPIRIT)) {
      return Loader.resolve(Path.of("SECT/DRGN0.BIN/1328"));
    }

    return Loader.resolve(Path.of("SECT/DRGN0.BIN/1317"));
  }

  @Override
  public Path getBattleModelPath(final CharacterData2c character, final PlayerBattleEntity bent) {
    String name = this.getRegistryId().entryId();

    if(bent.isDragoon() && gameState_800babc8.goods_19c.has(DIVINE_DRAGOON_SPIRIT)) {
      name = "divine";
    }

    final String file = bent.isDragoon() ? "dragoon" : "combat";
    return Loader.resolve(Path.of("characters", name, "models", file));
  }

  @Override
  public Path getBattleTexturePath(final CharacterData2c character, final PlayerBattleEntity bent) {
    String name = this.getRegistryId().entryId();

    if(bent.isDragoon() && gameState_800babc8.goods_19c.has(DIVINE_DRAGOON_SPIRIT)) {
      name = "divine";
    }

    final String file = bent.isDragoon() ? "dragoon" : "combat";
    return Loader.resolve(Path.of("characters", name, "textures", file));
  }

  @Override
  public CompletableFuture<List<FileData>> loadDragoonAttackAnimations(final CharacterData2c character, final PlayerBattleEntity bent) {
    final int fileIndex;
    if(!gameState_800babc8.goods_19c.has(DIVINE_DRAGOON_SPIRIT)) {
      fileIndex = 4103;
    } else {
      fileIndex = 4112;
    }

    return loadDrgnDir(0, fileIndex);
  }

  @Override
  public int getWeaponTrailColour(final CharacterData2c character, final PlayerBattleEntity bent) {
    return character.gameState.goods_19c.has(DIVINE_DRAGOON_SPIRIT.get()) ? 0x808080 : 0x2068e8;
  }

  @Override
  public int getSpellRingColour(final CharacterData2c character, final PlayerBattleEntity bent) {
    return character.gameState.goods_19c.has(DIVINE_DRAGOON_SPIRIT.get()) ? 0x808080 : 0x201996;
  }

  @Override
  public int getParticleColour(final CharacterData2c character, final PlayerBattleEntity bent) {
    return character.gameState.goods_19c.has(DIVINE_DRAGOON_SPIRIT.get()) ? 0xf5ffd7 : 0xff2000;
  }

  @Override
  public int getLeftHandModelPart(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 5;
  }

  @Override
  public int getRightHandModelPart(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 6;
  }

  @Override
  public int getFootModelPart(final CharacterData2c character, final PlayerBattleEntity bent) {
    if(bent.isDragoon()) {
      return character.gameState.goods_19c.has(DIVINE_DRAGOON_SPIRIT.get()) ? 7 : 8;
    }

    return 8;
  }

  @Override
  public int getWeaponModelPart(final CharacterData2c character, final PlayerBattleEntity bent) {
    return character.gameState.goods_19c.has(DIVINE_DRAGOON_SPIRIT.get()) ? 0 : 14;
  }

  @Override
  public int getWeaponTrailVertexComponent(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 0;
  }

  @Override
  public int getShadowSize(final CharacterData2c character, final PlayerBattleEntity bent) {
    return character.gameState.goods_19c.has(DIVINE_DRAGOON_SPIRIT.get()) ? 0x1500 : 0x1800;
  }

  @Override
  public int getSpecialTransformStage(final CharacterData2c character, final PlayerBattleEntity bent) {
    return character.gameState.goods_19c.has(DIVINE_DRAGOON_SPIRIT.get()) ? 78 : 71;
  }

  @Override
  public int getDragoonTransformDeff(final CharacterData2c character, final PlayerBattleEntity bent) {
    return character.gameState.goods_19c.has(DIVINE_DRAGOON_SPIRIT.get()) ? 46 : 32;
  }

  @Override
  public int getDragoonAttackDeff(final CharacterData2c character, final PlayerBattleEntity bent) {
    return character.gameState.goods_19c.has(DIVINE_DRAGOON_SPIRIT.get()) ? 57 : 48;
  }

  @Override
  public int getDragoonAttackSounds(final CharacterData2c character, final PlayerBattleEntity bent) {
    return 104;
  }
}
