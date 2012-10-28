package net.minecraft.src;

public interface IFriendAble {
	public final int FOLLOW_FLAG_INDEX=20;
	public final int ESCORT_FLAG_INDEX=21;
	public final int ACTIVE_ATTACK_FLAG_INDEX=22;
	public boolean hasSubChara();
	public String getSubChara();
	public int getLovePoint(String playerName);
	public void increaseLove(int value,String playerName);
	public void decreaseLove(int value,String playerName);
	public boolean isBlockingInteractive(String playerName);
	public int getLivingDays();
	public void IncreaseLivingDays();
	public void updateDays();
	public void setDayFinish();
	public boolean isDayFinished();
	public void actionAttack(int stranthOffset);
	public void shadowInit(EntityLiving motionTarget);
	public void revInit(EntityLiving revTarget);
	public boolean isActiveAttack();
	public void setActiveAttack(boolean activeAttack);
	public void switchActiveAttack();
	public boolean isFollowFlag() ;
	public void setFollowFlag(boolean followFlag);
	public void switchFollowFlag();
	public boolean isEscortFlag();
	public void setEscortFlag(boolean escortFlag);
	public void switchEscortFlag();
	public World getWorld();
}
