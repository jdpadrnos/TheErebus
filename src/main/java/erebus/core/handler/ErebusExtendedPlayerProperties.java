package erebus.core.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ErebusExtendedPlayerProperties implements IExtendedEntityProperties {

	public final static String EREBUS_EXT_PROP_NAME = "ErebusExtendedPlayerProperties";

	private final EntityPlayer player;

	private int x, z;
	private String dimension;
	private boolean justDied;

	public ErebusExtendedPlayerProperties(EntityPlayer player) {
		this.player = player;
		dimension = "None";
		x = this.z = 0;
		justDied = false;
	}

	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(ErebusExtendedPlayerProperties.EREBUS_EXT_PROP_NAME, new ErebusExtendedPlayerProperties(player));
	}

	public static final ErebusExtendedPlayerProperties get(EntityPlayer player) {
		return (ErebusExtendedPlayerProperties) player.getExtendedProperties(EREBUS_EXT_PROP_NAME);
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setString("dimension", dimension);
		properties.setInteger("xLocation", x);
		properties.setInteger("zLocation", z);
		properties.setBoolean("justDied", justDied);
		compound.setTag(EREBUS_EXT_PROP_NAME, properties);
		System.out.println("Dimension: "+ dimension + " X: " + x + " Z: " + z + " Recently deceased: " + justDied);
		
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EREBUS_EXT_PROP_NAME);
		dimension = properties.getString("dimension");
		x = properties.getInteger("xLocation");
		z = properties.getInteger("zLocation");
		justDied = properties.getBoolean("justDied");
		System.out.println("Dimension: "+ dimension + " X: " + x + " Z: " + z + " Recently deceased: " + justDied);
	}

	@Override
	public void init(Entity entity, World world) {
	}

	public void setRecentlyDeceased(boolean state) {
		justDied = state;
	}

	public boolean getRecentlyDeceased() {
		return justDied;
	}

	public void setDimension(String string) {
		dimension = string;
	}

	public String getDimension() {
		return dimension;
	}

	public void setXLocation(int xLocation) {
		x = xLocation;
	}

	public int getXLocation() {
		return x;
	}

	public void setZLocation(int zLocation) {
		z = zLocation;
	}

	public int getZLocation() {
		return z;
	}
}