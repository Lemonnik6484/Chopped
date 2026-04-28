package dev.lemonnik.chopped.registers;

import dev.lemonnik.chopped.Chopped;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class TagsRegistry {
    public static final TagKey<Block> PLANKS = create("chopped/planks");

    private static TagKey<Block> create(String path) {
        return TagKey.create(Registries.BLOCK, Chopped.id(path));
    }

    public static List<Block> listFromTag(TagKey<Block> tagKey) {
        return BuiltInRegistries.BLOCK.getTag(tagKey)
                .map(tag -> tag.stream()
                .map(Holder::value)
                .toList())
                .orElse(List.of());
    }

    public static List<Block> listFromBlock(Block block) {
        var registry = BuiltInRegistries.BLOCK;
        var holder = registry.wrapAsHolder(block);

        return registry.getTags()
                .filter(pair -> pair.getFirst().location().getNamespace().equals(Chopped.MOD_ID))
                .filter(pair -> pair.getFirst().location().getPath().startsWith("chopped/"))
                .filter(pair -> pair.getSecond().contains(holder))
                .findFirst()
                .map(pair -> pair.getSecond().stream()
                        .map(Holder::value)
                        .toList())
                .orElse(List.of());
    }

    public static void initialize() {
        Chopped.LOGGER.info("Registering Chopped tags...");
    }
}
