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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            .andExpect(content().string(containsString("name: \"add_to_cart\"")))
            .andExpect(content().string(containsString("name: \"submit_checkout\"")));
    }
    
    @Test
    void testToolDescriptionsHaveValidJsonExamples() throws Exception {
        // Fetch the HTML containing the WebMCP tool registrations
        final String htmlContent = mockMvc.perform(get("/en-US/"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        // Count how many tools are registered
        final int registeredToolsCount = htmlContent.split("window\\.navigator\\.modelContext\\.registerTool").length - 1;
        assertTrue(registeredToolsCount > 0, "Should have found at least one registered tool");

        // Extract the JSON objects following "Example payload:"
        final Pattern pattern = Pattern.compile("Example payload:\\s*(\\{.*?\\})\",");
        final Matcher matcher = pattern.matcher(htmlContent);
        
        final ObjectMapper mapper = new ObjectMapper();
        int matches = 0;
        while (matcher.find()) {
            final String jsonPayload = matcher.group(1).replace("\\\"", "\"");
            try {
                mapper.readTree(jsonPayload);
                matches++;
            } catch (JsonProcessingException e) {
                fail("Found invalid JSON in tool example payload: " + jsonPayload, e);
            }
        }
        
        // Enforce that EVERY registered tool has an example payload
        assertEquals(registeredToolsCount, matches, "Every registered tool must have an 'Example payload:' in its description that contains valid JSON");
    }
    
    private String extractExamplePayload(final String html, final String toolName) {
        final int nameIdx = html.indexOf("name: \"" + toolName + "\"");
        if (nameIdx == -1) fail("Tool not found: " + toolName);
        final int payloadIdx = html.indexOf("Example payload: ", nameIdx);
        if (payloadIdx == -1) fail("Example payload not found for tool: " + toolName);
        final int eolIdx = html.indexOf('\n', payloadIdx);
        final int endQuoteIdx = html.lastIndexOf('"', eolIdx);
        final String payload = html.substring(payloadIdx + "Example payload: ".length(), endQuoteIdx);
        return payload.replace("\\\"", "\"");
    }
    
    @Test
    void testEndToEndCheckoutUsingHtmlExamplePayloads() throws Exception {
        final String htmlContent = mockMvc.perform(get("/en-US/"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        final ObjectMapper mapper = new ObjectMapper();
        
        // 1. Search Catalog using the EXACT payload from the HTML documentation
        final String searchJson = extractExamplePayload(htmlContent, "search_catalog");
        final JsonNode searchParams = mapper.readTree(searchJson);
        mockMvc.perform(get("/api/v2/catalog/search")
                .param("q", searchParams.get("query").asText())
                .param("locale", "en-US")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        // 2. Add to Cart using the EXACT payload from the HTML documentation
        final MockHttpSession session = new MockHttpSession();
        final String addCartJson = extractExamplePayload(htmlContent, "add_to_cart");
        mockMvc.perform(post("/api/v2/cart/add")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addCartJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        // 3. Submit Checkout using the EXACT payload from the HTML documentation
        final String checkoutJson = extractExamplePayload(htmlContent, "submit_checkout");
        mockMvc.perform(post("/api/v2/checkout")
                .session(session) // MUST use the same session where the cart was added!
                .contentType(MediaType.APPLICATION_JSON)
                .content(checkoutJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.orderNumber").isNotEmpty());
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
        final String jsonPayload = "{\"productId\": 99999, \"quantity\": 1}";
        mockMvc.perform(post("/api/v2/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(containsString("Product not found")));
    }

    @Test
    void testJsonCheckoutEndpointHappyPath() throws Exception {
        final MockHttpSession session = new MockHttpSession();

        // Add a product first
        final String cartJson = "{\"productId\": 1, \"quantity\": 1}";
        mockMvc.perform(post("/api/v2/cart/add")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cartJson))
            .andExpect(status().isOk());

        // Now checkout
        final String checkoutJson = """
        {
            "shippingAddress": { "name": "Doe", "firstName": "John", "addressLine": "123 Main St", "city": "Anytown", "state": "CA", "zip": "90210", "country": "USA" },
            "billingAddress": { "name": "Doe", "firstName": "John", "addressLine": "123 Main St", "city": "Anytown", "state": "CA", "zip": "90210", "country": "USA" },
            "payment": { "cardNumber": "4242424242424242", "name": "John Doe", "expiry": "12/30", "cvv": "123" },
            "customer": { "email": "john.doe@example.com", "firstName": "John", "lastName": "Doe" }
        }
        """;

        mockMvc.perform(post("/api/v2/checkout")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(checkoutJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.orderNumber").isNotEmpty());
    }

    @Test
    void testJsonCheckoutInvalidCreditCard() throws Exception {
        final String checkoutJson = """
        {
            "shippingAddress": { "name": "Doe", "firstName": "John", "addressLine": "123 Main St", "city": "Anytown", "state": "CA", "zip": "90210", "country": "USA" },
            "billingAddress": { "name": "Doe", "firstName": "John", "addressLine": "123 Main St", "city": "Anytown", "state": "CA", "zip": "90210", "country": "USA" },
            "payment": { "cardNumber": "4242424242424243", "name": "John Doe", "expiry": "12/30", "cvv": "123" }
        }
        """;

        mockMvc.perform(post("/api/v2/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(checkoutJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errors", hasItem(containsString("cardNumber:Please enter a valid credit card number."))));
    }

    @Test
    void testJsonCheckoutMissingAddressFields() throws Exception {
        final String checkoutJson = """
        {
            "shippingAddress": { "name": "Doe", "addressLine": "123 Main St", "city": "Anytown", "zip": "90210", "country": "USA" },
            "billingAddress": { "name": "Doe", "firstName": "John", "addressLine": "123 Main St", "city": "Anytown", "state": "CA", "zip": "90210", "country": "USA" },
            "payment": { "cardNumber": "4242424242424242", "name": "John Doe", "expiry": "12/30", "cvv": "123" }
        }
        """;

        mockMvc.perform(post("/api/v2/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(checkoutJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errors", hasItem(containsString("shippingAddress.firstName:Missing field."))))
            .andExpect(jsonPath("$.errors", hasItem(containsString("shippingAddress.state:Missing field."))));
    }

    @Test
    void testJsonCheckoutEmptyCart() throws Exception {
        final MockHttpSession session = new MockHttpSession();
        // Notice we do NOT add any products to the session cart

        final String checkoutJson = """
        {
            "shippingAddress": { "name": "Doe", "firstName": "John", "addressLine": "123 Main St", "city": "Anytown", "state": "CA", "zip": "90210", "country": "USA" },
            "billingAddress": { "name": "Doe", "firstName": "John", "addressLine": "123 Main St", "city": "Anytown", "state": "CA", "zip": "90210", "country": "USA" },
            "payment": { "cardNumber": "4242424242424242", "name": "John Doe", "expiry": "12/30", "cvv": "123" },
            "customer": { "email": "john.doe@example.com", "firstName": "John", "lastName": "Doe" }
        }
        """;

        mockMvc.perform(post("/api/v2/checkout")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(checkoutJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errors", hasItem(containsString("cart:Cannot place an order for an empty cart."))));
    }
}
