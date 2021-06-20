package com.nodemules.stox

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.get
import java.nio.charset.Charset

@WithMockUser
@SpringBootTest(
    properties = [
        "integrations.alpha-vantage.host=http://localhost:8999/alpha-vantage",
        "integrations.alpha-vantage.api-key=TEST",
        "feign.hystrix.enabled=true"
    ]
)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8999)
class QuoteControllerIntegrationTests(
    @Autowired val mockMvc: MockMvc
) {

    @Test
    fun `getQuote() - SUCCESS`() {

        val given = "IBM"

        stubFor(
            get(urlPathEqualTo("/alpha-vantage/query"))
                .withQueryParam("function", equalTo("GLOBAL_QUOTE"))
                .withQueryParam("symbol", equalTo(given))
                .withQueryParam("apikey", equalTo("TEST"))
                .willReturn(
                    okJson(JSON_ALPHA_VANTAGE_QUOTE_IBM)
                )
        )

        mockMvc
            .get("/quote/$given")
            .andDo { log() }
            .andExpect {
                content {
                    status { isOk }
                    contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("symbol") { value(given) }
                jsonPath("latestTradingDay") { value("2021-06-18") }
                jsonPath("price") { value(143.1200) }
            }
    }

    @Test
    fun `getQuote() - FAILURE - Alpha Vantage 500 Response`() {

        val given = "IBM"

        stubFor(
            get(urlPathEqualTo("/alpha-vantage/query"))
                .withQueryParam("function", equalTo("GLOBAL_QUOTE"))
                .withQueryParam("symbol", equalTo(given))
                .withQueryParam("apikey", equalTo("TEST"))
                .willReturn(
                    serverError()
                )
        )

        mockMvc
            .get("/quote/$given")
            .andDo { log() }
            .andExpect {
                content {
                    ResultMatcher {
                        status { is5xxServerError }
                    }
                }
            }
    }

    companion object {
        private val JSON_ALPHA_VANTAGE_QUOTE_IBM =
            this::class.java.classLoader.getResource("json/alpha-vantage/quote/ibm.json")?.readText(Charset.defaultCharset())
    }
}