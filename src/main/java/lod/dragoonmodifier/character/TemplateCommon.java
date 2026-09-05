package lod.dragoonmodifier.character;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import legend.core.GameEngine;
import legend.game.additions.AdditionHitProperties10;
import legend.game.additions.AdditionHits80;
import legend.game.additions.AdditionSound;
import legend.game.additions.SimpleAddition;
import legend.game.characters.Element;
import legend.lodmod.additions.RetailAddition;
import legend.lodmod.spells.DragonSpell;
import legend.lodmod.spells.RetailSpell;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static lod.dragoonmodifier.DragoonModifier.DIFFICULTY;

public class TemplateCommon {
  public static List<String[]> loadCSV(final String path) {
    try(final FileReader fr = new FileReader(path, StandardCharsets.UTF_8);
        final CSVReader csv = new CSVReader(fr)) {
      final List<String[]> list = csv.readAll();
      list.removeFirst();
      return list;
    } catch(final IOException | CsvException e) {
      throw new RuntimeException(e);
    }
  }

  public static Map<String, List<Integer>> getBaseStats(final String template) {
    final List<Integer> speed = new ArrayList<>();
    final List<Integer> at = new ArrayList<>();
    final List<Integer> mat = new ArrayList<>();
    final List<Integer> df = new ArrayList<>();
    final List<Integer> mdf = new ArrayList<>();
    final List<Integer> hp = new ArrayList<>();
    final List<Integer> exp = new ArrayList<>();

    for(final String[] baseStats : loadCSV("./mods/dragoon_modifier/" + GameEngine.CONFIG.getConfig(DIFFICULTY.get()) + '/' + template + "/base_stats.csv")) {
      speed.add(Integer.parseInt(baseStats[1]));
      at.add(Integer.parseInt(baseStats[2]));
      mat.add(Integer.parseInt(baseStats[3]));
      df.add(Integer.parseInt(baseStats[4]));
      mdf.add(Integer.parseInt(baseStats[5]));
      hp.add(Integer.parseInt(baseStats[6]));
      exp.add(Integer.parseInt(baseStats[7]));
    }

    final Map<String, List<Integer>> stats = new LinkedHashMap<>();
    stats.put("Speed", speed);
    stats.put("AT", at);
    stats.put("MAT", mat);
    stats.put("DF", df);
    stats.put("MDF", mdf);
    stats.put("HP", hp);
    stats.put("EXP", exp);
    return stats;
  }

  public static Map<String, List<Integer>> getDragoonStats(final String template) {
    final List<Integer> maxMp = new ArrayList<>();
    final List<Integer> at = new ArrayList<>();
    final List<Integer> mat = new ArrayList<>();
    final List<Integer> df = new ArrayList<>();
    final List<Integer> mdf = new ArrayList<>();
    final List<Integer> dexp = new ArrayList<>();

    for(final String[] dragoonStats : loadCSV("./mods/dragoon_modifier/" + GameEngine.CONFIG.getConfig(DIFFICULTY.get()) + '/' + template + "/dragoon_stats.csv")) {
      maxMp.add(Integer.parseInt(dragoonStats[0]));
      at.add(Integer.parseInt(dragoonStats[2]));
      mat.add(Integer.parseInt(dragoonStats[3]));
      df.add(Integer.parseInt(dragoonStats[4]));
      mdf.add(Integer.parseInt(dragoonStats[5]));
      dexp.add(Integer.parseInt(dragoonStats[6]));
    }

    final Map<String, List<Integer>> stats = new LinkedHashMap<>();
    stats.put("MaxMP", maxMp);
    stats.put("DAT", at);
    stats.put("DMAT", mat);
    stats.put("DDF", df);
    stats.put("DMDF", mdf);
    stats.put("DEXP", dexp);
    return stats;
  }

  public static RetailAddition getAddition(final String template, final String addition) {
    final RFC4180Parser parser = new RFC4180ParserBuilder()
      .withSeparator(',')
      .withQuoteChar('"')
      .build();

    String[] line;
    final int additionFile;
    final boolean countsTowardMastery;
    final int levels;
    final int hits;
    final List<SimpleAddition.LevelMultipliers> multipliers = new ArrayList<>();
    final List<AdditionHitProperties10> properties = new ArrayList<>();
    final int additionSounds;
    int additionsHitIndexes;

    try (final CSVReader csv = new CSVReaderBuilder(new FileReader("./mods/dragoon_modifier/" + GameEngine.CONFIG.getConfig(DIFFICULTY.get()) + '/' + template + '/' + addition + ".csv"))
      .withCSVParser(parser)
      .build()) {
      csv.readNext();
      csv.readNext();
      line = csv.readNext();
      additionFile = Integer.parseInt(line[0]);
      countsTowardMastery = Boolean.parseBoolean(line[1]);
      levels = Integer.parseInt(line[2]);
      hits = Integer.parseInt(line[3]);

      csv.readNext();
      csv.readNext();
      csv.readNext();

      for(int i = 0; i < levels; i++) {
        line = csv.readNext();
        multipliers.add(new SimpleAddition.LevelMultipliers(Float.parseFloat(line[0]), Float.parseFloat(line[1])));
      }

      csv.readNext();
      csv.readNext();

      additionSounds = csv.readNext().length - 16;

      for(int i = 0; i < hits; i++) {
        line = csv.readNext();

        final int[] hitData = new int[16];
        for (int x = 0; x < 16; x++) {
          hitData[x] = Integer.parseInt(line[x].trim());
        }

        final AdditionSound[] sounds = new AdditionSound[additionSounds];
        additionsHitIndexes = 0;
        for(int x = 0; x < additionSounds; x++) {
          final String[] soundIndex = line[16 + x].trim().split(",");
          if(!soundIndex[0].isBlank()) {
            sounds[x] = new AdditionSound(
              Integer.parseInt(soundIndex[0]),
              Integer.parseInt(soundIndex[1])
            );
            additionsHitIndexes++;
          }
        }

        switch(additionsHitIndexes) {
          case 1 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0]));
          case 2 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0], sounds[1]));
          case 3 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0], sounds[1], sounds[2]));
          case 4 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0], sounds[1], sounds[2], sounds[3]));
          case 5 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0], sounds[1], sounds[2], sounds[3], sounds[4]));
          case 6 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0], sounds[1], sounds[2], sounds[3], sounds[4], sounds[5]));
          default -> throw new IllegalArgumentException("Addition sounds invalid for " + addition);
        }
      }
    } catch(final IOException | CsvValidationException | NumberFormatException e) {
      System.out.println("ADDITION ERROR ON: " + template + " - " + addition);
      throw new RuntimeException(e);
    }
    return new RetailAddition(additionFile, countsTowardMastery, multipliers.toArray(new SimpleAddition.LevelMultipliers[0]), properties.toArray(new AdditionHitProperties10[0]));
  }

  public static int[] getAdditionUnlockCriteria(final String template, final String addition) {
    final RFC4180Parser parser = new RFC4180ParserBuilder()
      .withSeparator(',')
      .withQuoteChar('"')
      .build();

    try(final CSVReader csv = new CSVReaderBuilder(new FileReader("./mods/dragoon_modifier/" + GameEngine.CONFIG.getConfig(DIFFICULTY.get()) + '/' + template + '/' + addition + ".csv"))
      .withCSVParser(parser)
      .build()) {

      csv.readNext();
      csv.readNext();

      final String[] line = csv.readNext();

      return new int[] {Boolean.parseBoolean(line[1]) ? 1 : 0, Integer.parseInt(line[4])};
    } catch(final IOException | CsvValidationException | NumberFormatException e) {
      System.out.println("ADDITION UNLOCK ERROR ON: " + template + " - " + addition);
      throw new RuntimeException(e);
    }
  }

  public static List<String[]> getAdditionList() {
    return loadCSV("./mods/dragoon_modifier/" + GameEngine.CONFIG.getConfig(DIFFICULTY.get()) + "/addition_list.csv");
  }

  public static RetailSpell getSpell(final String[] spell) {
    final int targetType = Integer.parseInt(spell[0]);
    final int flags = Integer.parseInt(spell[1]);
    final int specialEffect = Integer.parseInt(spell[2]);
    final int damage = Integer.parseInt(spell[3]);
    final int multi = Integer.parseInt(spell[4]);
    final int accuracy = Integer.parseInt(spell[5]);
    final int mp = Integer.parseInt(spell[6]);
    final int statusChance = Integer.parseInt(spell[7]);
    final int elementFlag = Integer.parseInt(spell[8]);
    final int statusType = Integer.parseInt(spell[9]);
    final int buffType = Integer.parseInt(spell[10]);
    final int _0b = Integer.parseInt(spell[11]);
    final int index = Integer.parseInt(spell[12]);
    final String battleStage = spell[13];
    final String deffIndex = spell[17];

    if(battleStage.isBlank()) {
      if(!deffIndex.isBlank()) {
        return new DeffSpell(targetType, flags, specialEffect, damage, multi, accuracy, mp, statusChance, Element.fromFlag(elementFlag), statusType, buffType, _0b, Integer.parseInt(deffIndex));
      } else {
        return new RetailSpell(targetType, flags, specialEffect, damage, multi, accuracy, mp, statusChance, Element.fromFlag(elementFlag), statusType, buffType, _0b, index);
      }
    } else {
      return new DragonSpell(targetType, flags, specialEffect, damage, multi, accuracy, mp, statusChance, Element.fromFlag(elementFlag), statusType, buffType, _0b, index, Integer.parseInt(battleStage));
    }
  }

  public static Map<Integer, String> getSpellUnlocks(final String template) {
    final Map<Integer, String> spellUnlock = new LinkedHashMap<>();

    int level = 1;
    for(final String[] spells : loadCSV("./mods/dragoon_modifier/" + GameEngine.CONFIG.getConfig(DIFFICULTY.get()) + '/' + template + "/dragoon_stats.csv")) {
      if(!spells[1].isBlank()) {
        spellUnlock.put(level, spells[1]);
      }
      level++;
    }
    return spellUnlock;
  }

  public static AdditionHits80 getDragoonAddition(final String template, final String addition) {
    final RFC4180Parser parser = new RFC4180ParserBuilder()
      .withSeparator(',')
      .withQuoteChar('"')
      .build();

    String[] line;
    final int additionFile;
    final boolean countsTowardMastery;
    final int levels;
    final int hits;
    final List<SimpleAddition.LevelMultipliers> multipliers = new ArrayList<>();
    final List<AdditionHitProperties10> properties = new ArrayList<>();
    final int additionSounds;
    int additionsHitIndexes;

    try (final CSVReader csv = new CSVReaderBuilder(new FileReader("./mods/dragoon_modifier/" + GameEngine.CONFIG.getConfig(DIFFICULTY.get()) + '/' + template + '/' + addition + ".csv"))
      .withCSVParser(parser)
      .build()) {
      csv.readNext();
      csv.readNext();
      line = csv.readNext();
      additionFile = Integer.parseInt(line[0]);
      countsTowardMastery = Boolean.parseBoolean(line[1]);
      levels = Integer.parseInt(line[2]);
      hits = Integer.parseInt(line[3]);

      csv.readNext();
      csv.readNext();
      csv.readNext();

      for(int i = 0; i < levels; i++) {
        line = csv.readNext();
        multipliers.add(new SimpleAddition.LevelMultipliers(Float.parseFloat(line[0]), Float.parseFloat(line[1])));
      }

      csv.readNext();
      csv.readNext();

      additionSounds = csv.readNext().length - 16;

      for(int i = 0; i < hits; i++) {
        line = csv.readNext();

        final int[] hitData = new int[16];
        for (int x = 0; x < 16; x++) {
          hitData[x] = Integer.parseInt(line[x].trim());
        }

        final AdditionSound[] sounds = new AdditionSound[additionSounds];
        additionsHitIndexes = 0;
        for(int x = 0; x < additionSounds; x++) {
          final String[] soundIndex = line[16 + x].trim().split(",");
          if(!soundIndex[0].isBlank()) {
            sounds[x] = new AdditionSound(
              Integer.parseInt(soundIndex[0]),
              Integer.parseInt(soundIndex[1])
            );
            additionsHitIndexes++;
          }
        }

        switch(additionsHitIndexes) {
          case 0 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15]));
          case 1 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0]));
          case 2 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0], sounds[1]));
          case 3 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0], sounds[1], sounds[2]));
          case 4 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0], sounds[1], sounds[2], sounds[3]));
          case 5 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0], sounds[1], sounds[2], sounds[3], sounds[4]));
          case 6 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0], sounds[1], sounds[2], sounds[3], sounds[4], sounds[5]));
          case 7 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0], sounds[1], sounds[2], sounds[3], sounds[4], sounds[5], sounds[6]));
          case 8 -> properties.add(new AdditionHitProperties10(hitData[0], hitData[1], hitData[2], hitData[3], hitData[4], hitData[5], hitData[6], hitData[7],hitData[8], hitData[9], hitData[10], hitData[11], hitData[12], hitData[13], hitData[14], hitData[15],
            sounds[0], sounds[1], sounds[2], sounds[3], sounds[4], sounds[5], sounds[6], sounds[7]));
          default -> throw new IllegalArgumentException("Addition sounds invalid for " + addition);
        }
      }
    } catch(final IOException | CsvValidationException | NumberFormatException e) {
      System.out.println("ADDITION ERROR ON: " + template + " - " + addition);
      throw new RuntimeException(e);
    }
    return new AdditionHits80(properties.toArray(new AdditionHitProperties10[0]));
  }
}
