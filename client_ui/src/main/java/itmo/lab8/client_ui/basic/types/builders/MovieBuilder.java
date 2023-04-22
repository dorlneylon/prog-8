package itmo.lab8.client_ui.basic.types.builders;


import itmo.lab8.client_ui.basic.baseclasses.Coordinates;
import itmo.lab8.client_ui.basic.baseclasses.Movie;
import itmo.lab8.client_ui.basic.baseclasses.Person;
import itmo.lab8.client_ui.basic.baseenums.MovieGenre;
import itmo.lab8.client_ui.basic.baseenums.MpaaRating;
import itmo.lab8.client_ui.basic.types.builders.annotations.Generated;
import itmo.lab8.client_ui.basic.types.builders.annotations.NotNull;
import itmo.lab8.client_ui.basic.types.builders.annotations.Value;

import java.time.ZonedDateTime;

public class MovieBuilder implements Builder {
    @NotNull
    @Generated
    @Value(min = 0)
    private Long id;
    @NotNull
    private String name;

    @NotNull
    private Coordinates coordinates;
    @NotNull
    @Generated
    private ZonedDateTime creationDate;

    @Value(min = 0)
    private long oscarsCount;

    @NotNull
    private MovieGenre genre;
    @NotNull
    private MpaaRating mpaaRating;
    @NotNull
    private Person director;

    @Override
    public Movie build() {
        return new Movie(id, name, coordinates, creationDate, oscarsCount, genre, mpaaRating, director);
    }
}
