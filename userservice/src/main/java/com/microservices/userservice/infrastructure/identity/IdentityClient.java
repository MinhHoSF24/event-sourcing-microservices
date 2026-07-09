package com.microservices.userservice.infrastructure.identity;

import com.microservices.userservice.infrastructure.identity.model.TokenExchangeResponse;
import com.microservices.userservice.infrastructure.identity.model.UserCreationParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {

    @PostMapping(value = "/realms/${idp.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeClientToken(@RequestBody MultiValueMap<String, String> form);

    @PostMapping(value = "/admin/realms/${idp.realm}/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(@RequestBody UserCreationParam body,
                    @RequestHeader("authorization") String token);

    @PostMapping(value = "/realms/${idp.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeUserToken(@RequestBody MultiValueMap<String, String> form);
}
