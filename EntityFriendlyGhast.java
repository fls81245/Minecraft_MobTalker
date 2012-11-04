package net.minecraft.src;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EntityFriendlyGhast extends EntityFlying implements IMob,IFriendAble
{
    public int courseChangeCooldown = 0;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    private Entity targetedEntity = null;
    public HashMap<String,Integer> loveMap=new HashMap<String,Integer>(5);
    /** Cooldown time between target loss and new target aquirement. */
    private int aggroCooldown = 0;
    public int prevAttackCounter = 0;
    public int attackCounter = 0;
    
    private int livingDays=0;
    protected long timeProde=-1;
    protected boolean dayTalkLimit=false;
    protected final int ATTACK_LOVE_LIMIT=5;
    public EntityFriendlyGhast(World par1World)
    {
        super(par1World);
        this.texture = "/mob/ghast.png";
        this.setSize(4.0F, 4.0F);
        this.isImmuneToFire = true;
        this.experienceValue = 5;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        if ("fireball".equals(par1DamageSource.getDamageType()) && par1DamageSource.getEntity() instanceof EntityPlayer)
        {
            super.attackEntityFrom(par1DamageSource, 1000);
            ((EntityPlayer)par1DamageSource.getEntity()).triggerAchievement(AchievementList.ghast);
            return true;
        }
        else
        {
        	if (par1DamageSource.getEntity() instanceof EntityPlayer) {
    			if (!worldObj.isRemote) {
    				try {
    					EntityLiving normalMob = EntityFriendlyMob
    							.RevFriendlyEntity(this);
    					mod_Mobtalker.addUntrusted(normalMob);
    					this.revInit(normalMob);
    					normalMob.setLocationAndAngles(this.posX, this.posY,
    							this.posZ, this.rotationYaw, this.rotationPitch);
    					this.setDead();
    					this.worldObj.spawnEntityInWorld(normalMob);
    					return normalMob.attackEntityFrom(par1DamageSource, par2);
    				} catch (Throwable e) {
    				}
    			}
    		}
            return super.attackEntityFrom(par1DamageSource, par2);
        }
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
        dataWatcher.addObject(FOLLOW_FLAG_INDEX, Byte.valueOf((byte)0));
        dataWatcher.addObject(ESCORT_FLAG_INDEX, Byte.valueOf((byte)0));
        dataWatcher.addObject(ACTIVE_ATTACK_FLAG_INDEX, Byte.valueOf((byte)0));
    }

    public int getMaxHealth()
    {
        return 10;
    }
    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("followFlag",this.isFollowFlag());
        par1NBTTagCompound.setBoolean("ActiveAttack",this.isActiveAttack());
        par1NBTTagCompound.setBoolean("escortFlag",this.isEscortFlag());
        if (!this.loveMap.isEmpty()){
        	final String LOVER="lover";
        	String[] nameArray=new String[this.loveMap.size()];
        	this.loveMap.keySet().toArray(nameArray);
        	for (int i=0;i<nameArray.length;i++){
        		par1NBTTagCompound.setString(LOVER+i,nameArray[i]);
        		par1NBTTagCompound.setInteger(nameArray[i], this.loveMap.get(nameArray[i]));
        	}
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	final String LOVER="lover";
        super.readEntityFromNBT(par1NBTTagCompound);
        if (par1NBTTagCompound.hasKey("ActiveAttack")){
			this.setActiveAttack(par1NBTTagCompound.getBoolean("ActiveAttack"));
			this.setFollowFlag(par1NBTTagCompound.getBoolean("followFlag"));
			this.setEscortFlag(par1NBTTagCompound.getBoolean("escortFlag"));
        }
        int caller=0,tmpLove=0;
        String nameTmp;
        do{
        	if(par1NBTTagCompound.hasKey(LOVER+caller)){
        		nameTmp=par1NBTTagCompound.getString(LOVER+caller);
        		tmpLove=par1NBTTagCompound.getInteger(nameTmp);
        		this.loveMap.put(nameTmp, tmpLove);
        	}
        	else break;
        	caller++;
        }while(true);
    }
    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
        byte var1 = this.dataWatcher.getWatchableObjectByte(16);
        this.texture = var1 == 1 ? "/mob/ghast_fire.png" : "/mob/ghast.png";
    }

    protected void updateEntityActionState()
    {
        /*if (!this.worldObj.isRemote && this.worldObj.difficultySetting == 0)
        {
            this.setDead();
        }

        this.despawnEntity();*/
        this.prevAttackCounter = this.attackCounter;
        double var1 = this.waypointX - this.posX;
        double var3 = this.waypointY - this.posY;
        double var5 = this.waypointZ - this.posZ;
        double var7 = var1 * var1 + var3 * var3 + var5 * var5;
        if (!updateCourseForFollowing()){
	        if (var7 < 1.0D || var7 > 3600.0D)
	        {
	            this.waypointX = this.posX + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
	            this.waypointY = this.posY + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
	            this.waypointZ = this.posZ + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
	        }
        }
        if (this.courseChangeCooldown-- <= 0)
        {
            this.courseChangeCooldown += this.rand.nextInt(5) + 2;
            var7 = (double)MathHelper.sqrt_double(var7);

            if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, var7))
            {
                this.motionX += var1 / var7 * 0.1D;
                this.motionY += var3 / var7 * 0.1D;
                this.motionZ += var5 / var7 * 0.1D;
            }
            else
            {
                this.waypointX = this.posX;
                this.waypointY = this.posY;
                this.waypointZ = this.posZ;
            }
        }

        if (this.targetedEntity != null && this.targetedEntity.isDead)
        {
            this.targetedEntity = null;
        }

        if (this.targetedEntity == null || this.aggroCooldown-- <= 0)
        {
            if (!this.isActiveAttack())this.targetedEntity = this.worldObj.getClosestVulnerablePlayerToEntity(this, 100.0D);
            if (this.isEscortFlag())this.findTargetToEscort();
            if (this.targetedEntity != null)
            {
                this.aggroCooldown = 20;
            }
        }

        double var9 = 64.0D;

        if (this.targetedEntity != null && this.targetedEntity.getDistanceSqToEntity(this) < var9 * var9)
        {
            double var11 = this.targetedEntity.posX - this.posX;
            double var13 = this.targetedEntity.boundingBox.minY + (double)(this.targetedEntity.height / 2.0F) - (this.posY + (double)(this.height / 2.0F));
            double var15 = this.targetedEntity.posZ - this.posZ;
            this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(var11, var15)) * 180.0F / (float)Math.PI;

            if (this.canEntityBeSeen(this.targetedEntity))
            {
                if (this.attackCounter == 10)
                {
                    this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1007, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                }

                ++this.attackCounter;

                if (this.attackCounter == 20)
                {
                    this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1008, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                    EntityLargeFireball var17 = new EntityLargeFireball(this.worldObj, this, var11, var13, var15);
                    double var18 = 4.0D;
                    Vec3 var20 = this.getLook(1.0F);
                    var17.posX = this.posX + var20.xCoord * var18;
                    var17.posY = this.posY + (double)(this.height / 2.0F) + 0.5D;
                    var17.posZ = this.posZ + var20.zCoord * var18;
                    this.worldObj.spawnEntityInWorld(var17);
                    this.attackCounter = -40;
                }
            }
            else if (this.attackCounter > 0)
            {
                --this.attackCounter;
            }
        }
        else
        {
            this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float)Math.PI;

            if (this.attackCounter > 0)
            {
                --this.attackCounter;
            }
        }

        if (!this.worldObj.isRemote)
        {
            byte var21 = this.dataWatcher.getWatchableObjectByte(16);
            byte var12 = (byte)(this.attackCounter > 10 ? 1 : 0);

            if (var21 != var12)
            {
                this.dataWatcher.updateObject(16, Byte.valueOf(var12));
            }
        }
    }

    private boolean updateCourseForFollowing() {
    	if (!this.isFollowFlag()) return false;
		Entity follower=this.worldObj.getClosestPlayerToEntity(this, 150);
		if (follower==null) return false;
		this.waypointX=follower.posX;
		this.waypointZ=follower.posZ;
		if (follower.onGround)this.waypointY=follower.posY+20;
		else this.waypointY=(follower.posY-5+rand.nextInt(10));
		return true;
	}

	/**
     * True if the ghast has an unobstructed line of travel to the waypoint.
     */
    private boolean isCourseTraversable(double par1, double par3, double par5, double par7)
    {
        double var9 = (this.waypointX - this.posX) / par7;
        double var11 = (this.waypointY - this.posY) / par7;
        double var13 = (this.waypointZ - this.posZ) / par7;
        AxisAlignedBB var15 = this.boundingBox.copy();

        for (int var16 = 1; (double)var16 < par7; ++var16)
        {
            var15.offset(var9, var11, var13);

            if (!this.worldObj.getCollidingBoundingBoxes(this, var15).isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.ghast.moan";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.ghast.scream";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.ghast.death";
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return Item.gunpowder.shiftedIndex;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int var3 = this.rand.nextInt(2) + this.rand.nextInt(1 + par2);
        int var4;

        for (var4 = 0; var4 < var3; ++var4)
        {
            this.dropItem(Item.ghastTear.shiftedIndex, 1);
        }

        var3 = this.rand.nextInt(3) + this.rand.nextInt(1 + par2);

        for (var4 = 0; var4 < var3; ++var4)
        {
            this.dropItem(Item.gunpowder.shiftedIndex, 1);
        }
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 10.0F;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return this.rand.nextInt(20) == 0 && super.getCanSpawnHere() && this.worldObj.difficultySetting > 0;
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 1;
    }

  

	public int getLivingDays() {
		return livingDays+1;
	}

	public void IncreaseLivingDays() {
		this.livingDays++;
	}
	public void updateDays(){
		long tmp=this.worldObj.getWorldTime() % 24000L;
		if (tmp<this.timeProde) {
			this.livingDays++;
			this.dayTalkLimit=false;
		}
		this.timeProde=tmp;
		//System.out.println(new StringBuilder().append(this.livingDays).append(" ").append(this.timeProde).toString());
	}
	public void setDayFinish(){
		this.dayTalkLimit=true;
	}
	public boolean isDayFinished(){
		return this.dayTalkLimit;
	}
	public void actionAttack(int stranthOffset){
		EntityPlayer targetPlayer=worldObj.getClosestPlayerToEntity(this, 15);
		if (targetPlayer==null) return;
		targetPlayer.attackEntityFrom(DamageSource.causeMobDamage(this), 6*stranthOffset);
	}

	public void shadowInit(EntityLiving motionTarget) {
		// TODO Auto-generated method stub
		
	}
	public void revInit(EntityLiving revTarget){
		
	}

	/**
	 * @return the activeAttack
	 */
	public boolean isActiveAttack() {
		return (this.dataWatcher.getWatchableObjectByte(ACTIVE_ATTACK_FLAG_INDEX)&1)!=(byte)0;
	}

	/**
	 * @param activeAttack the activeAttack to set
	 */
	public void setActiveAttack(boolean activeAttack) {
		this.dataWatcher.updateObject(ACTIVE_ATTACK_FLAG_INDEX, activeAttack?(byte)1:(byte)0);
	}
	public void switchActiveAttack(){
		this.dataWatcher.updateObject(ACTIVE_ATTACK_FLAG_INDEX, isActiveAttack()?(byte)0:(byte)1);
	}
	/**
	 * @return the followFlag
	 */
	public boolean isFollowFlag() {
		boolean result=(this.dataWatcher.getWatchableObjectByte(this.FOLLOW_FLAG_INDEX) & 1)!=(byte)0;
		return result;
	}

	/**
	 * @param followFlag the followFlag to set
	 */
	public void setFollowFlag(boolean followFlag) {
		this.dataWatcher.updateObject(FOLLOW_FLAG_INDEX, followFlag?(byte)1:(byte)0);
	}
	public void switchFollowFlag(){
		this.dataWatcher.updateObject(FOLLOW_FLAG_INDEX, isFollowFlag()?(byte)0:(byte)1);
	}
	/**
	 * @return the escortFlag
	 */
	public boolean isEscortFlag() {
		return (this.dataWatcher.getWatchableObjectByte(ACTIVE_ATTACK_FLAG_INDEX)&1)!=(byte)0;
	}

	/**
	 * @param escortFlag the escortFlag to set
	 */
	public void setEscortFlag(boolean escortFlag) {
		this.dataWatcher.updateObject(this.ESCORT_FLAG_INDEX, escortFlag?(byte)1:(byte)0);
	}
	public void switchEscortFlag(){
		this.dataWatcher.updateObject(this.ESCORT_FLAG_INDEX, isEscortFlag()?(byte)0:(byte)1);
	}
	public World getWorld() {
		return this.worldObj;
	}
	 protected  boolean findTargetToEscort() {
		 if(this.targetedEntity!=null && !this.targetedEntity.isDead) return false;
			EntityPlayer escortTarget=worldObj.getClosestPlayerToEntity(this, 100D);
			if (escortTarget==null)return false;
			EntityLiving attackTarget=escortTarget.getLastAttackingEntity();
			if (attackTarget==this) attackTarget=null;
			if (attackTarget!=null){
				targetedEntity=attackTarget;
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
				targetedEntity=attackTarget;
				return (attackTarget!=null);
			}
			
		}
		public boolean hasSubChara(){
			return false;
		}
		public String getSubChara(){
				return null;
		}

		@Override
		public int getLovePoint(String playerName) {
			if (!this.loveMap.containsKey(playerName)){
				this.loveMap.put(playerName, 0);
			}
			return this.loveMap.get(playerName);
		}
		@Override
		public void increaseLove(int value, String playerName) {
			if (!this.loveMap.containsKey(playerName)) return;
			int tmp=this.loveMap.get(playerName);
			this.loveMap.put(playerName, tmp+value);
		}
		@Override
		public void decreaseLove(int value, String playerName) {
			if (!this.loveMap.containsKey(playerName)) return;
			int tmp=this.loveMap.get(playerName);
			this.loveMap.put(playerName, tmp-value);
			
		}
		@Override
		public boolean isBlockingInteractive(String playerName) {
			if (!this.loveMap.containsKey(playerName)) return false;
			return this.loveMap.get(playerName)<0;
		}
}
