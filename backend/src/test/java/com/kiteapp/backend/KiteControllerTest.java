package com.kiteapp.backend;

import com.kiteapp.backend.error.ApiError;
import com.kiteapp.backend.kite.Kite;
import com.kiteapp.backend.kite.KiteRepository;
import com.kiteapp.backend.user.User;
import com.kiteapp.backend.user.UserRepository;
import com.kiteapp.backend.user.UserService;
import org.junit.After;
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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Autowired
    KiteRepository kiteRepository;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void cleanUp() {
        kiteRepository.deleteAll();
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @After
    public void cleanupAfter() {
        kiteRepository.deleteAll();
    }

    @Test
    public void postKite_whenKiteIsValidAndUserIsAuthorized_receiveOk() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Kite kite = TestUtil.createValidKite();
        ResponseEntity<Object> response = postKite(kite, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postKite_whenKiteIsValidAndUserIsUnauthorized_receiveUnauthorized() {
        Kite kite = TestUtil.createValidKite();
        ResponseEntity<Object> response = postKite(kite, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postKite_whenKiteIsValidAndUserIsUnauthorized_receiveApiError() {
        Kite kite = TestUtil.createValidKite();
        ResponseEntity<ApiError> response = postKite(kite, ApiError.class);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void postKite_whenKiteIsValidAndUserIsAuthorized_kiteSavedToDatabase() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Kite kite = TestUtil.createValidKite();
        postKite(kite, Object.class);
        assertThat(kiteRepository.count()).isEqualTo(1);
    }

    @Test
    public void postKite_whenKiteIsValidAndUserIsAuthorized_kiteSavedToDatabaseWithTimestamp() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Kite kite = TestUtil.createValidKite();
        postKite(kite, Object.class);
        assertThat(kiteRepository.count()).isEqualTo(1);
    }

    @Test
    public void postKite_whenKiteContentNullAndUserIsAuthorized_receiveBadRequest() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Kite kite = new Kite();
        ResponseEntity<Object> response = postKite(kite, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postKite_whenKiteContentLessThan10CharactersAndUserIsAuthorized_receiveBadRequest() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Kite kite = new Kite();
        kite.setContent("123456789");
        ResponseEntity<Object> response = postKite(kite, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postKite_whenKiteContentIs5000CharactersAndUserIsAuthorized_receiveOk() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Kite kite = new Kite();
        String longContent = IntStream.rangeClosed(1, 5000).mapToObj(i -> "x").collect(Collectors.joining());
        kite.setContent(longContent);
        ResponseEntity<Object> response = postKite(kite, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postKite_whenKiteContentMoreThan5000CharactersAndUserIsAuthorized_receiveBadRequest() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Kite kite = new Kite();
        String longContent = IntStream.rangeClosed(1, 5001).mapToObj(i -> "x").collect(Collectors.joining());
        kite.setContent(longContent);
        ResponseEntity<Object> response = postKite(kite, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postKite_whenKiteContentNullAndUserIsAuthorized_receiveApiErrorWithValidationErrors() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Kite kite = new Kite();
        ResponseEntity<ApiError> response = postKite(kite, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("content")).isNotNull();
    }

    @Test
    public void postKite_whenKiteIsValidAndUserIsAuthorized_kiteSavedToDatabaseWithAuthenticatedUserInfo() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Kite kite = TestUtil.createValidKite();
        postKite(kite, Object.class);

        Kite inDB = kiteRepository.findAll().get(0);

        assertThat(inDB.getUser().getUsername()).isEqualTo("user1");
    }

    @Test
    public void postKite_whenKiteIsValidAndUserIsAuthorized_kiteCanBeAccessedFromUserEntity() {
        User user = userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Kite kite = TestUtil.createValidKite();
        postKite(kite, Object.class);

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User inDBUser = entityManager.find(User.class, user.getId());
        assertThat(inDBUser.getKites().size()).isEqualTo(1);
    }

    private <T> ResponseEntity<T> postKite(Kite kite, Class<T> responseType) {
        return testRestTemplate.postForEntity(API_1_0_KITES, kite, responseType);
    }

    private void authenticate(String username) {
        testRestTemplate.getRestTemplate()
                .getInterceptors().add(new BasicAuthenticationInterceptor(username, "P4ssword"));
    }

}
