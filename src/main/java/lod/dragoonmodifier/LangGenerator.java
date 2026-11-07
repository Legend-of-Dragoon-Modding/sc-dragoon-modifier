package lod.dragoonmodifier;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Pattern;

public class LangGenerator {
  private static final Pattern PATTERN = Pattern.compile("%(?!s)");
  static BufferedReader input;
  static CSVReader csv;
  static List<String[]> list;

  static void main(final String[] args) {
    try(final PrintWriter output = new PrintWriter(Files.newBufferedWriter(Path.of("dragoon_modifier", args[0], "lang", "en.lang"), StandardOpenOption.CREATE))) {
      input = new BufferedReader(new FileReader(String.valueOf(Path.of("dragoon_modifier", "en.lang"))));
      for(final String line : input.lines().toList()) {
        output.println(line);
      }
      input.close();
      output.println();

      output.println("# Equips");
      openFile(Path.of("dragoon_modifier", args[0], "equip_stats.csv"));
      for(final String[] line : list) {
        output.println("dragoon_modifier.equipment." + line[38].split(":")[1] + ".name=" + line[36]);
        output.println("dragoon_modifier.equipment." + line[38].split(":")[1] + ".description=" + PATTERN.matcher(line[37].replace("§", "\\n")).replaceAll("%%"));
      }
      output.println();
      closeFile();

      output.println("# Items");
      openFile(Path.of("dragoon_modifier", args[0], "thrown_item_stats.csv"));
      for(final String[] line : list) {
        if(line[33].split(":")[1].length() >= 3) {
          output.println("dragoon_modifier.items." + line[33].split(":")[1] + ".name=" + line[25]);
          output.println("dragoon_modifier.items." + line[33].split(":")[1] + ".description=" + PATTERN.matcher(line[26].replace("§", "\\n")).replaceAll("%%"));
          output.println("dragoon_modifier.items." + line[33].split(":")[1] + ".battle_description=" + PATTERN.matcher(line[27].replace("§", "\\n")).replaceAll("%%"));
          output.println("dragoon_modifier.items." + line[33].split(":")[1] + ".use=" + PATTERN.matcher(line[34].replace("§", "\\n")).replaceAll("%%"));
        }
      }
      output.println();
      closeFile();

      output.println("# Spells");
      openFile(Path.of("dragoon_modifier", args[0], "spells.csv"));
      for(final String[] line : list) {
        if(line[14].length() >= 3) {
          output.println("dragoon_modifier.spells." + line[14] + ".name=" + line[15]);
          output.println("dragoon_modifier.spells." + line[14] + ".description=" + PATTERN.matcher(line[16].replace("§", "\\n")).replaceAll("%%"));
        }
      }
      closeFile();

      System.out.println("Created en.lang for " + args[0]);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  static void openFile(final Path path) {
    try {
      input = new BufferedReader(new FileReader(String.valueOf(path)));
      csv = new CSVReader(input);
      list = csv.readAll();
      list.remove(0);
    } catch(final IOException | CsvException e) {
      throw new RuntimeException(e);
    }
  }

  static void closeFile() {
    try {
      input.close();
      csv.close();
    } catch(final IOException e) {
      throw new RuntimeException(e);
    }
  }
}