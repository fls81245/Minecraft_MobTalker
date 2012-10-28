package net.minecraft.src;

import java.lang.reflect.InvocationTargetException;

public class SessionSpecCode extends SessionBase {
	enum checkFlag{
		YES,NO,SWITCH;
	}
	private EnumSpecHeaders codeEnum;
	private String subcode;
	@Override
	protected SessionBase addCode(String code) {
		codeEnum=EnumSpecHeaders.getEnumByStr(code);
		if (codeEnum==null) return this;
		if (codeEnum.subcode && code.length()>codeEnum.selfStr.length()+1){
			subcode=code.substring(codeEnum.selfStr.length()+1);
		}else subcode=null;
		return this;
	}

	@Override
	public String[] getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumFaces getFace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean changeFace() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasContent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasMotion() {
		return (this.motionTarget!=null && codeEnum!=null);
	}
	public void fetchNearestPlayerName(){
		String nameTmp=getNearestPlayerName();
		this.updateAllInheritDatas(motionTarget, nameTmp);
	}
	public String getNearestPlayerName(){
		if(this.motionTarget==null) return null;
		World tmpWorld=motionTarget.worldObj;
		EntityPlayer playerTmp=tmpWorld.getClosestPlayerToEntity(motionTarget, 16);
		if (playerTmp==null) return null;
		return playerTmp.username;
	}
	@SuppressWarnings("finally")
	@Override
	public void motion() {
		Entity shadow;
		if (this.codeEnum==null)return;
		switch(this.codeEnum){
			case makeShadow:
	            if (!motionTarget.worldObj.isRemote)
	            {
					if (EntityFriendlyMob.isFriendableMob(motionTarget)){
						try {
							shadow=(Entity) EntityFriendlyMob.getFriendlyShadow(motionTarget);
						} catch (Exception e) {
							return;
						} 
						shadow.setLocationAndAngles(motionTarget.posX, motionTarget.posY, motionTarget.posZ, motionTarget.rotationYaw, motionTarget.rotationPitch);
						((IFriendAble)shadow).shadowInit(motionTarget);
						motionTarget.setDead();
						shadow.worldObj.spawnEntityInWorld(shadow);
					}
	            }
				break;
			case attack:
				if (motionTarget instanceof IFriendAble){
					((IFriendAble)motionTarget).actionAttack(Integer.parseInt(subcode));
				}
				break;
			case increaseLove:
				if (motionTarget instanceof IFriendAble){
					IFriendAble friendlyTarget=(IFriendAble) motionTarget;
					friendlyTarget.increaseLove(Integer.parseInt(subcode),this.playerName);
				}
				break;
			case decreaseLove:
				if (motionTarget instanceof IFriendAble){
					IFriendAble friendlyTarget=(IFriendAble) motionTarget;
					friendlyTarget.decreaseLove(Integer.parseInt(subcode),this.playerName);
				}
				break;
			case finishDay:
				if (motionTarget instanceof IFriendAble){
					IFriendAble friendlyTarget=(IFriendAble) motionTarget;
					friendlyTarget.setDayFinish();
				}
				break;
			case fetchPlayerName:
				this.fetchNearestPlayerName();
				break;
			case RevTrans:
				if (motionTarget instanceof IFriendAble){
					try {
						EntityLiving normalMob=EntityFriendlyMob.RevFriendlyEntity((IFriendAble) motionTarget);
						((IFriendAble)motionTarget).revInit(normalMob);
						normalMob.setLocationAndAngles(motionTarget.posX, motionTarget.posY, motionTarget.posZ, motionTarget.rotationYaw, motionTarget.rotationPitch);
						motionTarget.setDead();
						motionTarget.worldObj.spawnEntityInWorld(normalMob);
					} catch (Throwable e) {}
				}
				break;
			case selfDead:
				motionTarget.setDead();
				this.next=null;
				break;
			case setName:
				int colorTmp=NameTextSqr.COLOR_DEFAULT;
				String nameTmp=motionTarget.getEntityString().replaceAll("Friendly", "");
				if (subcode!=null){
					if (subcode.equals("(playername)")){
						nameTmp=motionTarget.worldObj.getClosestPlayerToEntity(motionTarget, 16).username;
						colorTmp=NameTextSqr.COLOR_PLAYER;
					}
					else if(subcode.equals("null")) {
						nameTmp=null;
					}
					else
						nameTmp=subcode;
				}else{
					nameTmp=StatCollector.translateToLocal((new StringBuilder()).append("entity.").append(nameTmp).append(".name").toString());
					colorTmp=NameTextSqr.COLOR_MOB;
				}
				SessionBase.nameSqr.setCharaName(nameTmp);
				break;
			case setNamePos:
				EnumNamePostion result=EnumNamePostion.LEFT;
				try{
					result=EnumNamePostion.valueOf(subcode.toUpperCase());
				}catch(Throwable e){
					result=EnumNamePostion.LEFT;
				}finally{
					SessionBase.nameSqr.setPostion(result);
					break;
				}
			case setActiveAttack:
				IFriendAble tmp=(IFriendAble)(this.motionTarget);
				switch(deteme(this.subcode)){
				case YES:
					tmp.setActiveAttack(true);
					break;
				case NO:
					tmp.setActiveAttack(false);
					break;
				case SWITCH:
				default:
					tmp.switchActiveAttack();
				}
				break;
			case setFollow:
				IFriendAble tmp1=(IFriendAble)(this.motionTarget);
				switch(deteme(this.subcode)){
				case YES:
					tmp1.setFollowFlag(true);
					break;
				case NO:
					tmp1.setFollowFlag(false);
					break;
				case SWITCH:
				default:
					tmp1.switchFollowFlag();
				}
				break;
			case setEscort:
				IFriendAble tmp11=(IFriendAble)(this.motionTarget);
				switch(deteme(this.subcode)){
				case YES:
					tmp11.setEscortFlag(true);
					break;
				case NO:
					tmp11.setEscortFlag(false);
					break;
				case SWITCH:
				default:
					tmp11.switchEscortFlag();
				}
				break;
			default:
				return;
		}
	}

	private checkFlag deteme(String subcode2) {
		if (subcode2==null) return checkFlag.SWITCH;
			if (subcode.equalsIgnoreCase("YES")) return checkFlag.YES;
			if (subcode.equalsIgnoreCase("TRUE")) return checkFlag.YES;
			if (subcode.equalsIgnoreCase("ON")) return checkFlag.YES;
			if (subcode.equalsIgnoreCase("NO"))return checkFlag.NO;
			if (subcode.equalsIgnoreCase("FALSE"))return checkFlag.NO;
			if (subcode.equalsIgnoreCase("OFF"))return checkFlag.NO;
			return checkFlag.SWITCH;
	}

}
