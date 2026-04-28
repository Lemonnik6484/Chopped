package dev.lemonnik.chopped.items;

import dev.lemonnik.chopped.client.gui.ChiselScreen;
import dev.lemonnik.chopped.registers.TagsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChiselItem extends Item {
    public ChiselItem() {
        super(new Properties());
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Block clickedBlock = level.getBlockState(pos).getBlock();

        List<Block> variants = TagsRegistry.listFromBlock(clickedBlock);

        if (variants != null && !variants.isEmpty()) {
            if (level.isClientSide) {
                Minecraft.getInstance().setScreen(new ChiselScreen(pos, variants));
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
