package sample.connection.client.localization;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localizer {
    private ResourceBundle current;
    private final ResourceBundle rus;

    public Localizer() {
        rus = ResourceBundle.getBundle("sample.connection.client.localization.Rus",
                new Locale("ru", "RU"));
        current = rus;
    }

    void setRus() {
        current = rus;
    }

    public ResourceBundle get() {
        return current;
    }
}
