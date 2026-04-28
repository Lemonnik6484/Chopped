package dev.lemonnik.chopped.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.lemonnik.chopped.mixin.KeyMappingAccessor;
import dev.lemonnik.chopped.network.ChiselPayload;
import dev.lemonnik.chopped.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ChiselScreen extends Screen {
    private final BlockPos pos;
    private final List<Block> variants;
    private Block hoveredBlock = null;
    private Block lastHoveredBlock = null;
    private long openedAt = -1;
    private static final long GRACE_MS = 200;

    private static final int HEX_SIZE = 18;
    private static final int ITEM_SIZE = 16;

    public ChiselScreen(BlockPos pos, List<Block> variants) {
        super(Component.literal("Chisel"));
        this.pos = pos;
        this.variants = variants;
    }

    private int[] hexCenter(int col, int row, int originX, int originY) {
        double hexW = HEX_SIZE * 2.0;
        double hexH = Math.sqrt(3) * HEX_SIZE;

        double xSpacing = hexW * 0.75;
        double ySpacing = hexH;

        double cx = originX + col * xSpacing + HEX_SIZE;
        double cy = originY + row * ySpacing + (HEX_SIZE * Math.sqrt(3) / 2.0);

        if (col % 2 == 1) {
            cy += ySpacing / 2.0;
        }

        return new int[]{(int) cx, (int) cy};
    }

    private int[][] computeHexLayout(int count) {
        int cols = Math.max(1, (int) Math.ceil(Math.sqrt(count * 1.2)));
        int[][] layout = new int[count][2];
        int placed = 0;
        for (int row = 0; placed < count; row++) {
            for (int col = 0; col < cols && placed < count; col++) {
                layout[placed][0] = col;
                layout[placed][1] = row;
                placed++;
            }
        }
        return layout;
    }

    private int[] computeGridSize(int[][] layout) {
        double hexW = HEX_SIZE * 2.0;
        double hexH = Math.sqrt(3) * HEX_SIZE;
        double xSpacing = hexW * 0.75;
        double ySpacing = hexH;

        int maxCol = 0, maxRow = 0;
        for (int[] pos : layout) {
            if (pos[0] > maxCol) maxCol = pos[0];
            if (pos[1] > maxRow) maxRow = pos[1];
        }

        int gridW = (int) (maxCol * xSpacing + hexW);
        int gridH = (int) (maxRow * ySpacing + hexH + ySpacing / 2.0);
        return new int[]{gridW, gridH};
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (openedAt == -1) openedAt = System.currentTimeMillis();

        int size = variants.size();
        if (size == 0) return;

        int[][] layout = computeHexLayout(size);
        int[] gridSize = computeGridSize(layout);

        int originX = (this.width - gridSize[0]) / 2;
        int originY = (this.height - gridSize[1]) / 2;

        hoveredBlock = null;

        for (int i = 0; i < size; i++) {
            int col = layout[i][0];
            int row = layout[i][1];
            int[] center = hexCenter(col, row, originX, originY);
            int cx = center[0];
            int cy = center[1];

            Block block = variants.get(i);
            boolean isHovered = isInsideHex(mouseX, mouseY, cx, cy);

            if (isHovered) {
                hoveredBlock = block;
                guiGraphics.renderTooltip(font, block.getName(), mouseX, mouseY);
            }

            drawHexBackground(guiGraphics, cx, cy, isHovered);

            renderItemCentered(guiGraphics, block, cx, cy, isHovered ? 1.2f : 1.0f);
        }

        if (hoveredBlock != lastHoveredBlock) {
            if (hoveredBlock != null) {
                Minecraft.getInstance().getSoundManager().play(
                        SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 0.6f)
                );
            }
            lastHoveredBlock = hoveredBlock;
        }

        if (hoveredBlock != null) {
            for (int i = 0; i < size; i++) {
                if (variants.get(i) == hoveredBlock) {
                    int col = layout[i][0];
                    int row = layout[i][1];
                    int[] center = hexCenter(col, row, originX, originY);
                    drawSelectionFrame(guiGraphics, center[0], center[1]);
                    break;
                }
            }
        }

        boolean pastGrace = System.currentTimeMillis() - openedAt > GRACE_MS;
        if (pastGrace && !isUseKeyDown()) {
            onRelease();
        }
    }

    private void drawHexBackground(GuiGraphics guiGraphics, int cx, int cy, boolean hovered) {
        int r = HEX_SIZE - 1;
        int[] xs = new int[6];
        int[] ys = new int[6];
        for (int k = 0; k < 6; k++) {
            double angle = Math.toRadians(60.0 * k);
            xs[k] = (int) (cx + r * Math.cos(angle));
            ys[k] = (int) (cy + r * Math.sin(angle));
        }

        int bgColor = hovered ? 0xCC444444 : 0xAA222222;
        fillHexPoly(guiGraphics, xs, ys, bgColor);

        int borderColor = hovered ? 0xFFCCCCCC : 0xFF666666;
        for (int k = 0; k < 6; k++) {
            int next = (k + 1) % 6;
            drawLine(guiGraphics, xs[k], ys[k], xs[next], ys[next], borderColor);
        }
    }

    private void drawSelectionFrame(GuiGraphics guiGraphics, int cx, int cy) {
        int r = HEX_SIZE + 1;
        int[] xs = new int[6];
        int[] ys = new int[6];
        for (int k = 0; k < 6; k++) {
            double angle = Math.toRadians(60.0 * k);
            xs[k] = (int) (cx + r * Math.cos(angle));
            ys[k] = (int) (cy + r * Math.sin(angle));
        }

        int r2 = HEX_SIZE + 3;
        int[] xs2 = new int[6];
        int[] ys2 = new int[6];
        for (int k = 0; k < 6; k++) {
            double angle = Math.toRadians(60.0 * k);
            xs2[k] = (int) (cx + r2 * Math.cos(angle));
            ys2[k] = (int) (cy + r2 * Math.sin(angle));
        }
        for (int k = 0; k < 6; k++) {
            int next = (k + 1) % 6;
            drawLine(guiGraphics, xs2[k], ys2[k], xs2[next], ys2[next], 0x44FFFFFF);
        }

        for (int k = 0; k < 6; k++) {
            int next = (k + 1) % 6;
            drawLine(guiGraphics, xs[k], ys[k], xs[next], ys[next], 0xFFFFFFAA);
        }
    }

    private void fillHexPoly(GuiGraphics guiGraphics, int[] xs, int[] ys, int color) {
        for (int k = 1; k < 5; k++) {
            fillTriangle(guiGraphics, xs[0], ys[0], xs[k], ys[k], xs[k + 1], ys[k + 1], color);
        }
    }

    private void fillTriangle(GuiGraphics guiGraphics, int x0, int y0, int x1, int y1, int x2, int y2, int color) {
        if (y0 > y1) { int tx=x0; int ty=y0; x0=x1; y0=y1; x1=tx; y1=ty; }
        if (y0 > y2) { int tx=x0; int ty=y0; x0=x2; y0=y2; x2=tx; y2=ty; }
        if (y1 > y2) { int tx=x1; int ty=y1; x1=x2; y1=y2; x2=tx; y2=ty; }

        int totalH = y2 - y0;
        if (totalH == 0) return;

        for (int scanY = y0; scanY <= y2; scanY++) {
            boolean secondHalf = scanY > y1 || y1 == y0;
            int segH = secondHalf ? (y2 - y1) : (y1 - y0);
            if (segH == 0) continue;

            float alpha = (float)(scanY - y0) / totalH;
            float beta = secondHalf
                    ? (float)(scanY - y1) / segH
                    : (float)(scanY - y0) / segH;

            int ax = (int)(x0 + (x2 - x0) * alpha);
            int bx = secondHalf
                    ? (int)(x1 + (x2 - x1) * beta)
                    : (int)(x0 + (x1 - x0) * beta);

            if (ax > bx) { int t = ax; ax = bx; bx = t; }
            guiGraphics.fill(ax, scanY, bx + 1, scanY + 1, color);
        }
    }

    private void drawLine(GuiGraphics guiGraphics, int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0), dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1, sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            guiGraphics.fill(x0, y0, x0 + 1, y0 + 1, color);
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 < dx)  { err += dx; y0 += sy; }
        }
    }

    private boolean isInsideHex(int mx, int my, int cx, int cy) {
        double dx = Math.abs(mx - cx);
        double dy = Math.abs(my - cy);
        double r = HEX_SIZE - 1;

        if (dx > r) return false;
        if (dy > r * Math.sqrt(3) / 2.0) return false;
        return (r * Math.sqrt(3) / 2.0 * r - r / 2.0 * dy - r * dx) >= 0
                || dy <= r * Math.sqrt(3) / 2.0 && dx <= r / 2.0
                || (dx + dy / Math.sqrt(3)) <= r;
    }

    private void renderItemCentered(GuiGraphics guiGraphics, Block block, int cx, int cy, float scale) {
        ItemStack stack = new ItemStack(block);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(cx, cy, 0);
        guiGraphics.pose().scale(scale, scale, scale);
        guiGraphics.pose().translate(-ITEM_SIZE / 2.0f, -ITEM_SIZE / 2.0f, 0);
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