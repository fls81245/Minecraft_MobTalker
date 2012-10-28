package net.minecraft.src;

import java.util.Comparator;

public class EntityFriendlyAINearestAttackableTargetSorter implements Comparator
{
    private Entity theEntity;
    final EntityFriendlyAINearestAttackableTarget parent;

    public EntityFriendlyAINearestAttackableTargetSorter(EntityFriendlyAINearestAttackableTarget par1EntityAINearestAttackableTarget, Entity par2Entity)
    {
        parent = par1EntityAINearestAttackableTarget;
        theEntity = par2Entity;
    }

    public int func_48469_a(Entity par1Entity, Entity par2Entity)
    {
        double d = theEntity.getDistanceSqToEntity(par1Entity);
        double d1 = theEntity.getDistanceSqToEntity(par2Entity);

        if (d < d1)
        {
            return -1;
        }

        return d <= d1 ? 0 : 1;
    }

    public int compare(Object par1Obj, Object par2Obj)
    {
        return func_48469_a((Entity)par1Obj, (Entity)par2Obj);
    }
}
