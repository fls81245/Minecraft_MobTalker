package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderFriendlyGhast extends RenderLiving
{
    public RenderFriendlyGhast()
    {
        super(new ModelGhast(), 0.5F);
    }

    protected void func_4014_a(EntityFriendlyGhast par1EntityFriendlyGhast, float par2)
    {
        EntityFriendlyGhast EntityFriendlyGhast = par1EntityFriendlyGhast;
        float f = ((float)EntityFriendlyGhast.prevAttackCounter + (float)(EntityFriendlyGhast.attackCounter - EntityFriendlyGhast.prevAttackCounter) * par2) / 20F;

        if (f < 0.0F)
        {
            f = 0.0F;
        }

        f = 1.0F / (f * f * f * f * f * 2.0F + 1.0F);
        float f1 = (8F + f) / 2.0F;
        float f2 = (8F + 1.0F / f) / 2.0F;
        GL11.glScalef(f2, f1, f2);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLiving par1EntityLiving, float par2)
    {
        func_4014_a((EntityFriendlyGhast)par1EntityLiving, par2);
    }
}
