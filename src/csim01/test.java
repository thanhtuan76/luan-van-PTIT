/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csim01;

import java.util.LinkedList;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import org.cloudbus.cloudsim.CloudletExt;
import org.cloudbus.cloudsim.Log;
import org.uncommons.maths.random.GaussianGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 *
 * @author Admin
 */
public class test {
    private static List<Double> LastRT;

    public static void main(String args[]) {
//        for (int i = 0; i < 5; i++) {
//            long a = getRandomNumberInRange(100, 10);
//            Log.printLine("Test ===" + a);
//        }
        LastRT = new ArrayList<Double>();
        Double n1 = new Double(10);
        Double n2 = new Double(12);
        Double n3 = new Double(15);
        
        Double n4 = new Double(25);
        Double n5 = new Double(35);
        addLatestRT(n1);        
        addLatestRT(n2);
        addLatestRT(n3);
        printList(LastRT);
        
        addLatestRT(n4);
        removeOldestRT();
        printList(LastRT);
        
        addLatestRT(n5);
        removeOldestRT();
        printList(LastRT);
    }

    private static void addLatestRT(Double RT)
    {
        LastRT.add(RT);
    }
    
    private static void removeOldestRT()
    {
        LastRT.remove(0);
    }
    
    private static void printList(List<Double> lst)
    {
        Date d = new Date();        
        String str = "";
        for(Double num : lst)
        {
            str = str +num.toString()+ ",";
        }
        str = "At time ["+ d.toLocaleString()+"] :"+  str;
        Log.printLine(str);
    }
    
    private static long getRandomNumberInRange(int mean, int std) {
        Random rng = new MersenneTwisterRNG();
        GaussianGenerator cpuGen = new GaussianGenerator(mean, std, rng);
        return cpuGen.nextValue().longValue();
    }

    public static long getRandomNumber(long min, long max) {
        Random random = new Random();
        return random.nextLong() % (max - min) + max;
    }
}
