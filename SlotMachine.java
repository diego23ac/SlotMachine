import java.util.*;
import javax.swing.JOptionPane;

/**
 * Modela la máquina tragamonedas principal con sus ruedas
 * simbolos y palanca.
 */
public class SlotMachine {
    private static final int SCREEN_X = 70;
    private static final int SCREEN_Y = 15;
    private static final int SCREEN_WIDTH = 300;
    private static final int SCREEN_HEIGHT = 200;
    private static final int SIDE_MARGIN = 20;
    private static final int WHEEL_WIDTH = 25;
    private static final int WHEEL_HEIGHT = 120;
    private static final int MIN_N = 1;
    private static final int MAX_N = 50;
    private static final java.util.Random RANDOM = new java.util.Random();
 
    private ArrayList<Wheel> wheels;
    private Rectangle screen;
    private Rectangle handleDown;
    private Rectangle handleUp;
    private Circle handlePoint;
    private boolean isVisible;
    private boolean lastOperationOk;
    private boolean lastJackpotState;
 
    public SlotMachine() {
        wheels = new ArrayList<>();
        isVisible = false;
        lastOperationOk = true;
        lastJackpotState = false;
 
        screen = new Rectangle();
        screen.changeSize(SCREEN_HEIGHT, SCREEN_WIDTH);
        screen.changeColor("magenta");
 
        handleDown = new Rectangle();
        handleDown.changeSize(10, 50);
        handleDown.changeColor("black");
        handleDown.moveHorizontal(300);
        handleDown.moveVertical(100);
 
        handleUp = new Rectangle();
        handleUp.changeSize(50, 10);
        handleUp.changeColor("black");
        handleUp.moveHorizontal(350);
        handleUp.moveVertical(60);
 
        handlePoint = new Circle();
        handlePoint.changeSize(25);
        handlePoint.changeColor("red");
        handlePoint.moveHorizontal(390);
        handlePoint.moveVertical(50);
    }
    
    /**
     * Crea una máquina de n ruedas y n símbolos, inicializada
     * aleatoriamente (cada rueda arranca en una posición al azar) y
     * garantizando que ese estado inicial NUNCA sea ya un jackpot
     * @param n cantidad de ruedas y de símbolos
     */
    public SlotMachine(int n) {
        this();
        int cantidad = Math.max(MIN_N, Math.min(n, MAX_N));
        for (int i = 0; i < cantidad; i++) {
            insertarRueda(Wheel.TYPE, i + 1);
        }
        ArrayList<Symbol> catalogo = new ArrayList<>();
        String[] coloresBase = Symbol.getColoresPermitidos();
        for (int i = 0; i < cantidad; i++) {
            if (i < coloresBase.length) {
                catalogo.add(new NormalSymbol(coloresBase[i]));
            } else {
                catalogo.add(new NormalSymbol("extra" + (i - coloresBase.length + 1)));
            }
        }
        for (Wheel w : wheels) {
            w.setSymbols(catalogo);
        }
 
        for (Wheel w : wheels) {
            w.spin(RANDOM.nextInt(cantidad));
        }
 
        if (cantidad >= 2 && isJackpot()) {
            wheels.get(0).spin(1);
        }
        lastOperationOk = true;
    }
 
    public void addWheel(int pos) {
        addWheel(Wheel.TYPE, pos);
    }
    
    /**
     * Agrega una rueda de un tipo dado ("normal", "lefty", "rebel",
     * "reverse") en la posición indicada.
     * Si el tipo no existe, no se agrega nada y ok() queda en false.
     *
     * @param type nombre del tipo de rueda
     * @param pos posición donde insertar
     */
    public void addWheel(String type, int pos) {
        if (!insertarRueda(type, pos)) {
            lastOperationOk = false;
            manejarError("Tipo de rueda no válido: \"" + type + "\". Tipos permitidos: "
                + String.join(", ", Wheel.getTipos()) + ".");
            return;
        }
        lastOperationOk = true;
    }
    
    /**
     * Crea e inserta la rueda; devuelve false si el tipo no existe.
     */
    private boolean insertarRueda(String type, int pos) {
        Wheel nuevaRueda = Wheel.create(type, 0, 0, WHEEL_WIDTH, WHEEL_HEIGHT);
        if (nuevaRueda == null) {
            return false;
        }
        int maxPos = wheels.size() + 1;
        int targetPos = Math.max(1, Math.min(pos, maxPos));
 
        if (!wheels.isEmpty()) {
            nuevaRueda.setSymbols(wheels.get(0).getSymbols());
        }
        wheels.add(targetPos - 1, nuevaRueda);
        actualizarVecinos();
        actualizarPosicionesRuedas();
 
        if (isVisible) {
            nuevaRueda.makeVisible();
        }
        return true;
    }
    
    /**
     * Le informa a cada rueda cuál es su vecina de la izquierda (la
     * necesitan las ruedas zurdas). Se llama cada vez que cambia el
     * orden de las ruedas.
     */
    private void actualizarVecinos() {
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).setLeftNeighbor(i > 0 ? wheels.get(i - 1) : null);
        }
    }
    
    /**
     * Distribuye todas las ruedas   y estrictamente
     * dentro del ancho del chasis ("screen"). Se recalcula por completo
     * cada vez que se agrega o elimina una rueda.
     */
    private void actualizarPosicionesRuedas() {
        int n = wheels.size();
        if (n == 0) return;
        int[] posicionesX = calcularPosicionesX();
        int yFijo = filaYRuedas();
 
        for (int i = 0; i < n; i++) {
            wheels.get(i).setPosition(posicionesX[i], yFijo);
        }
    }
    
    /**
    * Elimina la rueda en la posición indicada y redistribuye las
    * restantes.
    * @param pos posición de la rueda a eliminar
    */
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            lastOperationOk = false;
            manejarError("No hay ruedas para eliminar.");
            return;
        }
        int index = validarPosicionRueda(pos);
        if (!wheels.get(index - 1).canBeDeleted()) {
            lastOperationOk = false;
            manejarError("La rueda " + index + " es de tipo '" + 
            wheels.get(index - 1).getType()+ "' y no se deja eliminar.");
            return;
        }
        Wheel w = wheels.remove(index - 1);
        w.makeInvisible();
        actualizarVecinos();
        actualizarPosicionesRuedas();
        lastOperationOk = true;
    }
    
    /**
     * Fuerza a que las ruedas y la palanca se vuelvan a dibujar al frente
     * del canvas, para evitar que queden ocultas detrás del chasis
     * (screen) justo después de que este cambie de color.
     */
    private void traerElementosAlFrente() {
        if (isVisible) {
            handleDown.makeVisible();
            handleUp.makeVisible();
            handlePoint.makeVisible();
            for (Wheel w : wheels) {
                w.makeVisible();
            }
        }
    }

    public void addSymbol(int pos, String color) {
        addSymbol(NormalSymbol.TYPE, pos, color);
    }

    public void addSymbol(String type, int pos, String color) {
        if (wheels.isEmpty()) {
            lastOperationOk = false;
            manejarError("No hay ruedas creadas para agregar símbolos.");
            return;
        }
        if (!Symbol.esTipoValido(type)) {
            lastOperationOk = false;
            manejarError("Tipo de símbolo no válido: \"" + type + 
            "\". Tipos permitidos: "+ String.join(", ", Symbol.getTipos()) + ".");
            return;
        }
        if (!Symbol.esColorValido(color)) {
            lastOperationOk = false;
            manejarError("Color no válido: \"" + color + 
            "\". Colores permitidos: "+ String.join(", ", Symbol.getColoresPermitidos()) + ".");
            return;
        }
        for (Wheel w : wheels) {
            w.addSymbol(pos, Symbol.create(type, color));
        }
        lastOperationOk = true;
    }
    
    public void delSymbol(String symbol) {
        boolean eliminado = false;
        for (Wheel w : wheels) {
            if (w.delSymbol(symbol)) {
                eliminado = true;
            }
        }
        lastOperationOk = eliminado;
        if (!eliminado) {
            manejarError("El símbolo especificado no existe en las ruedas.");
        }
    }

    /**
     * Fuerza el símbolo visible de una rueda específica, 
     * si dicho símbolo existe en su catálogo.
     * @param wheel posición de la rueda (1-indexada)
     * @param symbol símbolo que se quiere dejar visible en esa rueda
     */
    public void placeSymbol(int wheel, String symbol) {
        if (wheels.isEmpty()) {
            lastOperationOk = false;
            manejarError("No hay ruedas creadas.");
            return;
        }
        int index = validarPosicionRueda(wheel);
        boolean encontrado = wheels.get(index - 1).placeSymbol(symbol);
        lastOperationOk = encontrado;
        if (!encontrado) {
            manejarError("El símbolo especificado no existe en la rueda indicada.");
        }
    }
    
    /**
     * Intercambia el contenido incluida su posición visual dentro del screen.
     * @param wheel1
     * @param wheel2
     */
    public void swap(int wheel1, int wheel2) {
        if (wheels.size() < 2) {
            lastOperationOk = false;
            manejarError("Se necesitan al menos dos ruedas para intercambiar.");
            return;
        }
        int i1 = validarPosicionRueda(wheel1) - 1;
        int i2 = validarPosicionRueda(wheel2) - 1;
        
        if (!wheels.get(i1).canBeSwapped() || !wheels.get(i2).canBeSwapped()) {
            lastOperationOk = false;
            manejarError("Alguna de las ruedas indicadas no se deja intercambiar.");
            return;
        }
 
        Wheel temporal = wheels.get(i1);
        wheels.set(i1, wheels.get(i2));
        wheels.set(i2, temporal);
        actualizarVecinos();
        if (i1 != i2) {
            int[] posicionesX = calcularPosicionesX();
            int yFijo = filaYRuedas();
            wheels.get(i1).setPosition(posicionesX[i1], yFijo);
            wheels.get(i2).setPosition(posicionesX[i2], yFijo);
        }
        lastOperationOk = true;
    }
    
    /**
     * Fija una rueda
     * @param wheel posición de la rueda a fijar
     */
    public int lock(int wheel) {
        if (wheels.isEmpty()) {
            lastOperationOk = false;
            manejarError("No hay ruedas para fijar.");
            return 0;
        }
        int index = validarPosicionRueda(wheel);
        if (!wheels.get(index - 1).lock()) {
            lastOperationOk = false;
            manejarError("La rueda " + index + " es de tipo '" + wheels.get(index - 1).getType()
                + "' y no se deja bloquear.");
            return 0;
        }
        lastOperationOk = true;
        return index;
    }
 
    /**
     * desbloquea una rueda previamente fijada 
     * @param wheel posición de la rueda a soltar (1-indexada)
     */
    public void unlock(int wheel) {
        if (wheels.isEmpty()) {
            lastOperationOk = false;
            manejarError("No hay ruedas para soltar.");
            return;
        }
        int index = validarPosicionRueda(wheel);
        wheels.get(index - 1).unlock();
        lastOperationOk = true;
    }
 
    private int[] calcularPosicionesX() {
        int n = wheels.size();
        int[] posiciones = new int[n];
        if (n == 0) return posiciones;
 
        int usableWidth = SCREEN_WIDTH - (2 * SIDE_MARGIN);
        int slotWidth = usableWidth / n;
        for (int i = 0; i < n; i++) {
            int slotStart = SCREEN_X + SIDE_MARGIN + (i * slotWidth);
            posiciones[i] = slotStart + (slotWidth - WHEEL_WIDTH) / 2;
        }
        return posiciones;
    }
    
    private int filaYRuedas() {
        return SCREEN_Y + (SCREEN_HEIGHT - WHEEL_HEIGHT) / 2;
    }
    
    /**
    * Gira una rueda específica la cantidad de pasos indicada
    * @param wheel
    * @param steps
    */
    public void spin(int wheel,int steps) {
        if (wheels.isEmpty()) {
            lastOperationOk = false;
            manejarError("La rueda seleccionada no es válida para girar.");
            return;
        }
        int index = validarPosicionRueda(wheel);
        boolean giro = wheels.get(index - 1).spin(steps);
        lastOperationOk = giro;
        if (!giro) {
            manejarError("La rueda indicada está fija (lock); primero debe liberarla con unlock.");
            return;
        }
        actualizarEstadoJackpot();
    }

    /**
     * Gira una rueda específica un paso.
     * @param wheel
     */
    public void spin(int wheel) {
        spin(wheel, 1);
    }
    
    /**
     * Gira todas las ruedas un paso
     */
    public void spin() {
        for (Wheel w : wheels) {
            w.spin(1);
        }
        lastOperationOk = true;
        actualizarEstadoJackpot();
    }
    
    /**
     * coloca en cada rueda el simbolo correspondiente
     * @param setSymbols
     */
    public void spin(String[] setSymbols) {
        if (setSymbols == null || setSymbols.length != wheels.size()) {
            lastOperationOk = false;
            manejarError("La configuración indicada no coincide con el número de ruedas.");
            return;
        }
        boolean todasColocadas = true;
        for (int i = 0; i < wheels.size(); i++) {
            if (!wheels.get(i).placeSymbol(setSymbols[i])) {
                todasColocadas = false;
            }
        }
        lastOperationOk = todasColocadas;
        if (!todasColocadas) {
            manejarError("Alguno de los símbolos indicados no existe en su rueda correspondiente.");
            return;
        }
        actualizarEstadoJackpot();
    }
    
    private void actualizarEstadoJackpot() {
        boolean jackpotActual = isJackpot();
        if (jackpotActual != lastJackpotState) {
            screen.changeColor(jackpotActual ? "yellow" : "magenta");
            lastJackpotState = jackpotActual;
            traerElementosAlFrente();
        }
    }
    
    public String[] symbols() {
        if (wheels.isEmpty()) return new String[0];
        return wheels.get(0).getSymbolsArray();
    }

    public int distinctSymbols() {
        String[] config = configuration();
        HashMap<String, Boolean> distinct = new HashMap<>();
        for (String s : config) {
            distinct.put(s,true);
        }
        return distinct.size();
    }

    public String[] configuration() {
        String[] config = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            config[i] = wheels.get(i).getVisibleSymbol();
        }
        return config;
    }

    public boolean isJackpot() {
        String[] config = configuration();
        if (config.length == 0) return false;
        String primerSimbolo = config[0];
        for (String s : config) {
            if (!s.equals(primerSimbolo)) {
                return false;
            }
        }
        return true;
    }

    public void makeVisible() {
        isVisible = true;
        screen.makeVisible();
        handleDown.makeVisible();
        handleUp.makeVisible();
        handlePoint.makeVisible();
        for (Wheel w : wheels) {
            w.makeVisible();
        }
        lastJackpotState = isJackpot();
        if (lastJackpotState) {
            screen.changeColor("yellow");
            traerElementosAlFrente();
        }
    }

    public void makeInvisible() {
        isVisible = false;
        screen.makeInvisible();
        handleDown.makeInvisible();
        handleUp.makeInvisible();
        handlePoint.makeInvisible();
        for (Wheel w : wheels) {
            w.makeInvisible();
        }
    }

    public void exit() {
        makeInvisible();
        lastOperationOk = true;
    }

    public boolean ok() {
        return lastOperationOk;
    }

    private int validarPosicionRueda(int pos) {
        if (pos < 1){return 1;}
        if (pos > wheels.size()){return wheels.size();}
        return pos;
    }

    private void manejarError(String mensaje) {
        if (isVisible) {
            JOptionPane.showMessageDialog(null, mensaje, "Error en Simulador", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}