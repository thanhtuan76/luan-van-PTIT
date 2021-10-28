/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csim01;
import java.util.ArrayList;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
/**
 *
 * @author HaoLee
 */
public class Knn {
    static final int k = 5;

    public static void main(String[] args) throws Exception {
        DataSource source = new DataSource("G:/cloudsim/data/iris.arff");
        Instances data = source.getDataSet();
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1);
        }
        int numAttrs = data.numAttributes();
        Instance test = data.instance(2);
        data.delete(2);
        Item testItem = new Item(test, numAttrs);
        testItem.Label = null;

        int numInstances = data.numInstances();
        ArrayList<Item> Items = new ArrayList<>();

        for (int instIdx = 0; instIdx < numInstances; instIdx++) {
            Items.add(new Item(data.instance(instIdx), numAttrs));
            //Items.get(Items.size()-1).showData();
        }

        KnnClassifier knn = new KnnClassifier(Items, k);
        int found = knn.Evaluate(testItem);
        System.out.println("Result with my classifier: " + found);

        Classifier ibk = new IBk();
        ibk.buildClassifier(data);

        double class1 = ibk.classifyInstance(test);
        System.out.println("Result with ibk from weka: " + (int) class1);

    }
}
