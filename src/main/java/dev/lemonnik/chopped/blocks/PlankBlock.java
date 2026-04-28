package dev.lemonnik.chopped.blocks;

import dev.lemonnik.chopped.registers.BlocksRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.List;

public class PlankBlock extends Block implements ChoppedBlock {
    public PlankBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
        );
    }

    @Override
    public List<Block> getVariants() {
        List<Block> blocks = new ArrayList<>();

        blocks.add(BlocksRegistry.TEST_PLANKS);
        blocks.add(BlocksRegistry.SUPER_TEST_PLANKS);

        Registry<Block> registry = BuiltInRegistries.BLOCK;
        TagKey<Block> tag = BlockTags.PLANKS;

        registry.getTag(tag).ifPresent(named -> {
            named.forEach(holder -> blocks.add(holder.value()));
        });

        return blocks;
    }
}
