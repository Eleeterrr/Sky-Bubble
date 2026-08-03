package eleeter.skybubble.client.render;

import java.util.UUID;

public interface PlayerLocator
{
    boolean exists(UUID uuid);

    Vec3f getLerpedPosition(UUID uuid, float tickDelta);

    float getStandingEyeHeight(UUID uuid);

    float getNameTagAnchorHeight(UUID uuid);

    boolean isLocalPlayerFirstPerson(UUID uuid);
}
