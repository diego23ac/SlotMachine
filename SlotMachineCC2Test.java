import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de unidad
 */
public class SlotMachineCC2Test {

    @BeforeEach
    public void setUp()
    {
    }
    
    @Test
    public void accordingToDcSrShouldReachJackpotWhenThreeFreshWheelsMatch() {
        SlotMachine maquina = new SlotMachine();
        maquina.addWheel(1);
        maquina.addWheel(2);
        maquina.addWheel(3);
        assertTrue(maquina.isJackpot());
    }

    @Test
    public void accordingToDcSrShouldRejectUnsupportedColor() {
        SlotMachine maquina = new SlotMachine();
        maquina.addWheel(1);
        maquina.addSymbol(1, "purple");
        assertFalse(maquina.ok());
    }

    @Test
    public void accordingToDcSrShouldKeepLockedWheelUnchangedAfterSpin() {
        SlotMachine maquina = new SlotMachine();
        maquina.addWheel(1);
        maquina.lock(1);
        maquina.spin();
        assertEquals("red", maquina.configuration()[0]);
    }
}