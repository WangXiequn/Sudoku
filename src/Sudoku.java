import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

/**
 * @author 王协群
 * @date 2021/4/27 17:56
 */
class Node{
    int index;
    int left;
    int right;
    int up;
    int down;
    int rowNum;
    int colNum;
}
public class Sudoku {
    public byte[] org;
    public int size;
    public boolean[][] s;
    public boolean []chose,cover;
    HashMap<Integer,Node> index;
    int count;
    int colLen;
    int gridSize;
    Sudoku(int size,byte[] org){
        this.size=size;
        this.org=org;
        int temp = size*size;
        this.gridSize=size*size;
        s = new boolean[gridSize*gridSize*gridSize+1][gridSize*gridSize*4+1];
        index = new HashMap<>();
        this.colLen=temp*temp*4;
        chose = new boolean[gridSize*gridSize*gridSize+1];
        cover = new boolean[colLen+1];
        build();
    }
    public void build(){
        int count = 1;
        for (int i = 0; i < org.length; i++) {
            int rowID=i/(gridSize);
            int colID=i%(gridSize);
            int blockID=(rowID/size)*size+(colID/size);
            if (org[i]!=0){
             s[count][i+1] = true;
             s[count][gridSize*gridSize+rowID*gridSize+org[i]]=true;
             s[count][gridSize*gridSize*2+colID*gridSize+org[i]]=true;
             s[count][gridSize*gridSize*3+blockID*gridSize+org[i]]=true;
             count++;
            }else {
                for (int j = 0; j <gridSize ; j++) {
                    s[count][i+1] = true;
                    s[count][gridSize*gridSize+rowID*gridSize+j+1]=true;
                    s[count][gridSize*gridSize*2+colID*gridSize+j+1]=true;
                    s[count][gridSize*gridSize*3+blockID*gridSize+j+1]=true;
                    count++;
                }
            }
        }
        this.count=count;
    }
    public void buildLink(){
        int temp = colLen;
        for (int i = 0; i <= temp; i++) {
            Node node = new Node();
            node.rowNum=0;
            node.colNum =i;
            node.index=i;
            node.up=i;
            node.down=i;
            node.left = (i > 0) ? (i - 1) : temp;
            node.right = (i<temp)?(i+1) : 0;
            index.put(i,node);
            s[0][i]=true;
        }
        temp++;
        for (int i = 1; i < count; i++) {
            for (int j = 1; j < temp; j++) {
                if (s[i][j]){
                    Node node = new Node();
                    node.rowNum=i;
                    node.colNum=j;
                    node.index=i*temp+j;
                    for (int k = (i+1>=count)?0:i+1;; k = (k + 1 >= count) ? 0 : k + 1) {
                        if (s[k][j]){
                            node.down=k*temp+j;
                            if (k==0){
                                index.get(j).up=i*temp+j;
                            }
                            break;
                        }
                    }
                    for (int k = i-1;; k = (k - 1 < 0) ? count-1: k - 1) {
                        if (s[k][j]){
                            node.up=k*temp+j;
                            if (k==0){
                                index.get(j).down=i*temp+j;
                            }
                            break;
                        }
                    }
                    for (int k = (j+1>=temp)?0:j+1;; k = (k + 1 >= temp) ? 0: k+1) {
                        if (s[i][k]){
                            node.right=i*temp+k;
                            break;
                        }
                    }
                    for (int k = (j-1<1)?temp-1:j-1;; k = (k - 1 < 0) ? temp-1 : k - 1) {
                        if (s[i][k]){
                            node.left=i*temp+k;
                            break;
                        }
                    }
                    index.put(i*temp+j,node);
                }
            }
        }
    }
    public boolean Dance(int x){
        if (index.get(0).left==0){
            return true;
        }
        if (cover[index.get(x).colNum]){
            return Dance(x+1);
        }
        remove(x);
        for (int p = index.get(x).down; p != x; p = index.get(p).down) {

            chose[index.get(p).rowNum] = true;
            for (int q = index.get(p).right; q != p; q = index.get(q).right) {
                remove(index.get(q).colNum);
            }

            //下一个元素x+1
            if (Dance(x + 1)) {
                return true;
            }
            chose[index.get(p).rowNum] = false;
            for (int q = index.get(p).left; q != p; q = index.get(q).left) {
                resume(index.get(q).colNum);
            }
        }
        resume(x);
        return false;
    }
   public void resume(int i) {
        //在头节点的一行恢复节点i
       index.get(index.get(i).right).left=i;
       index.get(index.get(i).left).right=i;

       cover[index.get(i).colNum]=false;
        //所有包含i的子集
        for (int p = index.get(i).up; p != i; p = index.get(p).up) {
            //在列上恢复子集上除了i的其他节点
            for (int q = index.get(p).right; q != p; q = index.get(q).right) {
                index.get(index.get(q).down).up=q;
                index.get(index.get(q).up).down=q;
            }
        }
    }

    public void remove(int i){
        index.get(index.get(i).right).left=index.get(i).left;
        index.get(index.get(i).left).right=index.get(i).right;

        cover[i]=true;
        for (int p = index.get(i).down; p != i; p = index.get(p).down) {
            for (int q = index.get(p).right; q != p; q = index.get(q).right) {
                index.get(index.get(q).down).up=index.get(q).up;
                index.get(index.get(q).up).down=index.get(q).down;

            }
        }
    }


    public byte [][] DancingLink(){
        buildLink();
        boolean dance = Dance(1);
        System.out.println(dance);
        byte [][] answer = new byte[gridSize][gridSize];
        for (int i = 0; i < chose.length; i++) {
            if (chose[i]){
                int t1=0,t2=0;
                for (int j = 1; j < s[i].length; j++) {
                    if (j<=gridSize*gridSize&&s[i][j]){
                        t1=j;
                    }
                    if (j>gridSize*gridSize&&s[i][j]){
                        t2=j-gridSize*gridSize;
                    }
                }
                int row = (t1-1)/(gridSize);
                int col = (t1-1)%(gridSize);
                t2%=(gridSize);
                if (t2==0){
                    t2=gridSize;
                }
                answer[(byte)row][(byte)col]=(byte)t2;
            }
        }
        return answer;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int size = scanner.nextByte();
        byte[] arr = new byte[size*size*size*size];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = scanner.nextByte();
        }
        Sudoku sudoku = new Sudoku(size,arr);

       byte[][] temp =  sudoku.DancingLink();
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[i].length; j++) {
                System.out.print(temp[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}
