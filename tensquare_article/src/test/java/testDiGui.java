public class testDiGui {
    public static void main(String[] args) {
        int beginNum=100;
        int sum = diGuiAdd(beginNum);
        System.out.println("sum = " +sum);
    }

    public static int diGuiAdd(int initial){
        if (initial>0){
            System.out.println("加上" + initial);
            return initial+ diGuiAdd(initial-1);
        }else {
            return 0;
        }
    }
}
