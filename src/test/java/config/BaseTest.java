package config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

@SuppressWarnings("resource")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class BaseTest {
    protected static String BASE_URL;
    protected static String CHECKVAT_ENDPOINT = "/check-vat-number";
    protected String GETVAT_ENDPOINT = "/usage";
    protected String TODAY_DATE = String.valueOf(LocalDate.now());

    static {
        System.setProperty("org.testcontainers.docker.client.strategy",
                "org.testcontainers.dockerclient.EnvironmentAndSystemPropertyClientProviderStrategy");
    }

    @Container
    public static GenericContainer<?> viesContainer = new GenericContainer<>("cdq.demo.vies:latest")
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/swagger-ui/index.html")
                    .forStatusCode(200)
                    .forPort(8080));

    @BeforeAll
    void setUp() {
        log.info("Container ID: {}", viesContainer.getContainerId());
        BASE_URL = "http://" + viesContainer.getHost() + ":" + viesContainer.getMappedPort(8080);
        log.info("###Starting viesContainer: {}", BASE_URL);
        log.info("Trying to connect to: {}", BASE_URL);
        log.info("Mapped container port: {}", viesContainer.getMappedPort(8080));
        try {
            String logs = viesContainer.getLogs();
            log.info("Container logs:\n{}", logs);
        } catch (Exception e) {
            log.error("Failed to get logs", e);
        }
        System.setProperty("VIES_API_URL", BASE_URL);
    }
}
