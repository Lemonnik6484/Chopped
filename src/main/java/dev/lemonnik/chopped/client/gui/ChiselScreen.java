package dev.lemonnik.chopped.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.lemonnik.chopped.mixin.KeyMappingAccessor;
import dev.lemonnik.chopped.network.ChiselPayload;
import dev.lemonnik.chopped.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ChiselScreen extends Screen {
    private final BlockPos pos;
    private final List<Block> variants;
    private final int columns = 4;
    private Block hoveredBlock = null;
    private long openedAt = -1;
    private static final long GRACE_MS = 200;

    public ChiselScreen(BlockPos pos, List<Block> variants) {
        super(Component.literal("Chisel"));
        this.pos = pos;
        this.variants = variants;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (openedAt == -1) openedAt = System.currentTimeMillis();

        int size = variants.size();
        if (size == 0) return;

        int rows = (int) Math.ceil((double) size / columns);
        int cellWidth = 30;
        int cellHeight = 30;
        int totalWidth = Math.min(size, columns) * cellWidth;
        int totalHeight = rows * cellHeight;

        int startX = (this.width - totalWidth) / 2;
        int startY = (this.height - totalHeight) / 2;

        hoveredBlock = null;

        for (int i = 0; i < size; i++) {
            int row = i / columns;
            int col = i % columns;
            int x = startX + col * cellWidth;
            int y = startY + row * cellHeight;

            Block block = variants.get(i);
            boolean isHovered = mouseX >= x && mouseX < x + cellWidth && mouseY >= y && mouseY < y + cellHeight;

            if (isHovered) {
                hoveredBlock = block;
                renderBlock(guiGraphics, block, x, y, 1.5f);
            } else {
                renderBlock(guiGraphics, block, x, y, 1.0f);
            }
        }

        boolean pastGrace = System.currentTimeMillis() - openedAt > GRACE_MS;
        if (pastGrace && !isUseKeyDown()) {
            onRelease();
        }
    }

    private void renderBlock(GuiGraphics guiGraphics, Block block, int x, int y, float scale) {
        ItemStack stack = new ItemStack(block);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + 15, y + 15, 0);
        guiGraphics.pose().scale(scale, scale, scale);
        guiGraphics.pose().translate(-8, -8, 0);
        guiGraphics.renderItem(stack, 0, 0);
        guiGraphics.pose().popPose();
    }

    private boolean isUseKeyDown() {
        InputConstants.Key key = ((KeyMappingAccessor) Minecraft.getInstance().options.keyUse).getKey();
        long handle = Minecraft.getInstance().getWindow().getWindow();

        if (key.getType() == InputConstants.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(handle, key.getValue()) == GLFW.GLFW_PRESS;
        } else {
            return InputConstants.isKeyDown(handle, key.getValue());
        }
    }

    private void onRelease() {
        if (hoveredBlock != null) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(hoveredBlock);
            NetworkHandler.sendToServer(new ChiselPayload(pos, id.toString()));
        }
        this.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
