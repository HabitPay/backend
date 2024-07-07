package com.habitpay.habitpay;

import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.restdocs.RestDocsController;
import com.habitpay.habitpay.global.config.auth.CorsConfig;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestDocsController.class)
@Import({CorsConfig.class, TokenService.class, TokenProvider.class})
public class RestDocsControllerTest extends AbstractRestDocsTests {

    @MockBean
    TokenService tokenService;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    CorsConfig corsConfig;

    @Test
    void RestDocsTest() throws Exception {
        mockMvc.perform(get("/restDocs")).andExpect(status().isOk())
                .andDo(document("rest-docs"));
    }
}
