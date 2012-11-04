package net.minecraft.src;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderFriendlyIronGolem extends RenderLiving
{
    private ModelFriendlyIronGolem field_48422_c;

    public RenderFriendlyIronGolem()
    {
        super(new ModelFriendlyIronGolem(), 0.5F);
        field_48422_c = (ModelFriendlyIronGolem)mainModel;
    }

    public void doRenderIronGolem(EntityFriendlyIronGolem par1EntityIronGolem, double par2, double par4, double par6, float par8, float par9)
    {
        super.doRenderLiving(par1EntityIronGolem, par2, par4, par6, par8, par9);
    }

    protected void rotateIronGolemCorpse(EntityFriendlyIronGolem par1EntityIronGolem, float par2, float par3, float par4)
    {
        super.rotateCorpse(par1EntityIronGolem, par2, par3, par4);

        if ((double)par1EntityIronGolem.legYaw < 0.01D)
        {
            return;
        }
        else
        {
            float f = 13F;
            float f1 = (par1EntityIronGolem.legSwing - par1EntityIronGolem.legYaw * (1.0F - par4)) + 6F;
            float f2 = (Math.abs(f1 % f - f * 0.5F) - f * 0.25F) / (f * 0.25F);
            GL11.glRotatef(6.5F * f2, 0.0F, 0.0F, 1.0F);
            return;
        }
    }

    protected void renderIronGolemEquippedItems(EntityFriendlyIronGolem par1EntityIronGolem, float par2)
    {
        super.renderEquippedItems(par1EntityIronGolem, par2);

        if (par1EntityIronGolem.func_48117_D_() == 0)
        {
            return;
        }
        else
        {
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glPushMatrix();
            GL11.glRotatef(5F + (180F * field_48422_c.field_48233_c.rotateAngleX) / (float)Math.PI, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(-0.6875F, 1.25F, -0.9375F);
            GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
            float f = 0.8F;
            GL11.glScalef(f, -f, f);
            int i = par1EntityIronGolem.getBrightnessForRender(par2);
            int j = i % 0x10000;
            int k = i / 0x10000;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            loadTexture("/terrain.png");
            renderBlocks.renderBlockAsItem(Block.plantRed, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            return;
        }
    }

    protected void renderEquippedItems(EntityLiving par1EntityLiving, float par2)
    {
    	renderIronGolemEquippedItems((EntityFriendlyIronGolem)par1EntityLiving, par2);
    }

    protected void rotateCorpse(EntityLiving par1EntityLiving, float par2, float par3, float par4)
    {
    	rotateIronGolemCorpse((EntityFriendlyIronGolem)par1EntityLiving, par2, par3, par4);
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
    	doRenderIronGolem((EntityFriendlyIronGolem)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
    	doRenderIronGolem((EntityFriendlyIronGolem)par1Entity, par2, par4, par6, par8, par9);
    }
}
