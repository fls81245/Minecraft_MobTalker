package net.minecraft.src;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class EntityFriendlyMob extends EntityMob implements
		IFriendAble {
	/** How much damage this mob's attacks deal */
	protected int attackStrength;
	private int livingDays = 0;
	protected long timeProde = -1;
	protected boolean dayTalkLimit = false;
	protected final int ATTACK_LOVE_LIMIT = 5;
	protected int field_48310_h;
	private HashMap<String,Integer> loveMap=new HashMap<String,Integer>(5);
	public static HashMap<Class, Class> friendableMap = new HashMap<Class, Class>();
	public static HashMap<Class, Class> RevMap = new HashMap<Class, Class>();

	static {
		putMap(EntityZombie.class, EntityFriendlyZombie.class);
		putMap(EntityEnderman.class, EntityFriendlyEnderman.class);
		putMap(EntityCreeper.class, EntityFriendlyCreeper.class);
		putMap(EntitySilverfish.class, EntityFriendlySilverfish.class);
		putMap(EntitySkeleton.class, EntityFriendlySkeleton.class);
		putMap(EntitySpider.class, EntityFriendlySpider.class);
		putMap(EntityCaveSpider.class, EntityFriendlyCaveSpider.class);
		putMap(EntityBlaze.class, EntityFriendlyBlaze.class);
		putMap(EntitySlime.class, EntityFriendlySlime.class);
		putMap(EntityGhast.class, EntityFriendlyGhast.class);
		putMap(EntityMagmaCube.class, EntityFriendlyMagmaCube.class);
		putMap(EntityIronGolem.class, EntityFriendlyIronGolem.class);
		putMap(EntitySnowman.class, EntityFriendlySnowman.class);
	}

	public EntityFriendlyMob(World par1World) {
		super(par1World);
		attackStrength = 2;
		experienceValue = 5;
		timeProde = this.worldObj.getWorldTime() % 24000L;
	}

	/**
	 * Called frequently so the entity can update its state every tick as
	 * required. For example, zombies and skeletons use this to react to
	 * sunlight and start to burn.
	 */
	public static void putMap(Class normalMob, Class FriendlyMob) {
		friendableMap.put(normalMob, FriendlyMob);
		RevMap.put(FriendlyMob, normalMob);
	}

	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(FOLLOW_FLAG_INDEX, Byte.valueOf((byte) 0));
		dataWatcher.addObject(ESCORT_FLAG_INDEX, Byte.valueOf((byte) 0));
		dataWatcher.addObject(ACTIVE_ATTACK_FLAG_INDEX, Byte.valueOf((byte) 0));
	}

	protected boolean canDespawn() {
		return false;
	}

	public void onLivingUpdate() {
		float f = getBrightness(1.0F);

		if (f > 0.5F) {
			entityAge += 2;
		}
		updateDays();
		super.onLivingUpdate();
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	/*
	 * public void onUpdate() { super.onUpdate();
	 * 
	 * if (!worldObj.isRemote && worldObj.difficultySetting == 0) { setDead(); }
	 * }
	 */

	/**
	 * Finds the closest player within 16 blocks to attack, or null if this
	 * Entity isn't interested in attacking (Animals, Spiders at day, peaceful
	 * PigZombies).
	 */
	protected Entity findPlayerToAttack() {
		EntityPlayer var1 = this.worldObj.getClosestVulnerablePlayerToEntity(
				this, 16.0D);
		return var1 != null && this.canEntityBeSeen(var1) ? var1 : null;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
		if (par1DamageSource.getEntity() instanceof EntityPlayer) {
			if (!worldObj.isRemote) {
				try {
					EntityLiving normalMob = RevFriendlyEntity(this);
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
		if (super.attackEntityFrom(par1DamageSource, par2)) {
			Entity var3 = par1DamageSource.getEntity();

			if (this.riddenByEntity != var3 && this.ridingEntity != var3) {
				if (var3 != this) {
					this.entityToAttack = var3;
				}

				return true;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean attackEntityAsMob(Entity par1Entity) {
		int var2 = this.attackStrength;

		if (this.isPotionActive(Potion.damageBoost)) {
			var2 += 3 << this.getActivePotionEffect(Potion.damageBoost)
					.getAmplifier();
		}

		if (this.isPotionActive(Potion.weakness)) {
			var2 -= 2 << this.getActivePotionEffect(Potion.weakness)
					.getAmplifier();
		}

		return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this),
				var2);
	}

	/**
	 * Basic mob attack. Default to touch of death in EntityCreature. Overridden
	 * by each mob to define their attack.
	 */
	protected void attackEntity(Entity par1Entity, float par2) {
		if (this.attackTime <= 0 && par2 < 2.0F
				&& par1Entity.boundingBox.maxY > this.boundingBox.minY
				&& par1Entity.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			this.attackEntityAsMob(par1Entity);
		}
	}

	/**
	 * Takes a coordinate in and returns a weight to determine how likely this
	 * creature will try to path to the block. Args: x, y, z
	 */
	public float getBlockPathWeight(int par1, int par2, int par3) {
		return 0.5F - worldObj.getLightBrightness(par1, par2, par3);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);
		if (!this.loveMap.isEmpty()){
        	final String LOVER="lover";
        	String[] nameArray=new String[this.loveMap.size()];
        	this.loveMap.keySet().toArray(nameArray);
        	for (int i=0;i<nameArray.length;i++){
        		par1NBTTagCompound.setString(LOVER+i,nameArray[i]);
        		par1NBTTagCompound.setInteger(nameArray[i], this.loveMap.get(nameArray[i]));
        	}
        }
		par1NBTTagCompound.setInteger("livingDays", livingDays);
		par1NBTTagCompound.setLong("timeProde", timeProde);
		par1NBTTagCompound.setBoolean("dayTalkLimit", dayTalkLimit);
		par1NBTTagCompound.setBoolean("followFlag", this.isFollowFlag());
		par1NBTTagCompound.setBoolean("ActiveAttack", this.isActiveAttack());
		par1NBTTagCompound.setBoolean("escortFlag", this.isEscortFlag());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
    	final String LOVER="lover";
		super.readEntityFromNBT(par1NBTTagCompound);
		livingDays = par1NBTTagCompound.getInteger("livingDays");

		timeProde = par1NBTTagCompound.getLong("timeProde");
		dayTalkLimit = par1NBTTagCompound.getBoolean("dayTalkLimit");

		if (par1NBTTagCompound.hasKey("ActiveAttack")) {
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
	 * Checks to make sure the light is not too bright where the mob is spawning
	 */
	protected boolean isValidLightLevel() {
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(boundingBox.minY);
		int k = MathHelper.floor_double(posZ);

		if (worldObj.getSavedLightValue(EnumSkyBlock.Sky, i, j, k) > rand
				.nextInt(32)) {
			return false;
		}

		int l = worldObj.getBlockLightValue(i, j, k);

		if (worldObj.isThundering()) {
			int i1 = worldObj.skylightSubtracted;
			worldObj.skylightSubtracted = 10;
			l = worldObj.getBlockLightValue(i, j, k);
			worldObj.skylightSubtracted = i1;
		}

		return l <= rand.nextInt(8);
	}

	/**
	 * Checks if the entity's current position is a valid location to spawn this
	 * entity.
	 */
	public boolean getCanSpawnHere() {
		return isValidLightLevel() && super.getCanSpawnHere();
	}

	public static boolean isFriendableMob(EntityLiving asker) {
		Class askerClass = asker.getClass();
		return friendableMap.containsKey(askerClass);
	}

	/*
	 * public static IFriendAble getFriendlyShadow(EntityLiving asker) throws
	 * InstantiationException, IllegalAccessException, IllegalArgumentException,
	 * InvocationTargetException, NoSuchMethodException, SecurityException{
	 * Class shadowClass=friendableMap.get(asker.getClass()); IFriendAble
	 * result=(IFriendAble)(shadowClass.getConstructor(new
	 * Class[]{net.minecraft.src.World.class}).newInstance(new
	 * Object[]{asker.worldObj}));
	 * 
	 * return result; }
	 */
	public static Entity getFriendlyShadow(EntityLiving asker)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		Class shadowClass = friendableMap.get(asker.getClass());
		Entity result = (Entity) (shadowClass
				.getConstructor(new Class[] { net.minecraft.src.World.class })
				.newInstance(new Object[] { asker.worldObj }));

		return result;
	}

	public static EntityLiving RevFriendlyEntity(IFriendAble asker)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		Class normalCalss = RevMap.get(asker.getClass());
		EntityLiving result = (EntityLiving) (normalCalss
				.getConstructor(new Class[] { net.minecraft.src.World.class })
				.newInstance(new Object[] { asker.getWorld() }));

		return result;

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

	public void actionAttack(int stranthOffset) {
		EntityPlayer targetPlayer = worldObj.getClosestPlayerToEntity(this, 15);
		if (targetPlayer == null)
			return;
		targetPlayer.attackEntityFrom(DamageSource.causeMobDamage(this),
				this.attackStrength * stranthOffset);
	}

	public void shadowInit(EntityLiving motionTarget) {
		// TODO Auto-generated method stub

	}

	public void revInit(EntityLiving revTarget) {

	}

	/**
	 * @return the activeAttack
	 */
	public boolean isActiveAttack() {
		return (this.dataWatcher
				.getWatchableObjectByte(ACTIVE_ATTACK_FLAG_INDEX) & 1) != (byte) 0;
	}

	/**
	 * @param activeAttack
	 *            the activeAttack to set
	 */
	public void setActiveAttack(boolean activeAttack) {
		this.dataWatcher.updateObject(ACTIVE_ATTACK_FLAG_INDEX,
				activeAttack ? (byte) 1 : (byte) 0);
	}

	public void switchActiveAttack() {
		this.dataWatcher.updateObject(ACTIVE_ATTACK_FLAG_INDEX,
				isActiveAttack() ? (byte) 0 : (byte) 1);
	}

	/**
	 * @return the followFlag
	 */
	public boolean isFollowFlag() {
		boolean result = (this.dataWatcher
				.getWatchableObjectByte(this.FOLLOW_FLAG_INDEX) & 1) != (byte) 0;
		return result;
	}

	/**
	 * @param followFlag
	 *            the followFlag to set
	 */
	public void setFollowFlag(boolean followFlag) {
		this.dataWatcher.updateObject(FOLLOW_FLAG_INDEX, followFlag ? (byte) 1
				: (byte) 0);
	}

	public void switchFollowFlag() {
		this.dataWatcher.updateObject(FOLLOW_FLAG_INDEX,
				isFollowFlag() ? (byte) 0 : (byte) 1);
	}

	/**
	 * @return the escortFlag
	 */
	public boolean isEscortFlag() {
		return (this.dataWatcher
				.getWatchableObjectByte(this.ESCORT_FLAG_INDEX) & 1) != (byte) 0;
	}

	/**
	 * @param escortFlag
	 *            the escortFlag to set
	 */
	public void setEscortFlag(boolean escortFlag) {
		this.dataWatcher.updateObject(this.ESCORT_FLAG_INDEX,
				escortFlag ? (byte) 1 : (byte) 0);
	}

	public void switchEscortFlag() {
		this.dataWatcher.updateObject(this.ESCORT_FLAG_INDEX,
				isEscortFlag() ? (byte) 0 : (byte) 1);
	}

	public World getWorld() {
		return this.worldObj;
	}

	protected boolean findTargetToEscort() {
		if (this.getAITarget() != null && !this.getAITarget().isDead)
			return false;
		EntityPlayer escortTarget = worldObj.getClosestPlayerToEntity(this,
				100D);
		if (escortTarget == null)
			return false;
		EntityLiving attackTarget = escortTarget.getLastAttackingEntity();
		if (attackTarget == this)
			attackTarget = null;
		if (attackTarget != null) {
			this.setRevengeTarget(attackTarget);
			return true;
		} else {
			List entityList = worldObj.getEntitiesWithinAABB(
					EntityLiving.class, this.boundingBox.expand(16D, 50D, 16D));
			Iterator iter = entityList.iterator();
			EntityLiving tmp = null;
			do {
				if (!iter.hasNext())
					break;
				tmp = (EntityLiving) iter.next();
				if (tmp != null && tmp.getAITarget() == escortTarget) {
					attackTarget = tmp;
				}
			} while (attackTarget == null);
			this.setRevengeTarget(attackTarget);
			return (attackTarget != null);
		}

	}

	public boolean hasSubChara() {
		return false;
	}

	public String getSubChara() {
		return null;
	}

	protected boolean followPlayer() {
		final float MIN_DIS = 2.0f;
		final float MAX_DIS = 10F;
		EntityPlayer target = this.worldObj.getClosestPlayerToEntity(this, 16F);
		if (target == null)
			return false;
		PathNavigate pathFinder = this.getNavigator();
		this.getLookHelper().setLookPositionWithEntity(target, 10F,
				this.getVerticalFaceSpeed());

		if (--field_48310_h > 0) {
			return false;
		}

		field_48310_h = 10;

		if (pathFinder.tryMoveToEntityLiving(target, this.moveSpeed)) {
			return true;
		}

		if (this.getDistanceSqToEntity(target) < 144D) {
			return false;
		}

		int i = MathHelper.floor_double(target.posX) - 2;
		int j = MathHelper.floor_double(target.posZ) - 2;
		int k = MathHelper.floor_double(target.boundingBox.minY);

		for (int l = 0; l <= 4; l++) {
			for (int i1 = 0; i1 <= 4; i1++) {
				if ((l < 1 || i1 < 1 || l > 3 || i1 > 3)
						&& worldObj.isBlockNormalCube(i + l, k - 1, j + i1)
						&& !worldObj.isBlockNormalCube(i + l, k, j + i1)
						&& !worldObj.isBlockNormalCube(i + l, k + 1, j + i1)) {
					this.setLocationAndAngles((float) (i + l) + 0.5F, k,
							(float) (j + i1) + 0.5F, this.rotationYaw,
							this.rotationPitch);
					pathFinder.clearPathEntity();
					return true;
				}
			}
		}
		return true;
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
