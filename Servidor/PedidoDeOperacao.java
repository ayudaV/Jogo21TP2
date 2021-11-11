import java.io.Serial;

public class PedidoDeOperacao extends Comunicado
{
    @Serial
    private static final long serialVersionUID = -4L;

    private final String operacao;

    public PedidoDeOperacao (String operacao)
    {
        this.operacao = operacao;
    }

    public String getOperacao ()
    {
        return this.operacao;
    }

    public String toString ()
    {
        return (this.operacao);
    }
}
