import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The test class SlotMachineContestCTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class SlotMachineContestCTest {

    /**
     * Default constructor for test class SlotMachineContestCTest
     */
    public SlotMachineContestCTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
    }

    @Test
    public void accordingToDcSrShouldSolveMediumSizedMachine() {
        int[][] acciones = SlotMachineContest.solve(5);
        assertEquals(4, acciones.length);
    }
    
    @Test
    public void accordingToDcSrShouldCreateContestMachineWithMatchingWheelsAndSymbols() {
        SlotMachine maquina = new SlotMachine(6);
        assertEquals(6, maquina.configuration().length);
        assertEquals(6, maquina.symbols().length);
    }
    
    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
    }
}
