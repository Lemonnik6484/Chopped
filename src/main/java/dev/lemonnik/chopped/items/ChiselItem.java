package dev.lemonnik.chopped.items;

import dev.lemonnik.chopped.blocks.ChoppedBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ChiselItem extends Item {
    public ChiselItem() {
        super(new Properties());
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Block block = context.getLevel().getBlockState(context.getClickedPos()).getBlock();

        if (block instanceof ChoppedBlock choppedBlock) {

        }

        return InteractionResult.PASS;
    }
}
