package lod.dragoonmodifier.configs;

import legend.core.IoHelper;
import legend.core.MathHelper;
import legend.game.inventory.screens.controls.NumberSpinner;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;

public class ConfigUltimateBossDefeated extends ConfigEntry<Integer> {
    public ConfigUltimateBossDefeated() {
        super(1, ConfigStorageLocation.SAVE, ConfigUltimateBossDefeated::serializer, ConfigUltimateBossDefeated::deserializer);

        this.setEditControl((number, gameState) -> {
            final NumberSpinner<Integer> spinner = NumberSpinner.intSpinner(number, 1, 40);
            spinner.setDisabled(true);
            return spinner;
        });
    }

    private static byte[] serializer(final int val) {
        final byte[] data = new byte[2];
        MathHelper.set(data, 0, 2, val);
        return data;
    }

    private static int deserializer(final byte[] data) {
        if(data.length == 4) {
            return IoHelper.readInt(data, 0);
        }

        return 1;
    }
}
