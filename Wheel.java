import java.util.ArrayList;

/**
 * Representa una rueda (carrete) de la máquina tragamonedas. Cada rueda
 * mantiene una lista ordenada de símbolos (colores) y un índice que indica
 * cuál de ellos está actualmente visible. Se apoya en un Rectangle del
 * paquete "shapes" para dibujarse a sí misma sobre el Canvas.
 *
 * @version Ciclo 1 (corregido)
 */
public class Wheel {
    private ArrayList<String> symbols;
    private int currentIndex;
    private int xPosition;
    private int yPosition;
    private int width;
    private int height;
    private Rectangle visibleSlot;
    private boolean isVisible;

    public Wheel(int x, int y, int w, int h) {
        symbols = new ArrayList<>();
        symbols.add("red");
        symbols.add("yellow");
        symbols.add("blue");
        symbols.add("green");
        currentIndex = 0;
        xPosition = x;
        yPosition = y;
        width = w;
        height = h;
        isVisible = false;
        visibleSlot = new Rectangle();
        visibleSlot.changeSize(height, width);
        visibleSlot.moveHorizontal(x - 70);
        visibleSlot.moveVertical(y - 15);
    }

    public void setPosition(int x, int y) {
        int dx = x - xPosition;
        int dy = y - yPosition;
        xPosition = x;
        yPosition = y;
        visibleSlot.moveHorizontal(dx);
        visibleSlot.moveVertical(dy);
    }

    public void addSymbol(int pos, String color) {
        int maxPos = symbols.size() + 1;
        int index = Math.max(1, Math.min(pos, maxPos));
        symbols.add(index - 1, color);
        actualizarVisual();
    }

    public boolean delSymbol(String color) {
        boolean removed = symbols.remove(color);
        if (removed) {
            if (currentIndex >= symbols.size()) {
                currentIndex = symbols.isEmpty() ? 0 : symbols.size() - 1;
            }
            actualizarVisual();
        }
        return removed;
    }

    /**
     * Reemplaza por completo el catálogo de símbolos de esta rueda.
     * Se usa desde SlotMachine para mantener sincronizadas todas las
     * ruedas cuando se agrega una rueda nueva después de haber
     * modificado los símbolos de las ruedas existentes.
     */
    public void setSymbols(ArrayList<String> nuevosSimbolos) {
        symbols = new ArrayList<>(nuevosSimbolos);
        if (currentIndex >= symbols.size()) {
            currentIndex = 0;
        }
        actualizarVisual();
    }

    public ArrayList<String> getSymbols() {
        return new ArrayList<>(symbols);
    }

    /**
     * Fuerza el símbolo visible de la rueda a uno específico, si existe
     * en su catálogo. Devuelve false si el símbolo no está presente.
     */
    public boolean placeSymbol(String symbol) {
        int idx = symbols.indexOf(symbol);
        if (idx < 0) {
            return false;
        }
        currentIndex = idx;
        actualizarVisual();
        return true;
    }

    public void spin(int steps) {
        if (!symbols.isEmpty()) {
            currentIndex = (currentIndex + steps) % symbols.size();
            if (currentIndex < 0) {
                currentIndex += symbols.size();
            }
            actualizarVisual();
        }
    }

    public String getVisibleSymbol() {
        if (symbols.isEmpty()) return "";
        return symbols.get(currentIndex);
    }

    public String[] getSymbolsArray() {
        return symbols.toArray(new String[0]);
    }

    public void makeVisible() {
        isVisible = true;
        if (!symbols.isEmpty()) {
            visibleSlot.changeColor(getVisibleSymbol());
        }
        visibleSlot.makeVisible();
    }

    public void makeInvisible() {
        isVisible = false;
        visibleSlot.makeInvisible();
    }

    private void actualizarVisual() {
        if (isVisible && !symbols.isEmpty()) {
            visibleSlot.changeColor(getVisibleSymbol());
        }
    }
}