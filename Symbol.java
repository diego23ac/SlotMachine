/**
 * Símbolo de una rueda: un color más un comportamiento propio según su
 * tipo (normal, efímero, tímido...). Cada rueda tiene sus propias
 * instancias, porque algunos tipos guardan estado (tamaño actual,
 * visible/invisible).
 * <p>
 * Cada tipo sabe dibujarse a sí mismo con una forma distinta
 * (rectángulo, círculo, triángulo), de modo que los tipos se
 * distinguen claramente a simple vista. Para agregar un tipo nuevo se
 * crea una subclase y se agrega a {@link #TIPOS} y al switch de
 * {@link #create(String, String)}.
 *
 * @version Ciclo 4
 */
public abstract class Symbol {

    private static final String[] COLORES_VALIDOS = {
        "red", "black", "blue", "yellow", "green", "magenta", "white"
    };

    private static final String[] TIPOS = {
        EphemeralSymbol.TYPE, NormalSymbol.TYPE, ShySymbol.TYPE
    };

    private final String color;

    /**
     * @param color nombre del color del símbolo (ya validado por quien
     *              lo crea; ver {@link #create(String, String)})
     */
    protected Symbol(String color) {
        this.color = color;
    }

    /**
     * @return el color de este símbolo
     */
    public String getColor() {
        return color;
    }

    /**
     * @return el nombre del tipo de símbolo (por ejemplo "normal")
     */
    public abstract String getType();

    /**
     * @return un símbolo nuevo del mismo tipo y color, con el estado
     *         inicial; se usa para que cada rueda tenga sus propias
     *         instancias
     */
    public abstract Symbol copy();

    /**
     * Dibuja el símbolo dentro de la ventana de la rueda.
     *
     * @param x posición horizontal de la ventana
     * @param y posición vertical de la ventana
     * @param w ancho de la ventana
     * @param h alto de la ventana
     */
    public abstract void show(int x, int y, int w, int h);

    /**
     * Borra el símbolo de pantalla.
     */
    public abstract void hide();

    /**
     * Se invoca en todos los símbolos de una rueda cada vez que la
     * rueda gira (una vez por giro). Por defecto no hace nada.
     */
    public void onSpin() {
    }

    /**
     * Se invoca cuando la rueda selecciona este símbolo (al pasar por
     * él al girar o al colocarlo con placeSymbol). Por defecto no hace
     * nada.
     */
    public void onSelected() {
    }

    /**
     * Crea un símbolo del tipo y color indicados.
     *
     * @param tipo nombre del tipo ("normal", "ephemeral", "shy")
     * @param color color (debe ser uno que Canvas sepa dibujar)
     * @return el símbolo, o null si el tipo o el color no son válidos
     */
    public static Symbol create(String tipo, String color) {
        if (!esTipoValido(tipo) || !esColorValido(color)) {
            return null;
        }
        String colorNormalizado = normalizarColor(color);
        switch (normalizarColor(tipo)) {
            case NormalSymbol.TYPE:
                return new NormalSymbol(colorNormalizado);
            case EphemeralSymbol.TYPE:
                return new EphemeralSymbol(colorNormalizado);
            case ShySymbol.TYPE:
                return new ShySymbol(colorNormalizado);
            default:
                return null;
        }
    }

    /**
     * @param tipo nombre del tipo (no distingue mayúsculas)
     * @return true si es un tipo de símbolo existente
     */
    public static boolean esTipoValido(String tipo) {
        String normalizado = normalizarColor(tipo);
        for (int i = 0; i < TIPOS.length; i++) {
            if (TIPOS[i].equals(normalizado)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return los nombres de los tipos de símbolo, en orden alfabético
     */
    public static String[] getTipos() {
        String[] copia = new String[TIPOS.length];
        for (int i = 0; i < TIPOS.length; i++) {
            copia[i] = TIPOS[i];
        }
        return copia;
    }

    /**
     * @param color texto a normalizar (puede ser null)
     * @return el texto en minúsculas y sin espacios, o null si era null
     */
    public static String normalizarColor(String color) {
        return color == null ? null : color.trim().toLowerCase();
    }

    /**
     * @param color color a validar (cualquier mayúscula/minúscula)
     * @return true si Canvas sabe dibujar ese color
     */
    public static boolean esColorValido(String color) {
        String normalizado = normalizarColor(color);
        if (normalizado == null) {
            return false;
        }
        for (int i = 0; i < COLORES_VALIDOS.length; i++) {
            if (COLORES_VALIDOS[i].equals(normalizado)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return una copia de los colores que Canvas sabe dibujar
     */
    public static String[] getColoresPermitidos() {
        String[] copia = new String[COLORES_VALIDOS.length];
        for (int i = 0; i < COLORES_VALIDOS.length; i++) {
            copia[i] = COLORES_VALIDOS[i];
        }
        return copia;
    }
}