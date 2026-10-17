package lod.dragoonmodifier.character;

import legend.core.Latch;
import legend.game.characters.CharacterData2c;
import legend.game.characters.CharacterSpellInfo;
import legend.game.characters.SpellDragoonLevelUnlockCriterion;
import legend.game.characters.SpellDragoonSpiritUnlockCriterion;
import legend.game.textures.Image;
import legend.game.types.GameState52c;
import lod.dragoonmodifier.DragoonModifier;
import lod.lloydmod.LloydMod;
import lod.lloydmod.LloydSpells;
import lod.lloydmod.stats.LloydTemplate;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lod.dragoonmodifier.DragoonModifier.isHardMode;
import static lod.dragoonmodifier.DragoonModifier.isHellMode;

public class Lloyd extends LloydTemplate {
  private static final String template = "Lloyd";
  private static Map<String, List<Integer>> baseStats = new HashMap<>();
  private static Map<String, List<Integer>> dragoonStats = new HashMap<>();
  private final Latch<Image> portrait = new Latch<>(() -> Image.load(Path.of("mods",  DragoonModifier.MOD_ID, "gfx", "lloyd.png")));

  @Override
  public CharacterData2c make(final GameState52c gameState) {
    baseStats = TemplateCommon.getBaseStats(template);
    dragoonStats = TemplateCommon.getDragoonStats(template);

    final CharacterData2c character = super.make(gameState);

    if(isHardMode() || isHellMode()) {
      character.getAllSpells().clear();
      character.addSpell(LloydSpells.RUNNING_SLASH.getId(), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion()))).unlock(gameState.timestamp_a0);
      character.addSpell(LloydSpells.SPECTRAL_SWORD.getId(), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion()))).unlock(gameState.timestamp_a0 + 1);
      character.addSpell(LloydSpells.SWORD_RUSH.getId(), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion()))).unlock(gameState.timestamp_a0 + 2);
      character.addSpell(LloydSpells.SPINNING_GALE.getId(), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion()))).unlock(gameState.timestamp_a0 + 3);
      character.addSpell(LloydSpells.FATAL_BLIZZARD.getId(), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion(), new SpellDragoonLevelUnlockCriterion(2))));
      character.addSpell(LloydSpells.AERIAL_CRASH.getId(), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion(), new SpellDragoonLevelUnlockCriterion(2))));
      character.addSpell(LloydSpells.BALL_BARRAGE.getId(), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion(), new SpellDragoonLevelUnlockCriterion(3))));
      character.addSpell(LloydSpells.TRANS_LIGHT.getId(), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion(), new SpellDragoonLevelUnlockCriterion(3))));
      character.addSpell(LloydSpells.RAVE_TWISTER.getId(), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion(), new SpellDragoonLevelUnlockCriterion(4))));
      character.addSpell(LloydSpells.SIGIL_OF_EXPLOSION.getId(), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion(), new SpellDragoonLevelUnlockCriterion(5))));
      character.addSpell(LloydSpells.DANCING_RAY.getId(), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion(), new SpellDragoonLevelUnlockCriterion(6))));
      character.addSpell(LloydSpells.DRACONIC_IMPALE.getId(), new CharacterSpellInfo(List.of(new SpellDragoonSpiritUnlockCriterion(), new SpellDragoonLevelUnlockCriterion(7))));
    }

    return character;
  }

  @Override
  public Image loadPortrait() {
    return this.portrait.get();
  }

  @Override
  public boolean canUseSpells(final CharacterData2c character) {
    return true;
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
}
