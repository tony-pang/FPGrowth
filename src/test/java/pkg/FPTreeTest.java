package pkg;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class FPTreeTest{
    @Test
    public void loadFileTest(){
        String path ="";
        long startTime = System.currentTimeMillis();
        FPTree0 fpTree = new FPTree0(path, 3);
        System.out.println("Time : " + timeDifference(startTime));

//        fpTree.printFrequencies();
//        System.out.println("Time : " + timeDifference(startTime));
//
//        fpTree.printFile();
//        System.out.println("Time : " + timeDifference(startTime));


        fpTree.printTree();
        System.out.println(fpTree.getNodeLinks().keySet().toString());

        fpTree.printTree2();
        System.out.println("Time : " + timeDifference(startTime));

        for(Map.Entry<Integer, FPTree0.Node> entry : fpTree.getNodeLinks().entrySet()){
            System.out.println("header count : " + entry.getKey() + " " + entry.getValue().headerCount);
        }

        fpTree.searchFrequentPairs();

        System.out.println("Time : " + timeDifference(startTime));

    }
    private long timeDifference(long startTime){
        return System.currentTimeMillis() - startTime;
    }
}