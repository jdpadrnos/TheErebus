package erebus.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import erebus.ModBiomes;
import erebus.world.genlayer.GenLayerErebus;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.WorldTypeEvent;

public class WorldChunkManagerErebus extends WorldChunkManager {

	private static final float rainfall = 0F;
	private static final ArrayList<BiomeGenBase> allowedBiomes = new ArrayList<BiomeGenBase>(Arrays.asList(ModBiomes.undergroundJungle, ModBiomes.subterraneanSavannah));

	@SuppressWarnings("rawtypes")
	private final List biomesToSpawnIn;
	private final BiomeCache biomeCache;
	private final GenLayer biomeGenLayer;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public WorldChunkManagerErebus(World world) {
		biomesToSpawnIn = new ArrayList(allowedBiomes);
		biomeCache = new BiomeCache(this);
		biomeGenLayer = GenLayerErebus.initializeAllBiomeGenerators(world.getSeed(), world.getWorldInfo().getTerrainType())[1];
	}

	@Override
	public BiomeGenBase getBiomeGenAt(int chunkX, int chunkZ) {
		return biomeCache.getBiomeGenAt(chunkX, chunkZ);
	}

	@Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase biomesForGeneration[], int x, int z, int sizeX, int sizeZ) {
		IntCache.resetIntCache();

		if (biomesForGeneration == null || biomesForGeneration.length < sizeX * sizeZ)
			biomesForGeneration = new BiomeGenBase[sizeX * sizeZ];

		int[] biomeArray = biomeGenLayer.getInts(x, z, sizeX, sizeZ);

		for (int index = 0; index < sizeX * sizeZ; ++index)
			biomesForGeneration[index] = BiomeGenBase.getBiomeGenArray()[biomeArray[index]];

		return biomesForGeneration;
	}

	/*
	 * @Override public float[] getTemperatures(float temperatureArray[], int x, int z, int sizeX, int sizeZ) { if (temperatureArray == null || temperatureArray.length < sizeX * sizeZ) temperatureArray = new float[sizeX * sizeZ]; Arrays.fill(temperatureArray, 0, sizeX * sizeZ, temperature); return temperatureArray; }
	 */

	@Override
	public float[] getRainfall(float rainfallArray[], int x, int z, int sizeX, int sizeZ) {
		if (rainfallArray == null || rainfallArray.length < sizeX * sizeZ)
			rainfallArray = new float[sizeX * sizeZ];

		Arrays.fill(rainfallArray, 0, sizeX * sizeZ, rainfall);
		return rainfallArray;
	}

	@Override
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase biomesForGeneration[], int x, int z, int sizeX, int sizeZ) {
		return getBiomeGenAt(biomesForGeneration, x, z, sizeX, sizeZ, true);
	}

	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomesForGeneration, int x, int z, int sizeX, int sizeZ, boolean useCache) {
		IntCache.resetIntCache();

		if (biomesForGeneration == null || biomesForGeneration.length < sizeX * sizeZ)
			biomesForGeneration = new BiomeGenBase[sizeX * sizeZ];

		if (useCache && sizeX == 16 && sizeZ == 16 && (x & 15) == 0 && (z & 15) == 0) {
			BiomeGenBase[] cachedBiomes = biomeCache.getCachedBiomes(x, z);
			System.arraycopy(cachedBiomes, 0, biomesForGeneration, 0, sizeX * sizeZ);
			return biomesForGeneration;
		} else {
			int[] generatedBiomes = biomeGenLayer.getInts(x, z, sizeX, sizeZ);
			for (int index = 0; index < sizeX * sizeZ; ++index)
				biomesForGeneration[index] = BiomeGenBase.getBiomeGenArray()[generatedBiomes[index]];

			return biomesForGeneration;
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ChunkPosition findBiomePosition(int x, int z, int checkRadius, List viableBiomes, Random rand) {
		IntCache.resetIntCache();
		int minX = x - checkRadius >> 2;
		int minZ = z - checkRadius >> 2;
		int maxX = x + checkRadius >> 2;
		int maxZ = z + checkRadius >> 2;
		int sizeX = maxX - minX + 1;
		int sizeZ = maxZ - minZ + 1;
		int[] biomeArray = biomeGenLayer.getInts(minX, minZ, sizeX, sizeZ);
		ChunkPosition pos = null;
		int attempts = 0;

		for (int index = 0; index < sizeX * sizeZ; ++index) {
			int finalX = minX + index % sizeX << 2;
			int finalZ = minZ + index / sizeX << 2;

			if (viableBiomes.contains(BiomeGenBase.getBiomeGenArray()[biomeArray[index]]) && (pos == null || rand.nextInt(attempts + 1) == 0)) {
				pos = new ChunkPosition(finalX, 0, finalZ);
				++attempts;
			}
		}

		return pos;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean areBiomesViable(int x, int z, int checkRadius, List viableBiomes) {
		IntCache.resetIntCache();
		int minX = x - checkRadius >> 2;
		int minZ = z - checkRadius >> 2;
		int maxX = x + checkRadius >> 2;
		int maxZ = z + checkRadius >> 2;
		int sizeX = maxX - minX + 1;
		int sizeZ = maxZ - minZ + 1;
		int[] biomeArray = biomeGenLayer.getInts(minX, minZ, sizeX, sizeZ);

		for (int index = 0; index < sizeX * sizeZ; ++index)
			if (!viableBiomes.contains(BiomeGenBase.getBiomeGenArray()[biomeArray[index]]))
				return false;

		return true;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List getBiomesToSpawnIn() {
		return biomesToSpawnIn;
	}

	@Override
	public void cleanupCache() {
		biomeCache.cleanupCache();
	}

	@Override
	public GenLayer[] getModdedBiomeGenerators(WorldType worldType, long seed, GenLayer[] original) {
		WorldTypeEvent.InitBiomeGens event = new WorldTypeEvent.InitBiomeGens(worldType, seed, original);
		MinecraftForge.TERRAIN_GEN_BUS.post(event);
		return event.newBiomeGens;
	}
}
