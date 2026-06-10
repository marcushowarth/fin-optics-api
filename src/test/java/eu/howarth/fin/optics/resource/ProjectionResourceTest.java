package eu.howarth.fin.optics.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class ProjectionResourceTest {

    // Exercises polymorphic deserialization of all six FinancialItemDto subtypes
    // and the full projection path (nominal + real-terms scenarios).
    private static final String ALL_SIX_ITEMS = """
            {
              "from": "2026-01",
              "to": "2027-12",
              "base": "2026-01",
              "items": [
                {"type":"bank-account","name":"Monzo","description":"","startBalance":5000},
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
    void projectsAllSixItemTypesWithScenarios() {
        given()
                .contentType("application/json")
                .body(ALL_SIX_ITEMS)
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
