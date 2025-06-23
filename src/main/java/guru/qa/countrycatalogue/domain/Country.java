package guru.qa.countrycatalogue.domain;

import guru.qa.countrycatalogue.data.CountryEntity;
import jakarta.annotation.Nonnull;

public record Country(String name, String code) {

    public static @Nonnull Country fromEntity(@Nonnull CountryEntity entity) {
        return new Country(
                entity.getName(),
                entity.getCode());
    }
}
