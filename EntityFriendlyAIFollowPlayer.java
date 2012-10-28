package net.minecraft.src;

public class EntityFriendlyAIFollowPlayer extends EntityAIBase
{
    private IFriendAble follower;
    private EntityLiving theOwner;
    World theWorld;
    private float field_48303_f;
    private PathNavigate petPathfinder;
    private int field_48310_h;
    float maxDist;
    float minDist;
    private boolean field_48311_i;

    public EntityFriendlyAIFollowPlayer(IFriendAble par1EntityTameable, float par2, float par3, float par4)
    {
        follower = par1EntityTameable;
        theWorld = par1EntityTameable.getWorld();
        field_48303_f = par2*2;
        petPathfinder = ((EntityLiving)par1EntityTameable).getNavigator();
        minDist = par4;
        maxDist = par3;
        setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if (!follower.isFollowFlag()) return false;
    	EntityPlayer targetPlayer=theWorld.getClosestPlayerToEntity((Entity)follower, maxDist);
    	if (targetPlayer==null) return false;
        if (((Entity)follower).getDistanceSqToEntity(targetPlayer) < (double)(minDist * minDist))
        {
            return false;
        }
        else
        {
            theOwner = targetPlayer;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !petPathfinder.noPath() && ((Entity) follower).getDistanceSqToEntity(theOwner) > (double)(maxDist * maxDist) && follower.isFollowFlag();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        field_48310_h = 0;
        field_48311_i = ((EntityLiving) follower).getNavigator().getAvoidsWater();
        ((EntityLiving) follower).getNavigator().setAvoidsWater(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        theOwner = null;
        petPathfinder.clearPathEntity();
        ((EntityLiving) follower).getNavigator().setAvoidsWater(field_48311_i);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        ((EntityLiving) follower).getLookHelper().setLookPositionWithEntity(theOwner, 10F, ((EntityLiving) follower).getVerticalFaceSpeed());

        if (!follower.isFollowFlag())
        {
            return;
        }

        if (--field_48310_h > 0)
        {
            return;
        }

        field_48310_h = 10;

        if (petPathfinder.tryMoveToEntityLiving(theOwner, field_48303_f))
        {
            return;
        }

        if (((Entity) follower).getDistanceSqToEntity(theOwner) < 144D)
        {
            return;
        }

        int i = MathHelper.floor_double(theOwner.posX) - 2;
        int j = MathHelper.floor_double(theOwner.posZ) - 2;
        int k = MathHelper.floor_double(theOwner.boundingBox.minY);

        for (int l = 0; l <= 4; l++)
        {
            for (int i1 = 0; i1 <= 4; i1++)
            {
                if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && theWorld.isBlockNormalCube(i + l, k - 1, j + i1) && !theWorld.isBlockNormalCube(i + l, k, j + i1) && !theWorld.isBlockNormalCube(i + l, k + 1, j + i1))
                {
                    ((Entity) follower).setLocationAndAngles((float)(i + l) + 0.5F, k, (float)(j + i1) + 0.5F,((EntityLiving) follower).rotationYaw, ((EntityLiving)follower).rotationPitch);
                    petPathfinder.clearPathEntity();
                    return;
                }
            }
        }
    }
}
