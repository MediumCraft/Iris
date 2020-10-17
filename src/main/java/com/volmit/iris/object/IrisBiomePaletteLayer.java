package com.volmit.iris.object;

import com.volmit.iris.gen.atomics.AtomicCache;
import com.volmit.iris.manager.IrisDataManager;
import com.volmit.iris.noise.CNG;
import com.volmit.iris.util.ArrayType;
import com.volmit.iris.util.DependsOn;
import com.volmit.iris.util.Desc;
import com.volmit.iris.util.DontObfuscate;
import com.volmit.iris.util.FastBlockData;
import com.volmit.iris.util.KList;
import com.volmit.iris.util.MaxNumber;
import com.volmit.iris.util.MinNumber;
import com.volmit.iris.util.RNG;
import com.volmit.iris.util.Required;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Desc("A layer of surface / subsurface material in biomes")
@Data
public class IrisBiomePaletteLayer
{
	@DontObfuscate
	@Desc("The style of noise")
	private IrisGeneratorStyle style = NoiseStyle.STATIC.style();

	@DependsOn({"minHeight", "maxHeight"})
	@MinNumber(0)
	@MaxNumber(256)
	@DontObfuscate
	@Desc("The min thickness of this layer")
	private int minHeight = 1;

	@DependsOn({"minHeight", "maxHeight"})
	@MinNumber(1)
	@MaxNumber(256)
	@DontObfuscate
	@Desc("The max thickness of this layer")
	private int maxHeight = 1;

	@MinNumber(0.0001)
	@DontObfuscate
	@Desc("The terrain zoom mostly for zooming in on a wispy palette")
	private double zoom = 5;

	@Required
	@ArrayType(min = 1, type = IrisBlockData.class)
	@DontObfuscate
	@Desc("The palette of blocks to be used in this layer")
	private KList<IrisBlockData> palette = new KList<IrisBlockData>().qadd(new IrisBlockData("GRASS_BLOCK"));

	private final transient AtomicCache<KList<FastBlockData>> blockData = new AtomicCache<>();
	private final transient AtomicCache<CNG> layerGenerator = new AtomicCache<>();
	private final transient AtomicCache<CNG> heightGenerator = new AtomicCache<>();

	public CNG getHeightGenerator(RNG rng, IrisDataManager data)
	{
		return heightGenerator.aquire(() -> CNG.signature(rng.nextParallelRNG(minHeight * maxHeight + getBlockData(data).size())));
	}

	public FastBlockData get(RNG rng, double x, double y, double z, IrisDataManager data)
	{
		if(getBlockData(data).isEmpty())
		{
			return null;
		}

		if(getBlockData(data).size() == 1)
		{
			return getBlockData(data).get(0);
		}

		return getLayerGenerator(rng, data).fit(getBlockData(data), x / zoom, y / zoom, z / zoom);
	}

	public CNG getLayerGenerator(RNG rng, IrisDataManager data)
	{
		return layerGenerator.aquire(() ->
		{
			RNG rngx = rng.nextParallelRNG(minHeight + maxHeight + getBlockData(data).size());
			return style.create(rngx);
		});
	}

	public KList<IrisBlockData> add(String b)
	{
		palette.add(new IrisBlockData(b));

		return palette;
	}

	public KList<FastBlockData> getBlockData(IrisDataManager data)
	{
		return blockData.aquire(() ->
		{
			KList<FastBlockData> blockData = new KList<>();
			for(IrisBlockData ix : palette)
			{
				FastBlockData bx = ix.getBlockData(data);
				if(bx != null)
				{
					for(int i = 0; i < ix.getWeight(); i++)
					{
						blockData.add(bx);
					}
				}
			}

			return blockData;
		});
	}

	public IrisBiomePaletteLayer zero()
	{
		palette.clear();
		return this;
	}
}
