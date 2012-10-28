package net.minecraft.src;

public class EntityFriendlyCaveSpider extends EntityFriendlySpider
{
    public EntityFriendlyCaveSpider(World par1World)
    {
        super(par1World);
        texture = "/mob/cavespider.png";
        setSize(0.7F, 0.5F);
    }

    public int getMaxHealth()
    {
        return 12;
    }

    /**
     * How large the spider should be scaled.
     */
    public float spiderScaleAmount()
    {
        return 0.7F;
    }

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        if (super.attackEntityAsMob(par1Entity))
        {
            if (par1Entity instanceof EntityLiving)
            {
                byte byte0 = 0;

                if (worldObj.difficultySetting > 1)
                {
                    if (worldObj.difficultySetting == 2)
                    {
                        byte0 = 7;
                    }
                    else if (worldObj.difficultySetting == 3)
                    {
                        byte0 = 15;
                    }
                }

                if (byte0 > 0)
                {
                    ((EntityLiving)par1Entity).addPotionEffect(new PotionEffect(Potion.poison.id, byte0 * 20, 0));
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
