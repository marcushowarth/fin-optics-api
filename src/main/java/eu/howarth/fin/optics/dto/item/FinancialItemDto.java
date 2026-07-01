package eu.howarth.fin.optics.dto.item;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AssetDto.class,          name = "asset"),
        @JsonSubTypes.Type(value = InvestmentDto.class,     name = "investment"),
        @JsonSubTypes.Type(value = IncomeDto.class,         name = "income"),
        @JsonSubTypes.Type(value = ExpenditureDto.class,    name = "expenditure"),
        @JsonSubTypes.Type(value = LiabilityDto.class,      name = "liability"),
        @JsonSubTypes.Type(value = FinancialEventDto.class, name = "event")
})
public sealed interface FinancialItemDto
        permits AssetDto, InvestmentDto, IncomeDto, ExpenditureDto, LiabilityDto, FinancialEventDto {}
