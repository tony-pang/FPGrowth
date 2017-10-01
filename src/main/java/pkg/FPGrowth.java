package pkg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pkg.FPTree.HeaderNode;
import pkg.FPTree.Node;

import java.util.LinkedList;
import java.util.List;


@SuppressWarnings("unused")
public class FPGrowth{
    @Getter
    private int threshold;
    /**
     * contains the complete data set of the input file
     */
    @Getter
    private FPTree baseTree;

    private List<HeaderNode> orderedHeader;

    private List<FrequentPair> finalResult;

    /**
     * start from file loading
     *
     * @param path
     */
    public FPGrowth(String path){
        baseTree = new FPTree(path);
        threshold = baseTree.getThreshold();
        orderedHeader = baseTree.getHeaderOrdered();

        processData();
    }

    private List<FrequentPair> processData(){
        List<List<Node>> conditionalData;
        List<Node> branch;
        Node tempOut, tempIn;
        for(HeaderNode headerNode : orderedHeader){
            conditionalData = new LinkedList<>();
            tempOut = headerNode.getLast();
            while(tempOut != null){
                branch = new LinkedList<>();
                tempIn = tempOut;
                while(tempIn != null){
                    branch.add(0, tempIn);
                    tempIn = tempIn.getParent();
                }
                conditionalData.add(branch);
                tempOut = tempOut.getPrevSelf();
            }
            //
            // print test
            //            for(List<Node> nList :  conditionalData){
//                for(Node n : nList){
//                    System.out.print(n.getId() + " ");
//                }
//                System.out.println();
//            }
            buildConditionalFPTree(conditionalData);
        }
        return null;
    }

    private void buildConditionalFPTree(List<List<Node>> conditionalData){

    }

    /**
     * The wrapper for the final individual results
     */
    @Getter
    @RequiredArgsConstructor
    class FrequentPair{
        private final int a;
        private final int b;
        private final int freq;
    }

}
