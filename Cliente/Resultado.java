import java.io.Serial;
import java.util.Vector;

public class Resultado extends Comunicado
{
    @Serial
    private static final long serialVersionUID = -1L;

    private final Mao mao;
    private final String descarte;
    private final String vencedor;

    public Resultado(Mao mao, String descarte, String vencedor)
    {
        this.descarte = descarte;
        this.mao = mao;
        this.vencedor = vencedor;
    }

    public Vector<String> getMao()
    {
        return this.mao.getMao();
    }

    public String getDescarte()
    {
        return this.descarte;
    }

    public String getVencedor()
    {
        return this.vencedor;
    }

    public String getCarta(byte pos)
    {
        return this.mao.getCarta(pos);
    }

    public byte getSomaCartas()
    {
        return mao.getSoma();
    }

    public byte getTamanho()
    {
        return mao.getTamanho();
    }

    public String toString ()
    {
        return (""+this.mao.toString()+" "+this.descarte);
    }

}
