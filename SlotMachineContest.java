/**
 * Resuelve y simula el problema de la maratón (Slot Machine): dado un
 * número n de ruedas (y n símbolos), encuentra la secuencia de giros
 * que deja todas las ruedas mostrando el mismo símbolo (jackpot).
 */
public class SlotMachineContest {

    private SlotMachineContest() {
    }

    /**
     * Crea una máquina de n ruedas/símbolos (queda invisible) y
     * calcula la secuencia de acciones {rueda, pasos} necesarias para
     * alinear todas las ruedas en el mismo símbolo
     * @param n cantidad de ruedas y de símbolos
     * @return la secuencia de acciones, cada una como {rueda, pasos}
     */
    public static int[][] solve(int n) {
        SlotMachine maquina = new SlotMachine(n);
        return resolverAlineacion(maquina, tamanoEfectivo(n));
    }

    /**
     * Igual que solve, pero deja la máquina visible
     * desde antes de resolver, para que el proceso de alineación se
     * vea ocurrir en tiempo real en vez de calcular en silencio y
     * mostrar solo el resultado final.
     *
     * @param n cantidad de ruedas y de símbolos
     */
    public static void simulate(int n) {
        SlotMachine maquina = new SlotMachine(n);
        maquina.makeVisible();
        resolverAlineacion(maquina, tamanoEfectivo(n));
    }

    private static int tamanoEfectivo(int n) {
        return Math.max(1, Math.min(n, 50));
    }

    /**
     * Algoritmo real: alinea las ruedas 2..n contra la rueda 1 (que
     * nunca se toca), una a la vez, usando SOLO spin(wheel,steps) y
     * distinctSymbols() -las únicas dos herramientas de observación
     * permitidas para "resolver" la maratón-.
     */
    private static int[][] resolverAlineacion(SlotMachine maquina, int n) {
        if (n <= 1) {
            return new int[0][2];
        }

        int[][] acciones = new int[n - 1][2];

        for (int k = 2; k <= n; k++) {
            int[] vuelta1 = new int[n];
            for (int paso = 1; paso <= n; paso++) {
                maquina.spin(k, 1);
                vuelta1[paso - 1] = maquina.distinctSymbols();
            }
            maquina.spin(1, 1);
            int[] vuelta2 = new int[n];
            for (int paso = 1; paso <= n; paso++) {
                maquina.spin(k, 1);
                vuelta2[paso - 1] = maquina.distinctSymbols();
            }
            maquina.spin(1, n - 1);
            int pasosNecesarios = 0;
            for (int paso = 1; paso <= n; paso++) {
                if (vuelta1[paso - 1] < vuelta2[paso - 1]) {
                    pasosNecesarios = paso;
                    break;
                }
            }
            maquina.spin(k, pasosNecesarios);
            acciones[k - 2][0] = k;
            acciones[k - 2][1] = pasosNecesarios;
        }
        return acciones;
    }
}