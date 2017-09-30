package pkg;

import org.junit.Test;

public class FPTreeTest{
    @Test
    public void loadFileTest(){
        String path1 = "Sample_1.txt";
        String path2 = "Sample_2.txt";
        String path3 = "Sample_3.txt";
        String path4 = "test.tsv";
        long startTime = System.currentTimeMillis();
        FPTree fpTree = new FPTree(path4);
//        fpTree.printFile();
//        System.out.println("Time : " + timeDifference(startTime));

        fpTree.printFrequencies();
        System.out.println(fpTree.getHeaderOrder());
        System.out.println("Time : " + timeDifference(startTime));

        fpTree.printTree();
        System.out.println(fpTree.getHeaderOrder().toString());
//
//        fpTree.printTree2();
//        System.out.println("Time : " + timeDifference(startTime));
//
//        for(Map.Entry<Integer, FPTree0.Node> entry : fpTree.getNodeLinks().entrySet()){
//            System.out.println("header count : " + entry.getKey() + " " + entry.getValue().headerCount);
//        }
//
//        fpTree.searchFrequentPairs();
//
//        System.out.println("Time : " + timeDifference(startTime));

    }
    private long timeDifference(long startTime){
        return System.currentTimeMillis() - startTime;
    }
}