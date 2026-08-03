package eleeter.skybubble.client.mixin;

import eleeter.elfontlib.emoji.EmojiFont;
import eleeter.skybubble.client.gui.EmojiPanelWidget;
import eleeter.skybubble.client.platform.loaders.EmojiAtlasLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin
{
    private static EmojiFont skybubble_emojiFont;
    private static boolean skybubble_emojiFontLoadAttempted = false;
    private static boolean skybubble_panelWasOpen = false;

    @Shadow
    protected EditBox input;

    @Shadow
    public abstract void insertText(final String text, final boolean replace);

    private EmojiPanelWidget skybubble_emojiPanel;

    private static final int PANEL_WIDTH  = 148;
    private static final int PANEL_HEIGHT = 180;
    private static final int TOGGLE_SIZE  = 14;

    @Inject(method = "init()V", at = @At("TAIL"))
    private void skybubble_addEmojiPanel(CallbackInfo ci)
    {
        if (!skybubble_emojiFontLoadAttempted)
        {
            skybubble_emojiFontLoadAttempted = true;
            skybubble_emojiFont = EmojiAtlasLoader.loadEmojiFont(Minecraft.getInstance());
        }

        Screen self = (Screen) (Object) this;

        this.input.setWidth(this.input.getWidth() - TOGGLE_SIZE - 2);

        int toggleX = self.width - 4 - TOGGLE_SIZE;
        int toggleY = self.height - 12 - 1;

        int panelX = self.width - 4 - PANEL_WIDTH;
        int panelY = toggleY - PANEL_HEIGHT - 2;

        this.skybubble_emojiPanel = new EmojiPanelWidget(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, skybubble_emojiFont, EmojiAtlasLoader.skybubble_lastAtlasData, pickedEmoji -> this.insertText(pickedEmoji, false));
        this.skybubble_emojiPanel.visible = skybubble_panelWasOpen;

        ((ScreenAccessor) (Object) this).skybubble_addRenderableWidget(this.skybubble_emojiPanel);

        Button toggleButton = Button.builder(Component.literal("\u263A"), button ->
                {
                    this.skybubble_emojiPanel.toggle();skybubble_panelWasOpen = this.skybubble_emojiPanel.visible;
                }).bounds(toggleX, toggleY, TOGGLE_SIZE, TOGGLE_SIZE).build();
        ((ScreenAccessor) (Object) this).skybubble_addRenderableWidget(toggleButton);
    }
}