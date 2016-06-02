package erebus.world.genlayer;

import erebus.world.biomes.BiomeBaseErebus;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerSubBiomes extends GenLayerErebus {
	private static final byte[] offsetX = new byte[] { 0, 1, -1, 0, 0 }, offsetZ = new byte[] { 0, 0, 0, 1, -1 };

	public GenLayerSubBiomes(long seed, GenLayer parentGenLayer) {
		super(seed);
		parent = parentGenLayer;
	}

	@Override
	public int[] getInts(int x, int z, int sizeX, int sizeZ) {
		int[] currentBiomeInts = parent.getInts(x - 2, z - 2, sizeX + 4, sizeZ + 4);
		int[] biomeInts = IntCache.getIntCache(sizeX * sizeZ);

		for (int zz = 0; zz < sizeZ; ++zz)
			for (int xx = 0; xx < sizeX; ++xx) {
				initChunkSeed(xx + x, zz + z);
				biomeInts[xx + zz * sizeX] = currentBiomeInts[xx + 2 + (zz + 2) * (sizeX + 4)];
			}

		initChunkSeed(x, z);

		for (int attempt = 0, xx, zz; attempt < 6; attempt++) {
			xx = 1 + nextInt(sizeX - 2);
			zz = 1 + nextInt(sizeZ - 2);

			int biomeID = currentBiomeInts[xx + 2 + (zz + 2) * (sizeX + 4)];

			BiomeBaseErebus biome = (BiomeBaseErebus) BiomeGenBase.getBiomeGenArray()[biomeID];
			BiomeBaseErebus subBiome = biome.getRandomSubBiome(nextInt(101));

			if (subBiome != null && biome != subBiome)
				for (int a = 0, bx1, bx2, bz1, bz2, nx, nz; a < 5; a++) {
					nx = xx + offsetX[a];
					nz = zz + offsetZ[a];
					bz1 = currentBiomeInts[nx + 2 + (nz + 2 - 1) * (sizeX + 4)];
					bx1 = currentBiomeInts[nx + 2 + 1 + (nz + 2) * (sizeX + 4)];
					bx2 = currentBiomeInts[nx + 2 - 1 + (nz + 2) * (sizeX + 4)];
					bz2 = currentBiomeInts[nx + 2 + (nz + 2 + 1) * (sizeX + 4)];

					if (bx1 == biomeID && bx2 == biomeID && bz1 == biomeID && bz2 == biomeID && (a == 0 || nextInt(3) != 0)) {
						biomeInts[nx + nz * sizeX] = subBiome.biomeID;
						attempt = 999;
					} else if (a == 0)
						break;
				}
		}

		return biomeInts;
	}
}
