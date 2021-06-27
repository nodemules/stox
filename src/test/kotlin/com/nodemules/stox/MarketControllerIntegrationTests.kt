package com.nodemules.stox

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.nio.charset.Charset

@WithMockUser
@SpringBootTest(
    properties = [
        "integrations.yahoo.host=http://localhost:8999/yahoo"
    ]
)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8999)
class MarketControllerIntegrationTests(
    @Autowired val mockMvc: MockMvc
) {

    @Test
    fun `getTrending() - SUCCESS`() {

        stubFor(
            get("/yahoo/market/get-trending-tickers")
                .willReturn(
                    okJson(JSON_YAHOO_TRENDING_TICKERS)
                )
        )

        mockMvc.get("/market/trending")
            .andDo { log() }
            .andExpect {
                content {
                    status { isOk }
                    contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath(".symbol") { value(hasItem("BTC-USD")) }
                jsonPath("[*]") { value(hasSize<Any>(20)) }
            }
    }

    @Test
    fun `getTrending() - FAILURE`() {

        stubFor(
            get("/yahoo/market/get-trending-tickers")
                .willReturn(
                    ResponseDefinitionBuilder.responseDefinition()
                        .withStatus(500)
                )
        )

        mockMvc.get("/market/trending")
            .andDo { log() }
            .andExpect {
                content {
                    status { `is`(500) }
                    contentType(MediaType.APPLICATION_JSON)
                }
            }
    }

    @Test
    fun `getSparks() - SUCCESS`() {

        stubFor(
            get(urlPathEqualTo("/yahoo/market/get-spark"))
                .withQueryParam("symbols", equalTo("Z"))
                .willReturn(
                    okJson(JSON_YAHOO_SPARKS)
                )
        )

        mockMvc.get("/market/sparks") {
            param("symbol", "Z")
        }
            .andDo { log() }
            .andExpect {
                content {
                    status { isOk }
                    contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("[*]") { value(hasSize<Any>(1)) }
            }
    }

    @Test
    fun `getSparks() - FAILURE`() {

        stubFor(
            get(urlPathEqualTo("/yahoo/market/get-spark"))
                .withQueryParam("symbols", equalTo("Z"))
                .willReturn(
                    ResponseDefinitionBuilder.responseDefinition()
                        .withStatus(500)
                )
        )

        mockMvc.get("/market/sparks") {
            param("symbol", "Z")
        }
            .andDo { log() }
            .andExpect {
                content {
                    status { `is`(500) }
                    contentType(MediaType.APPLICATION_JSON)
                }
            }
    }

    companion object {

        private val JSON_YAHOO_TRENDING_TICKERS =
            this::class.java.classLoader.getResource("json/yahoo/market/get-trending-tickers.json")?.readText(Charset.defaultCharset())

        private val JSON_YAHOO_SPARKS =
            this::class.java.classLoader.getResource("json/yahoo/market/get-sparks.json")?.readText(Charset.defaultCharset())
    }
}