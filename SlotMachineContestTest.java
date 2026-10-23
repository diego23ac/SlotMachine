import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de unidad de Ciclo 3: el constructor SlotMachine(n) y
 * SlotMachineContest (solve/simulate).
 * <p>
 * Como SlotMachine(n) se inicializa al azar, estas pruebas verifican
 * propiedades estructurales e invariantes (tamaños, rangos válidos,
 * que nunca arranque ya en jackpot, que solve() se mantenga
 * invisible) en vez de valores exactos de color.
 */
public class SlotMachineContestTest {

    /**
     * Tears down the test fixture.
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void shouldCreateMachineWithNWheelsAndNSymbols() {
        SlotMachine maquina = new SlotMachine(4);
        assertEquals(4, maquina.configuration().length);
        assertEquals(4, maquina.symbols().length);
    }

    @Test
    public void shouldClampConstructorSizeBelowMinimum() {
        SlotMachine maquina = new SlotMachine(0);
        assertEquals(1, maquina.configuration().length);
    }

    @Test
    public void shouldClampConstructorSizeAboveMaximum() {
        SlotMachine maquina = new SlotMachine(1000);
        assertEquals(50, maquina.configuration().length);
    }

    @Test
    public void shouldUseExtraLabelsBeyondSevenColors() {
        SlotMachine maquina = new SlotMachine(10);
        assertEquals("extra1", maquina.symbols()[7]);
        assertEquals("extra3", maquina.symbols()[9]);
    }

    @Test
    public void shouldNeverStartAlreadyAsJackpotForTwoWheels() {
        for (int intento = 0; intento < 100; intento++) {
            SlotMachine maquina = new SlotMachine(2);
            assertFalse(maquina.isJackpot());
        }
    }

    @Test
    public void shouldReturnNMinusOneActionsFromSolve() {
        assertEquals(1, SlotMachineContest.solve(2).length);
        assertEquals(4, SlotMachineContest.solve(5).length);
        assertEquals(6, SlotMachineContest.solve(7).length);
    }

    @Test
    public void shouldReturnNoActionsForSingleWheel() {
        assertEquals(0, SlotMachineContest.solve(1).length);
    }

    @Test
    public void shouldClampVerySmallOrNegativeNInSolve() {
        assertEquals(0, SlotMachineContest.solve(0).length);
        assertEquals(0, SlotMachineContest.solve(-10).length);
    }

    @Test
    public void shouldClampVeryLargeNInSolve() {
        assertEquals(49, SlotMachineContest.solve(500).length);
    }

    @Test
    public void shouldReturnActionsWithValidWheelIndicesAndSteps() {
        int n = 6;
        int[][] acciones = SlotMachineContest.solve(n);
        for (int[] accion : acciones) {
            assertEquals(2, accion.length);
            assertTrue(accion[0] >= 2 && accion[0] <= n);
            assertTrue(accion[1] >= 0 && accion[1] < n);
        }
    }

    @Test
    public void shouldSolveQuicklyConfirmingItStaysInvisibleDuringSolve() {
        long inicio = System.currentTimeMillis();
        SlotMachineContest.solve(15);
        long duracionMs = System.currentTimeMillis() - inicio;
        assertTrue(duracionMs < 2000);
    }

    @Test
    public void shouldReachJackpotAfterResolvingRandomMachines() {
        for (int n = 2; n <= 10; n++) {
            for (int intento = 0; intento < 20; intento++) {
                SlotMachine maquina = new SlotMachine(n);
                SlotMachineContest.resolverAlineacion(maquina, n);
                assertTrue(maquina.isJackpot());
            }
        }
    }

    @Test
    public void shouldReproduceJackpotReplayingActionsOnIdenticalMachine() {
        for (int n = 2; n <= 10; n++) {
            SlotMachine original = new SlotMachine(n);
            SlotMachine copia = new SlotMachine(n);
            copia.spin(original.configuration());
            int[][] acciones = SlotMachineContest.resolverAlineacion(original, n);
            for (int[] accion : acciones) {
                copia.spin(accion[0], accion[1]);
            }
            assertTrue(copia.isJackpot());
        }
    }

    // ==========================================
    // SlotMachineContest.simulate(n)
    // ==========================================

    @Test
    public void shouldRunSimulateWithoutCrashingForSmallN() {
        SlotMachineContest.simulate(3);
    }
}