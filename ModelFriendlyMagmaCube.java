package net.minecraft.src;

public class ModelFriendlyMagmaCube extends ModelBase
{
    ModelRenderer field_40345_a[];
    ModelRenderer field_40344_b;

    public ModelFriendlyMagmaCube()
    {
        field_40345_a = new ModelRenderer[8];

        for (int i = 0; i < field_40345_a.length; i++)
        {
            byte byte0 = 0;
            int j = i;

            if (i == 2)
            {
                byte0 = 24;
                j = 10;
            }
            else if (i == 3)
            {
                byte0 = 24;
                j = 19;
            }

            field_40345_a[i] = new ModelRenderer(this, byte0, j);
            field_40345_a[i].addBox(-4F, 16 + i, -4F, 8, 1, 8);
        }

        field_40344_b = new ModelRenderer(this, 0, 16);
        field_40344_b.addBox(-2F, 18F, -2F, 4, 4, 4);
    }

    public int func_40343_a()
    {
        return 5;
    }

    /**
     * Sets the models various rotation angles.
     */
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
    {
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLiving par1EntityLiving, float par2, float par3, float par4)
    {
        EntityFriendlyMagmaCube entitymagmacube = (EntityFriendlyMagmaCube)par1EntityLiving;
        float f = entitymagmacube.field_767_b + (entitymagmacube.field_768_a - entitymagmacube.field_767_b) * par4;

        if (f < 0.0F)
        {
            f = 0.0F;
        }

        for (int i = 0; i < field_40345_a.length; i++)
        {
            field_40345_a[i].rotationPointY = (float)(-(4 - i)) * f * 1.7F;
        }
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        setRotationAngles(par2, par3, par4, par5, par6, par7);
        field_40344_b.render(par7);

        for (int i = 0; i < field_40345_a.length; i++)
        {
            field_40345_a[i].render(par7);
        }
    }
}
