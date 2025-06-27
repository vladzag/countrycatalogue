package guru.qa.countrycatalogue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.countrycatalogue.data.CountryEntity;
import guru.qa.countrycatalogue.data.CountryRepository;
import guru.qa.countrycatalogue.exception.CountryNotFoundException;
import guru.qa.countrycatalogue.graphql.CountryGql;
import guru.qa.countrycatalogue.graphql.CountryInputGql;
import guru.qa.countrycatalogue.model.CountryJson;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public @Nonnull CountryJson addCountry(CountryJson country) {
        CountryEntity ce = new CountryEntity();
        ce.setName(country.name());
        ce.setCode(country.code());

        CountryEntity createdCountry = countryRepository.save(ce);
        return CountryJson.fromEntity(createdCountry);
    }

    @Override
    public @Nonnull CountryJson updateCountry(CountryJson country) {
        return countryRepository.findByName(country.name()).map(
                countryEntity -> {
                    countryEntity.setName(country.name());
                    countryEntity.setCode(country.code());
                    return CountryJson.fromEntity(countryRepository.save(countryEntity));
                }
        ).orElseThrow(() -> new CountryNotFoundException(
                        "Can't find country with given name" + country.name()
                )
        );
    }

    @Override
    public @Nonnull CountryJson updateCountryName(String countryCode, String jsonName) {
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
                    return CountryJson.fromEntity(countryRepository.save(countryEntity));
                }
        ).orElseThrow(() -> new CountryNotFoundException(
                        "Can't find country by given country code " + countryCode
                )
        );
    }

    @Override
    public @Nonnull List<CountryJson> getAll() {
        return countryRepository.findAll()
                .stream()
                .map(CountryJson::fromEntity)
                .toList();
    }

    @Override
    public CountryGql updateCountryNameGql(String countryCode, String countryName) {
        return countryRepository.findByCode(countryCode).map(
                ce -> {
                    ce.setName(countryName);
                    CountryEntity saved = countryRepository.save(ce);
                    return new CountryGql(
                            saved.getId(),
                            saved.getName(),
                            saved.getCode()
                    );
                }
        ).orElseThrow(() -> new CountryNotFoundException(
                        "Can't find country by given country code " + countryCode
                )
        );
    }

    @Override
    public Page<CountryGql> getAllGql(Pageable pageable) {
        return countryRepository.findAll(pageable)
                .map(ce -> new CountryGql(
                        ce.getId(),
                        ce.getName(),
                        ce.getCode()
                ));
    }

    @Override
    public CountryGql addGqlCountry(CountryInputGql input) {
        CountryEntity ce = new CountryEntity();
        ce.setName(input.name());
        ce.setCode(input.code());

        CountryEntity saved = countryRepository.save(ce);

        return new CountryGql(
                saved.getId(),
                saved.getName(),
                saved.getCode());
    }
}
