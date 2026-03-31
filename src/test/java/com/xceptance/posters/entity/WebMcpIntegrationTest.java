package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WebMcpIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testWebMcpBrowserApiInjection() throws Exception {
        // The javascript modelContext.registerTool should be present in the default layout
        mockMvc.perform(get("/en-US/"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("window.navigator.modelContext.registerTool")));
    }
    
    @Test
    void testJsonSearchEndpointForAgents() throws Exception {
        // The JSON backend search endpoint should correctly return product data
        mockMvc.perform(get("/api/v2/catalog/search")
                .param("q", "Posters")
                .param("locale", "en-US")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray());
    }
}
