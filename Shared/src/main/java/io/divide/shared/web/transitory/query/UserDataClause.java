package io.divide.shared.web.transitory.query;

import io.divide.shared.web.transitory.TransientObject;

/**
 * Created by williamwebb on 11/13/13.
 */
public class UserDataClause extends Clause {
    protected UserDataClause(String before, OPERAND operand, String after) {
        super(TransientObject.USER_DATA + "." + before, operand, after);
    }

    protected UserDataClause(OPERAND.Conditional conditional,String before, OPERAND operand, String after) {
        super(conditional,TransientObject.USER_DATA + "." + before, operand, after);
    }
}
