package erebus.entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erebus.core.handler.configs.ConfigHandler;
import erebus.item.ItemMaterials;

public class EntityGlowWorm extends EntityCreature {
	public int lastX;
	public int lastY;
	public int lastZ;
	private boolean triggerOnce;

	public EntityGlowWorm(World world) {
		super(world);
		stepHeight = 0.0F;
		isImmuneToFire = true;
		setSize(1.5F, 0.5F);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(3, new EntityAIWander(this, 0.5D));
		tasks.addTask(4, new EntityAIPanic(this, 0.7F));
		tasks.addTask(5, new EntityAILookIdle(this));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(30, new Byte((byte) 0));
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public boolean getCanSpawnHere() {
		float light = getBrightness(1.0F);
		if (light >= 0F)
			return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
		return super.getCanSpawnHere();
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 2;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D); // Movespeed
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ConfigHandler.INSTANCE.mobHealthMultipier < 2 ? 15D : 15D * ConfigHandler.INSTANCE.mobHealthMultipier);
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}

	@Override
	protected String getLivingSound() {
		return "erebus:glowwormsound";
	}

	@Override
	protected String getHurtSound() {
		return "erebus:glowwormhurt";
	}

	@Override
	protected String getDeathSound() {
		return "erebus:squish";
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block) {
		playSound("mob.spider.step", 0.15F, 1.0F);
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		int chance = rand.nextInt(4) + rand.nextInt(1 + looting);
		int amount;
		for (amount = 0; amount < chance; ++amount)
			entityDropItem(ItemMaterials.DATA.BIO_LUMINESCENCE.makeStack(), 0.0F);
	}

	@Override
	public void onUpdate() {
		if (!worldObj.isRemote)
			findNearEntity();
		if (worldObj.isRemote && isGlowing())
			lightUp(worldObj, MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));
		if (worldObj.isRemote && !isGlowing())
			switchOff();
		super.onUpdate();
	}

	@SideOnly(Side.CLIENT)
	private void lightUp(World world, int x, int y, int z) {
		if (!ConfigHandler.INSTANCE.bioluminescence)
			return;
		world.setLightValue(EnumSkyBlock.Block, x, y, z, 9);
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++)
				for (int k = -1; k < 2; k++)
					if (x + i != lastX || y + j != lastY || z + k != lastZ || isDead) {
						world.updateLightByType(EnumSkyBlock.Block, lastX + i, lastY + j, lastZ + k);
						lastX = x;
						lastY = y;
						lastZ = z;
					}
		triggerOnce = true;
	}

	@SideOnly(Side.CLIENT)
	private void switchOff() {
		if (!ConfigHandler.INSTANCE.bioluminescence)
			return;
		if(triggerOnce) {
			worldObj.updateLightByType(EnumSkyBlock.Block, lastX, lastY, lastZ);
			worldObj.updateLightByType(EnumSkyBlock.Block, MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));
			triggerOnce = false;
		}
	}

	@SuppressWarnings("unchecked")
	protected Entity findNearEntity() {
		List<EntityLivingBase> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(posX - 0.5D, posY - 0.5D, posZ - 0.5D, posX + 0.5D, posY + 0.5D, posZ + 0.5D).expand(8D, 8D, 8D));
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null && !getIsNearEntity())
				setIsNearEntity(true);
		}
		if (list.isEmpty() && getIsNearEntity())
			setIsNearEntity(false);
		return null;
	}

	public boolean isGlowing() {
		return worldObj.getSunBrightness(1.0F) < 0.5F && getIsNearEntity();
	}

	public void setIsNearEntity(boolean entityNear) {
		dataWatcher.updateObject(30, entityNear ? (byte) 1 : (byte) 0);
	}

	public boolean getIsNearEntity() {
		return dataWatcher.getWatchableObjectByte(30) == 1 ? true : false;
	}

	@Override
	public void setDead() {
		super.setDead();
		if (worldObj.isRemote)
			switchOff();
	}
}
