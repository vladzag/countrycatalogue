package guru.qa.countrycatalogue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.countrycatalogue.data.CountryEntity;
import guru.qa.countrycatalogue.data.CountryRepository;
import guru.qa.countrycatalogue.domain.Country;
import guru.qa.countrycatalogue.exception.CountryNotFoundException;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CountryDbService implements CountryService {
    private final CountryRepository countryRepository;

    @Autowired
    public CountryDbService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public @Nonnull Country addCountry(Country country) {
        CountryEntity ce = new CountryEntity();
        ce.setName(country.name());
        ce.setCode(country.code());

        CountryEntity createdCountry = countryRepository.save(ce);
        return Country.fromEntity(createdCountry);
    }

    @Override
    public @Nonnull Country updateCountry(Country country) {
        return countryRepository.findByName(country.name()).map(
                countryEntity -> {
                    countryEntity.setName(country.name());
                    countryEntity.setCode(country.code());
                    return Country.fromEntity(countryRepository.save(countryEntity));
                }
        ).orElseThrow(() -> new CountryNotFoundException(
                        "Can't find country with given name" + country.name()
                )
        );
    }

    @Override
    public @Nonnull Country updateCountryName(String countryCode, String jsonName) {
        final String countryName;
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(jsonName);
            countryName = jsonNode.get("name").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return countryRepository.findByCode(countryCode).map(
                countryEntity -> {
                    countryEntity.setName(countryName);
                    return Country.fromEntity(countryRepository.save(countryEntity));
                }
        ).orElseThrow(() -> new CountryNotFoundException(
                        "Can't find country by given country code " + countryCode
                )
        );
    }

    @Override
    public @Nonnull List<Country> getAll() {
        return countryRepository.findAll()
                .stream()
                .map(Country::fromEntity)
                .toList();
    }}
