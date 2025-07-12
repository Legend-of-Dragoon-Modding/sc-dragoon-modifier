package lod.dragoonmodifier;

import legend.game.types.Flags;

import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;

public class scriptFlagTracker implements Runnable {
  public Flags scriptFlags2 = new Flags(32);
  public Flags scriptFlags1 = new Flags(8);
  public boolean firstRun = true;

  @Override
  public void run() {
    while(true) {
      try {
        if(!this.firstRun) {
          for(int i = 0; i < 32; i++) {
            if(gameState_800babc8.scriptFlags2_bc.getRaw(i) != this.scriptFlags2.getRaw(i)) {
              final int newBit = gameState_800babc8.scriptFlags2_bc.getRaw(i) - this.scriptFlags2.getRaw(i);
              System.out.println("[NEW SCRIPT 2][" + i + "] " + Integer.toHexString(newBit) + " - " + getBitIndex(newBit));
              this.scriptFlags2.setRaw(i, gameState_800babc8.scriptFlags2_bc.getRaw(i));
            }
          }

          for(int i = 0; i < 8; i++) {
            if(gameState_800babc8.scriptFlags1_13c.getRaw(i) != this.scriptFlags1.getRaw(i)) {
              final int newBit = gameState_800babc8.scriptFlags1_13c.getRaw(i) - this.scriptFlags1.getRaw(i);
              System.out.println("[NEW SCRIPT 1][" + i + "] " + Integer.toHexString(newBit) + " - " + getBitIndex(newBit));
              this.scriptFlags1.setRaw(i, gameState_800babc8.scriptFlags1_13c.getRaw(i));
            }
          }
        } else {
          boolean test = false;

          for(int i = 0; i < 32; i++) {
            if(gameState_800babc8.scriptFlags2_bc.getRaw(i) != this.scriptFlags2.getRaw(i)) {
              test = true;
            }
          }

          for(int i = 0; i < 8; i++) {
            if(gameState_800babc8.scriptFlags1_13c.getRaw(i) != this.scriptFlags1.getRaw(i)) {
              test = true;
            }
          }

          if(test) {
            for(int i = 0; i < 32; i++) {
              this.scriptFlags2.setRaw(i, gameState_800babc8.scriptFlags2_bc.getRaw(i));
            }
            for(int i = 0; i < 8; i++) {
              this.scriptFlags1.setRaw(i, gameState_800babc8.scriptFlags1_13c.getRaw(i));
            }
            this.firstRun = false;
          }
        }

        try {
          Thread.sleep(2000);
        } catch(final InterruptedException e) {
          e.printStackTrace();
        }
      } catch(final Exception ignored) {
      }
    }

  }

  public static int getBitIndex(int number) {
    if (!((number != 0) && ((number & (number - 1)) == 0))) {
      return -1;
    }
    int index = 0;
    while (number > 1) {
      number = number >> 1;
      index++;
    }
    return index;
  }
}
