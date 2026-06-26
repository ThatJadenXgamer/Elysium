package net.jadenxgamer.elysium_api.impl.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import static net.jadenxgamer.elysium_api.impl.config.ElysiumConfig.*;

public class ElysiumConfigImpl {

    public static ModConfigSpec COMMON;

    static {
        ModConfigSpec.Builder COMMON = new ModConfigSpec.Builder();

        COMMON.comment("Building Tools").push("buildingTools");
        BuildingTools.init(COMMON);
        COMMON.pop();

        ElysiumConfigImpl.COMMON = COMMON.build();
    }


    public static class BuildingTools {
        public static void init(ModConfigSpec.Builder builder) {
            BLOCK_SWAP = builder
                    .comment("Replace blocks without breaking them first")
                    .define("blockSwap", false);
        }
    }
}
