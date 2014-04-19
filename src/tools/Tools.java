package tools;

public class Tools {

	public static void print(Object o){
		System.out.println(o);
	}
	public static void printByte(byte[] b){
		for(int i=0;i<b.length;i++){
			System.out.print(b[i]);
		}
		System.out.print("\n");
	}
}
