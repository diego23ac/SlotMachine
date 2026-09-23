import java.util.ArrayList;

/**
 * Representa una rueda de la máquina tragamonedas. 
 * Cada rueda mantiene una lista ordenada de símbolos 
 * y un índice que indica cuál de ellos está actualmente visible
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
    private static final String[] VALID_COLORS = {
        "red", "black", "blue", "yellow", "green", "magenta", "white"
    };
    private boolean locked;
    
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
        locked = false;
        visibleSlot = new Rectangle();
        visibleSlot.changeSize(height, width);
        visibleSlot.moveHorizontal(x - 70);
        visibleSlot.moveVertical(y - 15);
    }
    
    /**
     * Reposiciona la rueda en coordenadas absolutas (x,y).
     *
     * @param x nueva posición horizontal, en píxeles
     * @param y nueva posición vertical, en píxeles
     */
    public void setPosition(int x, int y) {
        int dx = x - xPosition;
        int dy = y - yPosition;
        xPosition = x;
        yPosition = y;
        visibleSlot.moveHorizontal(dx);
        visibleSlot.moveVertical(dy);
    }
    
    /**
    * Agrega un símbolo (color) en la posición indicada. Devuelve false
    */
    public boolean addSymbol(int pos, String color) {
        String colorNormalizado = normalizarColor(color);
        if (!esColorValido(colorNormalizado)) {
            return false;
        }
        int maxPos = symbols.size() + 1;
        int index = Math.max(1, Math.min(pos, maxPos));
        symbols.add(index - 1, colorNormalizado);
        actualizarVisual();
        return true;
    }
    
     public static String[] getColoresPermitidos() {
        String[] copia = new String[VALID_COLORS.length];
        for (int i = 0; i < VALID_COLORS.length; i++) {
            copia[i] = VALID_COLORS[i];
        }
        return copia;
    }

    /**
     * borrar simbolos  
    */
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
    
    private static String normalizarColor(String color) {
        return color == null ? null : color.trim().toLowerCase();
    }
    
    private static boolean esColorValido(String color) {
        if (color == null) return false;
        for (int i = 0; i < VALID_COLORS.length; i++) {
            if (VALID_COLORS[i].equals(color)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * sincroniza las ruedas manteniendo los simbolos 
     * cuando se agrega una nueva rueda
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

    public boolean spin(int steps) {
        if (locked) {
            return false;
        }
        if (symbols.isEmpty()) {
            return true;
        }
        int delta = steps < 0 ? -1 : 1;
        int pasosRestantes = Math.abs(steps);
        for (int i = 0; i < pasosRestantes; i++) {
            currentIndex = (currentIndex + delta) % symbols.size();
            if (currentIndex < 0) {
                currentIndex += symbols.size();
            }
            actualizarVisual();
        }
        return true;
    }

    public String getVisibleSymbol() {
        if (symbols.isEmpty()) return "";
        return symbols.get(currentIndex);
    }

    /**
    * Bloquea la rueda
    */
    public void lock() {
        locked = true;
    }
    
    /**
     * Libera la rueda
     */
    public void unlock() {
        locked = false;
    }

    /**
     * @return true si la rueda está actualmente bloqueada
     */
    public boolean isLocked() {
        return locked;
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