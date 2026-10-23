import java.util.*;

/**
 * Representa una rueda de la máquina tragamonedas. 
 * Cada rueda mantiene una lista ordenada de símbolos 
 * y un índice que indica cuál de ellos está actualmente visible
 */
public class Wheel {
    public static final String TYPE = "normal";
    private static final int FRAME_BORDER = 3;
    private static final String[] TIPOS = {LeftyWheel.TYPE, TYPE, RebelWheel.TYPE, ReverseWheel.TYPE};
    private ArrayList<Symbol> symbols;
    private int currentIndex;
    private int xPosition;
    private int yPosition;
    private int width;
    private int height;
    private Rectangle visibleSlot;
    private boolean isVisible;
    private boolean locked;
    private Wheel leftNeighbor;
    private Symbol displayed;
    private Rectangle frame;
    private int frameX;
    private int frameY;
    
    /**
     * Crea una rueda con el catálogo por defecto (rojo, amarillo, azul
     * y verde, todos normales), invisible y sin bloquear.
     *
     * @param x posición horizontal, en píxeles
     * @param y posición vertical, en píxeles
     * @param w ancho de la ventana de la rueda, en píxeles
     * @param h alto de la ventana de la rueda, en píxeles
     */
    public Wheel(int x, int y, int w, int h) {
        symbols = new ArrayList<>();
        symbols.add(new NormalSymbol("red"));
        symbols.add(new NormalSymbol("yellow"));
        symbols.add(new NormalSymbol("blue"));
        symbols.add(new NormalSymbol("green"));
        currentIndex = 0;
        xPosition = x;
        yPosition = y;
        width = w;
        height = h;
        isVisible = false;
        locked = false;
    }
    
    /**
    * Crea una rueda del tipo indicado
    * @param tipo nombre del tipo ("normal", "lefty", "rebel", "reverse")
    * @return la rueda, o null si el tipo no es válido
    */
    public static Wheel create(String tipo, int x, int y, int w, int h) {
        if (!esTipoValido(tipo)) {
            return null;
        }
        switch (tipo.trim().toLowerCase()) {
            case TYPE:
                return new Wheel(x, y, w, h);
            case LeftyWheel.TYPE:
                return new LeftyWheel(x, y, w, h);
            case RebelWheel.TYPE:
                return new RebelWheel(x, y, w, h);
            case ReverseWheel.TYPE:
                return new ReverseWheel(x, y, w, h);
            default:
                return null;
        }
    }
    
    /**
    * @param tipo nombre del tipo (no distingue mayúsculas)
    * @return true si es un tipo de rueda existente
    */
    public static boolean esTipoValido(String tipo) {
        if (tipo == null) {
            return false;
        }
        String normalizado = tipo.trim().toLowerCase();
        for (int i = 0; i < TIPOS.length; i++) {
            if (TIPOS[i].equals(normalizado)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Reposiciona la rueda en coordenadas absolutas (x,y).
     *
     * @param x nueva posición horizontal, en píxeles
     * @param y nueva posición vertical, en píxeles
     */
    public void setPosition(int x, int y) {
        if (x == xPosition && y == yPosition) {
            return;
        }
        xPosition = x;
        yPosition = y;
        redraw(true);
    }
    
    /**
    * Agrega un símbolo (color) en la posición indicada. Devuelve false
    */
    public boolean addSymbol(int pos, Symbol symbol) {
        if (symbol==null){
            return false;
        }
        int maxPos = symbols.size() + 1;
        int index = Math.max(1, Math.min(pos, maxPos));
        symbols.add(index - 1, symbol);
        redraw(false);
        return true;
    }
    
     public static String[] getColoresPermitidos() {
        return Symbol.getColoresPermitidos();
    }

    /**
    * borrar simbolos  
    */
    public boolean delSymbol(String color) {
        int index = indexOfColor(Symbol.normalizarColor(color));
        if (index < 0) {
            return false;
        }
        Symbol removed = symbols.remove(index);
        if (removed == displayed) {
            removed.hide();
            displayed = null;
        }
         if (currentIndex >= symbols.size()) {
            currentIndex = symbols.isEmpty() ? 0 : symbols.size() - 1;
        }
        redraw(false);
        return true;
    }
    
    /**
     * sincroniza las ruedas manteniendo los simbolos 
     * cuando se agrega una nueva rueda
     */
    public void setSymbols(ArrayList<Symbol> nuevos) {
        if (displayed != null) {
            displayed.hide();
            displayed = null;
        }
        symbols = new ArrayList<>();
        for (Symbol s : nuevos) {
            symbols.add(s.copy());
        }
        if (currentIndex >= symbols.size()) {
            currentIndex = 0;
        }
        redraw(false);
    }

    public ArrayList<Symbol> getSymbols() {
        return new ArrayList<>(symbols);
    }

    /**
     * Fuerza el símbolo visible de la rueda a uno específico, si existe
     * en su catálogo. Devuelve false si el símbolo no está presente.
     */
    public boolean placeSymbol(String symbol) {
        int idx = indexOfColor(Symbol.normalizarColor(symbol));
        if (idx < 0) {
            return false;
        }
        currentIndex = idx;
        select();
        return true;
    }

    /**
     * Gira la rueda ciertos steps (negativo = en reversa),
     * mostrando cada paso si la rueda es visible. En cada giro avisa a
     * todos sus símbolos y luego se mueve
     * según su tipo. No hace nada si está
     * bloqueada.
     *
     * @param steps cantidad de pasos
     * @return false si la rueda está bloqueada; true en cualquier otro 
     * caso
     */
    public boolean spin(int steps) {
        if (locked) {
            return false;
        }
        if (symbols.isEmpty()|| steps == 0) {
            return true;
        }
        for (Symbol s : symbols) {
            s.onSpin();
        }
        turn(steps);
        return true;
    }
    
    protected void turn(int steps){
        advance(steps);
    }
    
    /**
     * Avanza ciertas posiciones, una a la vez, seleccionando (y
     * dibujando) cada símbolo por el que pasa.
     */
    protected final void advance(int steps) {
        int delta = steps < 0 ? -1 : 1;
        int pasos = Math.abs(steps);
        for (int i = 0; i < pasos; i++) {
            currentIndex = (currentIndex + delta) % symbols.size();
            if (currentIndex < 0) {
                currentIndex += symbols.size();
            }
            select();
        }
    }

    public String getVisibleSymbol() {
        Symbol actual = currentSymbol();
        return actual == null ? "" : actual.getColor();
    }
    
    /**
    * @return el símbolo seleccionado, o null si no hay
    */
    public Symbol getCurrentSymbol() {
        return currentSymbol();
    }

    /**
    * Bloquea la rueda
    * @return false si este tipo de rueda no se deja bloquear
    */
    public boolean lock() {
         if (!canBeLocked()) {
            return false;
        }
        locked = true;
        return true;
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
        String[] colores = new String[symbols.size()];
        for (int i = 0; i < symbols.size(); i++) {
            colores[i] = symbols.get(i).getColor();
        }
        return colores;
    }
    
    private Symbol currentSymbol() {
        return symbols.isEmpty() ? null : symbols.get(currentIndex);
    }
 
    private int indexOfColor(String color) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getColor().equals(color)) {
                return i;
            }
        }
        return -1;
    }

    public void makeVisible() {
        isVisible = true;
       redraw(true);
    }

    public void makeInvisible() {
        if (frame != null) {
            frame.makeInvisible();
        }
        if (displayed != null) {
            displayed.hide();
            displayed = null;
        }
        isVisible = false;
    }
    
    /**
    * @return el nombre del tipo de rueda
    */
    public String getType() {
        return TYPE;
    }
    
    /**
    * @return los nombres de los tipos de rueda, en orden alfabético
    */
    public static String[] getTipos() {
        String[] copia = new String[TIPOS.length];
        for (int i = 0; i < TIPOS.length; i++) {
            copia[i] = TIPOS[i];
        }
        return copia;
    }
    
    /**
    * @return el color del marco que distingue visualmente este tipo
    * de rueda, o null si no lleva marco (rueda normal)
    */
    protected String frameColor() {
        return null;
    }
    
    /**
     * @return false si este tipo de rueda no se deja bloquear
     */
    public boolean canBeLocked() {
        return true;
    }
 
    /**
     * @return false si este tipo de rueda no se deja intercambiar
     */
    public boolean canBeSwapped() {
        return true;
    }
 
    /**
     * @return false si este tipo de rueda no se deja eliminar
     */
    public boolean canBeDeleted() {
        return true;
    }
 
    /**
     * Informa cuál es la rueda que está a su izquierda (null si es la
     * primera). La mantiene SlotMachine cada vez que cambia el orden.
     *
     * @param left la rueda vecina de la izquierda, o null
     */
    public void setLeftNeighbor(Wheel left) {
        leftNeighbor = left;
    }
 
    /**
     * @return la rueda vecina de la izquierda, o null
     */
    public Wheel getLeftNeighbor() {
        return leftNeighbor;
    }
    
    private void select() {
        symbols.get(currentIndex).onSelected();
        redraw(false);
    }
 
    private void redraw(boolean withFrame) {
        if (!isVisible) {
            return;
        }
        if (withFrame) {
            drawFrame();
        }
        Symbol actual = currentSymbol();
        if (displayed != null && displayed != actual) {
            displayed.hide();
        }
        if (actual != null) {
            actual.show(xPosition, yPosition, width, height);
        }
        displayed = actual;
    }
 
    private void drawFrame() {
        String color = frameColor();
        if (color == null) {
            return;
        }
        if (frame == null) {
            frame = new Rectangle();
            frameX = 70;
            frameY = 15;
        }
        int izquierda = xPosition - FRAME_BORDER;
        int arriba = yPosition - FRAME_BORDER;
        frame.makeInvisible();
        frame.changeSize(height + 2 * FRAME_BORDER, width + 2 * FRAME_BORDER);
        frame.moveHorizontal(izquierda - frameX);
        frame.moveVertical(arriba - frameY);
        frameX = izquierda;
        frameY = arriba;
        frame.changeColor(color);
        frame.makeVisible();
    }
}