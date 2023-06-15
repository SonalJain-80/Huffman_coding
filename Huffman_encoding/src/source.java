import java.io.*;
import java.util.*;


//Structure of each node, the Huffman Tree is made up of

class Node
{
    char ch;  //an individual character in the Input String
    int freq; //no. of times the respective character has occurred in the entire Input String

    Node left;  //reference to left child
    Node right; //reference to right child
}




//To compare nodes on the basis of their frequency values (Comparator Class helps to compare the node on the basis of one of its attribute. )

class frequency_comparison implements Comparator<Node>
{
    public int compare(Node x, Node y)
    {
        return x.freq - y.freq;
    }
}


//To store the codes generated for the characters while traversing the Huffman Tree
class code_map
{
    Character alpha;
    String code;
    int i=0;
}

class source
{

    static code_map C[];   //to keep a record of the generated codes
    static int iterate=0;


    //Traversal of the Huffman Tree for generation of respective Huffman Codes
    public static void display_code(Node root, String s)
    {

        //Encoded Characters exist on Leaf Nodes Only
        if (root.left  == null && root.right== null  && (root.ch!='#' ))
        {

            System.out.println(root.ch + ":" + s);  //Print the code generated on traversal

            //To keep track of generated codes for encoding the Input into Final Output
            C[iterate]=new code_map();
            C[iterate].alpha=root.ch;
            C[iterate].code=s;
            iterate++;
            return;

        }

        // if we go to left then add "0" to the Huffman code.
        // if we go to the right add"1" to the Huffman code.

        // recursive calls for left and
        // right sub-tree of the generated tree.

        display_code(root.left,s+"0");
        display_code(root.right,s+"1");
    }

    public static void main(String args[]) throws IOException
    {

        Map<Character, Integer> frequencyMap = new HashMap<>();
            // Read the file using a Scanner
            Scanner scanner = new Scanner(new File("C:\\Users\\I527373\\Documents\\motto.txt"));
            //char test_array[] = new char[1000000000];
            String to_encode = "";

            // Iterate through each line of the file
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                // Iterate through each character of the line
                for (char c : line.toCharArray()) {
                    // Increment the frequency count for the character
                    frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
                }
                to_encode += line;
            }


        //Input String converted to Character Array

        //Display contents of table
        System.out.println("Characters and their Frequency of Occurence:");
        System.out.println(frequencyMap);
        System.out.println();


        //Implement MinHeap using Priority Queue
        PriorityQueue <Node> q = new PriorityQueue<Node>(frequencyMap.size(), new frequency_comparison());

        //Obtain Keys(i.e. Unique Characters) from Hash Table sequentially
        Set<Character> keys = frequencyMap.keySet();
        Iterator<Character> i = keys.iterator();

        //Obtain Values corresponding to the Keys(i.e. Frequency of Unique Character) from Hash Table sequentially
        Collection<Integer> getValues = frequencyMap.values();
        Iterator<Integer> j = getValues.iterator();

        //Create a node for the Huffman Tree using the Character-Frequency pair retrieved from the Hash Table
        while (i.hasNext())
        {
            Node huffman_node = new Node();
            huffman_node.ch=(char)i.next();
            huffman_node.freq=(int)j.next();

            huffman_node.left=null;
            huffman_node.right=null;

            q.add(huffman_node); //Add each node to the Minimum Priority Queue(Nodes are in ascending order of their frequency attribute)
        }

        //Creation of Huffman Tree
        //Create a root node
        Node root = null;


        while (q.size() > 1)
        {

            //Extract the Minm frequency Character First
            Node x = q.peek();
            q.poll();

            // Then Extract the next Minm frequency Character
            Node y = q.peek();
            q.poll();

            //create a new node, which represents the sum of the frequencies of the two minimum extracted nodes
            Node sum = new Node();


            // assigning values to the sum node.
            sum.freq = x.freq + y.freq;
            sum.ch = '#';

            // first extracted node as left child of sum node
            sum.left = x;

            // second extracted node as the right child of sum node
            sum.right = y;

            // marking the sum node as the root node.
            root = sum;

            // add this node to the priority-queue.
            q.add(sum);
        }
        C = new code_map[frequencyMap.size()];

        System.out.println("Prefix codes for the characters are : ");
        display_code(root,""); //Traverse the Huffman Tree created

        System.out.println();
        encoded(to_encode); //Display encoded Input String and write the same to a new file
        scanner.close();
        decoded();
    }

    static void decoded() throws IOException{
            String inputFilePath = "C:\\Users\\I527373\\Documents\\compressed.txt";
            String outputFilePath = "C:\\Users\\I527373\\Documents\\decompressed.txt";
            HashMap<Character, String> prefix_code = new HashMap<>();
            for(int j=0;j<C.length;j++) {
                prefix_code.put(C[j].alpha, C[j].code);
            }
            try {
                // Read the compressed prefix codes file
                FileInputStream fis = new FileInputStream(inputFilePath);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                // Prepare to write the decompressed text
                FileWriter fileWriter = new FileWriter(outputFilePath);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                // Decompression logic
                StringBuilder compressedBits = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    compressedBits.append(line);

                    // Iterate through the compressed bits
                    int currentIndex = 0;
                    while (currentIndex < compressedBits.length()) {
                        boolean foundCode = false;
                        for (Map.Entry<Character, String> entry : prefix_code.entrySet()) {
                            Character character = entry.getKey();
                            String prefixCode = entry.getValue();
                            if (compressedBits.substring(currentIndex).startsWith(prefixCode)) {
                                bufferedWriter.write(character);
                                currentIndex += prefixCode.length();
                                foundCode = true;
                                break;
                            }
                        }

                        if (!foundCode) {
                            // No matching prefix code found, break the loop
                            break;
                        }
                    }
                    compressedBits.delete(0, currentIndex);
                }

                // Close resources
                br.close();
                bufferedWriter.close();

                System.out.println("Decompression completed successfully.");
            } catch (IOException e) {
                System.out.println("An error occurred during decompression: " + e.getMessage());
            }
    }
    static void encoded(String to_encode) throws IOException
    {
        try {
            String filePath = "C:\\Users\\I527373\\Documents\\compressed.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for(int i=0;i<to_encode.length();i++)
            {
                for(int j=0;j<C.length;j++)
                {
                    if(to_encode.charAt(i)==C[j].alpha)
                    {
                        bufferedWriter.write(C[j].code);   //write the encoded version in the output file
                    }
                }
            }
            bufferedWriter.close();
            System.out.println("Compression completed successfully.");
        }
        catch (IOException e) {
            System.out.println("An error occurred during Compression: " + e.getMessage());
        }
    }
}
