import java.io.Serial;

public class PedidoDeDescarte extends Comunicado
{
    @Serial
    private static final long serialVersionUID = -2L;

    private final byte valor;

    public PedidoDeDescarte(byte valor)
    {
        this.valor = valor;
    }

    public byte getValor() {return valor;}

    public String toString ()
    {
        return (""+this.valor);
    }
}
