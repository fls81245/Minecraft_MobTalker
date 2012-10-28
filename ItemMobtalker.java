package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class ItemMobtalker extends Item {

	public ItemMobtalker(int par1) {
		super(par1);
		maxStackSize = 1;
	}

	public boolean itemInteractionForEntity(ItemStack par1ItemStack,
			EntityLiving par2EntityLiving) {
		EntityPlayer possibleTalker=par2EntityLiving.worldObj.getClosestPlayerToEntity(par2EntityLiving, 15);
		if (possibleTalker==null) return false;
		if (mod_Mobtalker.isUntrusted(par2EntityLiving)){
			 par2EntityLiving.renderBrokenItemStack(par1ItemStack);
			par1ItemStack.stackSize--;
			return true;
		}
		SessionBase checkScript = null;
		SessionBase.sourceName = par2EntityLiving.getEntityString();
		if (par2EntityLiving instanceof IFriendAble) {
			SessionBase.updateSourceNameToFriendly();
			checkScript = SessionBase
					.getFriendlyScript((IFriendAble) par2EntityLiving);
		} else {
			checkScript = SessionBase.getSessionFromEntity(par2EntityLiving);
		}
		if (checkScript != null) {
			checkScript.updateAllInheritDatas(par2EntityLiving,possibleTalker.username);
			if (!par2EntityLiving.worldObj.isRemote){
				Minecraft Corl = ModLoader.getMinecraftInstance();
				Corl.displayGuiScreen(null);
				if (!(checkScript instanceof SessionCondition))
					Corl.displayGuiScreen(new GuiTalking(checkScript));
				else
					Corl.displayGuiScreen(new GuiTalkingSelection(checkScript,EnumFaces.Normal));
			}
			return true;
		}else return false;

	}
}
