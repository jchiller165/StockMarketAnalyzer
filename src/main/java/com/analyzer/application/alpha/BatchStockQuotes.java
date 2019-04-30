package com.analyzer.application.alpha;

import com.analyzer.application.input.Function;
import com.analyzer.application.input.Symbols;
import com.analyzer.application.output.AlphaVantageException;
import com.analyzer.application.output.quote.BatchStockQuotesResponse;

/**
 * The Batch Stock Quotes api provides stock quotes give a list of stock symbols.
 */
public class BatchStockQuotes {

  private final ApiConnector apiConnector;

  /**
   * Constructs a Batch Stock Quotes api endpoint with the help of an {@link ApiConnector}
   *
   * @param apiConnector the connection to the api
   */
  public BatchStockQuotes(ApiConnector apiConnector) {
    this.apiConnector = apiConnector;
  }

  /**
   * This API returns stock quotes updated realtime.
   *
   * @param symbols the stock symbols to lookup
   * @return {@link BatchStockQuotesResponse} stock quote data
   */
  public BatchStockQuotesResponse quote(String... symbols) {
    if (symbols.length > 100) {
      throw new AlphaVantageException("Tried to get stock quotes for " + symbols.length + " stocks. The Batch Stock" +
              " Quotes API will only return quotes for the first 100 symbols.");
    }
    String json = apiConnector.getRequest(new Symbols(symbols), Function.BATCH_STOCK_QUOTES);
    return BatchStockQuotesResponse.from(json);
  }
}
