package ar.fiuba.tdd.template.tp0;

import java.util.ArrayList;

public class Token {
    private static int MAXIMA_CARDINALITY  = 10;

    boolean existsBackslash;
    char token;
    int quantify;

    ArrayList<Character> quantificationsId = new ArrayList<>( );
    ArrayList<Character> definitionId      = new ArrayList<>( );
    ArrayList<Character> backslashId       = new ArrayList<>( );

    public Token() {
        this.quantificationsId.add('?'); //0-1
        this.quantificationsId.add('*'); //0-n
        this.quantificationsId.add('+'); //1-n
        this.definitionId.add('.');
        this.definitionId.add( '[');
        this.definitionId.add( ']');
        this.backslashId.add('\\');
    }

    public String getTypeToken( ) {
        switch (this.token) {
            case '[': return "START_GROUP";
            case ']': return "END_OF_GROUP";
            case '.': return "RANDOM";
            default:
                return "NONE";
        }
    }

    public void setValue(char currentCharacter) {
        this.token             = currentCharacter;
        this.existsBackslash   = false;
        this.quantify          = setQuantify( );
    }

    public void setValue(char currentCharacter , boolean existsBackslash) {
        this.token             = currentCharacter;
        this.existsBackslash   = existsBackslash;
        this.quantify          = setQuantify( );
    }

    public char getValue( ) {
        return this.token ;
    }

    public boolean isQuantificationToken( ) {
        if (this.existsBackslash) {
            return false;
        } else {
            if (quantificationsId.contains(this.token)) {
                return true;
            }
            return false;
        }
    }

    public boolean isDefinitionToken( ) {
        if (this.existsBackslash) {
            return false;
        }
        if (definitionId.contains(this.token)) {
            return true;
        }
        return false;
    }

    public boolean isLiteralToken( ) {
        if (this.existsBackslash) {
            return true;
        }
        if (!isQuantificationToken( ) && !isDefinitionToken( ) && !isBackslashToken( )) {
            return true;
        }
        return false;
    }

    public boolean isBackslashToken( ) {
        if ( this.existsBackslash) {
            return false;
        } else {
            if (backslashId.contains(this.token)) {
                return true;
            }
            return false;
        }
    }

    public int getObligatoryQuantify( ) {
        return this.quantify;
    }

    public int getMaxQuantify( ) {
        switch (this.token) {
            case '?':
                return 1;
            case '*': case '+':
                return MAXIMA_CARDINALITY;
            default:
                return 0;
        }
    }

    private int setQuantify( ) {
        switch (this.token) {
            case '?': case '*': case '\\': case'[': case']':
                if (this.existsBackslash) {
                    return 1;
                } else {
                    return 0;
                }
            default:
                return 1;
        }
    }
}