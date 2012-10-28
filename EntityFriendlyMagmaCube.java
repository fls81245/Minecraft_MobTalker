package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class EntityFriendlyMagmaCube extends EntityFriendlySlime
{
    public EntityFriendlyMagmaCube(World par1World)
    {
        super(par1World);
        texture = "/mob/lava.png";
        isImmuneToFire = true;
        landMovementFactor = 0.2F;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return worldObj.difficultySetting > 0 && worldObj.checkIfAABBIsClear(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).size() == 0 && !worldObj.isAnyLiquid(boundingBox);
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        return getSlimeSize() * 3;
    }

    public int getBrightnessForRender(float par1)
    {
        return 0xf000f0;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float par1)
    {
        return 1.0F;
    }

    /**
     * Returns the name of a particle effect that may be randomly created by EntitySlime.onUpdate()
     */
    protected String getSlimeParticle()
    {
        return "flame";
    }

    protected EntityFriendlySlime createInstance()
    {
        return new EntityFriendlyMagmaCube(worldObj);
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return Item.magmaCream.shiftedIndex;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int i = getDropItemId();

        if (i > 0 && getSlimeSize() > 1)
        {
            int j = rand.nextInt(4) - 2;

            if (par2 > 0)
            {
                j += rand.nextInt(par2 + 1);
            }

            for (int k = 0; k < j; k++)
            {
                dropItem(i, 1);
            }
        }
    }

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        return false;
    }

    protected int func_40131_af()
    {
        return super.func_40131_af() * 4;
    }

    protected void func_40136_ag()
    {
        field_40139_a = field_40139_a * 0.9F;
    }

    /**
     * jump, Causes this entity to do an upwards motion (jumping)
     */
    protected void jump()
    {
        motionY = 0.42F + (float)getSlimeSize() * 0.1F;
        isAirBorne = true;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float f)
    {
    }

    protected boolean func_40137_ah()
    {
        return true;
    }

    protected int func_40130_ai()
    {
        return super.func_40130_ai() + 2;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.slime";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.slime";
    }

    protected String func_40138_aj()
    {
        if (getSlimeSize() > 1)
        {
            return "mob.magmacube.big";
        }
        else
        {
            return "mob.magmacube.small";
        }
    }

    /**
     * Whether or not the current entity is in lava
     */
    public boolean handleLavaMovement()
    {
        return false;
    }

    protected boolean func_40134_ak()
    {
        return true;
    }
}
