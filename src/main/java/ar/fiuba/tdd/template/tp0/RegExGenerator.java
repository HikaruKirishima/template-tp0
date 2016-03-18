package ar.fiuba.tdd.template.tp0;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RegExGenerator {
    int maxLength;
    boolean regExOk;
    int maxSpaceLiterals;

    Interpreter interpreter = new Interpreter( );

    public RegExGenerator(int maxLength) {
        this.maxLength = maxLength;
        this.regExOk = true;
    }

    private int getLengthRandomResult( ) {
        Random randomInt = new Random();
        int lenghtRandom = randomInt.nextInt( this.maxLength);

        if (lenghtRandom < this.maxSpaceLiterals) {
            return this.maxSpaceLiterals;
        }
        return lenghtRandom;
    }

    private boolean validateRegEx( Token currentToken , Token oldToken) throws RegExError {
        if ( oldToken == null) {
            if (!currentToken.isBackslashToken( )) {
                if (!(currentToken.isLiteralToken()) && !(currentToken.isDefinitionToken())) {
                    this.maxSpaceLiterals = 0;
                    throw new RegExError("ERROR");
                }
            }
        }
        return true;
    }

    private ArrayList<Token> readExpression( String regEx ) throws RegExError {
        ArrayList<Token> listTokens = new ArrayList<Token>();

        Token oldToken = null;
        this.maxSpaceLiterals = 0;

        for (char c : regEx.toCharArray( )) {
            Token token = new Token( );
            token.setValue(c);

            this.regExOk = validateRegEx(token,oldToken);

            if (this.regExOk) {
                if ( oldToken != null && oldToken.isBackslashToken( ) ) {
                    token.setValue(c,oldToken.isBackslashToken());
                }
                oldToken = token;
                if (!token.isBackslashToken( )) {
                    listTokens.add(token);
                }
                this.maxSpaceLiterals = token.getObligatoryQuantify( ) + this.maxSpaceLiterals;
            }
        }
        return listTokens;
    }

    private String writeResultRegEx( ArrayList<Token>  listRegExToken ) throws RegExError {
        StringBuilder buildingNewResult = new StringBuilder();

        interpreter.setSpaceOfQuantifiers(getLengthRandomResult() - this.maxSpaceLiterals);

        for (Token c : listRegExToken) {

            if (this.regExOk) {

                interpreter.read(c);

                buildingNewResult.append(interpreter.getBuffer());
                if (!interpreter.analyzeToken()) {
                    this.regExOk = false; //"Error"
                }
            }
        }

        buildingNewResult.append(interpreter.finishReading());

        return buildingNewResult.toString();
    }

    public List<String> generate(String regEx, int numberOfResults) throws RegExError {

        ArrayList<Token>  listRegExToken = readExpression( regEx);

        ArrayList<String> listResult     = new ArrayList<String>();

        for (int countResult = 0; countResult < numberOfResults; countResult++) {
            listResult.add(writeResultRegEx( listRegExToken));
        }

        return listResult;
    }
}