package localization;


import java.util.ResourceBundle;

public interface Localizable { // интерфейс реализуют все компоненты, которые должны обновляться при смене языка
    void updateLanguage(ResourceBundle bundle);
}