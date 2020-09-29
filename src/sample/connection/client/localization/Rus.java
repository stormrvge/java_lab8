package sample.connection.client.localization;

import java.util.ListResourceBundle;

public class Rus extends ListResourceBundle {
    private static final Object[][] contents =
            {
                    {"AuthorizeWindow", "Авторизация"},
                    {"MainWindow", "Управление коллекцией"},
                    {"AddWindow", "Добавить элемент"},
                    {"Login", "Логин"},
                    {"LoginIn", "Войти"},
                    {"Register", "Регистрация"},
                    {"LoginFail", "Неверный логин или пароль!"},
                    {"Authorization", "Авторизация"},
                    {"EnterLogin", "Введите логин"},
                    {"EnterPass", "Введите пароль"},
                    {"Collection", "Коллекция"},
                    {"Coordinates", "Координаты"},
                    {"Username", "Пользователь:"},
                    {"Logout", "Выход"},
                    {"AddElement", "Добавить элемент"},
                    {"InfoButton", "Информация"},
                    {"HelpButton", "Помощь"},
                    {"ClearCollection", "Очистить таблицу"},
                    {"FilterByDistance", "Фильтр. дистанция"},
                    {"Remove", "Удалить"},
                    {"UniqueDistanceButton", "Уник. дистанция"},
                    {"ShowButton", "Показать"},
                    {"Sort", "Сортировать"},
                    {"ExecuteScript", "Выполнить скрипт"},
                    {"FilteringText", "Фильтрация по"},
                    {"IdColumn", "ID"},
                    {"NameColumn", "Имя"},
                    {"XCoordColumn", "Коорд. х"},
                    {"YCoordColumn", "Коорд. у"},
                    {"XFromColumn", "От х"},
                    {"YFromColumn", "От у"},
                    {"ZFromColumn", "От z"},
                    {"XToColumn", "К х"},
                    {"YToColumn", "К у"},
                    {"ZToColumn", "K z"},
                    {"DistanceColumn", "Дистанция"},
                    {"CreationDateColumn", "Дата созд."},
                    {"OwnerColumn", "Создатель"},
                    {"AddName", "Имя"},
                    {"AddNamePrompt", "Введите имя"},
                    {"AddCoordinates", "Координаты"},
                    {"AddLocation", "Локация"},
                    {"AddFrom", "От"},
                    {"AddTo", "К"},
                    {"AddDistance", "Введите дистанцию"},
                    {"AddIfMax", "Доб. если макс."},
                    {"AddIfMin", "Доб. если мин."},
                    {"AddButton", "Добавить"},
                    {"ClearButton", "Очистить"},
                    {"AddElementSuccess", "Элемент был добавлен"},
                    {"AddElementFail", "Элемент не был добавлен"},
                    {"AddElementMaxFail", "Элемент не является максимальным"},
                    {"AddElementMinFail", "Элемент не является минимальным"},
                    {"RegisterSuccess", "Вы успешно зарегистрировались!"},
                    {"RegisterFail", "Вы не были зарегистрированы"},
                    {"NotSelectedElementForDelete", "Вы не выбрали элемент, который следует удалить."},
                    {"Unique distance", "Уникальная дистанция"},
                    {"Info", "Информация о коллекции"},
                    {"Help", "Помощь"},
                    {"Show", "Показать элементы коллекции"},
                    {"PermissionException", "У Вас нет доступа к изменению полей данного объекта!"},
                    {"NumberFormatException", "Неверно введены данные"}
            };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
