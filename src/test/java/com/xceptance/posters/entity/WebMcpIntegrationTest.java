package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.jayway.jsonpath.JsonPath;
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
            .andExpect(content().string(containsString("name: \"get_product_details\"")))
            .andExpect(content().string(containsString("name: \"add_to_cart\"")))
            .andExpect(content().string(containsString("name: \"submit_checkout\"")));
    }
    
    @Test
    void testWebMcpToolsUseInputSchema() throws Exception {
        final String htmlContent = mockMvc.perform(get("/en-US/"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        assertTrue(htmlContent.contains("inputSchema: {"), "WebMCP tool registrations must use inputSchema");
        assertFalse(htmlContent.contains("parameters: {"), "WebMCP tool registrations must NOT use parameters");
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
        
        int matches = 0;
        while (matcher.find()) {
            final String jsonPayload = matcher.group(1).replace("\\\"", "\"");
            try {
                JsonPath.parse(jsonPayload);
                matches++;
            } catch (Exception e) {
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

        // 1. Search Catalog using the EXACT payload from the HTML documentation
        final String searchJson = extractExamplePayload(htmlContent, "search_catalog");
        final String queryValue = JsonPath.read(searchJson, "$.query").toString();
        mockMvc.perform(get("/api/v2/catalog/search")
                .param("q", queryValue)
                .param("locale", "en-US")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        // 2. Fetch Details using EXACT payload (Grizzly Bear) natively ensuring sizes map properly
        final String detailsJson = extractExamplePayload(htmlContent, "get_product_details");
        final String targetProductId = String.valueOf((Object) JsonPath.read(detailsJson, "$.productId"));
        mockMvc.perform(get("/api/v2/catalog/product/" + targetProductId)
                .param("locale", "en-US")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Grizzly Bear"))
            .andExpect(jsonPath("$.availableFinishes.length()").value(1))
            .andExpect(jsonPath("$.distinctSizes.length()").value(2))
            .andExpect(jsonPath("$.variants.length()").value(2));

        // 3. Add to Cart using the EXACT payload from the HTML documentation
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
    void testJsonAddEndpointWithUnitsResolvesCorrectVariant() throws Exception {
        // The AI strictly submits sizes with spatial dimensions attached (e.g. "24x18 in").
        // This validates the underlying CartService perfectly normalizes it, avoiding default fallbacks.
        final MockHttpSession session = new MockHttpSession();
        final String jsonPayload = "{\"productId\": 1, \"quantity\": 1, \"size\": \"24x18 in\", \"finish\": \"matte\"}";
        
        mockMvc.perform(post("/api/v2/cart/add")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        // After registering, read the HTMX mini-cart natively to verify exactly which item was added
        mockMvc.perform(get("/en-US/miniCart").session(session))
            .andExpect(status().isOk())
            // It MUST find the '24x18 in' dimension mapped correctly. 
            // In the DB, the Grizzly Bear 24x18 dimension has a distinct $32.95 pricing footprint compared to the Default $17.00.
            .andExpect(content().string(containsString("$32.95")));
    }

    @Test
    void testJsonAddEndpointWithPaddedFinishResolvesCorrectly() throws Exception {
        // The AI sometimes hallucinates whitespace formatting when mapping JSON attributes (e.g. " matte  ").
        // This validates the underlying CartService safely trims strings guaranteeing precise Db evaluation constraints.
        final MockHttpSession session = new MockHttpSession();
        final String jsonPayload = "{\"productId\": 1, \"quantity\": 1, \"size\": \"24x18\", \"finish\": \" matte  \"}";

        mockMvc.perform(post("/api/v2/cart/add")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/en-US/miniCart").session(session))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("$32.95")));
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

    @Test
    void testJsonProductDetailsEndpointSecurity() throws Exception {
        // Negative Number
        mockMvc.perform(get("/api/v2/catalog/product/-1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // Zero
        mockMvc.perform(get("/api/v2/catalog/product/0")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // Malicious string
        mockMvc.perform(get("/api/v2/catalog/product/NaN")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // Malicious encoded semicolon injection mapping
        mockMvc.perform(get("/api/v2/catalog/product/1%3BSELECT")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testJsonProductDetailsEndpointForNonExistentProduct() throws Exception {
        // ID that does not exist in DB
        mockMvc.perform(get("/api/v2/catalog/product/99999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void testJsonProductDetailsEndpointLocaleCurrency() throws Exception {
        // English (USD) - Validates native imperial units
        mockMvc.perform(get("/api/v2/catalog/product/1")
                .param("locale", "en-US")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.distinctSizes[0].label").value(containsString("in")));

        // German (EUR) - Validates native metric unit transitions dynamically
        mockMvc.perform(get("/api/v2/catalog/product/1")
                .param("locale", "de-DE")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.distinctSizes[0].label").value(containsString("cm")));
    }

    @Test
    void testHtmlProductDetailBackwardsCompatibility() throws Exception {
        // Ensure the HTML template still natively compiles using the extracted `buildProductDetailDto(...)`
        mockMvc.perform(get("/en-US/product/grizzly-bear/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("Grizzly Bear")));
    }
}
