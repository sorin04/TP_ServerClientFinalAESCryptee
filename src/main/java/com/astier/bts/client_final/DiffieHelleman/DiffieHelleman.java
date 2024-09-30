package com.astier.bts.client_final.DiffieHelleman;

import com.astier.bts.client_final.tcp.TCP;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class DiffieHelleman {
    TCP tcp = new TCP();
    int taille = 0;
    byte [] byte_B = new byte[65535];
    BigInteger serveurB, a, A, k, p, g;

    public DiffieHelleman(TCP tcp, int taille) {
        this.tcp = tcp;
        this.taille = taille;
        a = new BigInteger(taille,new SecureRandom());
        p = new BigInteger(taille,new SecureRandom());
        do {
            g = new BigInteger(taille,new SecureRandom());
        }while (g.compareTo(p) > 0);
    }
    public byte [] recuperationParam(){
        try {
            tcp.outBin.write(p.toByteArray());
        Thread.sleep(100);
        tcp.outBin.write(g.toByteArray());
        Thread.sleep(100);
        A = g.modPow(a, p);
        tcp.outBin.write(A.toByteArray());
        int taille_b = tcp.inBin.read(byte_B);
        serveurB = new BigInteger(Arrays.copyOfRange(byte_B, 0, taille_b));
        k = serveurB.modPow(a, p);
        return k.toByteArray();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
