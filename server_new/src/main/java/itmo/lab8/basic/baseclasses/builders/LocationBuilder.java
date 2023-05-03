package itmo.lab8.basic.baseclasses.builders;

import itmo.lab8.basic.baseclasses.Location;
import itmo.lab8.basic.baseclasses.builders.annotations.NotNull;

public class LocationBuilder implements Builder {

    private long x;
    @NotNull
    private Double y;
    private double z;

    @Override
    public Location build() {
        return new Location(x, y, z);
    }
}
