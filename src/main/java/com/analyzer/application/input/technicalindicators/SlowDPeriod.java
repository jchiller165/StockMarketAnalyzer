package com.analyzer.application.input.technicalindicators;

import com.analyzer.application.input.ApiParameter;

public class SlowDPeriod implements ApiParameter {
  private final String period;

  private SlowDPeriod(String period) {
    this.period = period;
  }

  public static SlowDPeriod of(int time) {
    assert time > 0;
    return new SlowDPeriod(String.format("%d", time));
  }

  @Override
  public String getKey() {
    return "slowdperiod";
  }

  @Override
  public String getValue() {
    return period;
  }
}
