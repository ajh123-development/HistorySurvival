package uk.minersonline.Minecart.core.physics;

import uk.minersonline.Minecart.core.math.Vec3f;

public class Player {
    public RigidBodyCustom body;
    public PairCachingGhostObjectCustom ghost;
    public Vec3f velocity = new Vec3f();
    public boolean falling = true;
    public boolean noclip = true;
    public boolean jump = false;
}
