import java.util.Scanner;

/**
 * @author 王协群
 * @date 2021/4/28 17:03
 */
public class Test {
    public static void main(String[] args) {
        int grid[] = new int[625];
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < grid.length; i++) {
            grid[i] = sc.nextInt();
            grid[i] =(Math.random()>0.5?0:grid[i]);
        }
        for (int i = 0; i < grid.length; i++) {
            if (i%25==0){
                System.out.println();
            }
            System.out.print(grid[i]+" ");
        }

    }
}
