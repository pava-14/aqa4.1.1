package ru.netology.data;

import com.github.javafaker.Faker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

public class DataGenerator {
    private DataGenerator() {
    }

    public static class OrderInfo {
        private OrderInfo() {
        }

        /*
        Валидация поля на странице реализована с ошибкой
         if (!/^[- А-Яа-я]+$/.test(name.trim())) {
            setNameError('Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.');
            return;
        }
        Буква «ё» в кодировках расположена отдельно и её необходbимо
        указывать явно: [- А-Яа-яёЁ]
         */
        private static String getFullName(String Locale) {
            Faker faker = new Faker(new Locale(Locale));
            String fullName = faker.name().fullName();
            if (fullName.contains("ё")) {
                fullName = fullName.replace("ё", "е");
            }
            if (fullName.contains("Ё")) {
                fullName = fullName.replace("Ё", "Е");
            }
            return fullName;
        }

        private static String getCity(String Locale) {
            Faker faker = new Faker(new Locale(Locale));
            String[] cityList = new String[]{
                    "Абакан", "Анадырь", "Архангельск", "Астрахань", "Барнаул", "Белгород",
                    "Биробиджан", "Благовещенск", "Брянск", "Великий Новгород", "Владивосток",
                    "Владикавказ", "Владимир", "Волгоград", "Вологда", "Воронеж", "Горно-Алтайск",
                    "Грозный", "Екатеринбург", "Иваново", "Ижевск", "Иркутск", "Йошкар-Ола",
                    "Казань", "Калининград", "Калуга", "Кемерово", "Киров", "Кострома", "Краснодар",
                    "Красноярск", "Курган", "Курск", "Кызыл", "Липецк", "Магадан", "Магас", "Майкоп",
                    "Махачкала", "Москва", "Мурманск", "Нальчик", "Нарьян-Мар", "Нижний Новгород",
                    "Новосибирск", "Омск", "Орёл", "Оренбург", "Пенза", "Пермь", "Петрозаводск",
                    "Петропавловск-Камчатский", "Псков", "Ростов-на-Дону", "Рязань", "Салехард",
                    "Самара", "Санкт-Петербург", "Саранск", "Саратов", "Севастополь", "Симферополь",
                    "Смоленск", "Ставрополь", "Сыктывкар", "Тамбов", "Тверь", "Томск", "Тула", "Тюмень",
                    "Улан-Удэ", "Ульяновск", "Уфа", "Хабаровск", "Ханты-Мансийск", "Чебоксары",
                    "Челябинск", "Черкесск", "Чита", "Элиста", "Южно-Сахалинск", "Якутск", "Ярославль"};
            return cityList[faker.random().nextInt(0, cityList.length - 1)];
        }

        private static String getPhone(String Locale) {
            Faker faker = new Faker(new Locale(Locale));
            String phone = faker.phoneNumber().phoneNumber().replace("(", "");
            return phone.replace(")", "");
        }

        public static UserInfo generateUserInfo(String Locale) {
            Faker faker = new Faker(new Locale(Locale));
            return new UserInfo(
                    getFullName(Locale),
                    getPhone(Locale),
                    getCity(Locale),
                    generateOrderDate(Locale)
            );
        }

        public static LocalDateTime generateOrderDate(String Locale) {
            Faker faker = new Faker(new Locale(Locale));
            return LocalDateTime.now().plusDays((long) faker.random().nextInt(3, 365));
        }
    }
}
