package net.minecraft.src;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class EntityFriendlySlime extends EntityLiving implements IMob,
		IFriendAble {
	public float field_40139_a;
	public float field_768_a;
	public float field_767_b;
	public HashMap<String, Integer> loveMap = new HashMap<String, Integer>(5);
	/** the time between each jump of the slime */
	private int slimeJumpDelay;

	private int livingDays = 0;
	protected long timeProde = -1;
	protected boolean dayTalkLimit = false;
	protected final int ATTACK_LOVE_LIMIT = 5;
	private int field_48310_h;

	public EntityFriendlySlime(World par1World) {
		super(par1World);
		slimeJumpDelay = 0;
		texture = "/mob/slime.png";
		int i = 1 << rand.nextInt(3);
		yOffset = 0.0F;
		slimeJumpDelay = rand.nextInt(20) + 10;
		setSlimeSize(i);
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

	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(16, new Byte((byte) 1));
		dataWatcher.addObject(FOLLOW_FLAG_INDEX, Byte.valueOf((byte) 0));
		dataWatcher.addObject(ESCORT_FLAG_INDEX, Byte.valueOf((byte) 0));
		dataWatcher.addObject(ACTIVE_ATTACK_FLAG_INDEX, Byte.valueOf((byte) 0));
	}

	public void setSlimeSize(int par1) {
		dataWatcher.updateObject(16, new Byte((byte) par1));
		setSize(0.6F * (float) par1, 0.6F * (float) par1);
		setPosition(posX, posY, posZ);
		setEntityHealth(getMaxHealth());
		experienceValue = par1;
	}

	public int getMaxHealth() {
		int i = getSlimeSize();
		return i * i;
	}

	/**
	 * Returns the size of the slime.
	 */
	public int getSlimeSize() {
		return dataWatcher.getWatchableObjectByte(16);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("Size", getSlimeSize() - 1);
		par1NBTTagCompound.setBoolean("followFlag", this.isFollowFlag());
		par1NBTTagCompound.setBoolean("ActiveAttack", this.isActiveAttack());
		par1NBTTagCompound.setBoolean("escortFlag", this.isEscortFlag());
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
		setSlimeSize(par1NBTTagCompound.getInteger("Size") + 1);
		if (par1NBTTagCompound.hasKey("ActiveAttack")) {
			this.setActiveAttack(par1NBTTagCompound.getBoolean("ActiveAttack"));
			this.setFollowFlag(par1NBTTagCompound.getBoolean("followFlag"));
			this.setEscortFlag(par1NBTTagCompound.getBoolean("escortFlag"));
		}
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
	 * Returns the name of a particle effect that may be randomly created by
	 * EntitySlime.onUpdate()
	 */
	protected String getSlimeParticle() {
		return "slime";
	}

	protected String func_40138_aj() {
		return "mob.slime";
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {

		field_768_a = field_768_a + (field_40139_a - field_768_a) * 0.5F;
		field_767_b = field_768_a;
		boolean flag = onGround;
		super.onUpdate();

		if (onGround && !flag) {
			int i = getSlimeSize();

			for (int j = 0; j < i * 8; j++) {
				float f = rand.nextFloat() * (float) Math.PI * 2.0F;
				float f1 = rand.nextFloat() * 0.5F + 0.5F;
				float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
				float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;
				worldObj.spawnParticle(getSlimeParticle(), posX + (double) f2,
						boundingBox.minY, posZ + (double) f3, 0.0D, 0.0D, 0.0D);
			}

			if (func_40134_ak()) {
				worldObj.playSoundAtEntity(
						this,
						func_40138_aj(),
						getSoundVolume(),
						((rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			}

			field_40139_a = -0.5F;
		}

		func_40136_ag();
	}

	protected void updateEntityActionState() {
		// despawnEntity();
		updateDays();
		if (!this.isActiveAttack()
				&& this.getAttackTarget() instanceof EntityPlayer)
			this.setAttackTarget(null);
		EntityPlayer entityplayer = null;
		if (this.getAITarget() == null) {
			if (this.isFollowFlag())
				entityplayer = worldObj.getClosestPlayerToEntity(this, 16D);
			else
				entityplayer = worldObj.getClosestVulnerablePlayerToEntity(
						this, 16D);
		}
		if (entityplayer != null) {
			faceEntity(entityplayer, 10F, 20F);
		}
		/*
		 * if (this.isFollowFlag() && followPlayer() ){ return; }
		 */
		if (this.isEscortFlag()) {
			findTargetToEscort();
		}
		if (this.getAITarget() != null) {
			faceEntity(this.getAITarget(), 10f, 20f);
		}
		if (onGround && slimeJumpDelay-- <= 0) {
			slimeJumpDelay = func_40131_af();

			if (entityplayer != null) {
				slimeJumpDelay /= 3;
			}

			isJumping = true;

			if (func_40133_ao()) {
				worldObj.playSoundAtEntity(
						this,
						func_40138_aj(),
						getSoundVolume(),
						((rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
			}

			field_40139_a = 1.0F;
			moveStrafing = 1.0F - rand.nextFloat() * 2.0F;
			moveForward = 1 * getSlimeSize();
		} else {
			isJumping = false;

			if (onGround) {
				moveStrafing = moveForward = 0.0F;
			}
		}
	}

	private boolean setPathToTarget() {
		final float MAX_DIS = 10f;
		final float MIN_DIS = 2.0f;
		EntityLiving target = this.getAITarget();
		if (target == null)
			return false;
		PathNavigate pathFinder = this.getNavigator();
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

	private boolean followPlayer() {
		final float MAX_DIS = 10f;
		final float MIN_DIS = 2.0f;
		EntityPlayer target = worldObj.getClosestPlayerToEntity(this, MAX_DIS);
		if (target == null)
			return false;
		PathNavigate pathFinder = this.getNavigator();
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

	protected void func_40136_ag() {
		field_40139_a = field_40139_a * 0.6F;
	}

	protected int func_40131_af() {
		return rand.nextInt(20) + 10;
	}

	protected EntityFriendlySlime createInstance() {
		return new EntityFriendlySlime(worldObj);
	}

	/**
	 * Will get destroyed next tick.
	 */
	public void setDead() {
		int i = getSlimeSize();

		if (!worldObj.isRemote && i > 1 && getHealth() <= 0) {
			int j = 2 + rand.nextInt(3);

			for (int k = 0; k < j; k++) {
				float f = (((float) (k % 2) - 0.5F) * (float) i) / 4F;
				float f1 = (((float) (k / 2) - 0.5F) * (float) i) / 4F;
				EntityFriendlySlime entityslime = createInstance();
				entityslime.setSlimeSize(i / 2);
				entityslime.setLocationAndAngles(posX + (double) f,
						posY + 0.5D, posZ + (double) f1,
						rand.nextFloat() * 360F, 0.0F);
				worldObj.spawnEntityInWorld(entityslime);
			}
		}

		super.setDead();
	}

	/**
	 * Called by a player entity when they collide with an entity
	 */
	public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
		if (func_40137_ah()) {
			int i = getSlimeSize();

			if (canEntityBeSeen(par1EntityPlayer)
					&& (double) getDistanceToEntity(par1EntityPlayer) < 0.59999999999999998D * (double) i
					&& par1EntityPlayer.attackEntityFrom(
							DamageSource.causeMobDamage(this), func_40130_ai())) {
				worldObj.playSoundAtEntity(this, "mob.slimeattack", 1.0F,
						(rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
			}
		}
	}

	public void applyEntityCollision(Entity par1Entity) {
		if (!(par1Entity instanceof EntityPlayer)
				&& par1Entity == this.getAITarget()) {
			if (func_40137_ah()) {
				int i = getSlimeSize();

				if (canEntityBeSeen(par1Entity)
						&& (double) getDistanceToEntity(par1Entity) < 0.59999999999999998D * (double) i
						&& par1Entity.attackEntityFrom(
								DamageSource.causeMobDamage(this),
								func_40130_ai())) {
					worldObj.playSoundAtEntity(this, "mob.slimeattack", 1.0F,
							(rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
				}
			}
		}
		super.applyEntityCollision(par1Entity);
	}

	protected boolean func_40137_ah() {
		return getSlimeSize() > 1;
	}

	protected int func_40130_ai() {
		return getSlimeSize();
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound() {
		return "mob.slime";
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound() {
		return "mob.slime";
	}

	/**
	 * Returns the item ID for the item the mob drops on death.
	 */
	protected int getDropItemId() {
		if (getSlimeSize() == 1) {
			return Item.slimeBall.shiftedIndex;
		} else {
			return 0;
		}
	}

	/**
	 * Checks if the entity's current position is a valid location to spawn this
	 * entity.
	 */
	public boolean getCanSpawnHere() {
		Chunk chunk = worldObj.getChunkFromBlockCoords(
				MathHelper.floor_double(posX), MathHelper.floor_double(posZ));

		if ((getSlimeSize() == 1 || worldObj.difficultySetting > 0)
				&& rand.nextInt(10) == 0
				&& chunk.getRandomWithSeed(0x3ad8025fL).nextInt(10) == 0
				&& posY < 40D) {
			return super.getCanSpawnHere();
		} else {
			return false;
		}
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
		return 0.4F * (float) getSlimeSize();
	}

	/**
	 * The speed it takes to move the entityliving's rotationPitch through the
	 * faceEntity method. This is only currently use in wolves.
	 */
	public int getVerticalFaceSpeed() {
		return 0;
	}

	protected boolean func_40133_ao() {
		return getSlimeSize() > 1;
	}

	protected boolean func_40134_ak() {
		return getSlimeSize() > 2;
	}

	@Override
	public int getLivingDays() {
		return this.livingDays + 1;
	}

	@Override
	public void IncreaseLivingDays() {
		this.livingDays++;

	}

	@Override
	public void updateDays() {
		long tmp = this.worldObj.getWorldTime() % 24000L;
		if (tmp < this.timeProde) {
			this.livingDays++;
			this.dayTalkLimit = false;
		}
		this.timeProde = tmp;

	}

	@Override
	public void setDayFinish() {
		this.dayTalkLimit = true;

	}

	@Override
	public boolean isDayFinished() {
		return this.dayTalkLimit;
	}

	@Override
	public void actionAttack(int stranthOffset) {
		EntityPlayer targetPlayer = worldObj.getClosestPlayerToEntity(this, 15);
		targetPlayer.attackEntityFrom(DamageSource.causeMobDamage(this),
				func_40130_ai() * stranthOffset);

	}

	@Override
	public void shadowInit(EntityLiving motionTarget) {
		if (!(motionTarget instanceof EntitySlime))
			return;
		EntitySlime origin = (EntitySlime) motionTarget;
		this.setSlimeSize(origin.getSlimeSize());
	}

	@Override
	public void revInit(EntityLiving revTarget) {
		// TODO Auto-generated method stub

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
		return (this.dataWatcher.getWatchableObjectByte(this.ESCORT_FLAG_INDEX) & 1) != (byte) 0;
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

	private boolean findTargetToEscort() {
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

	@Override
	public boolean hasSubChara() {
		return true;
	}

	@Override
	public String getSubChara() {
		return String.valueOf(this.getSlimeSize());
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
