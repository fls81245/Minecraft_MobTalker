package net.minecraft.src;

public enum EnumSpecHeaders {
	makeShadow("#MAKESHADOW",false),
	RevTrans("#REV_TRANS",false),
	attack("#ATTACK",true),
	increaseLove("#INCREASE_LOVE",true),
	decreaseLove("#DECREASE_LOVE",true),
	finishDay("#FINISH_DAY",false),
	fetchPlayerName("#FETCH_NAME",false),
	selfDead("#SELF_DEAD",false),
	setName("#SET_NAME",true),
	setNamePos("#SET_POS",true),
	setActiveAttack("#SET_ACTIVE_ATTACK",true),
	setFollow("#SET_FOLLOW",true),
	setEscort("#SET_ESCORT",true);
	public final String selfStr;
	public final boolean subcode;
	private EnumSpecHeaders(String selfStrParm,boolean sub){
		selfStr=selfStrParm;
		subcode=sub;
	}
	public static EnumSpecHeaders getEnumByStr(String digit){
		EnumSpecHeaders[] tmpBase=EnumSpecHeaders.values();
		for (int i=0;i<tmpBase.length;i++){
			if (digit.contains(tmpBase[i].selfStr)) return tmpBase[i];
		}
		return null;
	}
}
