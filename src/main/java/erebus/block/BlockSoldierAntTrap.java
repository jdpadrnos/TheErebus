package erebus.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import erebus.ModTabs;
import erebus.tileentity.TileEntitySoldierAntTrap;

public class BlockSoldierAntTrap extends BlockContainer {

	public BlockSoldierAntTrap() {
		super(Material.rock);
		setStepSound(Block.soundTypeStone);
		setBlockUnbreakable();
		setResistance(6000000.0F);
		//setCreativeTab(ModTabs.blocks);
		setBlockTextureName("erebus:anthillBlock");
		setBlockName("erebus.soldierAntTrap");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntitySoldierAntTrap();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is) {
		int meta = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, meta == 0 ? 2 : meta == 1 ? 5 : meta == 2 ? 3 : 4, 2);
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
}