package com.analyzer.application.input.symbol;

import com.analyzer.application.input.ApiParameter;

/**
 * @author ilker Kopan
 */
public class ToSymbol implements ApiParameter {
    String toSymbol;

    public ToSymbol(String toSymbol) {
        this.toSymbol = toSymbol;
    }

    @Override
    public String getKey() {
        return "to_symbol";
    }

    @Override
    public String getValue() {
        return toSymbol;
    }

}