import java.io.*;
import java.util.*;

public class proj2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to my second neural network -- A Hopfield Net!\n");
        System.out.println("Enter 1 to use a training data file, enter 2 to use a trained weights settings data file:\n");
        int training_type = Integer.parseInt(sc.nextLine());
        if(training_type == 1){
            System.out.println("Enter the training data file name:\n");
            String filename = sc.nextLine();
            Readinput infile = new Readinput(filename);
            int[][] weights = train(infile);
            System.out.println("Training is complete!\n");
            System.out.println("Enter a filename to save the trained weight settings:\n");
            String outfile = sc.nextLine();
            writeweights(outfile, infile.dim_images, weights);
            System.out.println("Enter 1 to test/deploy using a testing/deploying data file, enter 2 to quit:\n");
            int test_choice = Integer.parseInt(sc.nextLine());
            if(test_choice == 1){
                    System.out.println("Enter the testing/deploying data file name:\n");
                    String inf = sc.nextLine();
                    System.out.println("Enter a file name to save the testing/deploying results:\n");
                    String outfilename = sc.nextLine();
                    Readinput infilename = new Readinput(inf);
                    int[][] output = test(weights, infilename.images, infilename.dim_images, infilename.num_images);
                    Readinput in = new Readinput(inf);
                    int[][] trainingimages = in.images;
                    writeresults(output, trainingimages, outfilename);
            }
            else if(test_choice == 2){
                System.exit(0);
            }
        }
        else if(training_type == 2){
            System.out.println("Enter the trained weight settings input data file name:\n");
            String weightsfilename = sc.nextLine();
            ReadWeights file = new ReadWeights(weightsfilename);
            int[][] weights = file.weights;
            System.out.println("Enter 1 to test/deploy using a testing/deploying data file, enter 2 to quit:\n");
            int test_choice = Integer.parseInt(sc.nextLine());
            if(test_choice == 1){
                System.out.println("Enter the testing/deploying data file name:\n");
                String filename = sc.nextLine();
                System.out.println("Enter a file name to save the testing/deploying results:\n");
                String outfilename = sc.nextLine();
                Readinput infilename = new Readinput(filename);
                int[][] output = test(weights, infilename.images, infilename.dim_images, infilename.num_images);
                Readinput in = new Readinput(filename);
                int[][] images = in.images;
                writeresults(output, images, outfilename);
            }
            else if(test_choice == 2){
                System.exit(0);
            }
        }
        sc.close();
    }

    public static int[][] train(Readinput infile) {
        int[][] weights = new int[infile.dim_images][infile.dim_images];
        for(int[] row: weights){
            Arrays.fill(row, 0);
        }
        for (int i = 0; i < infile.num_images; i++) {
            for (int j = 0; j < infile.dim_images; j++) {
                for (int k = 0; k < infile.dim_images; k++) {
                    if(k == j){
                        weights[k][j] = 0;
                    }
                    else{
                        weights[k][j] += infile.images[i][k] * infile.images[i][j];
                    }
                }
            }
        }
        return weights;
    }

    public static int[][] test(int[][] weights, int[][] images, int dim_images, int num_images){
        int[][] output = new int[num_images][dim_images];
        int[][] tmpimages = new int[num_images][dim_images];
        tmpimages = images;
        int index = 0;
        for(int i = 0; i < num_images; i++){
            boolean oneepoch = false;
            boolean cont = true;
            while(cont){

                Integer[] indexseen = new Integer[dim_images];
                Arrays.fill(indexseen,-1);
                int[] tmp = new int[dim_images];
                
                if(!oneepoch){
                    tmp = tmpimages[i];
                }
                else{
                    tmp = output[index];
                }
                boolean updated = false;

                for (int j = 0; j < dim_images; j++) {
                    List<Integer> indexlst = Arrays.asList(indexseen);
					double rand = Math.random() * dim_images;
					int random = (int)Math.floor(rand);
					while (indexlst.contains(random)){
						rand = Math.random() * dim_images;
						random = (int)Math.floor(rand);
					}
                    indexseen[j] = random;

                    int begin = tmp[random];
                    int y_in = tmp[random];

                    for (int k = 0; k < dim_images; k++) {
						y_in += tmp[k] * weights[k][random];
					}

                    int y = activation(y_in);

                    if(y != 0){
                        tmp[random] = y;
                    }
                    else if(y == 0 && oneepoch){
                        tmp[random] = output[index][random];
                    }
					if(y != begin){
                        updated = true;
                    }
					if(updated){
                        cont = true;
                    }
					else{
                        cont = false;
                    }
                }
                oneepoch = true;
                output[index] = tmp;
            }
            index++;
        }
        return output;
    }

    public static void writeweights(String outfile, int dim_weights, int weights[][]){
        try{
            FileWriter writer = new FileWriter(outfile);
            writer.write(dim_weights + "\n");
            for(int i=0; i<dim_weights; i++){
                writer.write("\n");
                for(int j=0; j<dim_weights; j++){
                    writer.write(weights[i][j] + " ");
                }
            }
            writer.flush();
            writer.close();
        }
        catch(FileNotFoundException e) {
            e.getMessage();
        }
        catch(IOException e) {
            e.getMessage();
        }
    }

    public static int activation(int y_in){
        if(y_in > 0){
            return 1;
        }
        else if(y_in < 0){
            return -1;
        }
        else{
            return 0;
        }
    }

    public static void writeresults(int[][] output, int[][] images, String outfile){
        boolean correct = false;
        int count = 0;
        
        try{
            FileWriter writer = new FileWriter(outfile);

            for(int k=0; k<output.length; k++){ 
                int row = (int) Math.sqrt(images[0].length);
                writer.write("Input testing image:\n");
                for(int i=0; i<row; i++){
                    for(int j=0; j<row; j++){
                        if(images[k][j+(row*i)] == 1){
                            writer.write('O');
                        }
                        else if(images[k][j+(row*i)] == -1){
                            writer.write(" ");
                        }
                        else{
                            writer.write("@");
                        }
                    }
                    writer.write("\n");
                }
                writer.write("\nThe associated stored image:\n");
                for(int i=0; i<row; i++){
                    for(int j=0; j<row; j++){
                        if(output[k][j+(row*i)] == 1){
                            writer.write('O');
                        }
                        else if (output[k][j+(row*i)] ==-1){
                            writer.write(" ");
                        }
                        else{
                            writer.write("@");
                        }
                    }
                    writer.write("\n");
                }
                for(int i=0; i < output.length; i++){
                    if(output[k][i] == 0){
                        correct = false;
                    }
                    else{
                        correct = true;
                    }
                }
                if(correct){
                    writer.write("\nPattern correctly associated\n");
                    count++;
                }
                else{
                    writer.write("\nPattern did not correctly associate\n");
                }
            }
            System.out.println(output.length);
            writer.write("The percentage of images associated is: " + (double)((count/output.length) * 100) + "%");
            writer.flush();
            writer.close();
        }
        catch(FileNotFoundException e) {
            e.getMessage();
        }
        catch(IOException e) {
            e.getMessage();
        }
    }
}