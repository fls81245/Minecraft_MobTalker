package net.minecraft.src;

import java.util.*;

public class EntityFriendlyAINearestAttackableTarget extends EntityAITarget
{
    EntityLiving targetEntity;
    Class targetClass;
    int field_48386_f;
    private EntityFriendlyAINearestAttackableTargetSorter field_48387_g;
    EntityFriendlyMob targetInterface;

    public EntityFriendlyAINearestAttackableTarget(EntityLiving par1EntityLiving, Class par2Class, float par3, int par4, boolean par5)
    {
        this(par1EntityLiving, par2Class, par3, par4, par5, false);
    }

    public EntityFriendlyAINearestAttackableTarget(EntityLiving par1EntityLiving, Class par2Class, float par3, int par4, boolean par5, boolean par6)
    {
        super(par1EntityLiving, par3, par5, par6);
        targetClass = par2Class;
        targetDistance = par3;
        field_48386_f = par4;
        field_48387_g = new EntityFriendlyAINearestAttackableTargetSorter(this, par1EntityLiving);
        setMutexBits(1);
        targetInterface=(EntityFriendlyMob)taskOwner;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if (!targetInterface.isActiveAttack()) return false;
        label0:
        {
            if (field_48386_f > 0 && taskOwner.getRNG().nextInt(field_48386_f) != 0)
            {
                return false;
            }

            if (targetClass == (net.minecraft.src.EntityPlayer.class))
            {
                EntityPlayer entityplayer = taskOwner.worldObj.getClosestVulnerablePlayerToEntity(taskOwner, targetDistance);

                if (isSuitableTarget(entityplayer, false))
                {
                    targetEntity = entityplayer;
                    return true;
                }

                break label0;
            }

            List list = taskOwner.worldObj.getEntitiesWithinAABB(targetClass, taskOwner.boundingBox.expand(targetDistance, 4D, targetDistance));
            Collections.sort(list, field_48387_g);
            Iterator iterator = list.iterator();
            EntityLiving entityliving;

            do
            {
                if (!iterator.hasNext())
                {
                    break label0;
                }

                Entity entity = (Entity)iterator.next();
                entityliving = (EntityLiving)entity;
            }
            while (!isSuitableTarget(entityliving, false));

            targetEntity = entityliving;
            return true;
        }
        return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        taskOwner.setAttackTarget(targetEntity);
        super.startExecuting();
    }
}
