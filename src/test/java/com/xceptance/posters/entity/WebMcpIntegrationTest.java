package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WebMcpIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testWebMcpBrowserApiInjection() throws Exception {
        // The javascript modelContext.registerTool should be present in the default layout for both tools
        mockMvc.perform(get("/en-US/"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("window.navigator.modelContext.registerTool")))
            .andExpect(content().string(containsString("name: \"search_catalog\"")))
            .andExpect(content().string(containsString("name: \"add_to_cart\"")));
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

    @Test
    void testJsonAddEndpointForAgents() throws Exception {
        // The JSON backend add-to-cart endpoint should correctly handle incoming agent payloads.
        // We supply an invalid product ID purely to verify structured JSON failure behavior (success=false).
        String jsonPayload = "{\"productId\": 99999, \"quantity\": 1}";
        mockMvc.perform(post("/api/v2/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(containsString("Product not found")));
    }
}
