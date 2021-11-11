import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisoraDeConexao extends Thread
{
    private Mao                       mao;
    private Vector<Mao>               maos;
    private final Vector<String>      nomes;
    private Parceiro                  usuario;
    private final Socket              conexao;
    private final Vector<Parceiro>    usuarios;
    PedidoDeLiberacao pLib = new PedidoDeLiberacao();
    PedidoDeJogo pJogo = new PedidoDeJogo();
    private int usuarioAtual = -1;
    boolean fimDePartida=false;

    public SupervisoraDeConexao(Socket conexao, Vector<Parceiro> usuarios) throws Exception
    {
        if (conexao==null)
            throw new Exception ("Conexao ausente");

        if (usuarios==null)
            throw new Exception ("Usuarios ausentes");

        this.conexao  = conexao;
        this.usuarios = usuarios;
        this.maos = new Vector<>();
        this.nomes = new Vector<>();
    }

    public void run ()
    {
        ObjectOutputStream transmissor;
        try
        {
            transmissor =
            new ObjectOutputStream(
            this.conexao.getOutputStream());
        }
        catch (Exception erro)
        {
            return;
        }
        
        ObjectInputStream receptor;
        try
        {
            receptor=
            new ObjectInputStream(
            this.conexao.getInputStream());
        }
        catch (Exception erro)
        {
            try
            {
                transmissor.close();
            }
            catch (Exception ignored)
            {} // so tentando fechar antes de acabar a thread
            
            return;
        }

        try
        {
            this.usuario =
            new Parceiro (this.conexao,
                          receptor,
                          transmissor);
        }
        catch (Exception ignored)
        {} // sei que passei os parametros corretos

        try
        {
            if(this.usuarios.size() < 3)
            {
                this.usuarios.add (this.usuario);
                System.out.println("Usuário conectou-se! " + usuarios.size() + "/3");
            }

            if(this.usuarios.size() == 3)
            {
                System.out.println("Sala completa!");

                //loop de solicitacao de nomes
                do {
                    for (byte i = 0; i < usuarios.size(); i++) {
                        Comunicado comunicado = this.usuarios.get(i).envie();

                        if (comunicado instanceof PedidoDeNome) {
                            PedidoDeNome pedidoDeNome = (PedidoDeNome) comunicado;
                            System.out.println("Nome do jogador: " + pedidoDeNome);
                            nomes.add(i, pedidoDeNome.toString());
                        }
                    }
                } while (nomes.size() < 3);

                this.jogo();
            }
        }
        catch (Exception erro)
        {
            try
            {
                transmissor.close ();
                receptor   .close ();
            }
            catch (Exception ignored)
            {} // so tentando fechar antes de acabar a thread

        }
    }

    public void proximoUsuario() throws Exception
    {
        usuarioAtual++;
        if(usuarioAtual == usuarios.size())
            usuarioAtual = 0;

        try
        {
            usuario = usuarios.get(usuarioAtual);
            mao = maos.get(usuarioAtual);
            usuario.receba(pLib);
        }
        catch (Exception erro)
        {
            throw new Exception("Erro ao chamar o próximo usuário!");
        }
        System.out.println("Mudança de usuario! Novo usuario: " + usuarioAtual);
    }

    public void jogo() throws Exception
    {
        while(usuarios.size()==3) //loop de jogo
        {
            String vencedor = "";
            usuarioAtual = -1;
            this.fimDePartida = false;

            byte pedidosDeJogo=0;

            do {
                for (Parceiro parceiro : usuarios) {
                    Comunicado comunicado = parceiro.envie();

                    if (comunicado instanceof PedidoDeJogo) {
                        pedidosDeJogo++;
                        System.out.println("Num do pedido: " + comunicado);
                    }
                    else if (comunicado instanceof PedidoParaSair)
                    {
                        this.usuarios.remove (parceiro);
                        System.out.println("Um jogador saiu! Jogo encerrando...");
                        parceiro.adeus();
                    }
                }
            } while (pedidosDeJogo < 3);

            System.out.println("Jogo começou!");
            Baralho.CriarBaralho();
            this.maos = new Vector<>();


            for (Parceiro parceiro : usuarios) {
                mao = new Mao(Baralho.criarMao());
                maos.add(mao);
                parceiro.receba(pJogo);
            }
            this.proximoUsuario();

            while (!this.fimDePartida) //loop de turno
            {
                Comunicado comunicado = this.usuario.envie ();

                if (comunicado==null)
                    return;

                else if (comunicado instanceof PedidoDeOperacao)
                {
                    System.out.println("Processando pedido de operacao");

                    PedidoDeOperacao pedidoDeOperacao = (PedidoDeOperacao)comunicado;

                    switch (pedidoDeOperacao.getOperacao())
                    {
                        case "CM":
                            this.mao.AdicionarCarta(Baralho.comprarMonte());
                            System.out.println("Mao do jogador: " + this.mao.toString());
                            break;

                        case "CD":
                            this.mao.AdicionarCarta(Baralho.comprarDescarte());
                            System.out.println("Mao do jogador: " + this.mao.toString());
                            break;

                        case "21":
                            vencedor = this.nomes.get(usuarioAtual);
                            System.out.println(vencedor + " Ganhou!");
                            break;
                    }
                }

                else if (comunicado instanceof PedidoDeDescarte)
                {
                    System.out.println("Processando pedido de descarte");

                    PedidoDeDescarte pedidoDeDescarte = (PedidoDeDescarte)comunicado;

                    Baralho.AdicionarAoDescarte(this.mao.getCarta((byte) (pedidoDeDescarte.getValor()-1)));
                    this.mao.RemoverCarta((byte)(pedidoDeDescarte.getValor()-1));
                    System.out.println("Mao do jogador: " + this.mao.toString());
                }

                else if(comunicado instanceof PedidoDeResultado)
                {
                    System.out.println("Processando pedido de resultado");
                    usuario.receba (new Resultado (this.mao, Baralho.getDescarte(), vencedor));
                }

                else if(comunicado instanceof PedidoDeFimDeTurno)
                {
                    if(!vencedor.equals(""))
                    {
                        for (Parceiro parceiro : usuarios) {
                            parceiro.receba(pLib);
                            parceiro.receba (new Resultado (this.mao, Baralho.getDescarte(), vencedor));
                        }
                        this.fimDePartida=true;
                    }
                    else
                    {
                        System.out.println("Processando pedido de fim de Turno");
                        this.proximoUsuario();
                    }
                }

                else if (comunicado instanceof PedidoParaSair)
                {
                    this.usuarios.remove (usuario);

                    usuario.adeus();
                }
            }
        }
    }

}
