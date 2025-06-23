package guru.qa.countrycatalogue.data;

import guru.qa.countrycatalogue.domain.Country;
import guru.qa.countrycatalogue.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/country")
public class CountryController {
    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Country addCountry(@RequestBody Country country) {
        return countryService.addCountry(country);
    }

    @PutMapping("/edit")
    public Country editCountry(@RequestBody Country country) {
        return countryService.updateCountry(country);
    }

    @PatchMapping(value = "/{countryCode}/edit")
    public Country editCountry(@PathVariable(value = "countryCode") String countryCode,
                               @RequestBody String countryName) {
        return countryService.updateCountryName(countryCode, countryName);
    }

    @GetMapping("/all")
    public List<Country> getAll() {
        return countryService.getAll();
    }
}
