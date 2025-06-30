package guru.qa.countrycatalogue.graphql;

import guru.qa.countrycatalogue.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CountryMutationController {

    private final CountryService countryService;

    @Autowired
    public CountryMutationController(CountryService countryService) {
        this.countryService = countryService;
    }

    @MutationMapping
    public CountryGql addCountry(@Argument InputCountryGql input) {
        return countryService.addGqlCountry(input);
    }

    @MutationMapping
    public CountryGql editCountry(@Argument String code,
                                  @Argument String name) {
        return countryService.updateCountryNameGql(code, name);
    }

}