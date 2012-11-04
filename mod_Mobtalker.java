package net.minecraft.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class mod_Mobtalker extends BaseMod {
	public static  Item mobTalker;
	public static modOptions mainOption=null;
	public static ArrayList<EntityLiving> unTrustedList=new ArrayList<EntityLiving>();
	private int entityHighestID=100;
	
	@Override
	public String getVersion() {
		return "MobTalker Beta-11 Engaged";
	}
	
	@Override
	public void load() {
		try {
			mainOption=new modOptions();
		} catch (IOException e) {
			new Exception("[mod_mobTalker ERROR]Config file not functional.").printStackTrace();
		}
		LoadItemID();
		mobTalker=new ItemMobtalker(mainOption.itemID[0]).setItemName("mobTalker").setCreativeTab(CreativeTabs.tabTools);
		ModLoader.addName(mobTalker, "Mob Talker");
		mobTalker.setIconIndex(ModLoader.addOverride("/gui/items.png", "/mobTalker_texture/Talker.png"));
		   ModLoader.addRecipe(new ItemStack(mobTalker, 1), new Object[] {
	             " X ", " | ", Character.valueOf('X'), Block.glowStone, Character.valueOf('|'), Item.stick
	        });
		/*ModLoader.registerEntityID(EntityFriendlyZombie.class, "FriendlyZombie",ModLoader.getUniqueEntityId());
		ModLoader.registerEntityID(EntityFriendlyEnderman.class, "FriendlyEnderman",ModLoader.getUniqueEntityId());
		ModLoader.registerEntityID(EntityFriendlyCreeper.class, "FriendlyCreeper",ModLoader.getUniqueEntityId());
		ModLoader.registerEntityID(EntityFriendlySilverfish.class, "FriendlySilverfish",ModLoader.getUniqueEntityId());
		ModLoader.registerEntityID(EntityFriendlySkeleton.class, "FriendlySkeleton",ModLoader.getUniqueEntityId());
		ModLoader.registerEntityID(EntityFriendlySpider.class, "FriendlySpider",ModLoader.getUniqueEntityId());
		ModLoader.registerEntityID(EntityFriendlyCaveSpider.class, "FriendlyCaveSpider",ModLoader.getUniqueEntityId());
		ModLoader.registerEntityID(EntityFriendlyBlaze.class, "FriendlyBlaze",ModLoader.getUniqueEntityId());
		ModLoader.registerEntityID(EntityFriendlySlime.class, "FriendlySlime",ModLoader.getUniqueEntityId());
		ModLoader.registerEntityID(EntityFriendlyGhast.class, "FriendlyGhast",ModLoader.getUniqueEntityId());
		ModLoader.registerEntityID(EntityFriendlyMagmaCube.class, "FriendlyLavaSlime",ModLoader.getUniqueEntityId());
		ModLoader.registerEntityID(EntityFriendlyIronGolem.class, "FriendlyVillagerGolem",ModLoader.getUniqueEntityId());
		ModLoader.registerEntityID(EntityFriendlySnowman.class, "FriendlySnowMan",ModLoader.getUniqueEntityId());*/
		   ModLoader.registerEntityID(EntityFriendlyZombie.class, "FriendlyZombie",getEntityID());
			ModLoader.registerEntityID(EntityFriendlyEnderman.class, "FriendlyEnderman",getEntityID());
			ModLoader.registerEntityID(EntityFriendlyCreeper.class, "FriendlyCreeper",getEntityID());
			ModLoader.registerEntityID(EntityFriendlySilverfish.class, "FriendlySilverfish",getEntityID());
			ModLoader.registerEntityID(EntityFriendlySkeleton.class, "FriendlySkeleton",getEntityID());
			ModLoader.registerEntityID(EntityFriendlySpider.class, "FriendlySpider",getEntityID());
			ModLoader.registerEntityID(EntityFriendlyCaveSpider.class, "FriendlyCaveSpider",getEntityID());
			ModLoader.registerEntityID(EntityFriendlyBlaze.class, "FriendlyBlaze",getEntityID());
			ModLoader.registerEntityID(EntityFriendlySlime.class, "FriendlySlime",getEntityID());
			ModLoader.registerEntityID(EntityFriendlyGhast.class, "FriendlyGhast",getEntityID());
			ModLoader.registerEntityID(EntityFriendlyMagmaCube.class, "FriendlyLavaSlime",getEntityID());
			ModLoader.registerEntityID(EntityFriendlyIronGolem.class, "FriendlyVillagerGolem",getEntityID());
			ModLoader.registerEntityID(EntityFriendlySnowman.class, "FriendlySnowMan",getEntityID());
		
		SessionBase.preLoading();
	}
	private int getEntityID() {
		int result=0;
		byte checker=0;
		do{
			result=this.entityHighestID++;
			checker=(byte)result;
		}while(checker<0);
		return result;
	}

	private void LoadItemID() {
		// TODO Auto-generated method stub
		
	}

	public void addRenderer(Map map) {
		map.put(EntityFriendlyZombie.class, new RenderBiped(new ModelZombie(), 0.5F));
		map.put(EntityFriendlyEnderman.class, new RenderFriendlyEnderman());
		map.put(EntityFriendlyCreeper.class, new RenderFriendlyCreeper());
		map.put(EntityFriendlySilverfish.class, new RenderFriendlySilverfish());
		map.put(EntityFriendlySkeleton.class, new RenderBiped(new ModelSkeleton(), 0.5F));
		map.put(EntityFriendlySpider.class, new RenderFriendlySpider());
		map.put(EntityFriendlyCaveSpider.class, new RenderFriendlySpider());
		map.put(EntityFriendlyBlaze.class, new RenderFriendlyBlaze());
		map.put(EntityFriendlySlime.class, new RenderFriendlySlime(new ModelSlime(16), new ModelSlime(0), 0.25F));
		map.put(EntityFriendlyGhast.class, new RenderFriendlyGhast());
		map.put(EntityFriendlyMagmaCube.class, new RenderFriendlyMagmaCube());
		map.put(net.minecraft.src.EntityFriendlyIronGolem.class, new RenderFriendlyIronGolem());
		map.put(net.minecraft.src.EntityFriendlySnowman.class, new RenderFriendlySnowMan());
	}
	public static void addUntrusted(EntityLiving parm){
		unTrustedList.add(parm);
	}
	public static boolean isUntrusted(EntityLiving parm){
		return unTrustedList.contains(parm);
	}
}
