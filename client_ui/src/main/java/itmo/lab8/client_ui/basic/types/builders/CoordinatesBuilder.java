package itmo.lab8.client_ui.basic.types.builders;


import itmo.lab8.client_ui.basic.baseclasses.Coordinates;
import itmo.lab8.client_ui.basic.types.builders.annotations.NotNull;
import itmo.lab8.client_ui.basic.types.builders.annotations.Value;

public class CoordinatesBuilder implements Builder {

    @Value(min = -521)
    private float x;
    @Value(max = 256)
    @NotNull
    private Integer y;

    @Override
    public Coordinates build() {
        return new Coordinates(x, y);
    }
}
