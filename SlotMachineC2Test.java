import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de unidad de SlotMachine
 */
public class SlotMachineC2Test {

    private SlotMachine maquina;

    /**
     * Sets up the test fixture.
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp() {
        maquina = new SlotMachine();
    }

    private boolean contiene(String[] arreglo, String valor) {
        for (String s : arreglo) {
            if (s.equals(valor)) return true;
        }
        return false;
    }

    @Test
    public void shouldCreateEmptyMachine() {
        assertEquals(0, maquina.configuration().length);
        assertTrue(maquina.ok());
    }

    @Test
    public void shouldAddWheelAndShowDefaultFirstSymbol() {
        maquina.addWheel(1);
        assertTrue(maquina.ok());
        assertEquals(1, maquina.configuration().length);
        assertEquals("red", maquina.configuration()[0]);
    }

    @Test
    public void shouldClampAddWheelPositionBelowMinimum() {
        maquina.addWheel(-5);
        assertTrue(maquina.ok());
        assertEquals(1, maquina.configuration().length);
    }

    @Test
    public void shouldClampAddWheelPositionAboveMaximum() {
        maquina.addWheel(1);
        maquina.addWheel(2);
        maquina.addWheel(99);
        assertTrue(maquina.ok());
        assertEquals(3, maquina.configuration().length);
    }

    @Test
    public void shouldRemoveWheelAndDecreaseConfiguration() {
        maquina.addWheel(1);
        maquina.addWheel(2);
        maquina.delWheel(1);
        assertTrue(maquina.ok());
        assertEquals(1, maquina.configuration().length);
    }

    @Test
    public void shouldNotRemoveWheelFromEmptyMachine() {
        maquina.delWheel(1);
        assertFalse(maquina.ok());
    }

    @Test
    public void shouldAddValidSymbolToAllWheels() {
        maquina.addWheel(1);
        maquina.addWheel(2);
        maquina.addSymbol(5, "white");
        assertTrue(maquina.ok());
        assertTrue(contiene(maquina.symbols(), "white"));
    }

    @Test
    public void shouldNotAddInvalidColorSymbol() {
        maquina.addWheel(1);
        maquina.addSymbol(1, "purple");
        assertFalse(maquina.ok());
        assertFalse(contiene(maquina.symbols(), "purple"));
    }

    @Test
    public void shouldNotAddSymbolWithoutAnyWheel() {
        maquina.addSymbol(1, "white");
        assertFalse(maquina.ok());
    }

    @Test
    public void shouldShiftVisibleSymbolWhenInsertingAtFront() {
        maquina.addWheel(1);
        maquina.addSymbol(1, "white");
        assertEquals("white", maquina.configuration()[0]);
    }

    @Test
    public void shouldRemoveExistingSymbolFromAllWheels() {
        maquina.addWheel(1);
        maquina.addWheel(2);
        maquina.delSymbol("green");
        assertTrue(maquina.ok());
        assertFalse(contiene(maquina.symbols(), "green"));
    }

    @Test
    public void shouldNotRemoveNonexistentSymbol() {
        maquina.addWheel(1);
        maquina.delSymbol("white");
        assertFalse(maquina.ok());
    }

    @Test
    public void shouldPlaceExistingSymbolOnWheel() {
        maquina.addWheel(1);
        maquina.placeSymbol(1, "blue");
        assertTrue(maquina.ok());
        assertEquals("blue", maquina.configuration()[0]);
    }

    @Test
    public void shouldNotPlaceNonexistentSymbolOnWheel() {
        maquina.addWheel(1);
        maquina.placeSymbol(1, "white");
        assertFalse(maquina.ok());
    }

    @Test
    public void shouldSpinSingleWheelOneStepForward() {
        maquina.addWheel(1);
        maquina.spin(1);
        assertTrue(maquina.ok());
        assertEquals("yellow", maquina.configuration()[0]);
    }

    @Test
    public void shouldNotSpinOnEmptyMachine() {
        maquina.spin(1);
        assertFalse(maquina.ok());
    }

    @Test
    public void shouldSpinAllWheelsTogether() {
        maquina.addWheel(1);
        maquina.addWheel(2);
        maquina.spin();
        assertTrue(maquina.ok());
        assertEquals("yellow", maquina.configuration()[0]);
        assertEquals("yellow", maquina.configuration()[1]);
    }

    @Test
    public void shouldDetectJackpotWhenAllSymbolsMatch() {
        maquina.addWheel(1);
        maquina.addWheel(2);
        assertTrue(maquina.isJackpot());
    }

    @Test
    public void shouldNotDetectJackpotWhenSymbolsDiffer() {
        maquina.addWheel(1);
        maquina.addWheel(2);
        maquina.spin(1);
        assertFalse(maquina.isJackpot());
    }

    @Test
    public void shouldCountDistinctSymbolsAcrossWheels() {
        maquina.addWheel(1);
        maquina.addWheel(2);
        maquina.addWheel(3);
        maquina.spin(1);
        maquina.spin(2, 2);
        assertEquals(3, maquina.distinctSymbols());
    }

    @Test
    public void shouldStayOkAfterExitWithoutKillingJvm() {
        maquina.addWheel(1);
        maquina.exit();
        assertTrue(maquina.ok());
        maquina.addWheel(2);
        assertTrue(maquina.ok());
    }

    // ==========================================
    // CICLO 2: swap, lock/unlock, spin extendido
    // ==========================================

    @Test
    public void shouldSwapWheelContents() {
        maquina.addWheel(1);
        maquina.addWheel(2);
        maquina.spin(1);
        maquina.swap(1, 2);
        assertTrue(maquina.ok());
        assertEquals("red", maquina.configuration()[0]);
        assertEquals("yellow", maquina.configuration()[1]);
    }

    @Test
    public void shouldNotSwapWithFewerThanTwoWheels() {
        maquina.addWheel(1);
        maquina.swap(1, 2);
        assertFalse(maquina.ok());
    }

    @Test
    public void shouldLockWheelAndPreventSpin() {
        maquina.addWheel(1);
        maquina.lock(1);
        maquina.spin(1);
        assertFalse(maquina.ok());
        assertEquals("red", maquina.configuration()[0]);
    }

    @Test
    public void shouldUnlockWheelAndAllowSpinAgain() {
        maquina.addWheel(1);
        maquina.lock(1);
        maquina.unlock(1);
        maquina.spin(1);
        assertTrue(maquina.ok());
        assertEquals("yellow", maquina.configuration()[0]);
    }

    @Test
    public void shouldSkipLockedWheelsOnSpinAllWithoutFailing() {
        maquina.addWheel(1);
        maquina.addWheel(2);
        maquina.lock(1);
        maquina.spin();
        assertTrue(maquina.ok());
        assertEquals("red", maquina.configuration()[0]);
        assertEquals("yellow", maquina.configuration()[1]);
    }

    @Test
    public void shouldSpinWheelMultipleStepsAtOnce() {
        maquina.addWheel(1);
        maquina.spin(1, 2);
        assertTrue(maquina.ok());
        assertEquals("blue", maquina.configuration()[0]);
    }

    @Test
    public void shouldSetExactConfigurationWithSpinArray() {
        maquina.addWheel(1);
        maquina.addWheel(2);
        maquina.spin(new String[]{"blue", "green"});
        assertTrue(maquina.ok());
        assertEquals("blue", maquina.configuration()[0]);
        assertEquals("green", maquina.configuration()[1]);
    }

    @Test
    public void shouldNotSetConfigurationWithWrongArrayLength() {
        maquina.addWheel(1);
        maquina.addWheel(2);
        maquina.spin(new String[]{"blue"});
        assertFalse(maquina.ok());
    }

    @Test
    public void shouldBypassLockWhenSettingExactConfiguration() {
        maquina.addWheel(1);
        maquina.lock(1);
        maquina.spin(new String[]{"green"});
        assertTrue(maquina.ok());
        assertEquals("green", maquina.configuration()[0]);
    }

    @Test
    public void shouldNotSetConfigurationWithNonexistentSymbol() {
        maquina.addWheel(1);
        maquina.spin(new String[]{"white"});
        assertFalse(maquina.ok());
    }
    
    /**
    * Tears down the test fixture.
    * Called after every test case method.
    */
    @AfterEach
    public void tearDown() {
    }

}