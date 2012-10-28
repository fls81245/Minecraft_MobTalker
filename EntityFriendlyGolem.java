package net.minecraft.src;

import java.util.HashMap;

public abstract class EntityFriendlyGolem extends EntityGolem implements
		IFriendAble {
	private int livingDays = 0;
	protected long timeProde = -1;
	protected boolean dayTalkLimit = false;
	protected final int ATTACK_LOVE_LIMIT = 5;
	public HashMap<String, Integer> loveMap = new HashMap<String, Integer>(5);

	public EntityFriendlyGolem(World par1World) {
		super(par1World);
	}

	/**
	 * Called when the mob is falling. Calculates and applies fall damage.
	 */
	protected void fall(float f) {
	}
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(FOLLOW_FLAG_INDEX, Byte.valueOf((byte)0));
        dataWatcher.addObject(ESCORT_FLAG_INDEX, Byte.valueOf((byte)0));
        dataWatcher.addObject(ACTIVE_ATTACK_FLAG_INDEX, Byte.valueOf((byte)0));
    }
	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
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

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("livingDays", livingDays);
		par1NBTTagCompound.setLong("timeProde", timeProde);
		par1NBTTagCompound.setBoolean("dayTalkLimit", dayTalkLimit);
        par1NBTTagCompound.setBoolean("followFlag",this.isFollowFlag());
        par1NBTTagCompound.setBoolean("ActiveAttack",this.isActiveAttack());
        par1NBTTagCompound.setBoolean("escortFlag",this.isEscortFlag());
		if (!this.loveMap.isEmpty()) {
			final String LOVER = "lover";
			String[] nameArray = new String[this.loveMap.size()];
			this.loveMap.keySet().toArray(nameArray);
			for (int i = 0; i < nameArray.length; i++) {
				par1NBTTagCompound.setString(LOVER + i, nameArray[i]);
				par1NBTTagCompound.setInteger(nameArray[i],
						this.loveMap.get(nameArray[i]));
			}
		}
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		final String LOVER = "lover";
		super.readEntityFromNBT(par1NBTTagCompound);
		livingDays = par1NBTTagCompound.getInteger("livingDays");
		timeProde = par1NBTTagCompound.getLong("timeProde");
		dayTalkLimit = par1NBTTagCompound.getBoolean("dayTalkLimit");

		this.setActiveAttack(par1NBTTagCompound.getBoolean("ActiveAttack"));
		this.setFollowFlag(par1NBTTagCompound.getBoolean("followFlag"));
		this.setEscortFlag(par1NBTTagCompound.getBoolean("escortFlag"));
		int caller = 0, tmpLove = 0;
		String nameTmp;
		do {
			if (par1NBTTagCompound.hasKey(LOVER + caller)) {
				nameTmp = par1NBTTagCompound.getString(LOVER + caller);
				tmpLove = par1NBTTagCompound.getInteger(nameTmp);
				this.loveMap.put(nameTmp, tmpLove);
			} else
				break;
			caller++;
		} while (true);

	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	protected String getLivingSound() {
		return "none";
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound() {
		return "none";
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound() {
		return "none";
	}

	/**
	 * Get number of ticks, at least during which the living entity will be
	 * silent.
	 */
	public int getTalkInterval() {
		return 120;
	}

	/**
	 * Determines if an entity can be despawned, used on idle far away entities
	 */
	protected boolean canDespawn() {
		return false;
	}

	public int getLivingDays() {
		return livingDays + 1;
	}

	public void IncreaseLivingDays() {
		this.livingDays++;
	}

	public void updateDays() {
		long tmp = this.worldObj.getWorldTime() % 24000L;
		if (tmp < this.timeProde) {
			this.livingDays++;
			this.dayTalkLimit = false;
		}
		this.timeProde = tmp;
		// System.out.println(new
		// StringBuilder().append(this.livingDays).append(" ").append(this.timeProde).toString());
	}

	public void setDayFinish() {
		this.dayTalkLimit = true;
	}

	public boolean isDayFinished() {
		return this.dayTalkLimit;
	}

	public abstract void actionAttack(int stranthOffset);

	public abstract void shadowInit(EntityLiving motionTarget);

	public abstract void revInit(EntityLiving revTarget);

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

	public boolean hasSubChara() {
		return false;
	}

	public String getSubChara() {
		return null;
	}

	public void onLivingUpdate() {
		if (!this.isActiveAttack()
				&& this.getAttackTarget() instanceof EntityPlayer)
			this.setAttackTarget(null);
		updateDays();
		super.onLivingUpdate();
	}

	@Override
	public int getLovePoint(String playerName) {
		if (!this.loveMap.containsKey(playerName)) {
			this.loveMap.put(playerName, 0);
		}
		return this.loveMap.get(playerName);
	}

	@Override
	public void increaseLove(int value, String playerName) {
		if (!this.loveMap.containsKey(playerName))
			return;
		int tmp = this.loveMap.get(playerName);
		this.loveMap.put(playerName, tmp + value);
	}

	@Override
	public void decreaseLove(int value, String playerName) {
		if (!this.loveMap.containsKey(playerName))
			return;
		int tmp = this.loveMap.get(playerName);
		this.loveMap.put(playerName, tmp - value);

	}

	@Override
	public boolean isBlockingInteractive(String playerName) {
		if (!this.loveMap.containsKey(playerName))
			return false;
		return this.loveMap.get(playerName) < 0;
	}
}
