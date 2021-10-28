/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csim01;
import java.util.HashMap;
import java.util.Map.Entry;
import weka.core.Attribute;
import weka.core.Instance;

/**
 *
 * @author HaoLee
 */
public class Item {
    public Integer Label = null;
    public HashMap<Integer, Double> Features = new HashMap<>();

    public Item(Instance currInst, int numAttrs) throws Exception {

        for (int attrIdx = 0; attrIdx < numAttrs; attrIdx++) {

            Attribute currAttr = currInst.attribute(attrIdx);

            if (currAttr.isNominal() && currInst.classIndex() == attrIdx) {
                Label = (int) currInst.value(attrIdx);
            } else if (currAttr.isNumeric()) {
                Features.put(attrIdx, +currInst.value(attrIdx));
            } else {
                throw new Exception("fail, not nominal index, nor numeric");
            }
            //System.out.println(currAttr + " = " + currInst.value(attrIdx));
        }
    }

    public void showData() {
        for (Entry<Integer, Double> entry : Features.entrySet()) {
            Integer key = entry.getKey();
            Double value = entry.getValue();
            System.out.println(key + " = " + value);
        }
        System.out.println("class = " + Label);
    }
}
