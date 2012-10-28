package net.minecraft.src;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EntityFriendlySpider extends EntityFriendlyMob
{
    public EntityFriendlySpider(World par1World)
    {
        super(par1World);
        texture = "/mob/spider.png";
        setSize(1.4F, 0.9F);
        moveSpeed = 0.8F;
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, new Byte((byte)0));
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!worldObj.isRemote)
        {
            func_40148_a(isCollidedHorizontally);
        }
    }

    public int getMaxHealth()
    {
        return 16;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)height * 0.75D - 0.5D;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
     * (Animals, Spiders at day, peaceful PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
    	if (!this.isActiveAttack()) return null;
        float f = getBrightness(1.0F);

        if (f < 0.5F)
        {
            double d = 16D;
            return worldObj.getClosestVulnerablePlayerToEntity(this, d);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.spider";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.spider";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.spiderdeath";
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
    protected void attackEntity(Entity par1Entity, float par2)
    {
        float f = getBrightness(1.0F);

        if (f > 0.5F && rand.nextInt(100) == 0)
        {
            entityToAttack = null;
            return;
        }

        if (par2 > 2.0F && par2 < 6F && rand.nextInt(10) == 0)
        {
            if (onGround)
            {
                double d = par1Entity.posX - posX;
                double d1 = par1Entity.posZ - posZ;
                float f1 = MathHelper.sqrt_double(d * d + d1 * d1);
                motionX = (d / (double)f1) * 0.5D * 0.80000001192092896D + motionX * 0.20000000298023224D;
                motionZ = (d1 / (double)f1) * 0.5D * 0.80000001192092896D + motionZ * 0.20000000298023224D;
                motionY = 0.40000000596046448D;
            }
        }
        else
        {
            super.attackEntity(par1Entity, par2);
        }
    }
    protected boolean followPlayer() {
		final float MIN_DIS=4.0f;
		final float MAX_DIS=15F;
		EntityPlayer target=this.worldObj.getClosestPlayerToEntity(this, 16F);
		if (target==null) return false;
		if (this.isEscortFlag() && this.entityToAttack!=null && this.entityToAttack!=target) return false;
		float par2=this.getDistanceToEntity(target);
		 if (par2 > MIN_DIS && par2 <MAX_DIS )
	        {
	            if (onGround)
	            {
	                double d = target.posX - posX;
	                double d1 = target.posZ - posZ;
	                float f1 = MathHelper.sqrt_double(d * d + d1 * d1);
	                motionX = (d / (double)f1) * 0.5D * 0.80000001192092896D + motionX * 0.20000000298023224D;
	                motionZ = (d1 / (double)f1) * 0.5D * 0.80000001192092896D + motionZ * 0.20000000298023224D;
	            }
	        }
		return true;
	}
	 protected  boolean findTargetToEscort() {
		 if(this.entityToAttack!=null && !this.entityToAttack.isDead) return false;
			EntityPlayer escortTarget=worldObj.getClosestPlayerToEntity(this, 100D);
			if (escortTarget==null)return false;
			EntityLiving attackTarget=escortTarget.getLastAttackingEntity();
			if (attackTarget==this) attackTarget=null;
			if (attackTarget!=null){
				this.entityToAttack=attackTarget;
				return true;
			}else{
				List entityList=worldObj.getEntitiesWithinAABB(EntityLiving.class, this.boundingBox.expand(16D, 50D, 16D));
				Iterator iter=entityList.iterator();
				EntityLiving tmp=null;
				do{
					if (!iter.hasNext()) break;
					tmp=(EntityLiving) iter.next();
					if (tmp!=null && tmp.getAITarget()==escortTarget){
						attackTarget=tmp;
					}
				}while(attackTarget==null);
				this.entityToAttack=attackTarget;
				return (attackTarget!=null);
			}
			
		}
    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return Item.silk.shiftedIndex;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        super.dropFewItems(par1, par2);

        if (par1 && (rand.nextInt(3) == 0 || rand.nextInt(1 + par2) > 0))
        {
            dropItem(Item.spiderEye.shiftedIndex, 1);
        }
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    public boolean isOnLadder()
    {
        return func_40149_l_();
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb()
    {
    }

    /**
     * How large the spider should be scaled.
     */
    public float spiderScaleAmount()
    {
        return 1.0F;
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    public boolean isPotionApplicable(PotionEffect par1PotionEffect)
    {
        if (par1PotionEffect.getPotionID() == Potion.poison.id)
        {
            return false;
        }
        else
        {
            return super.isPotionApplicable(par1PotionEffect);
        }
    }

    public boolean func_40149_l_()
    {
        return (dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void func_40148_a(boolean par1)
    {
        byte byte0 = dataWatcher.getWatchableObjectByte(16);

        if (par1)
        {
            byte0 |= 1;
        }
        else
        {
            byte0 &= 0xfe;
        }

        dataWatcher.updateObject(16, Byte.valueOf(byte0));
    }
    protected void updateEntityActionState()
    {
       
    	if (this.isFollowFlag() &&   followPlayer() ){
        	return;
        }
        if (this.isEscortFlag()){
        	findTargetToEscort();
        }
    	super.updateEntityActionState();
    }
}
