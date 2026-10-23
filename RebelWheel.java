/**
 * Rueda rebelde: no se deja bloquear, ni intercambiar, ni eliminar.
 * Se distingue por su marco negro.
 */
public class RebelWheel extends Wheel {
    public static final String TYPE = "rebel";

    public RebelWheel(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    protected String frameColor() {
        return "black";
    }

    @Override
    public boolean canBeLocked() {
        return false;
    }

    @Override
    public boolean canBeSwapped() {
        return false;
    }

    @Override
    public boolean canBeDeleted() {
        return false;
    }
}