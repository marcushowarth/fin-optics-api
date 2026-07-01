package eu.howarth.fin.optics.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class ProjectionResourceTest {

    // Exercises polymorphic deserialization of all five FinancialItemDto subtypes,
    // startingCash seeding, and the full projection path (nominal + real-terms).
    // bank-account was removed as an item type in favour of top-level startingCash.
    private static final String FIVE_ITEMS_WITH_STARTING_CASH = """
            {
              "from": "2026-01",
              "to": "2027-12",
              "base": "2026-01",
              "startingCash": 5000,
              "items": [
                {"type":"asset","name":"House","description":"","start":"2026-01","startValue":300000,"annualGrowthRate":0.03},
                {"type":"investment","name":"ISA","description":"","start":"2026-01","startValue":20000,"annualGrowthRate":0.05},
                {"type":"income","name":"Salary","description":"","start":"2026-01","monthlyAmount":4000,"annualGrowthRate":0.03},
                {"type":"expenditure","name":"Rent","description":"","start":"2026-01","end":"2027-12","monthlyAmount":1500},
                {"type":"liability","name":"Mortgage","description":"","start":"2026-01","balance":250000,"annualInterestRate":0.04,"monthlyRepayment":1200}
              ],
              "scenarios": [
                {"name":"low","annualRate":0.02},
                {"name":"base","annualRate":0.035},
                {"name":"high","annualRate":0.06}
              ]
            }
            """;

    @Test
    void projectsFiveItemTypesWithStartingCash() {
        given()
                .contentType("application/json")
                .body(FIVE_ITEMS_WITH_STARTING_CASH)
        .when()
                .post("/api/projection")
        .then()
                .statusCode(200)
                .body("nominal.netWorth", not(anEmptyMap()))
                .body("nominal.cashPosition.'2026-01'", notNullValue())
                .body("realTerms.base", notNullValue())
                .body("realTerms.netWorth.low", not(anEmptyMap()))
                .body("realTerms.netWorth.base", not(anEmptyMap()))
                .body("realTerms.netWorth.high", not(anEmptyMap()));
    }
}
