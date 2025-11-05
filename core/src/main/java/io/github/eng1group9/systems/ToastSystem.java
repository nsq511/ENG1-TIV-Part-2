package io.github.eng1group9.systems;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

public class ToastSystem {
    static class Toast {
        private String text;
        private long createdDate;
        private Color colour = new Color(1, 1, 1, 1);

        public Toast(String text, long createdDate) {
            this.text = text;
            this.createdDate = createdDate;
        }

        public Toast(String text, long createdDate, Color colour) {
            this.text = text;
            this.createdDate = createdDate;
            this.colour = colour;
        }

        public String getText() {
            return text;
        }

        public Color getColour() {
            return colour;
        }

        public void setColour(Color colour) {
            this.colour = colour;
        }

        public long getCreatedDate() {
            return createdDate;
        }
    }

    private static List<Toast> toasts = new LinkedList<>();

    public static void addToast(String text) {
        Toast toast = new Toast(text, System.currentTimeMillis());
        toasts.add(toast);
    }

    public static void addToast(String text, Color colour) {
        Toast toast = new Toast(text, System.currentTimeMillis(), colour);
        toasts.add(toast);
    }

    public static void clearExpiredToasts() {
        Toast toastToRemove = null;

        for (Toast t : toasts) {
            if (System.currentTimeMillis() - t.getCreatedDate() > 5000) {
                toastToRemove = t;
            }
        }

        if (toastToRemove != null) {
            toasts.remove(toastToRemove);
        }
    }

    // Get list of currently displayed toasts
    public static List<Toast> getToasts() {
        return toasts;
    }
    
    public static List<Color> getToastColourList() {
        List<Color> colourList = new LinkedList<>();
        for (Toast t : toasts) {
            colourList.add(t.getColour());
        }
        return colourList;
    }
}
