package com.analyzer.application.output.technicalindicators;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.analyzer.application.input.technicalindicators.Interval;
import com.analyzer.application.output.AlphaVantageException;
import com.analyzer.application.output.technicalindicators.data.IndicatorData;

/**
 * Representation of Kaufman adaptive moving average (KAMA) response from api.
 *
 * @see TechnicalIndicatorResponse
 */
public class KAMA extends TechnicalIndicatorResponse<IndicatorData> {

  private KAMA(final Map<String, String> metaData,
               final List<IndicatorData> indicatorData) {
    super(metaData, indicatorData);
  }

  /**
   * Creates {@code KAMA} instance from json.
   *
   * @param interval specifies how to interpret the date key to the data json object
   * @param json string to parse
   * @return KAMA instance
   */
  public static KAMA from(Interval interval, String json) {
    Parser parser = new Parser(interval);
    return parser.parseJson(json);
  }

  /**
   * Helper class for parsing json to {@code KAMA}.
   *
   * @see TechnicalIndicatorParser
   * @see JsonParser
   */
  private static class Parser extends TechnicalIndicatorParser<KAMA> {

    public Parser(Interval interval) {
      super(interval);
    }

    @Override
    String getIndicatorKey() {
      return "Technical Analysis: KAMA";
    }

    @Override
    KAMA resolve(Map<String, String> metaData,
                Map<String, Map<String, String>> indicatorData) throws AlphaVantageException {
      List<IndicatorData> indicators = new ArrayList<>();
      indicatorData.forEach((key, values) -> indicators.add(new IndicatorData(
              resolveDate(key),
              Double.parseDouble(values.get("KAMA"))
      )));
      return new KAMA(metaData, indicators);
    }
  }
}
