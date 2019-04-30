package com.analyzer.application.input.symbol;

import com.analyzer.application.input.ApiParameter;

/**
 * @author ilker Kopan
 */
public class FromSymbol implements ApiParameter {
    String fromSymbol;

    public FromSymbol(String fromSymbol) {
        this.fromSymbol = fromSymbol;
    }

    @Override
    public String getKey() {
        return "from_symbol";
    }

    @Override
    public String getValue() {
        return fromSymbol;
    }

}