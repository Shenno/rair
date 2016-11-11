package com.realdolmen.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.realdolmen.comperator.PriceRuleDiscountComperator;
import com.realdolmen.comperator.PriceRuleVolumeComperator;
import com.realdolmen.domain.FlightTravelCategory;
import com.realdolmen.domain.PaymentType;
import com.realdolmen.domain.PricingRule;

public final class PriceCalculatorUtil {

	private static Double preferredPaymentDiscount = 5.0;
	private static PaymentType preferredPaymentType = PaymentType.CREDIT_CARD;

	public static Double getPreferredPaymentDiscount() {
		return preferredPaymentDiscount;
	}

	public static PaymentType getPreferredPaymentType() {
		return preferredPaymentType;
	}

	public static PricingRule pricingRuleToApply(Integer amountOfSeats, List<PricingRule> allRules) {

		if (amountOfSeats != null && amountOfSeats != 0) {
			Collections.sort(allRules, new PriceRuleVolumeComperator());
			Collections.reverse(allRules);
			Collections.sort(allRules, new PriceRuleDiscountComperator());
			for (PricingRule pricingRule : allRules) {
				if (pricingRule.getVolume() < amountOfSeats) {
					return pricingRule;
				}
			}
		}
		return null;
	}

	public static Double getIndividualPrice(FlightTravelCategory ftg, List<PricingRule> allRules, Integer amount,
			Double displayPrice, PaymentType paymentType) {
		if (ftg != null && amount != null) {
			Double modifier = 0.0;
			Double prefDiscount = 0.0;

			PricingRule pRule = pricingRuleToApply(amount, allRules);
			if (pRule != null) {
				modifier = pRule.getDiscountValue();
			}
			Double priceToSubstract = displayPrice * modifier / 100;
			Double midPrice = displayPrice - priceToSubstract;

			if (preferredPaymentType.equals(paymentType)) {
				prefDiscount = midPrice * preferredPaymentDiscount / 100;
			}

			return roundTwoDecimals(midPrice - prefDiscount);
		}
		return 0.0;
	}

	public static Double getCombinedPrice(FlightTravelCategory ftg, List<PricingRule> allRules, Integer amount,
			Double displayPrice, PaymentType paymentType) {
		return getIndividualPrice(ftg, allRules, amount, displayPrice, paymentType) * amount;
	}

	public static Double getDisplayPrice(FlightTravelCategory ftg) {
		Double fPrice = 0.0;
		if (ftg.getOverruledPrice() == 0.0) {
			fPrice = ftg.getSeatPrice() + (ftg.getSeatPrice() * ftg.getCommission() / 100);
		} else {
			fPrice = ftg.getSeatPrice() + ftg.getOverruledPrice();
		}
		return fPrice;
	}

	private PriceCalculatorUtil() {
	}

	private static Double roundTwoDecimals(Double d) {
		BigDecimal bigDecimal = new BigDecimal(d);
        BigDecimal roundedWithScale = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
		return roundedWithScale.doubleValue();
	}

}