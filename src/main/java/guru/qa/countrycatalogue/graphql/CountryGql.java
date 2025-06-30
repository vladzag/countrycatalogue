package guru.qa.countrycatalogue.graphql;

import java.util.UUID;

public record CountryGql(
        UUID id,
        String name,
        String code
) {
    public static CountryGql instance(UUID id, String name, String code) {
        return new CountryGql(id, name, code);
    }
}