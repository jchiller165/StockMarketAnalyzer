package com.analyzer.application.input.exchange;

import com.analyzer.application.input.ApiParameter;

/**
 * The currency which is the base of the conversion in ForeignExchange functionality
 */
public class FromCurrency implements ApiParameter {
  String fromCurrency;

  public FromCurrency(String fromCurrency) {
    this.fromCurrency = fromCurrency;
  }

  @Override
  public String getKey() {
    return "from_currency";
  }

  @Override
  public String getValue() {
    return fromCurrency;
  }

}
