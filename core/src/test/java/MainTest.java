import io.github.eng1group9.Main;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTest {


    @Test
    void testCreate() {
        assertTrue(true);
    }

    @Test
    void testCheckForLongboi() {

        Main mazeGame = new Main();
        mazeGame.create();
        Main.checkForLongboi();
    }
}
