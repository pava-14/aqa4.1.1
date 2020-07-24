package ru.netology.carddelivery;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataGenerator;
import ru.netology.data.UserInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryFormTest {
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    /*
    data-step value:
            "-12" - year's left arrow
            "-1" - month's left arrow
            "12" - year's right arrow
            "1" - month's right arrow
    */
    private void calendarArrowClick(String dataStep) {
        $$(".calendar__arrow")
                .findBy(Condition.attribute("data-step", dataStep)).click();
    }

    private void arrowClick(int count, String dataStep) {
        if (count <= 0) return;
        for (int i = 0; i < count; i++) calendarArrowClick(dataStep);
    }

    private int getMonthArrowClickCount(LocalDateTime orderDate, LocalDateTime currentDate) {
        return orderDate.getMonth().getValue() - currentDate.getMonth().getValue();
    }

    private int getYearArrowClickCount(LocalDateTime orderDate, LocalDateTime currentDate) {
        return orderDate.getYear() - currentDate.getYear();
    }

    private void selectYearMonth(int yearArrowClickCount, int monthArrowClickCount, int newMonth, int currentMonth) {
        if (yearArrowClickCount == 0 && monthArrowClickCount == 0) return;

        if (yearArrowClickCount < 0 && Math.abs(yearArrowClickCount) == 1) {
            arrowClick(12 - newMonth + currentMonth, "-1");
            return;
        }

        if (yearArrowClickCount < 0 && Math.abs(yearArrowClickCount) > 1)
            arrowClick(Math.abs(yearArrowClickCount), "-12");
        if (yearArrowClickCount > 0) arrowClick(yearArrowClickCount, "12");

        if (monthArrowClickCount < 0) arrowClick(Math.abs(monthArrowClickCount), "-1");
        if (monthArrowClickCount > 0) arrowClick(monthArrowClickCount, "1");
    }

    private void selectCalendarDate(LocalDateTime orderDate, LocalDateTime currentDate) {
        selectYearMonth(getYearArrowClickCount(orderDate, currentDate),
                getMonthArrowClickCount(orderDate, currentDate), orderDate.getMonthValue(), currentDate.getMonthValue());
        SelenideElement calendar = $(".calendar");
        calendar.$(byText(String.valueOf(orderDate.getDayOfMonth()))).click();
    }

    @Test
    public void shouldCreditCardDeliveryReOrder() {
        UserInfo userInfo = DataGenerator.OrderInfo.generateUserInfo("ru");
        open("http://localhost:9999");

        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue(userInfo.getUserCity().substring(0, 2));
        $(byText(userInfo.getUserCity())).click();
        SelenideElement fieldDate = form.$("[data-test-id=date] input");
        fieldDate.click();
        selectCalendarDate(userInfo.getOrderDate(), LocalDateTime.now());

        form.$("[data-test-id=name] input").setValue(userInfo.getUserName());
        form.$("[data-test-id=phone] input").setValue(userInfo.getUserPhone());
        form.$("[data-test-id=agreement]").click();
        SelenideElement buttonPlan = $(withText("Запланировать"));
        buttonPlan.click();

        $(withText("Успешно!")).waitUntil(visible, 15000);
        $(byText("Встреча успешно запланирована на")).shouldBe(visible);
        $(byText(dateFormat.format(userInfo.getOrderDate()))).shouldBe(visible);

        fieldDate.click();
        LocalDateTime reorderDate = DataGenerator.OrderInfo.generateOrderDate("ru");
        selectCalendarDate(reorderDate, userInfo.getOrderDate());
        buttonPlan.click();
        $(withText("Необходимо подтверждение")).waitUntil(visible, 15000);
        $(byText("Перепланировать")).shouldBe(visible);
        $(withText("Перепланировать")).click();
        $(withText("Успешно!")).waitUntil(visible, 15000);
        $(byText("Встреча успешно запланирована на")).shouldBe(visible);
        $(byText(dateFormat.format(reorderDate))).shouldBe(visible);
    }
}
