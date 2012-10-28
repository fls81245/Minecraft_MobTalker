package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;

import net.minecraft.client.Minecraft;

public class SessionCondition extends SessionBase {
	public static final String HEADER = "#CONDITION";
	public static String scriptShortCut = null;
	public Vector<SessionBase> options = new Vector<SessionBase>();
	public Vector<String> optionText = new Vector<String>();
	public SessionBase specHook=null;
	private String motionSourceName;
	private int motionDay = 0;

	public SessionCondition() {
		// TODO Auto-generated constructor stub
	}

	public SessionCondition(String parm, int dayIndex) {
		super();
		this.motionSourceName = parm;
		this.motionDay = dayIndex;
	}

	@Override
	protected SessionBase addCode(String code) {
		String subCodeLayer1;
		String subCodeLayer2;
		int upperMajorParLoc = code.indexOf("{");
		int lowerMajorParLoc = code.indexOf("}");
		if (upperMajorParLoc<0 || lowerMajorParLoc<0) return this;
		subCodeLayer1 = code.substring(upperMajorParLoc + 1, lowerMajorParLoc);
		int upperSqrParLoc = subCodeLayer1.indexOf("[");
		int lowerSqrParLoc = subCodeLayer1.indexOf("]");
		String[] parms;
		int optionIndex = 0;
		while (upperSqrParLoc >= 0) {
			subCodeLayer2 = subCodeLayer1.substring(upperSqrParLoc + 1,
					lowerSqrParLoc);
			subCodeLayer1 = subCodeLayer1.substring(lowerSqrParLoc + 1);
			parms = subCodeLayer2.split(",");
			
			StringBuilder textBuilder=new StringBuilder();
			for (int i=0;i<parms.length-1;i++){
				textBuilder.append(parms[i]);
				if (i<parms.length-2) textBuilder.append(",");
			}
			optionText.add(optionIndex, textBuilder.toString());
			options.add(optionIndex++, getConditionCase(parms));
			
			upperSqrParLoc = subCodeLayer1.indexOf("[");
			lowerSqrParLoc = subCodeLayer1.indexOf("]");
		}
		return this;
	}

	private SessionBase getConditionCase(String[] parms) {
		if (parms[parms.length-1].equals("null")) return null;
		BufferedReader bufferedreader = null;
		try {
			bufferedreader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(Minecraft.getMinecraftDir(),
							GatherCondScriptPath(parms))), "UTF-8"));
		} catch (Throwable e) {
			return null;
		}
		return SessionBase.LoadScript(bufferedreader, motionSourceName,
				motionDay);
	}

	private String GatherCondScriptPath(String[] parms) {
		final String mobName = motionSourceName;
		final String TAIL = ".script";
		final String pathDefault;
		final int locIndex=parms.length-1;
		final String poker=parms[locIndex].replace(" ", "");
		if (SessionBase.loadingFriendly) {
			if (this.motionDay == 0)
				pathDefault = "/resources/mobTalker_script/Friendly/" + mobName
						+ "/default/" + poker + TAIL;
			else
				pathDefault = "/resources/mobTalker_script/Friendly/" + mobName
						+ "/day" + this.motionDay + "/" + poker + TAIL;
		} else {
			pathDefault="/resources/mobTalker_script/"+mobName+"/"+poker + TAIL;
		}

		String result = pathDefault;
		return result;
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
	public boolean hasMotion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void motion() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasContent() {
		// TODO Auto-generated method stub
		return false;
	}

	public void optionToken(int parm) {
		if (!this.options.isEmpty()){
			if (parm > this.options.size())
				parm = 0;
			this.next = this.options.get(parm);
		}
		SessionBase tmp=this.getLast();
		tmp.next=specHook;
		this.updateAllInheritDatas(motionTarget,this.playerName);
	}
	public void setNext(SessionBase parm){
		this.specHook=parm;
	}
}
