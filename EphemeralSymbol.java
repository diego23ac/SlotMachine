/**
 * Símbolo efímero: se dibuja como un círculo que, en cada giro de la
 * rueda, decrementa su tamaño hasta quedar como un punto (no vuelve a
 * crecer).
 */
public class EphemeralSymbol extends Symbol {
    public static final String TYPE = "ephemeral";
    private static final int LEVELS = 5;
    private static final int POINT = 3;
    private Circle shape;
    private int shapeX;
    private int shapeY;
    private int level;

    /**
     * @param color color del símbolo
     */
    public EphemeralSymbol(String color) {
        super(color);
        shape = new Circle();
        shapeX = 20;
        shapeY = 15;
        level = 0;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Symbol copy() {
        return new EphemeralSymbol(getColor());
    }

    /**
     * Reduce un nivel el tamaño del símbolo, hasta llegar a un punto.
     */
    @Override
    public void onSpin() {
        if (level < LEVELS) {
            level++;
        }
    }

    /**
     * @return cuántos giros ha "consumido" el símbolo (0 = tamaño
     *         completo)
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return true si el símbolo ya quedó reducido a un punto
     */
    public boolean isPoint() {
        return level >= LEVELS;
    }

    @Override
    public void show(int x, int y, int w, int h) {
        int maximo = Math.min(w, h);
        int diametro = Math.max(POINT, maximo * (LEVELS - level) / LEVELS);
        int izquierda = x + w / 2 - diametro / 2;
        int arriba = y + h / 2 - diametro / 2;
        shape.makeInvisible();
        shape.changeSize(diametro);
        shape.moveHorizontal(izquierda - shapeX);
        shape.moveVertical(arriba - shapeY);
        shapeX = izquierda;
        shapeY = arriba;
        shape.changeColor(getColor());
        shape.makeVisible();
    }

    @Override
    public void hide() {
        shape.makeInvisible();
    }
}