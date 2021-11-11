import java.net.*;
import java.io.*;

public class Cliente
{
	public static final String HOST_PADRAO  = "LOCALHOST";
	public static final int    PORTA_PADRAO = 3000;

	public static void main (String[] args)
	{
        if (args.length>2)
        {
            System.err.println ("Uso esperado: java Cliente [HOST [PORTA]]\n");
            return;
        }

        Socket conexao;
        try
        {
            String host = Cliente.HOST_PADRAO;
            int    porta= Cliente.PORTA_PADRAO;

            if (args.length>0)
                host = args[0];

            if (args.length==2)
                porta = Integer.parseInt(args[1]);

            conexao = new Socket (host, porta);
        }
        catch (Exception erro)
        {
            System.err.println ("Indique o servidor e a porta corretos!\n");
            return;
        }

        ObjectOutputStream transmissor;
        try
        {
            transmissor =
            new ObjectOutputStream(
            conexao.getOutputStream());
        }
        catch (Exception erro)
        {
            System.err.println ("Indique o servidor e a porta corretos!\n");
            return;
        }

        ObjectInputStream receptor;
        try
        {
            receptor =
            new ObjectInputStream(
            conexao.getInputStream());
        }
        catch (Exception erro)
        {
            System.err.println ("Indique o servidor e a porta corretos!\n");
            return;
        }

        Parceiro servidor;
        try
        {
            servidor =
            new Parceiro (conexao, receptor, transmissor);
        }
        catch (Exception erro)
        {
            System.err.println ("Indique o servidor e a porta corretos!\n");
            return;
        }

        TratadoraDeComunicadoDeDesligamento tratadoraDeComunicadoDeDesligamento = null;
        try
        {
			tratadoraDeComunicadoDeDesligamento = new TratadoraDeComunicadoDeDesligamento (servidor);
		}
		catch (Exception ignored)
		{} // sei que servidor foi instanciado

        tratadoraDeComunicadoDeDesligamento.start();


		System.out.println ("Você logou na sala de 21\n");
		System.out.print ("Digite seu nome de usuario: ");
		String nomeJogador;
		do
		{
			nomeJogador = Teclado.getUmString();
			System.out.println();
			try
			{
				servidor.receba(new PedidoDeNome(nomeJogador));
			}
			catch (Exception erro)
			{}
		}
		while (nomeJogador.equals(""));


		char jogar = ' ';
		try
		{
			Comunicado comunicado;
			System.out.println ("Aguarde mais jogadores...");

			do//rotação por partida
			{
				servidor.receba (new PedidoDeJogo ());
				do //esperando o servidor começar o jogo
				{ comunicado = servidor.espie(); }
				while (!(comunicado instanceof PedidoDeJogo));
				servidor.envie ();

				System.out.println ("O jogo começou!\n");

				for(;;) //jogo
				{
					System.out.println ("Aguarde sua vez...\n");


					servidor.receba (new PedidoDeLiberacao ());
					do //esperando ser sua vez
					{ comunicado = servidor.espie(); }
					while (!(comunicado instanceof PedidoDeLiberacao));
					servidor.envie ();


					servidor.receba (new PedidoDeResultado ());
					do //esperando o servidor retornar os valores
					{ comunicado = servidor.espie(); }
					while (!(comunicado instanceof Resultado));
					Resultado resultado = (Resultado)servidor.envie ();

					if(!resultado.getVencedor().equals(""))
					{
						System.out.println ("O jogo acabou! " + resultado.getVencedor() + " venceu!");
						break;
					}

					System.out.println ("-----------------");
					System.out.println ("Suas cartas:");
					for(byte i=0; i<resultado.getTamanho();i++)
						System.out.println ("(" + (i+1) + ") - " + resultado.getCarta(i));
					System.out.println ("-----------------");
					System.out.println ("Soma:"+resultado.getSomaCartas() + "\n");

					System.out.println ("Suas opcoes:");
					System.out.println ("Comprar do Monte 			[CM]");

					if(!resultado.getDescarte().equals(""))
						System.out.println ("Comprar do Descarte ("+resultado.getDescarte()+")	[CD]");

					if(resultado.getSomaCartas()== 21)
						System.out.println ("Bater 21 				[21]");

					boolean bateu21=false;
					for(;;)
					{
						String opcao = Teclado.getUmString();

						if (opcao.equals("CM")
								|| opcao.equals("CD") && !resultado.getDescarte().equals("")
								|| opcao.equals("21") && resultado.getSomaCartas()== 21)
						{
							servidor.receba(new PedidoDeOperacao(opcao));
							if(opcao.equals("21"))
							{
								bateu21=true;
								servidor.receba (new PedidoDeFimDeTurno ());
							}
							break;
						}
						else { System.err.println("Opcao invalida!\n"); }
					}
					if(bateu21)
						continue;

					servidor.receba (new PedidoDeResultado ());
					do //Esperando a resposta do servidor.
					{ comunicado = servidor.espie(); }
					while (!(comunicado instanceof Resultado));

					Resultado resDescarte = (Resultado)servidor.envie ();
					System.out.println ("-----------------");
					System.out.println ("Suas cartas:");
					for(byte i=0; i<resDescarte.getTamanho();i++)
						System.out.println ("(" + (i+1) + ") - " + resDescarte.getCarta(i));
					System.out.println ("-----------------");



					byte valor=0;
					do {
						System.out.print("Qual carta deseja descartar? ");
						try
						{
							valor = Teclado.getUmByte();
							System.out.println();
						}
						catch (Exception erro) {
							System.err.println("Valor invalido!\n");
						}
					} while (valor < 1 || valor > 4);

					servidor.receba (new PedidoDeDescarte (valor));

					servidor.receba (new PedidoDeResultado ());
					do
					{ comunicado = servidor.espie(); }
					while (!(comunicado instanceof Resultado));
					Resultado resFinal = (Resultado)servidor.envie ();

					if(resFinal.getSomaCartas() == 21)
					{
						System.out.println ("Deseja bater 21? (S/N)");
						Character bater = Character.toUpperCase(Teclado.getUmChar());
						if(bater.equals('S'))
							servidor.receba(new PedidoDeOperacao("21"));
					}
					servidor.receba (new PedidoDeFimDeTurno ());
				}


				System.out.println ("Deseja continuar jogando? (S/N)");
				try
				{
					jogar = (Character.toUpperCase(Teclado.getUmChar()));
				}
				catch (Exception erro)
				{}
			}
			while (jogar == 'S');
		}
		catch (Exception erro)
		{
			System.err.println ("Erro de comunicacao com o servidor;" + erro.getMessage());
			System.err.println ("Tente novamente!");
			System.err.println ("Caso o erro persista, termine o programa");
			System.err.println ("e volte a tentar mais tarde!\n");
		}

		try
		{
			servidor.receba (new PedidoParaSair ());
		}
		catch (Exception ignored)
		{}

		System.out.println ("Obrigado por usar este programa!");
		System.exit(0);
	}
}
