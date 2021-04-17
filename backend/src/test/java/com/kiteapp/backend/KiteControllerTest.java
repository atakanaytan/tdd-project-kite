package com.kiteapp.backend;

import com.kiteapp.backend.error.ApiError;
import com.kiteapp.backend.kite.Kite;
import com.kiteapp.backend.user.UserRepository;
import com.kiteapp.backend.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class KiteControllerTest {

    public static final String API_1_0_KITES = "/api/1.0/kites";

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Before
    public void cleanUp() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void postKite_whenHoaxIsValidAndUserIsAuthorized_receiveOk() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Kite kite = TestUtil.createValidKite();
        ResponseEntity<Object> response = postKite(kite, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postKite_whenHoaxIsValidAndUserIsUnauthorized_receiveUnauthorized() {
        Kite kite = TestUtil.createValidKite();
        ResponseEntity<Object> response = postKite(kite, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postKite_whenHoaxIsValidAndUserIsUnauthorized_receiveApiError() {
        Kite kite = TestUtil.createValidKite();
        ResponseEntity<ApiError> response = postKite(kite, ApiError.class);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private <T> ResponseEntity<T> postKite(Kite kite, Class<T> responseType) {
        return testRestTemplate.postForEntity(API_1_0_KITES, kite, responseType);
    }

    private void authenticate(String username) {
        testRestTemplate.getRestTemplate()
                .getInterceptors().add(new BasicAuthenticationInterceptor(username, "P4ssword"));
    }

}
