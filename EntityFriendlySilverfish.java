package net.minecraft.src;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EntityFriendlySilverfish extends EntityFriendlyMob
{
    /**
     * A cooldown before this entity will search for another Silverfish to join them in battle.
     */
    private int allySummonCooldown;
	private int field_48310_h;

    public EntityFriendlySilverfish(World par1World)
    {
        super(par1World);
        texture = "/mob/silverfish.png";
        setSize(0.3F, 0.7F);
        moveSpeed = 0.6F;
        attackStrength = 1;
    }

    public int getMaxHealth()
    {
        return 8;
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
        double d = 8D;
        return worldObj.getClosestVulnerablePlayerToEntity(this, d);
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.silverfish.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.silverfish.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.silverfish.kill";
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        if (allySummonCooldown <= 0 && (par1DamageSource instanceof EntityDamageSource))
        {
            allySummonCooldown = 20;
        }

        return super.attackEntityFrom(par1DamageSource, par2);
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
    protected void attackEntity(Entity par1Entity, float par2)
    {
        if (attackTime <= 0 && par2 < 1.2F && par1Entity.boundingBox.maxY > boundingBox.minY && par1Entity.boundingBox.minY < boundingBox.maxY)
        {
            attackTime = 20;
            par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackStrength);
        }
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    protected void playStepSound(int par1, int par2, int par3, int par4)
    {
        worldObj.playSoundAtEntity(this, "mob.silverfish.step", 1.0F, 1.0F);
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
        return 0;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        renderYawOffset = rotationYaw;
        super.onUpdate();
    }

    protected void updateEntityActionState()
    {
        super.updateEntityActionState();

        if (worldObj.isRemote)
        {
            return;
        }
        if (this.isFollowFlag() &&   followPlayer() ){
        	return;
        }
        if (this.isEscortFlag()){
        	findTargetToEscort();
        }
        if (allySummonCooldown > 0)
        {
            allySummonCooldown--;

            if (allySummonCooldown == 0)
            {
                int i = MathHelper.floor_double(posX);
                int k = MathHelper.floor_double(posY);
                int i1 = MathHelper.floor_double(posZ);
                boolean flag = false;

                for (int l1 = 0; !flag && l1 <= 5 && l1 >= -5; l1 = l1 > 0 ? 0 - l1 : 1 - l1)
                {
                    for (int j2 = 0; !flag && j2 <= 10 && j2 >= -10; j2 = j2 > 0 ? 0 - j2 : 1 - j2)
                    {
                        for (int k2 = 0; !flag && k2 <= 10 && k2 >= -10; k2 = k2 > 0 ? 0 - k2 : 1 - k2)
                        {
                            int l2 = worldObj.getBlockId(i + j2, k + l1, i1 + k2);

                            if (l2 != Block.silverfish.blockID)
                            {
                                continue;
                            }

                            worldObj.playAuxSFX(2001, i + j2, k + l1, i1 + k2, Block.silverfish.blockID + (worldObj.getBlockMetadata(i + j2, k + l1, i1 + k2) << 12));
                            worldObj.setBlockWithNotify(i + j2, k + l1, i1 + k2, 0);
                            Block.silverfish.onBlockDestroyedByPlayer(worldObj, i + j2, k + l1, i1 + k2, 0);

                            if (!rand.nextBoolean())
                            {
                                continue;
                            }

                            flag = true;
                            break;
                        }
                    }
                }
            }
        }

        if (entityToAttack == null && !hasPath())
        {
            int j = MathHelper.floor_double(posX);
            int l = MathHelper.floor_double(posY + 0.5D);
            int j1 = MathHelper.floor_double(posZ);
            int k1 = rand.nextInt(6);
            int i2 = worldObj.getBlockId(j + Facing.offsetsXForSide[k1], l + Facing.offsetsYForSide[k1], j1 + Facing.offsetsZForSide[k1]);

            if (BlockSilverfish.getPosingIdByMetadata(i2))
            {
                worldObj.setBlockAndMetadataWithNotify(j + Facing.offsetsXForSide[k1], l + Facing.offsetsYForSide[k1], j1 + Facing.offsetsZForSide[k1], Block.silverfish.blockID, BlockSilverfish.getMetadataForBlockType(i2));
                spawnExplosionParticle();
                setDead();
            }
            else
            {
                updateWanderPath();
            }
        }
        else if (entityToAttack != null && !hasPath())
        {
            entityToAttack = null;
        }
    }

	/**
     * Takes a coordinate in and returns a weight to determine how likely this creature will try to path to the block.
     * Args: x, y, z
     */
    public float getBlockPathWeight(int par1, int par2, int par3)
    {
        if (worldObj.getBlockId(par1, par2 - 1, par3) == Block.stone.blockID)
        {
            return 10F;
        }
        else
        {
            return super.getBlockPathWeight(par1, par2, par3);
        }
    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel()
    {
        return true;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        if (super.getCanSpawnHere())
        {
            EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(this, 5D);
            return entityplayer == null;
        }
        else
        {
            return false;
        }
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }
   
}
