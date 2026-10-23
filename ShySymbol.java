/**
 * Símbolo tímido: se dibuja como un triángulo que alterna entre
 * visible e invisible cada vez que la rueda lo selecciona. Empieza
 * visible.

 */
public class ShySymbol extends Symbol {
    public static final String TYPE = "shy";
    private Triangle shape;
    private int shapeX;
    private int shapeY;
    private boolean revealed;

    /**
     * @param color color del símbolo
     */
    public ShySymbol(String color) {
        super(color);
        shape = new Triangle();
        shapeX = 140;
        shapeY = 15;
        revealed = true;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Symbol copy() {
        return new ShySymbol(getColor());
    }

    /**
     * Alterna entre visible e invisible.
     */
    @Override
    public void onSelected() {
        revealed = !revealed;
    }

    /**
     * @return true si el símbolo está actualmente visible
     */
    public boolean isRevealed() {
        return revealed;
    }

    @Override
    public void show(int x, int y, int w, int h) {
        shape.makeInvisible();
        if (!revealed) {
            return;
        }
        int alto = Math.min(h, 2 * w);
        int apiceX = x + w / 2;
        int apiceY = y + (h - alto) / 2;
        shape.changeSize(alto, w);
        shape.moveHorizontal(apiceX - shapeX);
        shape.moveVertical(apiceY - shapeY);
        shapeX = apiceX;
        shapeY = apiceY;
        shape.changeColor(getColor());
        shape.makeVisible();
    }

    @Override
    public void hide() {
        shape.makeInvisible();
    }
}