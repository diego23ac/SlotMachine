import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Modela la máquina tragamonedas principal con sus ruedas, chasis y lógica de interacción.
 * 
 * @author (Tu nombre)
 * @version (Versión)
 */
public class SlotMachine {
    private static final int SCREEN_X = 70;
    private static final int SCREEN_Y = 15;
    private static final int SCREEN_WIDTH = 300;
    private static final int SCREEN_HEIGHT = 200;
    private static final int SIDE_MARGIN = 20;
    private static final int WHEEL_WIDTH = 25;
    private static final int WHEEL_HEIGHT = 120;
 
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
 
    public void addWheel(int pos) {
        int maxPos = wheels.size() + 1;
        int targetPos = Math.max(1, Math.min(pos, maxPos));
 
        boolean huboRuedasPrevias = !wheels.isEmpty();
        Wheel nuevaRueda = new Wheel(0, 0, WHEEL_WIDTH, WHEEL_HEIGHT);
        if (huboRuedasPrevias) {
            nuevaRueda.setSymbols(wheels.get(0).getSymbols());
        }
        wheels.add(targetPos - 1, nuevaRueda);
        actualizarPosicionesRuedas(); 
        lastOperationOk = true;
        if (isVisible) {
            nuevaRueda.makeVisible();
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
 
        int usableWidth = SCREEN_WIDTH - (2 * SIDE_MARGIN);
        int slotWidth = usableWidth / n;
        int yFijo = SCREEN_Y + (SCREEN_HEIGHT - WHEEL_HEIGHT) / 2;
 
        for (int i = 0; i < n; i++) {
            int slotStart = SCREEN_X + SIDE_MARGIN + (i * slotWidth);
            int nuevoX = slotStart + (slotWidth - WHEEL_WIDTH) / 2;
            wheels.get(i).setPosition(nuevoX, yFijo);
        }
    }
    
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            lastOperationOk = false;
            manejarError("No hay ruedas para eliminar.");
            return;
        }
        int index = validarPosicionRueda(pos);
        Wheel w = wheels.remove(index - 1);
        w.makeInvisible();
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
        if (wheels.isEmpty()) {
            lastOperationOk = false;
            manejarError("No hay ruedas creadas para agregar símbolos.");
            return;
        }
        
        for(Wheel w : wheels){
            w.addSymbol(pos, color);
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

    public void spin(int wheel) {
        if (wheels.isEmpty()) {
            lastOperationOk = false;
            manejarError("La rueda seleccionada no es válida para girar.");
            return;
        }
        int index = validarPosicionRueda(wheel);
        wheels.get(index - 1).spin(1);
        lastOperationOk = true;
        actualizarEstadoJackpot();
    }

    public void spin() {
        for (Wheel w : wheels) {
            w.spin(1);
        }
        lastOperationOk = true;
        actualizarEstadoJackpot();
    }
    
    private void actualizarEstadoJackpot() {
        boolean jackpotActual = isjackpot();
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
        java.util.HashSet<String> distinct = new java.util.HashSet<>();
        for (String s : config) {
            distinct.add(s);
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

    public boolean isjackpot() {
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
        lastJackpotState = isjackpot();
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
        System.exit(0);
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

    private void verificarEstadoJackpotVisual() {
        if (isjackpot()) {
            screen.changeColor("yellow");
        } else {
            screen.changeColor("magenta");
        }
    }
}