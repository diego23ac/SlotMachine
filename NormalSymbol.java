/**
 * Símbolo normal: un rectángulo de color que llena la ventana de la
 * rueda
 */
public class NormalSymbol extends Symbol {
    public static final String TYPE = "normal";
    private Rectangle shape;
    private int shapeX;
    private int shapeY;

    /**
     * @param color color del símbolo
     */
    public NormalSymbol(String color) {
        super(color);
        shape = new Rectangle();
        shapeX = 70;
        shapeY = 15;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Symbol copy() {
        return new NormalSymbol(getColor());
    }

    @Override
    public void show(int x, int y, int w, int h) {
        shape.makeInvisible();
        shape.changeSize(h, w);
        shape.moveHorizontal(x - shapeX);
        shape.moveVertical(y - shapeY);
        shapeX = x;
        shapeY = y;
        shape.changeColor(getColor());
        shape.makeVisible();
    }

    @Override
    public void hide() {
        shape.makeInvisible();
    }
}