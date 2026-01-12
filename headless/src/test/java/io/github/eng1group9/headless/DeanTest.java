package io.github.eng1group9.headless;

import io.github.eng1group9.entities.Dean;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.math.Vector2;

public class DeanTest extends AbstractHeadlessGdxTest {
    Dean dean;

    @BeforeEach
    void setUp() {
        dean = new Dean(
            new Vector2(0, 0),
            0,
            new Character[] {}
        );
    }

    @Test
    void testDeanExists() {
        assertNotNull(dean);
    }
}
