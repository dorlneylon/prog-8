package itmo.lab7.basic.types.builders;

import itmo.lab7.basic.baseclasses.Location;
import itmo.lab7.basic.baseclasses.Person;
import itmo.lab7.basic.baseenums.Color;
import itmo.lab7.basic.types.builders.annotations.NotNull;
import itmo.lab7.basic.types.builders.annotations.Value;

import java.util.Date;

public class DirectorBuilder implements Builder {
    @NotNull
    private String name;

    @NotNull
    private Date birthday;

    @Value(min = 0)
    private int height;
    @NotNull
    private Color hairColor;
    @NotNull
    private Location location;

    @Override
    public Person build() {
        return new Person(name, birthday, height, hairColor, location);
    }
}
