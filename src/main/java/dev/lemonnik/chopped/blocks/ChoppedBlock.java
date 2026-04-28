package dev.lemonnik.chopped.blocks;

import net.minecraft.world.level.block.Block;

import java.util.List;

public interface ChoppedBlock {
    List<Block> getVariants();
}
