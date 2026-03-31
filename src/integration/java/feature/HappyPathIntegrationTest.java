package feature;

import com.spring.songify.SongifyApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = SongifyApplication.class)
@Testcontainers
class HappyPathIntegrationTest {


    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @Test
    public void f(){
        // Jeśli test przejdzie na zielono, oznacza to, że kontener wstał!
    }
}