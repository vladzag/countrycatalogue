package guru.qa.countrycatalogue.service;

import com.google.protobuf.Empty;
import guru.qa.countrycatalogue.model.graphql.CountryGql;
import guru.qa.countrycatalogue.model.graphql.CountryInputGql;
import guru.qa.grpc.countrycatalog.*;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;


@Service
public class GrpcCountrycatalogService extends CountrycatalogServiceGrpc.CountrycatalogServiceImplBase {

    private final CountryService countryService;


    public GrpcCountrycatalogService(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public void addCountry(CountryRequest request, StreamObserver<CountryResponse> responseObserver) {
        CountryGql countryGql = countryService.addGqlCountry(
                new CountryInputGql(
                        request.getName(),
                        request.getCode()
                )
        );

        responseObserver.onNext(CountryResponse.newBuilder()
                .setCode(countryGql.code())
                .setName(countryGql.name())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<CountryRequest> addCountries(StreamObserver<CountResponse> responseObserver) {
        AtomicInteger count = new AtomicInteger();

        return new StreamObserver<>() {

            @Override
            public void onNext(CountryRequest countryRequest) {
                countryService.addGqlCountry(
                        new CountryInputGql(
                                countryRequest.getCode(),
                                countryRequest.getName()
                        )
                );

                count.incrementAndGet();
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                CountResponse response = CountResponse.newBuilder()
                        .setCount(count.get())
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void updateCountry(CountryUpdateRequest request, StreamObserver<CountryResponse> responseObserver) {
        CountryGql updatedCountry = countryService.updateCountryNameGql(request.getCode(), request.getName());

        responseObserver.onNext(
                CountryResponse.newBuilder()
                        .setCode(updatedCountry.code())
                        .setName(updatedCountry.name())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void getAll(Empty request, StreamObserver<CountriesResponse> responseObserver) {
        CountriesResponse response = CountriesResponse.newBuilder()
                .addAllAllCountries(countryService.getAll().stream()
                        .map(countryJson -> CountryResponse.newBuilder()
                                .setCode(countryJson.code())
                                .setName(countryJson.name())
                                .build()
                        ).toList()
                ).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}