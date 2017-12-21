package Test;

import java.util.*;

public class Test {
    public static void main(String args[]) {
        Random random = new Random();
        int[] nb = new int[10];
        for(int i = 0;i<10;i++)
        {
            nb[i] =   random.nextInt(10);
            System.out.println(nb[i]);
        }
        Random random2 = new Random();
        int index = random2.nextInt(10);
        System.out.println("second="+index);
    }
}