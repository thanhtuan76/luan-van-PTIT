/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csim01;

import java.io.BufferedReader;
import java.io.FileReader;
import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.functions.LinearRegression;

/**
 *
 * @author HaoLee
 */
public class RegressionTest {

   public static void main(String args[]) throws Exception {
//load data
        Instances data = new Instances(new BufferedReader(new FileReader("D:/cloudsim/test/house.arff")));
        data.setClassIndex(data.numAttributes() - 1);
//build model
        LinearRegression model = new LinearRegression();
        model.buildClassifier(data); //the last instance with missing not used

        System.out.println(model);
//classify the last instance
        Instance myHouse = data.lastInstance();
        double price = model.classifyInstance(myHouse);
        System.out.println("My house (" + myHouse + "): " + price);
    }
}
