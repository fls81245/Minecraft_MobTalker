package net.minecraft.src;

public class EntityFriendlyAIPlayerHurtTarget extends EntityAITarget
{
    IFriendAble field_48392_a;
    EntityLiving field_48391_b;

    public EntityFriendlyAIPlayerHurtTarget(IFriendAble par1EntityTameable)
    {
        super((EntityLiving)par1EntityTameable, 32F, false);
        field_48392_a = par1EntityTameable;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!field_48392_a.isEscortFlag())
        {
            return false;
        }

        EntityLiving entityliving = field_48392_a.getWorld().getClosestPlayerToEntity((Entity)field_48392_a, 32F);

        if (entityliving == null)
        {
            return false;
        }
        else
        {
            field_48391_b = entityliving.getLastAttackingEntity();
            return isSuitableTarget(field_48391_b, false);
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        taskOwner.setAttackTarget(field_48391_b);
        super.startExecuting();
    }
}
