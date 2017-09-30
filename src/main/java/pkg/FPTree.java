package pkg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Log4j2
@SuppressWarnings({"usued","Duplicates"})
public class FPTree{
    /**
     * ROOT node
     */
    @Getter
    private final Node root = new Node(null, 0, null);
    /**
     * Start time constant
     */
    long start;
    /**
     * Thresh hold index
     */
    @Getter
    private int threshold = -1;
    /**
     * this file contains raw info
     */
    @Getter
    private List<int[]> file;
    @Getter
    private Map<Integer, Integer> frequency;
    /**
     * both are reference based, modifying one will change the other in memory
     */
    @Getter
    private Map<Integer, HeaderNode> header;//for O(1) access
    @Getter
    private List<HeaderNode> headerOrder;//for sorting and all reading MUST follow this order

    /**
     * constructor load from file, determine the minimum support
     * @param path
     */
    FPTree(String path){
        start = System.currentTimeMillis();
        readFileAndCreateFrenquecyMap(path);
        createHeaderMap();
        filterDataByThresholdAndAddTOfpTree();
    }


    /**
     * @param path
     */
    private void readFileAndCreateFrenquecyMap(String path){

        ClassLoader classLoader = getClass().getClassLoader();
        try(
                FileReader fileReader = new FileReader(new File(classLoader.getResource(path).getFile()));
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ){
            String line;
            String tabDelim[];
            file = new ArrayList<>();
            frequency = new HashMap<>();
            while((line=bufferedReader.readLine())!=null){
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
                            if(frequency.containsKey(temp)) frequency.put(temp, frequency.get(temp)+1);
                            else frequency.put(temp,1);
                        }
                        file.add(parsedToInt);//Save to raw : file
                    }
                }
            }
            System.out.println("FILE LOADING AND FREQUENCY COUNTIN TIME:" + (System.currentTimeMillis() - start));
        }catch(IOException | NullPointerException | NumberFormatException e){
            log.error(e.getMessage(),e);
        }
    }

    /**
     * Create header file that contains the frequency of each element in form of headerNode object
     */
    private void createHeaderMap(){
        header = new HashMap<>();
        headerOrder = new LinkedList<>();
        HeaderNode headerNode;
        for(Map.Entry<Integer, Integer> entry : frequency.entrySet())
            if(entry.getValue() >= threshold){
                headerNode = new HeaderNode(entry.getKey(), entry.getValue());
                header.put(entry.getKey(), headerNode);
                headerOrder.add(headerNode);
            }
        Collections.sort(headerOrder);
        System.out.println("HEADER MAP CREATION TIME:" + (System.currentTimeMillis() - start));
    }

    /**
     * Remove the low frequency item from data set
     */
    private void filterDataByThresholdAndAddTOfpTree(){
        for(int[] line : file){
            List<Pair> list = new LinkedList<>();
            for(int i = 0; i < line.length; ++i){
                int temp = line[i];
                int freq = frequency.get(temp);
                if(freq >= threshold) list.add(new Pair(temp, freq));
            }
            Collections.sort(list);

            System.out.println("Sorted list: " + list.toString());

            root.add(0, list, 1);
        }
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
        for(HeaderNode node : headerOrder){
            System.out.println(node.toString());
            Node tempTail = node.getLast();
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
                    System.out.print(listToPrint.get(i).getId() + "(" + listToPrint.get(i).getCount() + ")"
                    );
                tempTail = tempTail.prevSelf;
                System.out.println();
            }
        }
    }

    /**
     * FP-Tree node
     */
    @ToString(exclude = {"parent", "prevSelf", "children"})
    private class Node{
        /**
         * Node id
         */
        @Getter
        private final Integer id;
        /**
         * counter
         */
        @Getter
        @Setter
        private int count = 0;
        /**
         *
         */
        @Getter
        private final Node parent;
        /**
         *
         */
        @Setter
        @Getter
        private Node prevSelf = null;
        /**
         *
         */
        @Getter
        private Map<Integer, Node> children = null;

        /**
         * constructor
         *
         * @param id
         * @param count
         * @param parent
         */
        Node(Integer id, int count, Node parent){
            this.id = id;
            this.count = count;
            this.parent = parent;
        }

        void add(int index, List<Pair> list, int count){
            if(children == null) children = new HashMap<>();
            Node child = children.get(list.get(index).getName());
            if(child != null){//child found in children
                child.count += count;
                ++index;
                if(index < list.size()) child.add(index, list, count);
            }else addNewChild(index, list, count);
        }

        private void addNewChild(int index, List<Pair> list, int count){
            if(children == null) children = new HashMap<>();
            int item = list.get(index).getName();
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

            prevSelf = header.get(id).getLast();
            header.get(id).setLast(this);
        }
    }

    /**
     * new implementation of header
     * keep
     */
    @RequiredArgsConstructor
    @ToString(exclude = "last")
    class HeaderNode implements Comparable<HeaderNode>{
        @Getter
        private final int id;
        /**
         *
         */
        @Getter
        private final int count;
        @Getter
        @Setter
        private Node last;

        @Override
        public int compareTo(HeaderNode o){
            if(o.count == this.count) return o.id - this.id;
            else return o.count - this.count;
        }
    }

    @Getter
    @Setter
    @ToString
    @RequiredArgsConstructor
    class Pair implements Comparable<Pair>{
        private final int name;
        private final int freq;

        @Override
        public int compareTo(Pair o){
            if(o.freq == this.freq) return this.name - o.name;
            else return o.freq - this.freq;
        }
    }

}
