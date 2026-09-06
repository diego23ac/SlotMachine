import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Modela la máquina tragamonedas principal con sus ruedas, chasis y lógica de interacción.
 * 
 * @author (Tu nombre)
 * @version (Versión)
 */
public class SlotMachine {
    
    private ArrayList<Wheel> wheels;
    private Rectangle screen;
    private Rectangle handleDown;
    private Rectangle handleUp;
    private Circle handlePoint;
    private boolean isVisible;
    private boolean lastOperationOk;

    /**
     * Constructor de SlotMachine según el diagrama de clases oficial.
     */
    public SlotMachine() {
        wheels = new ArrayList<>();
        isVisible = false;
        lastOperationOk = true;
        screen = new Rectangle();
        handleDown = new Rectangle();
        handleUp = new Rectangle();
        handlePoint = new Circle();
        screen.changeSize(200, 300);
        screen.changeColor("magenta");
    }

    public void addWheel(int pos) {
        int xOffset = 50 + (wheels.size() * 50);
        Wheel nuevaRueda = new Wheel(xOffset, 50, 40, 100);
        
        if (wheels.isEmpty()) {
            wheels.add(nuevaRueda);
            lastOperationOk = true;
        } else {
            int index = validarPosicionRueda(pos);
            wheels.add(index - 1, nuevaRueda);
            lastOperationOk = true;
        }
        
        if (isVisible) {
            nuevaRueda.makeVisible();
        }
    }

    public void delWheel(int pos) {
        int index = validarPosicionRueda(pos);
        if (!wheels.isEmpty() && index <= wheels.size()) {
            Wheel w = wheels.remove(index - 1);
            w.makeInvisible();
            lastOperationOk = true;
        } else {
            lastOperationOk = false;
            manejarError("No se pudo eliminar la rueda en la posición especificada.");
        }
    }

    public void addSymbol(int pos, String color) {
        if (!wheels.isEmpty()) {
            wheels.get(0).addSymbol(pos, color);
            lastOperationOk = true;
        } else {
            lastOperationOk = false;
            manejarError("No hay ruedas creadas para agregar símbolos.");
        }
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

    public void placeSymbol(int wheel, String symbol) {
        lastOperationOk = true;
    }

    public void spin(int wheel) {
        int index = validarPosicionRueda(wheel);
        if (!wheels.isEmpty() && index <= wheels.size()) {
            wheels.get(index - 1).spin(1);
            lastOperationOk = true;
            verificarEstadoJackpotVisual();
        } else {
            lastOperationOk = false;
            manejarError("La rueda seleccionada no es válida para girar.");
        }
    }

    public void spin() {
        for (Wheel w : wheels) {
            w.spin(1);
        }
        lastOperationOk = true;
        verificarEstadoJackpotVisual();
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
        if (pos < 1) return 1;
        if (pos > wheels.size()) return wheels.size();
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