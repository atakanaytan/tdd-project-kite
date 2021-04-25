package com.kiteapp.backend;

import com.kiteapp.backend.error.ApiError;
import com.kiteapp.backend.kite.Kite;
import com.kiteapp.backend.kite.KiteRepository;
import com.kiteapp.backend.kite.KiteService;
import com.kiteapp.backend.kite.vm.KiteVM;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.lang.reflect.Type;
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

    @Autowired
    KiteService kiteService;

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

    @Test
    public void getKites_whenThereAreNoKites_receiveOk() {
        ResponseEntity<Object> response = getKites(new ParameterizedTypeReference<Object>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getKites_whenThereAreNoKites_receivePageWithZeroItems() {
        ResponseEntity<TestPage<Object>> response = getKites(new ParameterizedTypeReference<TestPage<Object>>() {});
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    public void getKites_whenThereAreKites_receivePageWithItems() {
        User user = userService.save(TestUtil.createValidUser("user1"));
        kiteService.save(user, TestUtil.createValidKite());
        kiteService.save(user, TestUtil.createValidKite());
        kiteService.save(user, TestUtil.createValidKite());

        ResponseEntity<TestPage<Object>> response = getKites(new ParameterizedTypeReference<TestPage<Object>>() {});
        assertThat(response.getBody().getTotalElements()).isEqualTo(3);
    }

    @Test
    public void getKites_whenThereAreKites_receivePageWithKiteVM() {
        User user = userService.save(TestUtil.createValidUser("user1"));
        kiteService.save(user, TestUtil.createValidKite());

        ResponseEntity<TestPage<KiteVM>> response = getKites(new ParameterizedTypeReference<TestPage<KiteVM>>() {});
        KiteVM storedKite = response.getBody().getContent().get(0);
        assertThat(storedKite.getUser().getUsername()).isEqualTo("user1");
    }

    @Test
    public void postKite_whenKiteIsValidAndUserIsAuthorized_receiveKiteVM() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Kite kite = TestUtil.createValidKite();
        ResponseEntity<KiteVM> response = postKite(kite, KiteVM.class);
        assertThat(response.getBody().getUser().getUsername()).isEqualTo("user1");
    }

    @Test
    public void getKitesOfUser_whenUserExist_receiveOk() {
        userService.save(TestUtil.createValidUser("user1"));
        ResponseEntity<Object> response = getKitesOfUser("user1", new ParameterizedTypeReference<Object>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getKitesOfUser_whenUserDoesNotExist_receiveNotFound() {
        ResponseEntity<Object> response = getKitesOfUser("unknown-user", new ParameterizedTypeReference<Object>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getKitesOfUser_whenUserExist_receivePageWithZeroKites() {
        userService.save(TestUtil.createValidUser("user1"));
        ResponseEntity<TestPage<Object>> response = getKitesOfUser("user1", new ParameterizedTypeReference<TestPage<Object>>() {});
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    public void getKitesOfUser_whenUserExistWithKite_receivePageWithKiteVM() {
        User user = userService.save(TestUtil.createValidUser("user1"));
        kiteService.save(user, TestUtil.createValidKite());

        ResponseEntity<TestPage<KiteVM>> response = getKitesOfUser("user1", new ParameterizedTypeReference<TestPage<KiteVM>>() {});
        KiteVM storedKite = response.getBody().getContent().get(0);
        assertThat(storedKite.getUser().getUsername()).isEqualTo("user1");
    }

    @Test
    public void getKitesOfUser_whenUserExistWithMultipleKites_receivePageWithMathcingKitesCount() {
        User user = userService.save(TestUtil.createValidUser("user1"));
        kiteService.save(user, TestUtil.createValidKite());
        kiteService.save(user, TestUtil.createValidKite());
        kiteService.save(user, TestUtil.createValidKite());

        ResponseEntity<TestPage<KiteVM>> response = getKitesOfUser("user1", new ParameterizedTypeReference<TestPage<KiteVM>>() {});
        assertThat(response.getBody().getTotalElements()).isEqualTo(3);
    }

    @Test
    public void getKitesOfUser_whenMultipleUserExistWithMultipleKites_receivePageWithMatchingKitesCount() {
        User userWithThreeKites = userService.save(TestUtil.createValidUser("user1"));
        IntStream.rangeClosed(1, 3).forEach(i -> {
            kiteService.save(userWithThreeKites, TestUtil.createValidKite());
        });

        User userWithFiveKites = userService.save(TestUtil.createValidUser("user2"));
        IntStream.rangeClosed(1, 5).forEach(i -> {
            kiteService.save(userWithFiveKites, TestUtil.createValidKite());
        });

        ResponseEntity<TestPage<Kite>> response = getKitesOfUser("user2", new ParameterizedTypeReference<TestPage<Kite>>() {});
        assertThat(response.getBody().getTotalElements()).isEqualTo(5);
    }

    private <T> ResponseEntity<T> getKitesOfUser(String username, ParameterizedTypeReference<T> responseType) {
        String path = "/api/1.0/users/" + username + "/kites";
        return testRestTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> getKites(ParameterizedTypeReference<T> responseType) {
        return testRestTemplate.exchange(API_1_0_KITES, HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> postKite(Kite kite, Class<T> responseType) {
        return testRestTemplate.postForEntity(API_1_0_KITES, kite, responseType);
    }

    private void authenticate(String username) {
        testRestTemplate.getRestTemplate()
                .getInterceptors().add(new BasicAuthenticationInterceptor(username, "P4ssword"));
    }

}
