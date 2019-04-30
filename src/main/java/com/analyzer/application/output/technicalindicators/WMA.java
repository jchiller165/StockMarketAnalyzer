package com.analyzer.application.output.technicalindicators;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.analyzer.application.input.technicalindicators.Interval;
import com.analyzer.application.output.AlphaVantageException;
import com.analyzer.application.output.technicalindicators.data.IndicatorData;


/**
 * Representation of weighted moving average (WMA) response from api.
 *
 * @see TechnicalIndicatorResponse
 */
public class WMA extends TechnicalIndicatorResponse<IndicatorData> {

  private WMA(final Map<String, String> metaData,
              final List<IndicatorData> indicatorData) {
    super(metaData, indicatorData);
  }

  /**
   * Creates {@code WMA} instance from json.
   *
   * @param interval specifies how to interpret the date key to the data json object
   * @param json string to parse
   * @return WMA instance
   */
  public static WMA from(Interval interval, String json) {
    Parser parser = new Parser(interval);
    return parser.parseJson(json);
  }

  /**
   * Helper class for parsing json to {@code WMA}.
   *
   * @see TechnicalIndicatorParser
   * @see JsonParser
   */
  private static class Parser extends TechnicalIndicatorParser<WMA> {

    public Parser(Interval interval) {
      super(interval);
    }

    @Override
    String getIndicatorKey() {
      return "Technical Analysis: WMA";
    }

    @Override
    WMA resolve(Map<String, String> metaData,
                Map<String, Map<String, String>> indicatorData) throws AlphaVantageException {
      List<IndicatorData> indicators = new ArrayList<>();
      indicatorData.forEach((key, values) -> indicators.add(new IndicatorData(
              resolveDate(key),
              Double.parseDouble(values.get("WMA"))
      )));
      return new WMA(metaData, indicators);
    }
  }
}
