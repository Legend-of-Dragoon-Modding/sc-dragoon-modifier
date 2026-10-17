package lod.dragoonmodifier.external.lloyd;

import legend.game.characters.RegisterCharacterTemplatesEvent;

public interface LloydInterface {
  void registerEvents();
  void registerTemplate(final RegisterCharacterTemplatesEvent event);
}
