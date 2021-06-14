package com.kirtesh.subwaytransition.rules.impl;

import com.kirtesh.subwaytransition.rules.ExpressionHandler;
import org.mvel2.MVEL;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Rule engine service.
 */
@Service
@Qualifier("expressionHandler")
public class ExpressionHandlerImpl implements ExpressionHandler {
    /**
     *
     * @param statement -> Java code to be compiled.
     * @return serilizable byte code for execution
     */
    @Override
    public Serializable compileStatement(String statement) {
        return MVEL.compileExpression(statement);
    }

    /**
     *
     * @param compiledStatement - serializable byte code
     * @param travelDay - of format yyyy-MM-dd
     * @param startTime - of format HH:mm
     * @return true / false depending upong execution result of compiled statement.
     */
    @Override
    public boolean executeStatement(Serializable compiledStatement, String travelDay, String startTime) {
        Map<String, String> vars = getHashMap(travelDay.toUpperCase(), startTime);
        return (Boolean) MVEL.executeExpression(compiledStatement, vars);
    }

    private Map<String, String> getHashMap(String travelDay, String startTime) {
        Map<String, String> map = new HashMap<>();
        map.put("travelDay", travelDay);
        map.put("startTime", startTime);
        return map;
    }
}
