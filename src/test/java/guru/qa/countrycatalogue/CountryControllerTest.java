package guru.qa.countrycatalogue;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.countrycatalogue.model.CountryJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Test
    void newCountryShouldBeCreated() throws Exception {
        CountryJson country = new CountryJson(
                "France",
                "FR"
        );
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/country/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(country))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("France"))
                .andExpect(jsonPath("$.code").value("FR"));
    }

    @Test
    @Sql(scripts = "/countryShouldBeUpdated.sql")
    void countryShouldBeUpdated() throws Exception {
        CountryJson country = new CountryJson(
                "France",
                "FR"
        );
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/country/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(country))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("France"))
                .andExpect(jsonPath("$.code").value("FR"));
    }

    @Test
    @Sql(scripts = "/allCountriesShouldBeReturned.sql")
    void allCountriesShouldBeReturned() throws Exception {
        mockMvc.perform(get("/api/country/all")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].name").value("Russia"))
                .andExpect(jsonPath("$[0].code").value("RU"));
    }

    @Test
    @Sql(scripts = "/countryShouldBeUpdated.sql")
    void countryNameShouldBeUpdated() throws Exception {
        String code = "FR";
        Map<String,String> map = new HashMap<>();
        map.put("name", "Jean-Pierrie");

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/country/" + code + "/edit")
                        .content(om.writeValueAsString(map))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jean-Pierrie"))
                .andExpect(jsonPath("$.code").value("FR"));
    }
}