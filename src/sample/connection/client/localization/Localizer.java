package sample.connection.client.localization;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localizer {
    private ResourceBundle current;
    private final ResourceBundle rus;
    private final ResourceBundle eng;

    public Localizer() {
        rus = ResourceBundle.getBundle("sample.connection.client.localization.Rus",
                new Locale("ru", "RU"));
        eng = ResourceBundle.getBundle("sample.connection.client.localization.Eng", new Locale("en", "EN"));
        current = rus;
    }

    public void setRus() {
        current = rus;
    }
    public void setEng() {
        current = eng;
    }

    public ResourceBundle get() {
        return current;
    }
}
