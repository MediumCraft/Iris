package com.volmit.iris.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.block.data.BlockData;

public class ParallaxSection implements Writable
{
	private final DataPalette<BlockData> block;
	private final KSet<Short> updates;

	public ParallaxSection(DataInputStream in) throws IOException
	{
		this();
		read(in);
	}

	public ParallaxSection()
	{
		updates = new KSet<Short>();
		this.block = new DataPalette<BlockData>(B.get("AIR").getBlockData())
		{
			@Override
			public void writeType(BlockData t, DataOutputStream o) throws IOException
			{
				o.writeUTF(t.getAsString(true));
			}

			@Override
			public BlockData readType(DataInputStream i) throws IOException
			{
				return B.get(i.readUTF()).getBlockData();
			}
		};
	}

	public void clearUpdates()
	{
		updates.clear();
	}

	public void update(int x, int y, int z)
	{
		updates.add((short) (y << 8 | z << 4 | x));
	}

	public void dontUpdate(int x, int y, int z)
	{
		updates.remove((short) (y << 8 | z << 4 | x));
	}

	public void setBlock(int x, int y, int z, BlockData d)
	{
		block.set(x, y, z, d);

		if(B.isUpdatable(FastBlockData.of(d)))
		{
			update(x, y, z);
		}

		else
		{
			dontUpdate(x, y, z);
		}
	}

	public BlockData getBlock(int x, int y, int z)
	{
		return block.get(x, y, z);
	}

	@Override
	public void write(DataOutputStream o) throws IOException
	{
		block.write(o);
		o.writeShort(updates.size());

		for(Short i : updates)
		{
			o.writeShort(i);
		}
	}

	@Override
	public void read(DataInputStream i) throws IOException
	{
		block.read(i);
		updates.clear();
		int m = i.readShort();

		for(int v = 0; v < m; v++)
		{
			updates.add(i.readShort());
		}
	}
}
