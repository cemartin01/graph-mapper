package io.github.cemartin01.graphmapper.graphql.coercing;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

public class GraphqlCodeCoercing implements Coercing<Code, Code> {

    private Code convert(Object input) {
        if (input instanceof Code) {
            return (Code) input;
        } else if (input instanceof String) {
            return new Code((String)input);
        } else {
            return null;
        }
    }

    @Override
    public Code serialize(Object input) throws CoercingSerializeException {
        Code result = this.convert(input);
        if (result == null) {
            throw new CoercingSerializeException();
        } else {
            return result;
        }
    }

    @Override
    public Code parseValue(Object input) throws CoercingParseValueException {
        Code result = this.convert(input);
        if (result == null) {
            throw new CoercingParseValueException();
        } else {
            return result;
        }
    }

    @Override
    public Code parseLiteral(Object input) throws CoercingParseLiteralException {
        if (!(input instanceof StringValue)) {
            throw new CoercingParseLiteralException();
        } else {
            return new Code(((StringValue)input).getValue());
        }
    }

}
