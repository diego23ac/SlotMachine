import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Modela una rueda individual de la maquina tragamonedas con sus simbolos (colores) 
 * y su representación gráfica basada en el paquete shapes.
 * 
 * @author (Tu nombre)
 * @version (Versión)
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

    /**
     * Constructor de la clase Wheel
     */
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
    }

    /**
     * Adiciona un símbolo a la rueda en una posición específica (ajustando límites).
     */
    public void addSymbol(int pos, String color) {
        if (symbols.isEmpty()) {
            symbols.add(color);
        } else {
            int index = ajustarIndice(pos);
            symbols.add(index - 1, color);
        }
    }

    /**
     * Elimina un símbolo por su color.
     */
    public boolean delSymbol(String color) {
        return symbols.remove(color);
    }

    /**
     * Gira la rueda un número determinado de pasos.
     */
    public void spin(int steps) {
        if (!symbols.isEmpty()) {
            currentIndex = (currentIndex + steps) % symbols.size();
            if (currentIndex < 0) {
                currentIndex += symbols.size();
            }
            actualizarVisual();
        }
    }

    /**
     * Retorna el color del símbolo actualmente visible.
     */
    public String getVisibleSymbol() {
        if (symbols.isEmpty()) return "";
        return symbols.get(currentIndex);
    }

    /**
     * Retorna todos los símbolos de la rueda en un arreglo.
     */
    public String[] getSymbolsArray() {
        return symbols.toArray(new String[0]);
    }

    public int getNumberOfSymbols() {
        return symbols.size();
    }

    /**
     * Ajusta la posición de la rueda en el lienzo.
     */
    public void setPosition(int x, int y) {
        xPosition = x;
        yPosition = y;
    }

    public void makeVisible() {
        isVisible = true;
        visibleSlot.makeVisible();
        actualizarVisual();
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

    private int ajustarIndice(int pos) {
        if (pos < 1) return 1;
        if (pos > symbols.size() + 1) return symbols.size() + 1;
        return pos;
    }
}