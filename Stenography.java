import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

public class Stenography {
	
	public static void main(String args[]){
		Stenography sten = new Stenography();
		if (! sten.cmdline_valid(args)){
			System.err.println("Please enter valid commands");
			return;
		}
		if (args[0].equals("-D")){
			//decrypt
			sten.decrypt(args[1], args[2]);
		}
		else
		{
			try{
				sten.encrypt(args[1],args[2]);
			}catch(IOException e)
			{
				System.out.println("Error Encrypting: "+e.toString());
			}

		}
	}
	
	private boolean cmdline_valid(String[] args){
		if (args.length != 3)
			return false;
		
		if (!args[0].equals("-E") && !args[0].equals("-D"))
			return false;
		
		return true;				
	}

	private String decrypt(String encrypted_image, String output_file)
	{
		BufferedImage img = null;
        try {
            img = ImageIO.read(new File(encrypted_image));
        } catch (IOException e) {
        	System.err.println("image could not be opened");
        }

        String output="";
        for(int row = 0; row < img.getHeight(); row++)
        
        	for(int column = 0; column < img.getWidth(); column++)
	        
	       	while(true)
	       	{
	        	String[] bitarray = read_3Bytes(img, row, column);

		   	/*Converts the 3 bytes to characters that are added to output string*/
		   		for(int count = 0; count < 3; count++)
		   		{
		   			int val = Integer.parseInt(bitarray[count],2);
		   			System.out.println(val);
		   			if(val != 0)
		   				output+=""+(char)val;
		   			else
		   			{
		   				System.out.println(output);
		   				return output;
		   			}
		   		}
		   		column+=8;
		   		if(column >= img.getWidth())
		   		{
		   			column -= (img.getWidth()-1);
		   			row+=1;
		   		}
	   		}
	   	
		System.out.println();
		return output;
		
	}
	
	private void encrypt(String image, String msg) throws IOException
	{
		BufferedReader br;
		BufferedImage img = null;
        try {
            img = ImageIO.read(new File(image));
        } catch (IOException e) {
        	System.err.println("image could not be opened");
        }
        int height = img.getHeight();
        int width = img.getWidth();
        long amountPixel = height * width;

		// This prints the image height and width and a specific pixel. 
        System.out.println("filename: " +image +"\nnumber of pixels: "+ amountPixel+ "\nheight: "+height  + "\nwidth: " +  width);
	    

	    
	    		FileInputStream fstream = new FileInputStream(msg);
	    		DataInputStream in = new DataInputStream(fstream);
	    		br = new BufferedReader(new InputStreamReader(in));

    	int[] msgChars = new int[3]; //Holds 3 chars
    	boolean end_of_msg = false;
    	int row = 0;
    	int col = 0; 
    	
    	while(!end_of_msg)
    	{
    		//Read 3 characters from message
    		for(int count = 0; count < 3; count++)
    		{
    			msgChars[count] = br.read();
    			
    			if(msgChars[count] == -1)
    			{
    				end_of_msg = true;
    				msgChars[count] = 0;
    				while(count<3)
    				{
    					msgChars[count] = 0;
    					count++;
    				}
    				break;
    			}
    		}

    		/*Write 3 characters to image. Note that 3 characters is 24 bits
    		so we must change 24 integer values which is 8 rgb values or pixels*/
    		String bitarray="";

    		for(int k = 0; k < 3; k++)
    		{
    			String bin = Integer.toBinaryString(msgChars[k]);
    			//Pad 0's on the left
    			bitarray+= ("00000000"+bin).substring(bin.length());
    		}
   			System.out.println(bitarray);
    		for(int k = 0; k < 8; k++)
    		{
    			//Get the RGB value at the current pixel
    			int[] color = convertRGB(img.getRGB(col,row));
    			/*System.out.println("Row: "+row+" Col: "+col);
    			System.out.println("Old Red: "+color[0]);
     			System.out.println("Old Green: "+color[1]);
    			System.out.println("Old Blue: "+color[2]);*/

  
    			//Add the value in the bitstring to either r,g, or b
    			for(int color_count=0; color_count < 3; color_count++)
    			{
    				if(color[color_count]%2 != Integer.parseInt(""+bitarray.charAt(k*3+color_count))%2)
    				{
    					color[color_count]+=1;
    					color[color_count] = color[color_count] % 256;	
    				}
    			}
    			//Store the new r,g,b values into the pixel rgb
    			String str_rgb = "00000000"+Integer.toBinaryString(color[2])+
    								Integer.toBinaryString(color[1])+
    									Integer.toBinaryString(color[0]);
/*    			System.out.println("New Red: "+ color[0]);
    			System.out.println("New Green: "+color[1]);
    			System.out.println("New Blue: "+ color[2]);*/

    			img.setRGB(col, row, Integer.parseInt(str_rgb,2));

    			col++;
    			if(col==width)
    			{
    				col=0;
    				row++;
    			}
    		}

    	}
    	File output_file = new File("output.bmp");
    	ImageIO.write(img,"bmp",output_file);
	}

	private int[] convertRGB(long rgb)
	{
	    int[] color = new int[3];				
 		color[0] = (int) rgb & 0xFF;
	   	color[1] = (int) (rgb>>8) & 0xFF;
	    color[2] = (int) (rgb>>16) & 0xFF;
	    return color;
	}
	
	private String[] read_3Bytes(BufferedImage img, int row, int column)
	{ //Need to read 24 bits = 24 integer vals
		String[] bitstrings = {"","",""}; //each string should be 8 bits
	//3 bits per rgb * 8 times = 24 bits
	    for(int count=0; count < 8; count++)
	     {
	     	long rgb = img.getRGB(column, row); //Three bits from rgb

	     	int[] color = convertRGB(rgb);

    		for(int k = 0; k <3; k++)
   			{
   				if(color[k] % 2 == 0)
   					bitstrings[(count*3 + k)/8]+='0';
   				else
   					bitstrings[(count*3 + k)/8]+='1';
   			}
   			   		column++;
   		if(column == img.getWidth())
	     	{
	     		column = 0;
	     		row++;
	     	}
   		}
   		

   		/*System.out.println(bitstrings[0]);
   		System.out.println(bitstrings[1]);
   		System.out.println(bitstrings[2]);
   		System.out.println();*/

   		return bitstrings;
	}
}
