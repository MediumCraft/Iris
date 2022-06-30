package com.volmit.iris.engine.dimension;

import com.volmit.iris.engine.editor.Mutated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Accessors(fluent = true, chain = true)
public class IrisDimension implements Mutated
{
    @Builder.Default
    private IrisDimensionMeta meta = new IrisDimensionMeta();
}