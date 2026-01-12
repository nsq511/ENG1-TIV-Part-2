import com.badlogic.gdx.graphics.Color;
import io.github.eng1group9.Main;
import io.github.eng1group9.systems.ToastSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ToastTest {
//    ToastSystem toast;
//
//    @BeforeEach
//    void setUp() {
//        toast = new ToastSystem();
//    }

    @Test
    void toastTest() {
        assertTrue(ToastSystem.getToasts().isEmpty());
        ToastSystem.addToast("Test Toast!");
        assertEquals(1, ToastSystem.getToasts().size());
        ToastSystem.addToast("Test Coloured Toast!", new Color(100, 100, 100, 100));
        assertEquals(2, ToastSystem.getToasts().size());
    }

}
