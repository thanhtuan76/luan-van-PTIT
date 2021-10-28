/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csim01;

import weka.associations.tertius.IndividualInstance;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.supervised.attribute.AddClassification;

/**
 *
 * @author HaoLee
 */
public class SVMPredictTest {

    public static void main(String[] args) throws Exception {
        DataSource source = new DataSource("G:/cloudsim/test/cloudlet.arff");
        Instances traindata = source.getDataSet();
        traindata.setClassIndex(traindata.numAttributes() - 1);
        int numClasses = traindata.numClasses();
        for (int i = 0; i < numClasses; i++) {
            String classValue = traindata.classAttribute().value(i);
            System.out.println("the " + i + "th class value:" + classValue);
        }
        /**
         * naive bayes classifier
         */
        NaiveBayes nb = new NaiveBayes();
        nb.buildClassifier(traindata);
        /**
         * load test data
         */
        DataSource source2 = new DataSource("G:/cloudsim/test/cloudlet-unknown.arff");
        Instances testdata = source2.getDataSet();
        Instance test1 = testdata.instance(1);
        Instance test = new Instance(6);
        test.setValue(0, 3780);
        test.setValue(1, 1530.5);
        test.setValue(2, 1);
        test.setValue(3, 1);
        test.setValue(4, 105.6988743180709527);            
        //test.setValue(5, 1);
        test.setMissing(5);
        testdata.add(test);        
        testdata.setClassIndex(testdata.numAttributes() - 1);

        /**
         * make prediction by naive bayes classifier
         */
        for (int j = 0; j < testdata.numInstances(); j++) {
            double actualClass = testdata.instance(j).classValue();
            //System.out.println(actualClass);
            String actual = testdata.classAttribute().value((int) actualClass);
            //System.out.println(actual);
            Instance newInst = testdata.instance(j);            
            System.out.println("actual class:" + newInst.stringValue(newInst.numAttributes() - 1));
            double preNB = nb.classifyInstance(newInst);
            String predString = testdata.classAttribute().value((int) preNB);
            System.out.println(actual + "," + predString);
        }
    }
}
