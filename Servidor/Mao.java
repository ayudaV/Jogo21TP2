import java.io.Serializable;
import java.util.Vector;

public class Mao implements Serializable
{
    private Vector<String> cartas;

    public Mao(Vector<String> cartas)
    {
        this.cartas = cartas;
    }

    public Vector<String> getMao()
    {
        return cartas;
    }

    public static byte getValor(String carta)
    {
        switch(carta.charAt(0)) {
            case 'A': return 1;
            case '2': return 2;
            case '3': return 3;
            case '4': return 4;
            case '5': return 5;
            case '6': return 6;
            case '7': return 7;
            case '8': return 8;
            case '9': return 9;
            case '1':
            case 'V':
            case 'D':
            case 'R': return 10;
        }
        return 0;
    }

    public void AdicionarCarta(String carta)
    {
        cartas.add(carta);
    }

    public String getCarta(byte i)
    {
        return cartas.get(i);
    }

    public void RemoverCarta(byte i)
    {
        cartas.remove(i);
    }

    public byte getTamanho()
    {
        return (byte) cartas.size();
    }

    public byte getSoma()
    {
        byte sum=0;
        for(byte i=0; i < this.cartas.size(); i++)
        {
            sum += (Mao.getValor(cartas.get(i)));
        }
        return sum;
    }
    public String toString()
    {
        return cartas.toString();
    }
}
