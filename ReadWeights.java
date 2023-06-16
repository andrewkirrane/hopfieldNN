import java.io.*;
import java.util.*;

public class ReadWeights {
    int dim_weights;
    int[][] weights;

    public ReadWeights(String filename){
        try {
            File infile = new File(filename);
            Scanner sca = new Scanner(infile);

            this.dim_weights = sca.nextInt();
            this.weights = new int[dim_weights][dim_weights];

            for(int i = 0; i < this.dim_weights; i++){
                for(int j = 0; j < this.dim_weights; j++){
                    this.weights[i][j] = sca.nextInt();
                }
            }
            sca.close();
        }
        catch (FileNotFoundException e){
            e.getMessage();
        }
    }
}
