package task8;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestFlettesortering {

	public static void main(String[] args) throws InterruptedException {
		ArrayList<Integer> list = new ArrayList<>();
		Random r=new Random();
		for (int i=0;i<1000000;i++) {
			list.add(Math.abs(r.nextInt()%10000));
		}
		
		// System.out.println(list);

		FletteThread ft1 = new FletteThread(list, 0, list.size() / 2 - 1);
		FletteThread ft2 = new FletteThread(list, list.size() / 2, list.size() - 1);
		long l1,l2;
		l1 = System.nanoTime();
		ft1.start();
		ft2.start();
		ft1.join();
		ft2.join();
		FletteSortering fs = new FletteSortering();
		fs.merge(list, 0, list.size() / 2, list.size() - 1);
		l2 = System.nanoTime();
		System.out.println();
		System.out.println("K�retiden var " + (l2-l1)/1000000);
		System.out.println();
		// System.out.println(list);
	}

}
