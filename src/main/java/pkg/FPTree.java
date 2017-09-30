package pkg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@SuppressWarnings({"usued","Duplicates"})
public class FPTree{
    private int minimumSupport;

    private final Node root = new Node(null, 0, null);

    private List<String[]> file;
    private Map<Integer, Integer> frequency;

    /**
     * constructor load from file, determine the minimum support
     * @param path
     */
    public FPTree(String path){
        readFileAndCreateFrenquecyMap(path);
    }

    private void readFileAndCreateFrenquecyMap(String path){
        try(
                FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ){
            String line;
            String tabDelim[];
            file = new ArrayList<>();
            frequency = new HashMap<>();
            while((line=bufferedReader.readLine())!=null){
                if(file.size()==0){
                    //first line contains only the support
                    try{
                        minimumSupport = Integer.parseInt(line);
                    }catch(NumberFormatException nfe){
                        log.error(nfe.getMessage(), nfe);
                    }
                }else{
                    tabDelim = line.split("\\t");
                    String commaDelim[];
                    for(String s : tabDelim){
                        commaDelim = s.split(",");
                        file.add(commaDelim);
                        for(int i = 1; i<commaDelim.length ;++i){
                            int temp = Integer.parseInt(commaDelim[i]);
                            //increaseCounter to frequency list
                            if(frequency.containsKey(temp)) frequency.put(temp, frequency.get(temp)+1);
                            else frequency.put(temp,1);
                        }
                    }
                }
            }
        }catch(IOException e){
            log.error(e.getMessage(),e);
        }
    }







    /**
     * FP-Tree node
     */
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
        private Node nextSelf = null;
        /**
         *
         */
        @Getter
        private Map<Integer, Node> children = null;

        Node(Integer id, int count, Node parent){
            this.id = id;
            this.count = count;
            this.parent = parent;
        }

    }

    /**
     * new implementation of header
     * keep
     */
    @RequiredArgsConstructor
    class HeaderNode implements Comparable<HeaderNode>
    {
        private final int id;
        /**
         *
         */
        @Getter
        @Setter
        private int count;
        @Getter
        private final Node head;

        @Override
        public int compareTo(HeaderNode o){
            return 0;
        }
    }

}
