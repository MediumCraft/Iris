package com.volmit.iris.v2.scaffold.engine;

import com.volmit.iris.util.RollingSequence;
import com.volmit.iris.v2.generator.IrisComplex;
import com.volmit.iris.v2.scaffold.hunk.Hunk;
import com.volmit.iris.v2.scaffold.parallax.ParallaxAccess;
import com.volmit.iris.manager.IrisDataManager;
import com.volmit.iris.object.IrisDimension;

public interface EngineActuator<O> extends EngineComponent
{
    public void actuate(int x, int z, Hunk<O> output);
}