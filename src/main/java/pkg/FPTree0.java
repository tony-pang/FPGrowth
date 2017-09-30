package pkg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@SuppressWarnings({"unused", "Duplicates"})
public class FPTree0{

    private List<String[]> inputFile;
    private Map<Integer, Integer> frequency;
    private Node root;
    private Map<Integer, Node> nodeLinks;
    private List<Node> sortedNodeLinks;

    /**
     * constructor
     * @param path path of the file (data)
     * @param threshold minimum support
     */
    FPTree0(String path, int threshold){
        root = new Node(null, null);
        nodeLinks = new HashMap<>();
        readFileAndCreateFrenquecyMap(path);
        filterDataByThresholdAndAddTOfpTree(threshold);
    }

    /**
     *
     * @param conditionalBranch
     */
    FPTree0(List<List<Node>> conditionalBranch){
        root = new Node(null, null);
        nodeLinks = new HashMap<>();
    }

    /**
     * read file and construct frequency map
     * @param path absolute file path
     */
    private void readFileAndCreateFrenquecyMap(String path){
        try(
                FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ){
            String line;
            String tabDelim[];
            inputFile = new ArrayList<>();
            frequency = new HashMap<>();
            while((line=bufferedReader.readLine())!=null){
                tabDelim = line.split("\\t");
                String commaDelim[];
                for(String s : tabDelim){
                    commaDelim = s.split(",");
                    inputFile.add(commaDelim);
                    for(int i = 1; i<commaDelim.length ;++i){
                        int temp = Integer.parseInt(commaDelim[i]);
                        //increaseCounter to frequency list
                        if(frequency.containsKey(temp)) frequency.put(temp, frequency.get(temp)+1);
                        else frequency.put(temp,1);
                    }
                }
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Filter and add to FP-Tree
     * @param threshold minimum support
     */
    private void filterDataByThresholdAndAddTOfpTree(int threshold){
        for(String[] line : inputFile){
            List<Pair> list = new LinkedList<>();
            for(int i = 1 ; i < line.length ;++i){
                int temp = Integer.parseInt(line[i]);
                int freq = frequency.get(temp);
                if(freq>=threshold) list.add(new Pair(temp, freq));
            }
            Collections.sort(list);

            System.out.println("Sorted list: "+list.toString());
            addToFPTree(list.get(0).getKey(), list, root);

            sortNodeLinks();
        }
    }

    /**
     * recursive calls to add all nodes
     * @param firstOfList the item to add
     * @param list list of current transactions that has been filtered and sorted according to its support
     * @param parent of which this node should be linked to
     */
    private void addToFPTree(int firstOfList, List<Pair> list, Node parent){
        list.remove(0);
        parent.increaseCounter();
        Node child;
        if(!parent.contains(firstOfList)) {
            child = new Node(firstOfList, parent);
            parent.append(child); //node reference will also be added to the nodeLink map
            addToNodeLinks(child);
        }else child = parent.get(firstOfList);
        increaseHeaderCount(child);
        if(list.size() != 0) addToFPTree(list.get(0).getKey(), list, child);
        else parent.get(firstOfList).increaseCounter();
    }

    /**
     * Add to node link list (header map)
     * @param node the node that needs to be added to header map
     */
    private void addToNodeLinks(Node node){
        Node head = nodeLinks.get(node.name);
        if(head==null)  nodeLinks.put(node.name, node);
        else {
            Node temp = head;
            while(temp.nextSelf!=null) temp = temp.nextSelf;
            temp.nextSelf = node;
        }
    }
    /**
     * Header count increase
     * @param node node whose counter needs to be increased
     */
    private void increaseHeaderCount(Node node){
        ++(nodeLinks.get(node.name).headerCount);
    }

    /**
     * Sort Node link according to their support counter
     */
    private void sortNodeLinks(){
        sortedNodeLinks = new LinkedList<>();
        for(int key : nodeLinks.keySet()) sortedNodeLinks.add(nodeLinks.get(key));
        Collections.sort(sortedNodeLinks);
    }

    /* -----------------------------------------------------------------------------------------------------------------
     * Searching for frequent pairs
     * -----------------------------------------------------------------------------------------------------------------
     */
    void searchFrequentPairs(){
        List<List<Node>> conditionalData;
        List<Node> branch;
        Node tempOut, tempIn;

        for(Node headerNode : sortedNodeLinks){
            conditionalData = new LinkedList<>();
            tempOut = headerNode;
            while(tempOut!=null){
                branch = new LinkedList<>();
                tempIn = tempOut;
                while(tempIn.name!=null){
                    branch.add(0,tempIn);
                    tempIn = tempIn.parent;
                }
                conditionalData.add(branch);
                tempOut = tempOut.nextSelf;
            }
//            for(List<Node> nList :  conditionalData){
//                for(Node n : nList){
//                    System.out.print(n.name + " ");
//                }
//                System.out.println();
//            }
            buildConditionalFPTree(conditionalData);

        }
    }

    private void buildConditionalFPTree(List<List<Node>> conditional){
        Node conditionalRoot = new Node(null, null);















    }


    /* -----------------------------------------------------------------------------------------------------------------
     * Print functions
     * -----------------------------------------------------------------------------------------------------------------
     */

    /**
     * print each unique branch from top to bottom
     */
    void printTree(){
        List<Pair> list = new LinkedList<>();
        printTreePrivate(root, list);
        counter=0;
    }

    /**
     * counter for the printing
     */
    private int counter = 0;
    private void printTreePrivate(Node node, List<Pair> list){
        List<Pair> newList = new LinkedList<>(list);
        if(node.name!=null) newList.add(new Pair(node.name, node.count));
        if(node.children==null){
            System.out.print(++counter+" : ");
            for(Pair pair : newList) System.out.print(pair.key+"("+pair.value+")");
            System.out.println();
        }else for(int key: node.children.keySet()) printTreePrivate(node.get(key), newList);
    }

    /**
     * Print tree using parent pointer
     */
    void printTree2(){
        for(Node node : sortedNodeLinks){
            System.out.println(node.name + "  " + node.headerCount);
            Node tempTail = node;
            List<Node> listToPrint;
            Node tempParent;
            while(tempTail!=null){
                listToPrint = new LinkedList<>();
                tempParent = tempTail;
                while(tempParent!=null){
                    listToPrint.add(tempParent);
                    tempParent = tempParent.parent;
                }
                for(int i = listToPrint.size()-2; i>=0; --i )
                    System.out.print(listToPrint.get(i).name + "("+listToPrint.get(i).count+")");
                tempTail = tempTail.nextSelf;
                System.out.println();
            }
        }
    }

    /**
     * print file line by line
     */
    void printFile(){
        for(String[] array : inputFile)
            for(int i = 0 ; i<array.length; ++i){
                System.out.print(array[i]+" ");
                if(i==array.length-1) System.out.println();
        }
    }

    /**
     * print frequency of all unique item
     */
    public void printFrequencies(){
        for(Map.Entry<Integer, Integer> set : frequency.entrySet())
            System.out.println(set.getKey() + ","+set.getValue());
    }


    /* -----------------------------------------------------------------------------------------------------------------
     * getters
     * -----------------------------------------------------------------------------------------------------------------
     */

    /**
     * get the frequency map
     * @return frequency map
     */
    public Map<Integer, Integer> getFrequency(){
        return frequency;
    }

    /**
     * get the root node
     * @return root node reference
     */
    public Node getRoot(){
        return root;
    }

    /**
     * get the node link header map
     * @return node link map
     */
    Map<Integer, Node> getNodeLinks(){
        return nodeLinks;
    }

    /* -----------------------------------------------------------------------------------------------------------------
     * Pair
     * For linked list sorting efficiency, data is saved in Pair class
     * -----------------------------------------------------------------------------------------------------------------
     */
    class Pair implements Comparable<Pair>{
        private int key, value;
        Pair(int key, int value){
            this.key = key;
            this.value = value;
        }

        /**
         * get Key of Pair
         * @return key
         */
        int getKey(){
            return key;
        }

        /**
         * get Value of Pair
         * @return value
         */
        int getValue(){
            return value;
        }

        @Override
        public int compareTo(Pair o){
            return o.value-this.value;
        }
        public String toString(){
            return key + "-" + value;
        }
    }
    /* -----------------------------------------------------------------------------------------------------------------
     * Node
     * Tree node class
     * -----------------------------------------------------------------------------------------------------------------
     */
    class Node implements Comparable<Node>{
        /**
         * initialized to 0, so the first adding will not add extra counter
         */
        int count = 0;
        /**
         * Integer class can be null
         */
        Integer name;
        /**
         * parent of this node
         */
        Node parent = null;
        /**
         * singly linked list of same item
         */
        Node nextSelf = null;
        /**
         * contains all children's pointer
         */
        Map<Integer, Node> children;

        /**
         * counter used only for the header node in nodeLinks map
         */
        int headerCount = 0;

        /**
         * constructor with name
         * @param name name of the node
         */
        Node(Integer name, Node parent){
            this.name = name;
            this.parent = parent;
        }

        /**
         * constructor for node with preset support
         * @param name name of the node
         * @param parent parent reference
         * @param support minimun support
         */
        Node(Integer name , Node parent, int support){
            this.name = name;
            this.parent = parent;
            this.count = support;
        }

        /**
         * increase counter
         */
        void increaseCounter(){
            ++this.count;
        }

        /**
         * Only called when parent does not have this child
         * @param node node to be appended to children map
         */
        void append(Node node){
            if(children==null) children = new HashMap<>();
            children.put(node.name, node);
        }
        boolean contains(int name){
            return children != null && children.containsKey(name);
        }
        Node get(int name){
            return children.get(name);
        }

        @Override
        public int compareTo(Node o){
            return this.headerCount - o.headerCount;
        }
    }
}
