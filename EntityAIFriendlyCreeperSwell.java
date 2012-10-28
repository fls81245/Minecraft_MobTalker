package net.minecraft.src;

public class EntityAIFriendlyCreeperSwell extends EntityAIBase
{
    /** The creeper that is swelling. */
    EntityFriendlyCreeper swellingCreeper;

    /**
     * The creeper's attack target. This is used for the changing of the creeper's state.
     */
    EntityLiving creeperAttackTarget;

    public EntityAIFriendlyCreeperSwell(EntityFriendlyCreeper par1EntityFriendlyCreeper)
    {
        swellingCreeper = par1EntityFriendlyCreeper;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLiving entityliving = swellingCreeper.getAttackTarget();
        return swellingCreeper.getCreeperState() > 0 || entityliving != null && swellingCreeper.getDistanceSqToEntity(entityliving) < 9D;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        swellingCreeper.getNavigator().clearPathEntity();
        creeperAttackTarget = swellingCreeper.getAttackTarget();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        creeperAttackTarget = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
    	if (this.creeperAttackTarget == null)
        {
            this.swellingCreeper.setCreeperState(-1);
        }
        else if (this.swellingCreeper.getDistanceSqToEntity(this.creeperAttackTarget) > 49.0D)
        {
            this.swellingCreeper.setCreeperState(-1);
        }
        else if (!this.swellingCreeper.getEntitySenses().canSee(this.creeperAttackTarget))
        {
            this.swellingCreeper.setCreeperState(-1);
        }
        else
        {
            this.swellingCreeper.setCreeperState(1);
        }
    }
}
