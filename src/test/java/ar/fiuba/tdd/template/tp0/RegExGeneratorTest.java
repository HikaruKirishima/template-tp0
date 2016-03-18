package ar.fiuba.tdd.template.tp0;

import org.junit.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class RegExGeneratorTest {
    static final int MAX_LENGHT_RESULT = 50;

    private boolean validate(String regEx, int numberOfResults) throws RegExError  {

        RegExGenerator generator = new RegExGenerator(MAX_LENGHT_RESULT);

        List<String> results = generator.generate(regEx, numberOfResults);
        // force matching the beginning and the end of the strings
        Pattern pattern = Pattern.compile("^" + regEx + "$");
        return results
                .stream()
                .reduce(true,
                    (acc, item) -> {
                        Matcher matcher = pattern.matcher(item);
                        return acc && matcher.find();
                    },
                    (item1, item2) -> item1 && item2);
    }


    @Test
    public void testAnyCharacter() throws RegExError {
        assertTrue(validate(".", 1));
    }

    @Test
    public void testMultipleCharacters() throws RegExError {
        assertTrue(validate("...", 1));
    }

    @Test
    public void testLiteral() throws RegExError {
        assertTrue(validate("\\@", 1));
    }

    @Test
    public void testLiteralDotCharacter() throws RegExError {
        assertTrue(validate("\\@..", 1));
    }

    @Test
    public void testZeroOrOneCharacter() throws RegExError {
        assertTrue(validate("\\@.h?", 5));
    }

    @Test
    public void testCharacterSet() throws RegExError {
        assertTrue(validate("[abc]", 3));
    }

    @Test
    public void testCharacterSetWithQuantifiers() throws RegExError {
        assertTrue(validate("[abc]+", 10));
    }

    @Test
    public void testCharacterSetWithQuantifiersZeroToOne() throws RegExError {
        assertTrue(validate("Z[123]?Z[4567890]?Z[ABC]?Z", 10));
    }

    @Test
    public void testCharacterSetWithQuantifiersZeroToN() throws RegExError {
        assertTrue(validate("[#@)]*Z[98]*[10]*", 10));
    }

    @Test
    public void testExampleTP0PDF() throws RegExError {
        assertTrue(validate("..+[ab]*d?c", 3));
    }

    @Test
    public void testCharacterSetWithQuantifiersAndLiterals() throws RegExError {
        assertTrue(validate("a[abc]*f", 2));
    }

    @Test
    public void testLenghtResultAlwaysLessThanMax() throws RegExError {
        validate(".+",400);
    }

    @Test
    public void testLenghtResultAlwaysLessThanMaxWithLastLiteral() throws RegExError {
        validate(".+FIN",400);
    }

    @Test
    public void testLenghtResultAlwaysLessThanMaxWithMoreQuantifiers() throws RegExError {
        validate(".+@_@[456]?_@D*@_.+",400);
    }

    @Test
    public void testAllQuantifiersWithbackslash() throws RegExError {
        validate("a?\\@+\\?*",3);
    }

    @Test
    public void testAllQuantifiersWithoutbackslash() throws RegExError {
        validate("Z?j+9?ABCi*",3);
    }

    @Test(expected = RegExError.class)
    public void testErrorOpenGroup() throws RegExError {
        validate("[[]",1);
    }

    @Test(expected = RegExError.class)
    public void testErrorCloseGroup() throws RegExError {
        validate("[a]b]*",1);
    }

    @Test(expected = RegExError.class)
    public void testErrorOnlyQuantifier() throws RegExError {
        validate("+",1);
    }

}