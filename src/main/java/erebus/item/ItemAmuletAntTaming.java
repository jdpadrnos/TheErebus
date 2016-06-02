package erebus.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erebus.ModBlocks;
import erebus.ModTabs;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemAmuletAntTaming extends Item {

	public ItemAmuletAntTaming() {
		setMaxDamage(16);
		setMaxStackSize(1);
		setCreativeTab(ModTabs.specials);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		if (hasTag(stack) && stack.stackTagCompound.hasKey("homeX")) {
			list.add(StatCollector.translateToLocalFormatted("tooltip.erebus.siloblockx", stack.getTagCompound().getInteger("homeX")));
			list.add(StatCollector.translateToLocalFormatted("tooltip.erebus.siloblocky", stack.getTagCompound().getInteger("homeY")));
			list.add(StatCollector.translateToLocalFormatted("tooltip.erebus.siloblockz", stack.getTagCompound().getInteger("homeZ")));
		} else {
			list.add(StatCollector.translateToLocal("tooltip.erebus.anttamingamulet_1"));
			list.add(StatCollector.translateToLocal("tooltip.erebus.anttamingamulet_2"));
		}
	}

	@Override
	public boolean onItemUseFirst(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && hasTag(is)) {
			Block block = world.getBlock(x, y, z);
			if (!world.isRemote && block != null) {
				if (block == ModBlocks.siloTank) {
					is.getTagCompound().setInteger("homeX", x);
					is.getTagCompound().setInteger("homeY", y);
					is.getTagCompound().setInteger("homeZ", z);
				}
				player.swingItem();
				is.damageItem(1, player);
				return true;
			}
		}
		return false;
	}

	private boolean hasTag(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			return false;
		}
		return true;
	}
}