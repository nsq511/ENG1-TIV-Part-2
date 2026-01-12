import io.github.eng1group9.Main;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTests {
    Main main;

    @BeforeEach
    void setUp() {
        main = new Main();
    }

    @Test
    void testNoFail() {
        assertTrue(true);
    }
}
