package guru.qa.countrycatalogue.service.soap;

import guru.qa.countrycatalogue.config.AppConfig;
import guru.qa.countrycatalogue.domain.Country;
import guru.qa.countrycatalogue.graphql.CountryGql;
import guru.qa.countrycatalogue.model.CountryJson;
import guru.qa.countrycatalogue.service.CountryService;
import guru.qa.grpc.countrycatalog.CountriesResponse;
import guru.qa.grpc.countrycatalog.CountryResponse;
import guru.qa.xml.countrycatalogue.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;

@Endpoint
public class CountryEndpoint {
    private final CountryService countryService;

    public CountryEndpoint(CountryService countryService) {
        this.countryService = countryService;
    }

    @PayloadRoot(namespace = AppConfig.SOAP_NAMESPACE, localPart = "nameUpdateRequest")
    @ResponsePayload
    public CountryResponse updateName(@RequestPayload NameUpdateRequest request) {
        CountryGql countryGql = countryService.updateCountryNameGql(
                request.getCode(),
                request.getName()
        );
        CountryResponse response = new CountryResponse();
        Country xmlCountry = new Country();
        xmlCountry.setName(countryGql.name());
        xmlCountry.setCode(countryGql.code());
        response.setCountry(xmlCountry);
        return response;
    }

    @PayloadRoot(namespace = AppConfig.SOAP_NAMESPACE, localPart = "countryInputRequest")
    @ResponsePayload
    public CountryResponse addCountry(@RequestPayload CountryInputRequest request) {
        CountryJson countryJson = countryService.addCountry(
                new CountryJson(
                        request.getName(),
                        request.getCode()
                )
        );
        CountryResponse response = new CountryResponse();
        Country xmlCountry = new Country();
        xmlCountry.setName(countryJson.name());
        xmlCountry.setCode(countryJson.code());
        response.setCountry(xmlCountry);
        return response;
    }

    @PayloadRoot(namespace = AppConfig.SOAP_NAMESPACE, localPart = "countriesRequest")
    @ResponsePayload
    public CountriesResponse getAll(@RequestPayload CountriesRequest request) {
        List<CountryJson> all = countryService.getAll();
        CountriesResponse response = new CountriesResponse();
        response.getCountries().addAll(
                all.stream()
                        .map(CountryEndpoint::fromJson).toList()
        );
        return response;
    }

    @PayloadRoot(namespace = AppConfig.SOAP_NAMESPACE, localPart = "countryPageRequest")
    @ResponsePayload
    public CountryPageResponse getAllPage(@RequestPayload CountryPageRequest request) {
        Page<CountryGql> all = countryService.getAllGql(
                PageRequest.of(
                        request.getPage(),
                        request.getSize()
                )
        );
        CountryPageResponse response = new CountryPageResponse();
        response.setTotalPages(all.getTotalPages());
        response.setTotalElements(all.getTotalElements());
        response.getCountries().addAll(
                all.stream()
                        .map(countryGql -> {
                            Country xmlCountry = new Country();
                            xmlCountry.setName(countryGql.name());
                            xmlCountry.setCode(countryGql.code());
                            return xmlCountry;
                        }).toList()
        );
        return response;
    }

    private static Country fromJson(CountryJson countryJson) {
        Country xmlCountry = new Country();
        xmlCountry.setName(countryJson.name());
        xmlCountry.setCode(countryJson.code());
        return xmlCountry;
    }

}