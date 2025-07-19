package lod.dragoonmodifier.saves;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SaveFile {
  private SaveFile() {}

  public static void save(final DraModSaveFile save, final String location) {
    try(final FileOutputStream file = new FileOutputStream(location)) {
      new ObjectOutputStream(file).writeObject(save);
    } catch(final IOException e) {
      System.out.println("[DRAGOON MODIFIER] FAILED TO SAVE");
    }
  }

  public static DraModSaveFile load(final String location) {
    try(final FileInputStream file = new FileInputStream(location)) {
      return (DraModSaveFile) new ObjectInputStream(file).readObject();
    } catch(final IOException | ClassNotFoundException e) {
      System.out.println("[DRAGOON MODIFIER] FAILED TO LOAD: " + location);
      return new DraModSaveFile();
    }
  }

  public static void delete(final Path location, final String fileName) {
    try {
      Files.delete(location.resolve(fileName + ".dragoon_modifier"));
    } catch(IOException e) {
      System.out.println("[DRAGOON MODIFIER] FAILED TO DELETE: " + location.resolve(fileName + ".dragoon_modifier"));
    }
  }
}
