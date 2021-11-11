import java.io.Serial;

public class PedidoDeNome extends Comunicado
{
    @Serial
    private static final long serialVersionUID = -3L;

    private final String nomeUsuario;

    public PedidoDeNome(String nomeUsuario)
    {
        this.nomeUsuario = nomeUsuario;
    }

    public String toString() {return nomeUsuario;}
}
