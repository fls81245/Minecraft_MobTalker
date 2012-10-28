package net.minecraft.src;

import java.io.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import net.minecraft.client.Minecraft;

public abstract class SessionBase {
	final static int END_DAY_DEFAULT_CODE = 101;
	final static int MAX_SHOWN_LINES = 4;
	protected SessionBase next = null;
	protected String playerName = null;
	public static boolean loadingFriendly = false;
	public static String sourceName;
	public static String[] supportedEntityNames;
	public static HashMap<String, SessionBase> dataBase = new HashMap<String, SessionBase>();
	public static HashMap<String, Vector> friendlyHashMap = new HashMap<String, Vector>();
	public static boolean preLoad = true;
	public static NameTextSqr nameSqr = new NameTextSqr();
	protected EntityLiving motionTarget = null;

	public SessionBase() {

	}

	public SessionBase(EntityLiving motionTar) {
		this.motionTarget = motionTar;
	}

	/**
	 * 
	 * 從Entity建立一個bufferedReader然後轉化成session集
	 * 
	 * @return
	 */
	public static void preLoading() {
		System.out.println("Start Preloading");
		// if (!preLoad)
		// return;
		File scriptLoc = new File(Minecraft.getMinecraftDir(),
				"/resources/mobTalker_script");
		if (!scriptLoc.exists()) {
			System.out.println(scriptLoc.getAbsolutePath() + " not found.");
			return;
		}

		File tmpSubDir;
		Vector<String> resultTmp = new Vector();
		String[] subDirTmp = scriptLoc.list();
		for (int i = 0; i < subDirTmp.length; i++) {
			tmpSubDir = new File(scriptLoc, subDirTmp[i]);
			if (tmpSubDir.isDirectory())
				resultTmp.add(subDirTmp[i]);
		}
		supportedEntityNames = new String[resultTmp.size()];
		for (int i = 0; i < supportedEntityNames.length; i++) {
			supportedEntityNames[i] = new String(resultTmp.get(i));
			sourceName = supportedEntityNames[i];
			dataBase.put(sourceName, getSessionFromStatic());
		}
		try {
			loadingFriendly = true;
			preLoadFriendly();
			loadingFriendly = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void preLoadFriendly() throws IOException {
		File scriptLoc = new File(Minecraft.getMinecraftDir(),
				"/resources/mobTalker_script/Friendly/");
		if (!scriptLoc.exists())
			return;
		File tmpSubDir, tmpSubDirL2, defaultDayLoc, outerDefaultScript = null;
		String[] subDirTmp = scriptLoc.list();
		String[] subDirTmpLayer2;
		int dayIndex = 0, loveIndex = 0;

		// defaultDayLoc=new File(scriptLoc,"dafault");
		// if (!defaultDayLoc.exists()) return;
		for (int i = 0; i < subDirTmp.length; i++) {
			tmpSubDir = new File(scriptLoc, subDirTmp[i]);
			Vector<Vector> daysMap = new Vector<Vector>();
			if (tmpSubDir.isDirectory()) {
				subDirTmpLayer2 = tmpSubDir.list();
				for (dayIndex = 0; dayIndex < subDirTmpLayer2.length; dayIndex++) {
					if (dayIndex == 0) {
						tmpSubDirL2 = new File(tmpSubDir, "default");
						outerDefaultScript = new File(tmpSubDirL2,
								"default.script");
					} else
						tmpSubDirL2 = new File(tmpSubDir, "day" + dayIndex);
					if (tmpSubDirL2.isDirectory()) {
						Vector<SessionBase> loveMap = new Vector<SessionBase>();
						loveMap.setSize(102);
						File locTmp = new File(Minecraft.getMinecraftDir(),
								GatherFriendlyScriptPath(subDirTmp[i],
										dayIndex, END_DAY_DEFAULT_CODE));
						if (!locTmp.exists())
							if (outerDefaultScript != null
									&& (outerDefaultScript).exists())
								locTmp = outerDefaultScript;
						FileInputStream fis = new FileInputStream(locTmp);
						UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(
								fis);
						ubis.skipBOM();
						BufferedReader bufferedreader = new BufferedReader(
								new InputStreamReader(ubis, "UTF-8"));
						loveMap.set(END_DAY_DEFAULT_CODE,
								LoadScript(bufferedreader, subDirTmp[i]));
						for (loveIndex = 0; loveIndex <= 100; loveIndex++) {
							File tester = new File(tmpSubDirL2, "love"
									+ loveIndex + ".script");
							if (!tester.exists())
								continue;
							fis = new FileInputStream(new File(
									Minecraft.getMinecraftDir(),
									GatherFriendlyScriptPath(subDirTmp[i],
											dayIndex, loveIndex)));
							ubis = new UnicodeBOMInputStream(fis);
							ubis.skipBOM();
							bufferedreader = new BufferedReader(
									new InputStreamReader(ubis, "UTF-8"));
							loveMap.set(
									loveIndex,
									LoadScript(bufferedreader, subDirTmp[i],
											dayIndex));
						}
						daysMap.add(dayIndex, loveMap);
					}
				}
			}
			friendlyHashMap.put(subDirTmp[i], daysMap);
		}

	}

	private static String GatherFriendlyScriptPath(String mobName,
			int dayIndex, int loveIndex) {
		final String HEADER = "/resources/mobTalker_script/Friendly/";
		final String SOURCE_DIR = "/day" + dayIndex;
		final String LOVE_FILE_NAME = "/love" + loveIndex;
		final String DEFAULT_FILE_NAME = "/default";
		final String TAIL = ".script";
		final String pathDefault;
		;
		if (loveIndex == END_DAY_DEFAULT_CODE)
			if (dayIndex == 0)
				pathDefault = HEADER + mobName + DEFAULT_FILE_NAME
						+ DEFAULT_FILE_NAME + TAIL;
			else
				pathDefault = HEADER + mobName + SOURCE_DIR + DEFAULT_FILE_NAME
						+ TAIL;
		else if (dayIndex == 0)
			pathDefault = HEADER + mobName + DEFAULT_FILE_NAME + LOVE_FILE_NAME
					+ TAIL;
		else
			pathDefault = HEADER + mobName + SOURCE_DIR + LOVE_FILE_NAME + TAIL;
		String result = pathDefault;
		return result;
	}

	public static SessionBase getSessionFromEntity(EntityLiving source) {
		String tmp = source.getEntityString();
		if (!preLoad) {
			sourceName = tmp;
			return getSessionFromStatic();
		} else {
			return dataBase.get(tmp);
		}
	}

	public static SessionBase getSessionFromStatic() {
		if (sourceName.equals("Friendly"))
			return null;
		System.out.println("Processing mob:" + sourceName);
		BufferedReader bufferedreader = null;
		SessionBase result;
		try {
			File fi = new File(Minecraft.getMinecraftDir(), GatherScriptPath(
					sourceName, EnumStatment.Normal));
			UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(
					new FileInputStream(fi));
			ubis.skipBOM();
			bufferedreader = new BufferedReader(new InputStreamReader(ubis,
					"UTF-8"));
		} catch (Throwable e) {
			return null;
		}
		result = LoadScript(bufferedreader, sourceName);
		return result;
	}

	public static String GatherScriptPath(String sourceName, EnumStatment state) {
		final String HEADER = "/resources/mobTalker_script/";
		final String SOURCE_DIR = sourceName + "/";
		final String TAIL = ".script";
		final String pathDefault = HEADER + SOURCE_DIR + state.toString()
				+ TAIL;
		String result = pathDefault;
		return result;
	}

	/**
	 * 從BufferedReader讀取code並且填上相對應的子Session
	 * 
	 * @param script
	 * @param sourceName
	 * 
	 * @return
	 */
	protected static SessionBase LoadScript(BufferedReader script,
			String sourceName) {
		return LoadScript(script, sourceName, 0);
	}

	protected static SessionBase LoadScript(BufferedReader script,
			String sourceName, int dayIndex) {
		SessionBase result = null;
		String codeTmp;
		try {
			codeTmp = script.readLine();
		} catch (IOException e) {
			return null;
		}
		if (codeTmp == null || codeTmp.equals("#END")) {
			try {
				script.close();
			} catch (IOException e) {
			}
			return null;
		}

		if (codeTmp.contains("#FACE"))
			result = new SessionFace();
		else if (EnumSpecHeaders.getEnumByStr(codeTmp) != null)
			result = new SessionSpecCode();
		else if (codeTmp.contains(SessionCondition.HEADER))
			result = new SessionCondition(sourceName, dayIndex);
		else
			result = new SessionText();
		result = result.addCode(codeTmp);
		result.setNext(LoadScript(script, sourceName, dayIndex));
		return result;
	}

	protected abstract SessionBase addCode(String code);

	public String getFacePath() {
		StringBuilder result=new StringBuilder();
		IFriendAble tmp;
		result.append( "/mobTalker_texture/").append(sourceName ).append("/" );
		if (this.motionTarget instanceof IFriendAble){
			tmp=(IFriendAble) this.motionTarget;
			if (tmp!=null && tmp.hasSubChara()) result.append(tmp.getSubChara()).append("/");
		}else{
			String tmpS=checkSubFaceCode();
			if (tmpS!=null){
					result.append(tmpS).append("/");
			}

		}
		return result.toString();
	}

	private String checkSubFaceCode() {
		String result=null;
		if (this.motionTarget instanceof EntitySlime){
			EntitySlime hook=(EntitySlime)this.motionTarget;
			result=String.valueOf(hook.getSlimeSize());
		}
		return result;
	}

	public abstract String[] getContent();

	public abstract EnumFaces getFace();

	public abstract boolean changeFace();

	public abstract boolean hasMotion();

	public abstract void motion();

	public boolean hasNext() {
		return (next != null);
	}

	public SessionBase getNext() {
		return next;
	}

	public void updateAllInheritDatas(EntityLiving motionTar,
			String playerNamePar) {
		SessionBase Loc = this;
		while (Loc != null) {
			Loc.motionTarget = motionTar;
			Loc.playerName = playerNamePar;
			Loc = Loc.next;
		}
	}

	public abstract boolean hasContent();

	public static SessionBase getFriendlyScript(IFriendAble targetMob) {
		String possibleTalkerName=targetMob.getWorld().getClosestPlayerToEntity((Entity)targetMob, 15).username;
		int days = targetMob.getLivingDays();
		int love = targetMob.getLovePoint(possibleTalkerName);
		if (targetMob.isDayFinished())
			love = END_DAY_DEFAULT_CODE;
		Vector Layer1, Layer2;
		SessionBase result = null;
		String oriMobName = ((Entity) targetMob).getEntityString();
		String mobName = oriMobName.substring("Friendly".length());
		Layer1 = friendlyHashMap.get(mobName);
		if (Layer1 == null)
			return null;
		if (days >= Layer1.size())
			Layer2 = (Vector) Layer1.get(0);
		else {
			Layer2 = (Vector) Layer1.get(days);
			if (Layer2 == null)
				Layer2 = (Vector) Layer1.get(0);
		}
		result=(SessionBase) Layer2.get(love);
		if (result==null) 
			try{
			result=(SessionBase) Layer2.get(END_DAY_DEFAULT_CODE);
			}catch(Throwable e){
				return null;
			}
		result.updateAllInheritDatas((EntityLiving)targetMob, possibleTalkerName);
		return result;

	}

	public static void updateSourceNameToFriendly() {
		sourceName = sourceName.substring("Friendly".length());
	}

	public SessionBase getLast() {
		SessionBase tmp = this;
		while (tmp.hasNext())
			tmp = tmp.next;
		return tmp;
	}

	public void setNext(SessionBase parm) {
		this.next = parm;
	}
}
