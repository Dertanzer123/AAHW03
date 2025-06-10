import java.io.*;
import java.util.ArrayList;


public class  Main{
    static double epsilon=0.0001d;
    static double[] baseCostConstraints ={1d,100d};
    static int[] cashierCountConstraints ={1,10};
    static int[] maxTypePerCashierConstraints ={1,50};




    public Main() throws IOException {


        ArrayList<parameters> solutionParameters=new ArrayList<parameters>();

        String inputFile="inputs/input00.txt";
        String outputFile="outputs/output00.txt";

        BufferedReader br=new BufferedReader(new FileReader(inputFile));

        String line;
        double base_cost=0;
        int cashier_count=0;
        int max_type_per_cashier=0;
        ArrayList<Integer> items=new ArrayList<Integer>();

        while(true){
            line=br.readLine();
            if(line==null){

                break;
            }
            else {
                base_cost = Double.parseDouble(line);
            }line=br.readLine();
            if(line==null){
                throw new RuntimeException("invalid input");
            }
            else {
                cashier_count = Integer.parseInt(line);
            }line=br.readLine();
            if(line==null){
                throw new RuntimeException("invalid input");
            }
            else {
                max_type_per_cashier = Integer.parseInt(line);
            }
            line=br.readLine();
            String[] split=line.split(",");
            for (String s : split) {
                items.add(Integer.parseInt(s.trim().split(" ")[1]));

            }
            int[] itemsArray=new int[items.size()];
            for(int i=0;i<items.size();i++){
                itemsArray[i]=items.get(i);
            }
            items.clear();// bugfix
            if(cashier_count<cashierCountConstraints[0]||cashier_count>cashierCountConstraints[1]||max_type_per_cashier<maxTypePerCashierConstraints[0]||max_type_per_cashier>maxTypePerCashierConstraints[1]||base_cost<baseCostConstraints[0]||base_cost>baseCostConstraints[1])
            {
                System.out.println("input is no in constraints");
            }
            else
            {
            solutionParameters.add(new parameters(base_cost, cashier_count, max_type_per_cashier, itemsArray));
            }
        }
        br.close();

        System.out.println("inputs read,total input amount: "+solutionParameters.size());

        BufferedWriter bw=new BufferedWriter(new FileWriter(outputFile));
        int i=0;
        for(parameters p:solutionParameters)
        {
        double totalcost=0;
        totalcost=calculate_cost(p.items,p.cashier_count,p.max_type_per_cashier,p.base_cost);
        System.out.println(i+".totalcost: "+totalcost);
        bw.write(i+".totalcost: "+totalcost+"\n");
        i++;

        }
        bw.close();








    }
     double calculate_cost(int[] items, int cashier_count, int max_type_per_cashier, double base_cost)
    {



        cashier[] cashiers=new cashier[cashier_count];
        for(int i=0;i<cashier_count;i++){
            cashiers[i]=new cashier(max_type_per_cashier);
        }
        double totalcost=0;
        int commited_transactions=0;
        for(int item:items){
            double min_item_cost=Double.MAX_VALUE/2;
            double holder;
            int cashier_pointer=-1;
            for(int i=0;i<cashier_count;i++){
               holder= cashiers[i].test_item(item,base_cost);
               if(holder<min_item_cost&&holder>-.5d){
                   min_item_cost=holder;
                   cashier_pointer=i;
               }
            }
                if(cashier_pointer==-1){
                    return -1;
                }else{
                    cashiers[cashier_pointer].add_item(item,base_cost);
                    totalcost+=min_item_cost;
                    commited_transactions++;
                    if(commited_transactions==5)
                    {   base_cost+=1d;
                        commited_transactions=0;
                    }
                }
        }
        return totalcost;
    }


    class cashier{
        boolean is_fatigue;
        int item_type_count;
        int[] item_types;
        int item_type_pointer;
        ArrayList<Integer> Last_items;

        cashier(int item_type_count)
        {
            this.item_type_count=item_type_count;
            item_types=new int[item_type_count];
            item_type_pointer=0;
            Last_items=new ArrayList<Integer>();
            is_fatigue=false;
        }
        double test_item(int item_type,double base_cost)
        {
            boolean is_contains=false;
            for(int i=0;i<item_type_pointer;i++)// if its contains
            {
                if(item_types[i]==item_type)
                {
                    is_contains=true;
                    break;
                }
            }
            if(!is_contains&&item_type_pointer==item_type_count) //if its not contains and its full
            {
                return -1;
            }

            if(Last_items.isEmpty())//if its empty
            {
                return 0;
            }
            if(is_fatigue&&Last_items.get(Last_items.size()-1)==item_type)//if its fatigue and last item is same
            {
                return base_cost*1.5d;
            }
            if(Last_items.get(Last_items.size()-1)>item_type)//if last item is bigger than current
            {
                return base_cost*0.8d;
            }
            if(!is_fatigue&&Last_items.get(Last_items.size()-1)==item_type) //this part if transaction between same type is zero cost
            {
                return 0;
            }

            return base_cost;//if its not fatigue and last item is same

        }
        void add_item(int item_type,double base_cost)
        {
            if(epsilon>(test_item(item_type,base_cost)+1))
            {
                throw new RuntimeException("invalid add_item request");
            }
            else
            {
                boolean is_contains=false;
                for(int i=0;i<item_type_pointer;i++)
                {
                    if(item_types[i]==item_type)
                    {
                       is_contains=true;
                       break;
                    }
                }
                if(!is_contains) {
                    item_types[item_type_pointer] = item_type;
                    item_type_pointer++;
                }

                if(!Last_items.isEmpty() &&Last_items.get(Last_items.size()-1)==item_type)
                {
                    is_fatigue=true;
                }
                else
                {
                    is_fatigue=false;
                }
                Last_items.add(item_type);
                if(Last_items.size() > 3) {
                    Last_items.remove(0);
                }


            }
        }


    }


    class parameters{
        double base_cost;
        int cashier_count;
        int max_type_per_cashier;
        int[] items;
        parameters(double base_cost, int cashier_count, int max_type_per_cashier, int[] items) {
            this.base_cost = base_cost;
            this.cashier_count = cashier_count;
            this.max_type_per_cashier = max_type_per_cashier;
            this.items = items;
        }
        int item_count(){
            return items.length;
        }

    }


}