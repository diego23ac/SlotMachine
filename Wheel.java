import java.util.ArrayList;

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
        int index = Math.max(1, Math.min(pos, symbols.size() + 1));
        symbols.add(index - 1, color);
    }

    public boolean delSymbol(String color) {
        return symbols.remove(color);
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
}