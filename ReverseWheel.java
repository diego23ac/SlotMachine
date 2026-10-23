/**
 * Rueda invertida (tipo nuevo propuesto por nosotros): gira en sentido
 * contrario al pedido. spin(wheel, 1) la lleva al símbolo ANTERIOR y
 * un giro negativo la lleva hacia adelante. Se distingue por su marco
 * verde.

 */
public class ReverseWheel extends Wheel {
    public static final String TYPE = "reverse";

    public ReverseWheel(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    protected String frameColor() {
        return "green";
    }

    @Override
    protected void turn(int steps) {
        super.turn(-steps);
    }
}