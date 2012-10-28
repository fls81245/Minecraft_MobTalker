package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderFriendlySlime extends RenderLiving
{
    private ModelBase scaleAmount;

    public RenderFriendlySlime(ModelBase par1ModelBase, ModelBase par2ModelBase, float par3)
    {
        super(par1ModelBase, par3);
        scaleAmount = par2ModelBase;
    }

    protected int func_40287_a(EntityFriendlySlime par1EntityFriendlySlime, int par2, float par3)
    {
        if (par2 == 0)
        {
            setRenderPassModel(scaleAmount);
            GL11.glEnable(GL11.GL_NORMALIZE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            return 1;
        }

        if (par2 == 1)
        {
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        return -1;
    }

    /**
     * sets the scale for the slime based on getSlimeSize in EntityFriendlySlime
     */
    protected void scaleSlime(EntityFriendlySlime par1EntityFriendlySlime, float par2)
    {
        int i = par1EntityFriendlySlime.getSlimeSize();
        float f = (par1EntityFriendlySlime.field_767_b + (par1EntityFriendlySlime.field_768_a - par1EntityFriendlySlime.field_767_b) * par2) / ((float)i * 0.5F + 1.0F);
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
        scaleSlime((EntityFriendlySlime)par1EntityLiving, par2);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
    {
        return func_40287_a((EntityFriendlySlime)par1EntityLiving, par2, par3);
    }
}
