package com.analyzer.application.output.technicalindicators;




import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.analyzer.application.input.technicalindicators.Interval;
import com.analyzer.application.output.technicalindicators.data.IndicatorData;

/**
 * Representation of the minus directional indicator (MINUS_DI) response from api.
 *
 * @see TechnicalIndicatorResponse
 */
public class MINUS_DI extends TechnicalIndicatorResponse<IndicatorData> {

  private MINUS_DI(final Map<String, String> metaData,
                   final List<IndicatorData> indicators) {
    super(metaData, indicators);
  }

  /**
   * Creates {@code MINUS_DI} instance from json.
   *
   * @param interval specifies how to interpret the date key to the data json object
   * @param json string to parse
   * @return MINUS_DI instance
   */
  public static MINUS_DI from(Interval interval, String json) {
    Parser parser = new Parser(interval);
    return parser.parseJson(json);
  }

  /**
   * Helper class for parsing json to {@code MINUS_DI}.
   *
   * @see TechnicalIndicatorParser
   * @see JsonParser
   */
  private static class Parser extends TechnicalIndicatorParser<MINUS_DI> {

    public Parser(Interval interval) {
      super(interval);
    }

    @Override
    String getIndicatorKey() {
      return "Technical Analysis: MINUS_DI";
    }

    @Override
    MINUS_DI resolve(Map<String, String> metaData,
                   Map<String, Map<String, String>> indicatorData) {
      List<IndicatorData> indicators = new ArrayList<>();
      indicatorData.forEach((key, values) -> indicators.add(new IndicatorData(
              resolveDate(key),
              Double.parseDouble(values.get("MINUS_DI"))
      )));
      return new MINUS_DI(metaData, indicators);
    }
  }
}

