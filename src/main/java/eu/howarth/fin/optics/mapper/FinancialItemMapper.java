package eu.howarth.fin.optics.mapper;

import eu.howarth.fin.optics.dto.item.*;
import eu.howarth.fin.planning.*;

import java.time.YearMonth;
import java.util.Optional;

public class FinancialItemMapper {

    public static FinancialItem toModel(FinancialItemDto dto) {
        return switch (dto) {
            case AssetDto a -> new Asset(
                    a.name(), a.description(),
                    YearMonth.parse(a.start()),
                    a.startValue(), a.annualGrowthRate(),
                    Optional.ofNullable(a.saleDate()).map(YearMonth::parse));

            case InvestmentDto i -> new Investment(
                    i.name(), i.description(),
                    YearMonth.parse(i.start()),
                    i.startValue(), i.annualGrowthRate(),
                    Optional.ofNullable(i.drawdownStart()).map(YearMonth::parse),
                    Optional.ofNullable(i.monthlyDrawdown()));

            case BankAccountDto b -> new BankAccount(
                    b.name(), b.description(), b.startBalance());

            case IncomeDto i -> new Income(
                    i.name(), i.description(),
                    YearMonth.parse(i.start()),
                    Optional.ofNullable(i.end()).map(YearMonth::parse),
                    i.monthlyAmount(), i.annualGrowthRate());

            case ExpenditureDto e -> new Expenditure(
                    e.name(), e.description(),
                    YearMonth.parse(e.start()),
                    Optional.ofNullable(e.end()).map(YearMonth::parse),
                    e.monthlyAmount());

            case LiabilityDto l -> new Liability(
                    l.name(), l.description(),
                    YearMonth.parse(l.start()),
                    l.balance(), l.annualInterestRate(), l.monthlyRepayment());
        };
    }
}
