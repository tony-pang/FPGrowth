package pkg;

import org.junit.Test;

public class FPGrowthTest{
    String path1 = "Sample_1.txt";
    String path2 = "Sample_2.txt";
    String path3 = "Sample_3.txt";
    String path4 = "test.tsv";

    @Test
    public void FPGTest(){
        FPGrowth growth = new FPGrowth(path4);
        growth.getBaseTree().printTree();
    }

}