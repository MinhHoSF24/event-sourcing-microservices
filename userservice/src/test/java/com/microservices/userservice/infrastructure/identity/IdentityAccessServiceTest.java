package com.microservices.userservice.infrastructure.identity;

import com.microservices.userservice.command.command.LoginCommand;
import com.microservices.userservice.infrastructure.identity.model.TokenExchangeResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IdentityAccessServiceTest {

    @Test
    void loginSendsPasswordGrantAsFormBody() {
        IdentityClient identityClient = mock(IdentityClient.class);
        TokenExchangeResponse response = TokenExchangeResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .expiresIn(300L)
                .build();
        when(identityClient.exchangeUserToken(any())).thenReturn(response);

        IdentityAccessService service = new IdentityAccessService(identityClient);
        service.clientId = "library-client";
        service.clientSecret = "secret";

        TokenExchangeResponse token = service.login(new LoginCommand(" minh ", "password"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<MultiValueMap<String, String>> formCaptor =
                ArgumentCaptor.forClass((Class<MultiValueMap<String, String>>) (Class<?>) MultiValueMap.class);
        verify(identityClient).exchangeUserToken(formCaptor.capture());
        MultiValueMap<String, String> form = formCaptor.getValue();

        assertThat(token).isSameAs(response);
        assertThat(form.getFirst("grant_type")).isEqualTo("password");
        assertThat(form.getFirst("client_id")).isEqualTo("library-client");
        assertThat(form.getFirst("client_secret")).isEqualTo("secret");
        assertThat(form.getFirst("scope")).isEqualTo("openid");
        assertThat(form.getFirst("username")).isEqualTo("minh");
        assertThat(form.getFirst("password")).isEqualTo("password");
    }
}
