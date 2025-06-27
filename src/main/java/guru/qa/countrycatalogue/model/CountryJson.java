package guru.qa.countrycatalogue.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.countrycatalogue.data.CountryEntity;
import jakarta.annotation.Nonnull;

public record CountryJson(
        @JsonProperty("name")
        String name,
        @JsonProperty("code")
        String code
) {

    public static @Nonnull CountryJson fromEntity(@Nonnull CountryEntity entity) {
        return new CountryJson(
                entity.getName(),
                entity.getCode());
    }
}