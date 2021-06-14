package com.kirtesh.subwaytransition.rules;

import java.io.Serializable;

public interface ExpressionHandler {

    public Serializable compileStatement(String statement);

    public boolean executeStatement(Serializable compiledStatement, String travelDay, String startTime);
}
