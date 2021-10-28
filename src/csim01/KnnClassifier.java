/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csim01;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 *
 * @author HaoLee
 */
public class KnnClassifier {
    ArrayList<Item> items;
    int k;

    public KnnClassifier(ArrayList<Item> initItems, int initK) {
        items = initItems;
        k = initK;
    }

    public static Map sortMapByValueWithoutLast(Map unsortMap) {
        List list = new LinkedList(unsortMap.entrySet());

        Collections.sort(list, new Comparator() {
            //desc o2 with o1 to get opposite order, removing first element is faster
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });
        list.remove(0);

        LinkedHashMap sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public int Evaluate(Item item) {

        Map<Item, Double> knn = new LinkedHashMap<>();
        double maxAllowedDistance = calculateDistance(items.get(0), item);

        for (Item currItem : items) {
            double currDist = calculateDistance(currItem, item);
            if (knn.size() < k) {
                knn.put(currItem, currDist);
                if (currDist > maxAllowedDistance) {
                    maxAllowedDistance = currDist;
                }
            } else {
                if (currDist < maxAllowedDistance) {
                    knn.put(currItem, currDist);
                    maxAllowedDistance = knn.get(knn.keySet().iterator().next());
                    knn = sortMapByValueWithoutLast(knn);
                }
            }
        }
        maxAllowedDistance = knn.get(knn.keySet().iterator().next());
        System.out.println("Found " + knn.size() + " nearest neighboors");
        System.out.println("Furthest is " + maxAllowedDistance);

        HashMap<Integer, Double> LabelSums = new HashMap<>();

        for (Item key : knn.keySet()) {
            double dist = knn.get(key);
            double reciproc = 1 / dist;
            double weight = reciproc * maxAllowedDistance;
            if (LabelSums.get(key.Label) == null) {
                LabelSums.put(key.Label, weight);
            } else {
                LabelSums.put(key.Label, LabelSums.get(key.Label) + weight);
            }

            System.out.println(dist + " with weight " + weight);
        }
        double maxWeight = 0;
        int foundClass = -1;
        for (Integer key : LabelSums.keySet()) {
            double weight = LabelSums.get(key);
            if (weight > maxWeight) {
                foundClass = key;
                maxWeight = weight;
            }
        }
        return foundClass;

    }

    private double calculateDistance(Item item1, Item item2) {
        double dist = 0;
        for (int key : item1.Features.keySet()) {
            double a = item1.Features.get(key);
            double b = item2.Features.get(key);
            dist += (a - b) * (a - b);
        }
        return Math.sqrt(dist);
    }
}
