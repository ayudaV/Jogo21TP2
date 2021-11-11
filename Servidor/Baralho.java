import java.util.Arrays;
import java.util.Vector;

public class Baralho {

    public static String[] cartas =
    {
            "A de paus",
            "2 de paus",
            "3 de paus",
            "4 de paus",
            "5 de paus",
            "6 de paus",
            "7 de paus",
            "8 de paus",
            "9 de paus",
            "10 de paus",
            "Valete de paus",
            "Dama de paus",
            "Rei de paus",
            "A de copas",
            "2 de copas",
            "3 de copas",
            "4 de copas",
            "5 de copas",
            "6 de copas",
            "7 de copas",
            "8 de copas",
            "9 de copas",
            "10 de copas",
            "Valete de copas",
            "Dama de copas",
            "Rei de copas",
            "A de ouros",
            "2 de ouros",
            "3 de ouros",
            "4 de ouros",
            "5 de ouros",
            "6 de ouros",
            "7 de ouros",
            "8 de ouros",
            "9 de ouros",
            "10 de ouros",
            "Valete de ouros",
            "Dama de ouros",
            "Rei de ouros",
            "A de espadas",
            "2 de espadas",
            "3 de espadas",
            "4 de espadas",
            "5 de espadas",
            "6 de espadas",
            "7 de espadas",
            "8 de espadas",
            "9 de espadas",
            "10 de espadas",
            "Valete de espadas",
            "Dama de espadas",
            "Rei de espadas"
    };
    public static Vector<String> monte;
    public static Vector<String> descarte;

    public static void CriarBaralho()
    {
        monte = new Vector<>();
        descarte = new Vector<>();
        monte.addAll(Arrays.asList(cartas));
        System.out.println("Baralho criado!");
        Baralho.Embaralhar();
        System.out.println("Baralho embaralhado!");
    }

    private static void Embaralhar()
    {
        byte tamanhoBaralho = (byte) monte.size();
        for(byte i=0;i<tamanhoBaralho;i++)
        {
            String troca = monte.get(i);
            byte novaPos = (byte) (Math.random() * tamanhoBaralho);
            monte.set(i, monte.get(novaPos));
            monte.set(novaPos,troca);
        }
    }

    public static Vector<String> criarMao()
    {
        Vector<String> cartas = new Vector<>();
        for(byte i=0; i < 3;i++)
        {
            cartas.add(monte.get(monte.size()-1));
            monte.remove(monte.size()-1);
        }
        System.out.println("Mao criada! " + cartas.toString());
        return cartas;
    }

    public static String comprarMonte()
    {
        String carta = monte.get(monte.size()-1);
        monte.remove(monte.size()-1);
        System.out.println("Carta comprada do monte! " + carta);
        return carta;
    }

    public static String comprarDescarte()
    {
        String carta = descarte.get(descarte.size()-1);
        descarte.remove(descarte.size()-1);
        System.out.println("Carta comprada do descarte! " + carta);
        return carta;
    }

    public static void AdicionarAoDescarte(String carta)
    {
        descarte.add(carta);
    }

    public static String getDescarte()
    {
        if(descarte.size()==0)
            return "";

        return descarte.get(descarte.size()-1);
    }

    public static byte getTamanhoMonte()
    {
        return (byte) monte.size();
    }

    public static byte getTamanhoDescarte()
    {
        return (byte) descarte.size();
    }
}
