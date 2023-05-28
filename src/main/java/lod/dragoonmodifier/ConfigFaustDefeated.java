package lod.dragoonmodifier;

import legend.core.IoHelper;
import legend.core.MathHelper;
import legend.game.inventory.screens.controls.NumberSpinner;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;

public class ConfigFaustDefeated extends ConfigEntry<Integer> {
    public ConfigFaustDefeated() {
        super(0, ConfigStorageLocation.SAVE, ConfigFaustDefeated::serializer, ConfigFaustDefeated::deserializer);

        this.setEditControl((number, gameState) -> {
            final NumberSpinner<Integer> spinner = NumberSpinner.intSpinner(number, 0, 9999);
            spinner.setDisabled(true);
            return spinner;
        });
    }

    private static byte[] serializer(final int val) {
        final byte[] data = new byte[4];
        MathHelper.set(data, 0, 4, val);
        return data;
    }

    private static int deserializer(final byte[] data) {
        if(data.length == 4) {
            return IoHelper.readInt(data, 0);
        }

        return 0;
    }
}
