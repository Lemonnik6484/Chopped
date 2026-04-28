package dev.lemonnik.chopped.items;

import dev.lemonnik.chopped.blocks.ChoppedBlock;
import dev.lemonnik.chopped.client.gui.ChiselScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ChiselItem extends Item {
    public ChiselItem() {
        super(new Properties());
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof ChoppedBlock choppedBlock) {
            if (level.isClientSide) {
                Minecraft.getInstance().setScreen(new ChiselScreen(pos, choppedBlock.getVariants()));
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
