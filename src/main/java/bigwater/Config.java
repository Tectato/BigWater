package bigwater;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue FALLBACK_SCALE = BUILDER
            .comment("The default scale for fluid textures if resource packs don't provide any.")
            .defineInRange("fallbackTextureScale", 1, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue FORCE_FALLBACK_SCALE = BUILDER
            .comment("Whether to override pack-provided scales with fallbackTextureScale.")
            .define("forceFallbackTextureScale", false);

    static final ModConfigSpec SPEC = BUILDER.build();
}
