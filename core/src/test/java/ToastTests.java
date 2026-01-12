import com.badlogic.gdx.graphics.Color;
import io.github.eng1group9.systems.ToastSystem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ToastTests {

    @Test
    void testToast() {
        assertEquals(0, ToastSystem.getToasts().size());

        ToastSystem.addToast("First Toast");
        assertEquals(1, ToastSystem.getToasts().size());

        ToastSystem.addToast("Second Toast", new Color(100, 100, 100, 100));
        assertEquals("First Toast", ToastSystem.getToasts().get(0).getText());
        assertEquals(new Color(100, 100, 100, 100), ToastSystem.getToasts().get(0).getColour());

        ToastSystem.getToasts().get(1).setColour(Color.YELLOW);

        ToastSystem.clearExpiredToasts();

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ToastSystem.clearExpiredToasts();


    }


}
