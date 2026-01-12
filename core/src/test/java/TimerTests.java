import com.badlogic.gdx.graphics.Color;
import io.github.eng1group9.systems.TimerSystem;
import io.github.eng1group9.systems.ToastSystem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimerTests {
    TimerSystem timerSystem;

    @BeforeEach
    void setUp() {
        timerSystem = new TimerSystem(999);
    }

    @Test
    void testNoFail() {
        assertTrue(true);
    }
}
