package ar.fiuba.tdd.template.tp0;

//import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

//import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * Created by HikaruKirishima on 3/13/2016.
 */

public class Interpreter {

    private static final int MIN_ASCII = 33;
    private static final int MAX_ASCII = 125;
    private static final String NEXT   = "";

    private String buffer;
    private int lenghtFreeOfQuantifiers;
    private boolean isGroup;

    private Token currentToken;
    private Token beforeToken;

    public Interpreter() {
        initialize( );
    }

    private void initialize( ) {
        this.isGroup                 = false;
        this.buffer                  = "";
        this.lenghtFreeOfQuantifiers = 0 ;
        this.currentToken            = null;
        this.beforeToken             = null;
        cleanBuffer( );
    }

    public String finishReading( ) {
        verifyOnlyGroup( this.currentToken);
        String lastResult = this.buffer;
        initialize( );
        return lastResult;
    }

    private void verifyOnlyGroup( Token verifyToken ) {
        if (verifyToken != null) {
            if (verifyToken.getTypeToken().equals("END_OF_GROUP")) {
                this.buffer = readOnlyOneValue( );
            }
        }
    }

    private void cleanBuffer( ) {
        this.buffer = "";
    }

    public void setSpaceOfQuantifiers( int lenghtFreeOfQuantifiers ) {
        this.lenghtFreeOfQuantifiers = lenghtFreeOfQuantifiers ;
    }

    public void read( Token currentPos ) {
        this.beforeToken  = this.currentToken;
        this.currentToken = currentPos;
    }

    public String getBuffer( ) {
        String bufferTemporal = this.buffer;
        if (this.isGroup) {
            return NEXT;
        }
        if (this.currentToken.isDefinitionToken( )) {
            verifyOnlyGroup(this.beforeToken);
            cleanBuffer( );
            return bufferTemporal;
        } else {
            if (this.currentToken.isQuantificationToken( )) {
                return NEXT;
            } else {
                verifyOnlyGroup(this.beforeToken);
                cleanBuffer( );
                return bufferTemporal;
            }
        }
    }

    private boolean errorAnalyze( ) throws RegExError {
        if (this.currentToken.isQuantificationToken() && this.beforeToken.isQuantificationToken()) {
            throw new RegExError("ERROR");
        }
        if (Objects.equals(this.beforeToken.getTypeToken(), "START_GROUP") && this.currentToken.isQuantificationToken()) {
            throw new RegExError("ERROR");
        }
        return false;
    }

    public boolean analyzeToken( ) throws RegExError {
        String result = "";
        boolean isOkToken = true;
        if (this.currentToken.isLiteralToken( )) {
            result = result + Character.toString(this.currentToken.getValue( ));
            this.buffer = this.buffer + result;
        } else {
            if (this.currentToken.isDefinitionToken()) {
                result = readDefinition( );
                this.buffer = this.buffer + result;
            } else {
                if (this.currentToken.isQuantificationToken()) {
                    isOkToken = !(errorAnalyze( ));
                    this.buffer = generateQuantification( );
                }
            }
        }
        return (isOkToken);
    }

    public String readDefinition( ) throws RegExError {
        switch (this.currentToken.getTypeToken()) {
            case "RANDOM":
                return getRandomValue( );
            case "START_GROUP":
                if (this.isGroup) {
                    throw new RegExError("ERROR");
                } else {
                    this.isGroup     = true;
                }
                return NEXT;
            case "END_OF_GROUP":
                if (!this.isGroup  ) {
                    throw new RegExError("ERROR");
                } else {
                    this.isGroup = false;
                }
                return NEXT;
            default:
                throw new RegExError("ERROR");
        }
    }

    private String generateQuantification( ) {

        StringBuilder buildingNewBuffer = new StringBuilder( );

        if (Objects.equals(this.beforeToken.getTypeToken(), "RANDOM")) {
            this.buffer = getRandomString( determinateLenghtQuantifiere( ) );
        }

        for (char value : this.buffer.toCharArray()) {
            buildingNewBuffer.append(copyNtimes(value,determinateLenghtQuantifiere( )));
        }

        return (buildingNewBuffer.toString());
    }

    private int getMinQuantifiere( ) {
        int lenght;
        Random randomInt = new Random();

        if (this.currentToken.getMaxQuantify( ) == 1) {
            if (randomInt.nextBoolean()) {
                return 1;
            } else {
                return 0;
            }
        } else {
            lenght = randomInt.nextInt(this.lenghtFreeOfQuantifiers);

            if (lenght < this.currentToken.getObligatoryQuantify()) {
                return (this.currentToken.getObligatoryQuantify());
            } else {
                if (lenght > this.currentToken.getMaxQuantify()) {
                    return (this.currentToken.getMaxQuantify());
                }
            }
            return lenght;
        }
    }

    private int determinateLenghtQuantifiere() {
        int lenght;

        if (this.lenghtFreeOfQuantifiers == 0) {
            this.lenghtFreeOfQuantifiers = 1;
        }
        lenght = getMinQuantifiere( );
        this.lenghtFreeOfQuantifiers = this.lenghtFreeOfQuantifiers - lenght;

        return lenght;
    }

    private String readOnlyOneValue( ) {
        Random randomInt = new Random();
        int posAscii = randomInt.nextInt(this.buffer.length());
        return  Character.toString(this.buffer.charAt(posAscii));
    }

    private String getRandomValue( ) {
        Random randomInt = new Random();
        int posAscii = randomInt.nextInt(MAX_ASCII);
        if (posAscii < MIN_ASCII) {
            posAscii = MIN_ASCII + posAscii;
        }
        return  Character.toString((char)posAscii);
    }

    private String getRandomString( int lenghtRandom ) {
        if (lenghtRandom == 0) {
            return NEXT;
        }
        StringBuilder buildingNewBuffer = new StringBuilder(lenghtRandom);

        for (int iterator = 0; iterator < lenghtRandom; iterator++) {
            buildingNewBuffer.append(getRandomValue( ));
        }
        return buildingNewBuffer.toString();
    }

    private String copyNtimes( char charToCopy, int numTimes) {
        if ( numTimes == 0 ) {
            return NEXT;
        }
        StringBuilder buildingNewBuffer = new StringBuilder( numTimes);
        for (int iterator = 0; iterator < numTimes; iterator++) {
            buildingNewBuffer.append(charToCopy);
        }
        return buildingNewBuffer.toString();
    }

}
