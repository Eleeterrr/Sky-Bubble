package eleeter.skybubble.client.mixin;

import eleeter.skybubble.client.chat.ChatBubbleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ChatType;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ChatMessageMixin
{
    private static boolean isWhisperType(ClientboundPlayerChatPacket packet)
    {
        Holder<ChatType> holder = packet.chatType().chatType();
        return holder.is(ChatType.MSG_COMMAND_INCOMING) || holder.is(ChatType.MSG_COMMAND_OUTGOING) || holder.is(ChatType.TEAM_MSG_COMMAND_INCOMING) || holder.is(ChatType.TEAM_MSG_COMMAND_OUTGOING);
    }

    @Inject(at = @At("HEAD"), method = "handlePlayerChat")
    private void skybubble_onChatMessage(ClientboundPlayerChatPacket packet, CallbackInfo ci)
    {
        try
        {
            if (isWhisperType(packet))
            {
                return;
            }

            java.util.UUID uuid = packet.sender();

            if (uuid == null)
            {
                return;
            }

            String raw = packet.body().content();
            if (raw == null || raw.isBlank())
            {
                return;
            }
            Minecraft.getInstance().execute(() -> ChatBubbleManager.onChatMessage(uuid, raw));
        } catch (Exception e)
        {

            System.err.println("ChatMessageMixin error: " + e.getMessage());
        }
    }
}