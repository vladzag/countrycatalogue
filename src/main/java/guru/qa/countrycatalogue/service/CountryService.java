package guru.qa.countrycatalogue.service;

import guru.qa.countrycatalogue.graphql.CountryGql;
import guru.qa.countrycatalogue.graphql.InputCountryGql;
import guru.qa.countrycatalogue.model.CountryJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CountryService {
    CountryJson addCountry(CountryJson country);

    CountryJson updateCountry(CountryJson country);

    CountryJson updateCountryName(String countryCode, String countryName);

    List<CountryJson> getAll();

    CountryGql updateCountryNameGql(String countryCode, String countryName);

    Page<CountryGql> getAllGql(Pageable pageable);

    CountryGql addGqlCountry(InputCountryGql input);
}
