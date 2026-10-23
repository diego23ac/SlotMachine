/**
 * Rueda zurda: si hay una rueda a su izquierda, al girar copia su
 * estado (queda mostrando el mismo símbolo que ella) en vez de
 * moverse por su cuenta. Si es la primera, gira como una rueda
 * normal. Se distingue por su marco blanco.

 */
public class LeftyWheel extends Wheel {

    /** Nombre del tipo. */
    public static final String TYPE = "lefty";

    /** @see Wheel#Wheel(int, int, int, int) */
    public LeftyWheel(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    protected String frameColor() {
        return "white";
    }

    @Override
    protected void turn(int steps) {
        Wheel izquierda = getLeftNeighbor();
        if (izquierda != null && placeSymbol(izquierda.getVisibleSymbol())) {
            return;
        }
        super.turn(steps);
    }
}