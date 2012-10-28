package net.minecraft.src;

import java.io.PrintStream;
import org.lwjgl.opengl.GL11;

public class RenderFriendlyMagmaCube extends RenderLiving
{
    private int field_40276_c;

    public RenderFriendlyMagmaCube()
    {
        super(new ModelFriendlyMagmaCube(), 0.25F);
        field_40276_c = ((ModelFriendlyMagmaCube)mainModel).func_40343_a();
    }

    public void renderMagmaCube(EntityFriendlyMagmaCube par1EntityMagmaCube, double par2, double par4, double par6, float par8, float par9)
    {
        int i = ((ModelFriendlyMagmaCube)mainModel).func_40343_a();

        if (i != field_40276_c)
        {
            field_40276_c = i;
            mainModel = new ModelFriendlyMagmaCube();
            System.out.println("new lava slime model");
        }

        super.doRenderLiving(par1EntityMagmaCube, par2, par4, par6, par8, par9);
    }

    protected void scaleMagmaCube(EntityFriendlyMagmaCube par1EntityMagmaCube, float par2)
    {
        int i = par1EntityMagmaCube.getSlimeSize();
        float f = (par1EntityMagmaCube.field_767_b + (par1EntityMagmaCube.field_768_a - par1EntityMagmaCube.field_767_b) * par2) / ((float)i * 0.5F + 1.0F);
        float f1 = 1.0F / (f + 1.0F);
        float f2 = i;
        GL11.glScalef(f1 * f2, (1.0F / f1) * f2, f1 * f2);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLiving par1EntityLiving, float par2)
    {
        scaleMagmaCube((EntityFriendlyMagmaCube)par1EntityLiving, par2);
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        renderMagmaCube((EntityFriendlyMagmaCube)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        renderMagmaCube((EntityFriendlyMagmaCube)par1Entity, par2, par4, par6, par8, par9);
    }
}
