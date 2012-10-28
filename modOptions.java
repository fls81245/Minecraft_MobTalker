package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.client.Minecraft;

/**
 * 本程式負責讀取mobTalker的諸元可定義設置
 * 
 * @author FlammaRilva
 * 
 */
public class modOptions {
	private static final String RESOURCES_MOB_TALKER_CG = "/resources/mobTalker_cg/"; // 預設CG路徑
	private static final String MESG_SPEED = "mesgSpeed"; // Property關於顯示速度的預設索引
	private static final String CG_FILE_LOCATION = "cgFileLocation"; // Property關於CG路徑的預設索引
	public static int[] itemID={2012};
	public static String[] itemName={"ItemMobTalker"};
	private Properties mainProperty = null; // 主要Property
	private Properties storeBack = new Properties(); // 回存用Property
	private final File cfgLocation = new File(Minecraft.getMinecraftDir(),
			"/config/mobTalker_Config.cfg"); // 設置檔抽像類別
	private final File DEFAULT_CG_LOCATION = new File(
			Minecraft.getMinecraftDir(), RESOURCES_MOB_TALKER_CG); // CG路徑預設抽像類別
	private File cgFileLocation = DEFAULT_CG_LOCATION; // CG路徑抽像類別
	private int mesgSpeed = 1; // 訊息顯示速度

	/**
	 * 建構子。建構時同時初始化
	 */
	public modOptions() throws IOException {
		/*
		 * 若預設CG路徑不存在則建立一個
		 */
		if (!DEFAULT_CG_LOCATION.exists())
			DEFAULT_CG_LOCATION.mkdir();
		/*
		 * 若預設設置檔不存在則建立一個空白的
		 */
		if (!cfgLocation.exists())
			cfgLocation.createNewFile();
		/*
		 * 設置主Property
		 */
		mainProperty = new Properties();
		mainProperty.load(new FileInputStream(cfgLocation));
		/*
		 * 初始化其餘數據
		 */
		initData();

	}

	/**
	 * 初始化。本物件要經過這個函數才能運作
	 */
	private void initData() throws FileNotFoundException, IOException {
		/*
		 * 路徑暫存。有預設值
		 */
		String TmpLoc = RESOURCES_MOB_TALKER_CG;
		/*
		 * 若主property未設置則跳出
		 */
		if (this.mainProperty == null)
			return;
		/*
		 * 在主Property尋找自定義的CG路徑
		 */
		if (mainProperty.containsKey(CG_FILE_LOCATION)) {
			TmpLoc = (String) mainProperty.get(CG_FILE_LOCATION);
			cgFileLocation = new File(Minecraft.getMinecraftDir(), TmpLoc);
			/*
			 * 自定義路徑不存在時建立一個
			 */
			if (!cgFileLocation.exists())
				cgFileLocation.mkdir();
		}
		/*
		 * 在主Property裡尋找自定義的訊息速度
		 */
		if (mainProperty.containsKey(MESG_SPEED))
			this.mesgSpeed = Integer.parseInt((String) mainProperty
					.get(MESG_SPEED));
		/*
		 * 在主Property裡存取道具ID
		 */
			for (int i=0;i<itemID.length;i++){
				if (mainProperty.containsKey(itemName[i])){
					itemID[i]= Integer.parseInt((String) mainProperty
							.get(itemName[i]));
				}
			}
		/*
		 * 設置存回用Property內容
		 */
		storeBack.setProperty(CG_FILE_LOCATION, TmpLoc);
		storeBack.setProperty(MESG_SPEED, String.valueOf(this.mesgSpeed));
		for (int i=0;i<itemID.length;i++){
			storeBack.setProperty(itemName[i], String.valueOf(itemID[i]));
		}
		/*
		 * 存回數據
		 */
		storeBack.store(new FileOutputStream(cfgLocation),
				"Mod MobTalker Configs");
	}

	/**
	 * @return CG路徑抽像類別
	 */
	public File getCgFileLocation() {
		return cgFileLocation;
	}

	/**
	 * @return 訊息顯示速度
	 */
	public int getMesgSpeed() {
		return mesgSpeed;
	}
}
