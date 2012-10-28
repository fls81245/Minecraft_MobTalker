package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderFriendlySpider extends RenderLiving
{
    public RenderFriendlySpider()
    {
        super(new ModelSpider(), 1.0F);
        setRenderPassModel(new ModelSpider());
    }

    protected float setSpiderDeathMaxRotation(EntityFriendlySpider par1EntityFriendlySpider)
    {
        return 180F;
    }

    /**
     * Sets the spider's glowing eyes
     */
    protected int setSpiderEyeBrightness(EntityFriendlySpider par1EntityFriendlySpider, int par2, float par3)
    {
        if (par2 != 0)
        {
            return -1;
        }
        else
        {
            loadTexture("/mob/spider_eyes.png");
            float f = 1.0F;
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
            int i = 61680;
            int j = i % 0x10000;
            int k = i / 0x10000;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, f);
            return 1;
        }
    }

    protected void scaleSpider(EntityFriendlySpider par1EntityFriendlySpider, float par2)
    {
        float f = par1EntityFriendlySpider.spiderScaleAmount();
        GL11.glScalef(f, f, f);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLiving par1EntityLiving, float par2)
    {
        scaleSpider((EntityFriendlySpider)par1EntityLiving, par2);
    }

    protected float getDeathMaxRotation(EntityLiving par1EntityLiving)
    {
        return setSpiderDeathMaxRotation((EntityFriendlySpider)par1EntityLiving);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
    {
        return setSpiderEyeBrightness((EntityFriendlySpider)par1EntityLiving, par2, par3);
    }
}
