/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csim01;

import java.io.BufferedReader;
import java.io.FileReader;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author HL-Pad
 */
public class RegTest {
    public static void main(String args[]) throws Exception {
        testPo(9);
        testRam(9);
        testCPU(9);
    }
     
    public static void testPo(int no) throws Exception
    {
        //load data
        DataSource source = new DataSource("D:/cloudsim/kCTPA/RegData_Po.arff");
        Instances data = source.getDataSet();
        Instance test = data.instance(no);
        data.delete(no);
        data.setClassIndex(data.numAttributes()-1);
        test.setClassMissing();
       
        //build model
        LinearRegression model = new LinearRegression();
        model.buildClassifier(data); 
      
        //classify the last instance
        double price = model.classifyInstance(test);
        System.out.println("Power Consume: (" + test + "): " + price);
    }
    
    public static void testRam(int no) throws Exception
    {
        //load data
        DataSource source = new DataSource("D:/cloudsim/kCTPA/RegData_RAM.arff");
        Instances data = source.getDataSet();
        Instance test = data.instance(no);
        data.delete(no);
        data.setClassIndex(data.numAttributes()-1);
        test.setClassMissing();
       
        //build model
        LinearRegression model = new LinearRegression();
        model.buildClassifier(data); 
        
        //Classify
        double price = model.classifyInstance(test);
        System.out.println("RAM Usage: (" + test + "): " + price);
    }
    
    public static void testCPU(int no) throws Exception
    {
        //load data
        DataSource source = new DataSource("D:/cloudsim/kCTPA/RegData_CPU.arff");
        Instances data = source.getDataSet();
        Instance test = data.instance(no);
        data.delete(no);
        data.setClassIndex(data.numAttributes()-1);
        test.setClassMissing();
       
        //build model
        LinearRegression model = new LinearRegression();
        model.buildClassifier(data); 
        
        //Classify
        double price = model.classifyInstance(test);
        System.out.println("CPU Usage: (" + test + "): " + price);
    }
}
