package pkg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public class FPTree{
    /**
     * ROOT node
     */
    private final Node root = new Node(null, 0, null);
    /**
     * Thresh hold index
     */
    private int threshold = -1;
    /**
     * this file contains raw info
     */
    private List<int[]> file;
    private Map<Integer, Integer> frequency;
    /**
     * both are reference based, modifying one will change the other in memory
     */
    private Map<Integer, HeaderNode> header;//for O(1) access
    private List<HeaderNode> headerOrdered;//for sorting and all reading MUST follow this order

    private List<List<Pair>> conditionalBranch;

    /**
     * FINAL RESULT OF FREQUENT PAIRS
     */
    private List<FrequentPair> frequentPairList;
    /**
     * constructor load from file, determine the minimum support
     * @param path path
     */
    FPTree(String path){
        readFileAndCreateFrequencyMap(path);
        createHeaderMap();
        filterDataByThresholdAndAddToFpTree();
        processData();
    }

    private FPTree(List<List<Pair>> conditionalBranch, int threshold, Map<Integer, Integer> frequency){
        this.conditionalBranch = conditionalBranch;
        this.frequency = frequency;
        this.threshold = threshold;

        createConditionalHeaderMap();
        addToFpTree();
    }

    public static void main(String[] args){
        String path1 = "Sample_1.txt";
        String path2 = "Sample_2.txt";
        String path3 = "Sample_3.txt";
        String path4 = "test.tsv";
        long startTime = System.currentTimeMillis();
        FPTree fpTree = new FPTree(path3);
        System.out.println("Time : " + (System.currentTimeMillis() - startTime));
    }

    /**
     * @param path path
     */
    private void readFileAndCreateFrequencyMap(String path){

        ClassLoader classLoader = getClass().getClassLoader();
        try(FileReader fileReader = new FileReader(new File(classLoader.getResource(path).getFile()))){
            try(BufferedReader bufferedReader = new BufferedReader(fileReader)){
                String line;
                String tabDelim[];
                file = new ArrayList<>();
                frequency = new HashMap<>();
                while((line = bufferedReader.readLine()) != null){
                    if(threshold == -1) threshold = Integer.parseInt(line);//first line contains only the support
                    else{
                        tabDelim = line.split("\\t");
                        String commaDelim[];
                        for(String s : tabDelim){
                            commaDelim = s.split(",");
                            int len = commaDelim.length;
                            int[] parsedToInt = new int[len - 1];
                            int temp;
                            for(int i = 1; i < len; ++i){
                                temp = Integer.parseInt(commaDelim[i]);
                                parsedToInt[i - 1] = temp;
                                //increaseCounter of frequency list
                                if(frequency.containsKey(temp)) frequency.put(temp, frequency.get(temp) + 1);
                                else frequency.put(temp, 1);
                            }
                            file.add(parsedToInt);//Save to raw : file
                        }
                    }
                }
            }
        }catch(IOException | NullPointerException | NumberFormatException e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Create header file that contains the frequency of each element in form of headerNode object
     */
    private void createHeaderMap(){
        header = new HashMap<>();
        headerOrdered = new LinkedList<>();
        HeaderNode headerNode;
        for(Map.Entry<Integer, Integer> entry : frequency.entrySet())
            if(entry.getValue() >= threshold){
                headerNode = new HeaderNode(entry.getKey(), entry.getValue());
                header.put(entry.getKey(), headerNode);
                headerOrdered.add(headerNode);
            }
        Collections.sort(headerOrdered);
    }

    /**
     * Remove the low frequency item from data set
     */
    private void filterDataByThresholdAndAddToFpTree(){
        for(int[] line : file){
            List<Pair> list = new LinkedList<>();
            for(int i : line){
                int freq = frequency.get(i);
                if(freq >= threshold) list.add(new Pair(i, freq));
            }
            Collections.sort(list);
            root.add(0, list, 1);
        }
    }

    private void processData(){
        /*
         * instantiate the FINAL output result
         */
        frequentPairList = new LinkedList<>();

        List<Pair> branch;
        Node tempOut, tempIn;
        for(HeaderNode headerNode : headerOrdered){
            conditionalBranch = new LinkedList<>();
            tempOut = headerNode.last;
            while(tempOut != null){
                branch = new LinkedList<>();
                tempIn = tempOut.parent;
                int tempSupport = tempOut.count;
                while(tempIn != null){
                    branch.add(0, new Pair(tempIn.id, tempSupport));
                    tempIn = tempIn.parent;
                }
                if(branch.size() != 0)
                    conditionalBranch.add(branch); //no term add -> this node is the head of the chain
                tempOut = tempOut.prevSelf;
            }
            buildConditionalFPTree(headerNode.id, headerNode.count);
        }
        Collections.sort(frequentPairList);
        System.out.println(frequentPairList);
    }

    private void buildConditionalFPTree(int id, int count){
        FPTree conditionalTree = new FPTree(conditionalBranch, threshold, frequency);
        frequentPairList.addAll(conditionalTree.returnFrequentPair(id, count));
    }

    /**
     * create an empty header map for incoming conditional branches
     */
    private void createConditionalHeaderMap(){
        header = new HashMap<>();
        for(Map.Entry<Integer, Integer> entry : frequency.entrySet())
            header.put(entry.getKey(), new HeaderNode(entry.getKey(), 0));
    }

    /**
     *
     */
    private void addToFpTree(){
        for(List<Pair> pairs : conditionalBranch) root.add(0, pairs, pairs.get(0).freq);

        for(Map.Entry<Integer, HeaderNode> entry : header.entrySet()){
            HeaderNode headerNode = entry.getValue();
            Node last = headerNode.last;
            int count = 0;
            while(last != null){
                count += last.count;
                last = last.prevSelf;
            }
            headerNode.count = count;
        }
    }


    private List<FrequentPair> returnFrequentPair(int id, int idCount){
        List<FrequentPair> ret = new LinkedList<>();
        FrequentPair pair;
        int freq;
        for(Map.Entry<Integer, HeaderNode> node : header.entrySet()){
            freq = node.getValue().count;
            if(freq >= threshold){
                pair = new FrequentPair(id, node.getKey(), freq > idCount ? idCount : freq);
                ret.add(pair);
            }
        }
        return ret;
    }
    /* -----------------------------------------------------------------------------------------------------------------
     * Print functions
     * -----------------------------------------------------------------------------------------------------------------
     */

    /**
     * print file line by line
     */
    void printFile(){
        for(int[] array : file)
            for(int i = 0; i < array.length; ++i){
                System.out.print(array[i] + " ");
                if(i == array.length - 1) System.out.println();
            }
    }

    /**
     * print frequency of all unique item
     */
    public void printFrequencies(){
        for(Map.Entry<Integer, Integer> set : frequency.entrySet())
            System.out.println(set.getKey() + "," + set.getValue());
    }

    /**
     * Print tree using parent pointer
     */
    void printTree(){
        for(HeaderNode node : headerOrdered){
            System.out.println(node.toString());
            Node tempTail = node.last;
            List<Node> listToPrint;
            Node tempParent;
            while(tempTail != null){
                listToPrint = new LinkedList<>();
                tempParent = tempTail;
                while(tempParent != null){
                    listToPrint.add(tempParent);
                    tempParent = tempParent.parent;
                }
                for(int i = listToPrint.size() - 1; i >= 0; --i)
                    System.out.print(listToPrint.get(i).id + "(" + listToPrint.get(i).count + ")"
                    );
                tempTail = tempTail.prevSelf;
                System.out.println();
            }
        }
    }

    /**
     * FP-Tree node
     */
    class Node{
        /**
         * Node id
         */
        final Integer id;
        /**
         *
         */
        final Node parent;
        /**
         * counter
         */
        int count = 0;
        /**
         *
         */
        Node prevSelf = null;
        /**
         *
         */
        Map<Integer, Node> children = null;

        /**
         * constructor
         *
         * @param id id
         * @param count count
         * @param parent parent
         */
        Node(Integer id, int count, Node parent){
            this.id = id;
            this.count = count;
            this.parent = parent;
        }

        void add(int index, List<Pair> list, int count){
            if(children == null) children = new HashMap<>();
            Node child = children.get(list.get(index).name);
            if(child != null){//child found in children
                child.count += count;
                ++index;
                if(index < list.size()) child.add(index, list, count);
            }else addNewChild(index, list, count);
        }

        private void addNewChild(int index, List<Pair> list, int count){
            if(children == null) children = new HashMap<>();
            int item = list.get(index).name;
            Node child = new Node(item, count, id == null ? null : this);
            child.addToNodeLinks();
            children.put(item, child);
            ++index;
            if(index < list.size()) child.addNewChild(index, list, count);
        }

        /**
         * Add to node link list (header map)
         */
        private void addToNodeLinks(){
            prevSelf = header.get(id).last;
            header.get(id).setLast(this);
        }
    }

    /**
     * new implementation of header
     * keep
     */
    class HeaderNode implements Comparable<HeaderNode>{
        final int id;

        int count;
        Node last;

        HeaderNode(int id, int count){
            this.id = id;
            this.count = count;
        }

        @Override
        public int compareTo(HeaderNode o){
            if(o.count == this.count) return this.id - o.id;
            else return this.count - o.count;
        }

        void setLast(Node last){
            this.last = last;
        }

        @Override
        public String toString(){
            return "id: " + id + ", count: " + count + "\n";
        }
    }


    class Pair implements Comparable<Pair>{
        final int name;
        final int freq;

        Pair(int name, int freq){
            this.name = name;
            this.freq = freq;
        }

        public String toString(){
            return "Name: " + name + "-Frequency: " + freq + "\n";
        }

        @Override
        public int compareTo(Pair o){
            if(o.freq == this.freq) return this.name - o.name;
            else return o.freq - this.freq;
        }
    }

    /**
     * The wrapper for the final individual results
     */
    class FrequentPair implements Comparable<FrequentPair>{
        final int a;
        final int b;
        final int freq;

        FrequentPair(int a, int b, int freq){
            this.a = a;
            this.b = b;
            this.freq = freq;
        }

        public String toString(){
            return a + "-" + b + "->" + freq + "\n";
        }

        @Override
        public int compareTo(FrequentPair o){
            if(o.freq != this.freq){
                return o.freq - this.freq;
            }else{
                if(o.a != this.a) return this.a - o.a;
                else return this.b - o.b;
            }
        }
    }

}
