package eleeter.skybubble.client.platform;

import eleeter.skybubble.client.render.PlayerLocator;
import eleeter.skybubble.client.render.Vec3f;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class MinecraftPlayerLocator implements PlayerLocator
{

    private Player resolve(UUID uuid)
    {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null)
        {
            return null;
        }
        return client.level.getPlayerByUUID(uuid);
    }

    @Override
    public boolean exists(UUID uuid)
    {
        return resolve(uuid) != null;
    }

    @Override
    public Vec3f getLerpedPosition(UUID uuid, float tickDelta)
    {
        Player player = resolve(uuid);
        if (player == null)
        {
            return new Vec3f(0, 0, 0);
        }

        Vec3 pos = player.getPosition(tickDelta);
        return new Vec3f(pos.x, pos.y, pos.z);
    }

    @Override
    public float getStandingEyeHeight(UUID uuid)
    {
        Player player = resolve(uuid);
        return player != null ? player.getEyeHeight() : 0f;
    }

    @Override
    public float getNameTagAnchorHeight(UUID uuid)
    {
        Player player = resolve(uuid);
        if (player == null)
        {
            return 0f;
        }
        return (float) player.getBoundingBox().getYsize();
    }

    @Override
    public boolean isLocalPlayerFirstPerson(UUID uuid)
    {
        Minecraft client = Minecraft.getInstance();
        Player player = resolve(uuid);
        return player == client.player && client.options.getCameraType().isFirstPerson();
    }
}