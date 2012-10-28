package net.minecraft.src;

public class EntityFriendlyAIPlayerHurtByTarget extends EntityAITarget
{
	IFriendAble field_48394_a;
    EntityLiving field_48393_b;

    public EntityFriendlyAIPlayerHurtByTarget(IFriendAble par1EntityTameable)
    {
        super((EntityLiving)par1EntityTameable, 32F, false);
        field_48394_a = par1EntityTameable;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!field_48394_a.isEscortFlag())
        {
            return false;
        }

        EntityLiving entityliving = field_48394_a.getWorld().getClosestPlayerToEntity((Entity)field_48394_a, 32F);

        if (entityliving == null)
        {
            return false;
        }
        else
        {
            field_48393_b = entityliving.getAITarget();
            return isSuitableTarget(field_48393_b, false);
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        taskOwner.setAttackTarget(field_48393_b);
        super.startExecuting();
    }
}
