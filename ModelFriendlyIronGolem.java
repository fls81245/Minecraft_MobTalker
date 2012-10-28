package net.minecraft.src;

public class ModelFriendlyIronGolem extends ModelBase
{
    public ModelRenderer field_48234_a;
    public ModelRenderer field_48232_b;
    public ModelRenderer field_48233_c;
    public ModelRenderer field_48230_d;
    public ModelRenderer field_48231_e;
    public ModelRenderer field_48229_f;

    public ModelFriendlyIronGolem()
    {
        this(0.0F);
    }

    public ModelFriendlyIronGolem(float par1)
    {
        this(par1, -7F);
    }

    public ModelFriendlyIronGolem(float par1, float par2)
    {
        char c = '\200';
        char c1 = '\200';
        field_48234_a = (new ModelRenderer(this)).setTextureSize(c, c1);
        field_48234_a.setRotationPoint(0.0F, 0.0F + par2, -2F);
        field_48234_a.setTextureOffset(0, 0).addBox(-4F, -12F, -5.5F, 8, 10, 8, par1);
        field_48234_a.setTextureOffset(24, 0).addBox(-1F, -5F, -7.5F, 2, 4, 2, par1);
        field_48232_b = (new ModelRenderer(this)).setTextureSize(c, c1);
        field_48232_b.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
        field_48232_b.setTextureOffset(0, 40).addBox(-9F, -2F, -6F, 18, 12, 11, par1);
        field_48232_b.setTextureOffset(0, 70).addBox(-4.5F, 10F, -3F, 9, 5, 6, par1 + 0.5F);
        field_48233_c = (new ModelRenderer(this)).setTextureSize(c, c1);
        field_48233_c.setRotationPoint(0.0F, -7F, 0.0F);
        field_48233_c.setTextureOffset(60, 21).addBox(-13F, -2.5F, -3F, 4, 30, 6, par1);
        field_48230_d = (new ModelRenderer(this)).setTextureSize(c, c1);
        field_48230_d.setRotationPoint(0.0F, -7F, 0.0F);
        field_48230_d.setTextureOffset(60, 58).addBox(9F, -2.5F, -3F, 4, 30, 6, par1);
        field_48231_e = (new ModelRenderer(this, 0, 22)).setTextureSize(c, c1);
        field_48231_e.setRotationPoint(-4F, 18F + par2, 0.0F);
        field_48231_e.setTextureOffset(37, 0).addBox(-3.5F, -3F, -3F, 6, 16, 5, par1);
        field_48229_f = (new ModelRenderer(this, 0, 22)).setTextureSize(c, c1);
        field_48229_f.mirror = true;
        field_48229_f.setTextureOffset(60, 0).setRotationPoint(5F, 18F + par2, 0.0F);
        field_48229_f.addBox(-3.5F, -3F, -3F, 6, 16, 5, par1);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        setRotationAngles(par2, par3, par4, par5, par6, par7);
        field_48234_a.render(par7);
        field_48232_b.render(par7);
        field_48231_e.render(par7);
        field_48229_f.render(par7);
        field_48233_c.render(par7);
        field_48230_d.render(par7);
    }

    /**
     * Sets the models various rotation angles.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6)
    {
        field_48234_a.rotateAngleY = par4 / (180F / (float)Math.PI);
        field_48234_a.rotateAngleX = par5 / (180F / (float)Math.PI);
        field_48231_e.rotateAngleX = -1.5F * func_48228_a(par1, 13F) * par2;
        field_48229_f.rotateAngleX = 1.5F * func_48228_a(par1, 13F) * par2;
        field_48231_e.rotateAngleY = 0.0F;
        field_48229_f.rotateAngleY = 0.0F;
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLiving par1EntityLiving, float par2, float par3, float par4)
    {
        EntityFriendlyIronGolem entityirongolem = (EntityFriendlyIronGolem)par1EntityLiving;
        int i = entityirongolem.func_48114_ab();

        if (i > 0)
        {
            field_48233_c.rotateAngleX = -2F + 1.5F * func_48228_a((float)i - par4, 10F);
            field_48230_d.rotateAngleX = -2F + 1.5F * func_48228_a((float)i - par4, 10F);
        }
        else
        {
            int j = entityirongolem.func_48117_D_();

            if (j > 0)
            {
                field_48233_c.rotateAngleX = -0.8F + 0.025F * func_48228_a(j, 70F);
                field_48230_d.rotateAngleX = 0.0F;
            }
            else
            {
                field_48233_c.rotateAngleX = (-0.2F + 1.5F * func_48228_a(par2, 13F)) * par3;
                field_48230_d.rotateAngleX = (-0.2F - 1.5F * func_48228_a(par2, 13F)) * par3;
            }
        }
    }

    private float func_48228_a(float par1, float par2)
    {
        return (Math.abs(par1 % par2 - par2 * 0.5F) - par2 * 0.25F) / (par2 * 0.25F);
    }
}
