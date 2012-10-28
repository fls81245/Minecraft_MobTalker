package net.minecraft.src;

public class RenderFriendlyBlaze extends RenderLiving
{
    private int field_40278_c;

    public RenderFriendlyBlaze()
    {
        super(new ModelBlaze(), 0.5F);
        field_40278_c = ((ModelBlaze)mainModel).func_78104_a();
    }

    public void renderBlaze(EntityFriendlyBlaze par1EntityFriendlyBlaze, double par2, double par4, double par6, float par8, float par9)
    {
        int i = ((ModelBlaze)mainModel).func_78104_a();

        if (i != field_40278_c)
        {
            field_40278_c = i;
            mainModel = new ModelBlaze();
        }

        super.doRenderLiving(par1EntityFriendlyBlaze, par2, par4, par6, par8, par9);
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        renderBlaze((EntityFriendlyBlaze)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        renderBlaze((EntityFriendlyBlaze)par1Entity, par2, par4, par6, par8, par9);
    }
}
