package erebus.item.bambucket;

import cpw.mods.fml.common.eventhandler.Event;
import erebus.ModBlocks;
import erebus.ModItems;
import erebus.ModTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class ItemBambucket extends Item {

	private final Block fluid;

	public ItemBambucket() {
		this(Blocks.air);
		setUnlocalizedName("erebus.bambucket");
		setTextureName("erebus:bambucket");
	}

	public ItemBambucket(Block fluid) {
		this.fluid = fluid;
		setMaxStackSize(16);
		setCreativeTab(ModTabs.specials);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return fluid != Blocks.air;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return hasContainerItem(stack) ? new ItemStack(ModItems.bambucket) : null;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {
		if (entity instanceof EntityCow) {
			if (!player.capabilities.isCreativeMode)
				stack.stackSize--;
			if (!player.inventory.addItemStackToInventory(new ItemStack(ModItems.bambucketMilk)))
				player.dropPlayerItemWithRandomChoice(new ItemStack(ModItems.bambucketMilk), false);
			return true;
		}
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		boolean flag = fluid == Blocks.air;
		MovingObjectPosition pos = getMovingObjectPositionFromPlayer(world, player, flag);

		if (pos == null)
			return stack;
		else {
			FillBucketEvent event = new FillBucketEvent(player, stack, world, pos);
			if (MinecraftForge.EVENT_BUS.post(event))
				return stack;

			if (event.getResult() == Event.Result.ALLOW) {
				if (player.capabilities.isCreativeMode)
					return stack;

				if (--stack.stackSize <= 0)
					return event.result;

				if (!player.inventory.addItemStackToInventory(event.result))
					player.dropPlayerItemWithRandomChoice(event.result, false);

				return stack;
			}
			if (pos.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				int x = pos.blockX;
				int y = pos.blockY;
				int z = pos.blockZ;

				if (!world.canMineBlock(player, x, y, z))
					return stack;

				if (flag) {
					if (!player.canPlayerEdit(x, y, z, pos.sideHit, stack))
						return stack;

					Block block = world.getBlock(x, y, z);
					int meta = world.getBlockMetadata(x, y, z);

					if (block == Blocks.water && meta == 0) {
						world.setBlockToAir(x, y, z);
						return addBucketToPlayer(stack, player, ModItems.bambucketWater);
					}

					if (block == ModBlocks.honeyBlock && meta == 0) {
						world.setBlockToAir(x, y, z);
						return addBucketToPlayer(stack, player, ModItems.bambucketHoney);
					}

					if (block == ModBlocks.formicAcid && meta == 0) {
						world.setBlockToAir(x, y, z);
						return addBucketToPlayer(stack, player, ModItems.bambucketFormicAcid);
					}
				} else {
					if (pos.sideHit == 0)
						y--;
					if (pos.sideHit == 1)
						y++;
					if (pos.sideHit == 2)
						z--;
					if (pos.sideHit == 3)
						z++;
					if (pos.sideHit == 4)
						x--;
					if (pos.sideHit == 5)
						x++;

					if (!player.canPlayerEdit(x, y, z, pos.sideHit, stack))
						return stack;

					if (tryPlaceContainedLiquid(world, x, y, z) && !player.capabilities.isCreativeMode) {
						stack.stackSize--;
						if (stack.stackSize <= 0)
							return new ItemStack(ModItems.bambucket);
						else
							player.inventory.addItemStackToInventory(new ItemStack(ModItems.bambucket));
						return stack;
					}
				}
			}

			return stack;
		}
	}

	private ItemStack addBucketToPlayer(ItemStack stack, EntityPlayer player, Item item) {
		if (player.capabilities.isCreativeMode)
			return stack;
		else if (--stack.stackSize <= 0)
			return new ItemStack(item);
		else {
			if (!player.inventory.addItemStackToInventory(new ItemStack(item)))
				player.dropPlayerItemWithRandomChoice(new ItemStack(item), false);

			return stack;
		}
	}

	private boolean tryPlaceContainedLiquid(World world, int x, int y, int z) {
		if (fluid == Blocks.air)
			return false;
		else {
			Material material = world.getBlock(x, y, z).getMaterial();
			boolean flag = !material.isSolid();

			if (!world.isAirBlock(x, y, z) && !flag)
				return false;
			else {
				if (world.provider.isHellWorld && fluid == Blocks.flowing_water) {
					world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

					for (int i = 0; i < 8; i++)
						world.spawnParticle("largesmoke", x + Math.random(), y + Math.random(), z + Math.random(), 0.0D, 0.0D, 0.0D);
				} else {
					if (!world.isRemote && flag && !material.isLiquid())
						world.func_147480_a(x, y, z, true);

					world.setBlock(x, y, z, fluid, 0, 3);
				}

				return true;
			}
		}
	}
}