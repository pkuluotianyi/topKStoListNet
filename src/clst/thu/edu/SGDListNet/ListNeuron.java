package cslt.thu.edu.SGDListNet;

import java.util.ArrayList; 
import java.util.Iterator; 
import java.util.List;
import java.util.Random;

public class ListNeuron extends Neuron {
	
	protected double[] d1;
	protected double[] d2;	
	
	protected ArrayList<ArrayList<Integer>> totalrst = new ArrayList<ArrayList<Integer>>();
	protected ArrayList<ArrayList<Integer>> temprst = new ArrayList<ArrayList<Integer>>();
	protected ArrayList<ArrayList<Integer>> rst = new ArrayList<ArrayList<Integer>>();
	protected int[] num = new int[2];
	protected int numOfk = 2;
	protected int lengthOfd1 = 0;
	protected int lengthOfd2 = 0;
	public ListNeuron()
	{
		
	}
	
	public void computeDelta(PropParameter param)
	{
		double sumLabelExp = 0;
		double sumScoreExp = 0;
		for(int i=0;i<outputs.size();i++)//outputs[i] ==> the output of the current neuron on the i-th document
		{
			sumLabelExp += Math.exp(param.labels[i]);
			sumScoreExp += Math.exp(outputs.get(i));
		}

		d1 = new double[outputs.size()];
		d2 = new double[outputs.size()];
		for(int i=0;i<outputs.size();i++)
		{
			d1[i] = Math.exp(param.labels[i])/sumLabelExp;
			d2[i] = Math.exp(outputs.get(i))/ sumScoreExp;
		}
		if (d1.length != lengthOfd1) {
			totalrst.clear();
			if (ListNet.kNum == 2) {
				numOfk = 2;
				for (int u = 0;u < d1.length;u++) {
					for (int j = 0;j < d1.length;j++) {
						if ((u != j) && (u < j)) {
							num[0] = u;
							num[1] = j;
							rst = permute(num);
							for(int ii = 0; ii<rst.size(); ii++){
								if(totalrst.contains(rst.get(ii))){
						               continue;
						        } else {
						        	totalrst.add(rst.get(ii));
						        }
							}
							
						}
					}
				}
			}
			lengthOfd1 = d1.length;
		}
			
		
	}
	public ArrayList<ArrayList<Integer>> permute(int[] num) {
        ArrayList<ArrayList<Integer>> rst = new ArrayList<ArrayList<Integer>>();
        if (num == null || num.length == 0) {
            return rst;
        }

        ArrayList<Integer> list = new ArrayList<Integer>();
        helper(rst, list, num);
        return rst;
   }
   
   public void helper(ArrayList<ArrayList<Integer>> rst, ArrayList<Integer> list, int[] num){
       if(list.size() == num.length) {
           rst.add(new ArrayList<Integer>(list));
           return;
       }
       
       for(int i = 0; i<num.length; i++){
           if(list.contains(num[i])){
               continue;
           }
           list.add(num[i]);
           helper(rst, list, num);
           list.remove(list.size() - 1);
       }
       
   }
	
	public void updateWeight(PropParameter param)
	{
		Synapse s = null;
		if (ListNet.kNum == 2) {		
			//for(int k=0;k<inLinks.size()*0.36;k++) {
			for(int k=0;k<inLinks.size();k++) {
//				int maxNum = inLinks.size() - 1;
//				Random random = new Random();
//			    int tempK = random.nextInt(maxNum);
//				s = inLinks.get(tempK);
				
				s = inLinks.get(k);
				
				if (d1.length != lengthOfd1) {
					totalrst.clear();
					if (ListNet.kNum == 2) {
						numOfk = 2;
						for (int u = 0;u < d1.length;u++) {
							for (int j = 0;j < d1.length;j++) {
								if ((u != j) && (u < j)) {
									num[0] = u;
									num[1] = j;
									rst = permute(num);
									for(int ii = 0; ii<rst.size(); ii++){
										if(totalrst.contains(rst.get(ii))){
								               continue;
								        } else {
								        	totalrst.add(rst.get(ii));
								        }
									}
									
								}
							}
						}
					}
					lengthOfd1 = d1.length;
				}
				
				double dw = 0;
				//if (temprst.size()!=lengthOfd2) {
					temprst.clear();					
				//}
				
				//System.out.println(totalrst.size());
				for(int i = 0;temprst.size() < 5;i++) {
//					if ((temprst.size() >= 5)||(i > 60)||(temprst.size() == totalrst.size())) {
//						break;
//					}
					int maxNum1 = totalrst.size() - 1;
					Random random = new Random();
				    int tempK1 = random.nextInt(maxNum1);
				    int[] tempList = new int[2];
					ArrayList<Integer> tempArrayList = new ArrayList<Integer>();
					tempArrayList = totalrst.get(tempK1);
					
					int temNum = 12;
					if ((param.labels[tempArrayList.get(0)] > 0.0d) && (param.labels[tempArrayList.get(1)] > 0.0d)) {
						int temNumHa = random.nextInt(temNum);
				    	if (temNumHa >= 0 && (temNumHa <=12)) {
				    		//if (temprst.contains(tempArrayList)) {
				    			
				    		//} else {
				    			temprst.add(tempArrayList);
				    		//}
				    	} else {
				    		i--;
				    		continue;
				    	}	
					}
					
					if ((param.labels[tempArrayList.get(0)] == 0.0d) && (param.labels[tempArrayList.get(1)] > 0.0d)) {
						int temNumHa = random.nextInt(temNum);
				    	if (temNumHa >= 0 && (temNumHa <=10)) {
				    		//if (temprst.contains(tempArrayList)) {
			    			
				    		//} else {
				    			temprst.add(tempArrayList);
				    		//}
				    	} else {
				    		i--;
				    		continue;
				    	}
					}
					
					if ((param.labels[tempArrayList.get(0)] > 0.0d) && (param.labels[tempArrayList.get(1)] == 0.0d)) {
						int temNumHa = random.nextInt(temNum);
				    	if (temNumHa >= 0 && (temNumHa <=10)) {
				    		//if (temprst.contains(tempArrayList)) {
			    			
				    		//} else {
				    			temprst.add(tempArrayList);
				    		//}
				    		
				    	} else {
				    		i--;
				    		continue;
				    	}
					}
					
					if ((param.labels[tempArrayList.get(0)] == 0.0d) && (param.labels[tempArrayList.get(1)] == 0.0d)) {
						int temNumHa = random.nextInt(temNum);
				    	if (temNumHa >= 0 && (temNumHa <=8)) {
				    		//if (temprst.contains(tempArrayList)) {
			    			
				    		//} else {
				    			temprst.add(tempArrayList);
				    		//}
				    	} else {
				    		i--;
				    		continue;
				    	}
					}					
				}
				lengthOfd2 = temprst.size();
				
				for(int i = 0;i < temprst.size();i++) {
					//each permutation select which t items.
//					int maxNum1 = totalrst.size() - 1;
//					Random random = new Random();
//				    int tempK1 = random.nextInt(maxNum1);
					
					
					int[] tempList = new int[2];
					ArrayList<Integer> tempArrayList = new ArrayList<Integer>();
					//tempArrayList = totalrst.get(tempK1);
					tempArrayList = temprst.get(i);
					for (int j = 0;j < tempArrayList.size();j++) {
						tempList[j] = tempArrayList.get(j);
					}
					
					
					
//					int[] tempList = new int[2];
//					ArrayList<Integer> tempArrayList = new ArrayList<Integer>();
//					//tempArrayList = totalrst.get(tempK1);
//					tempArrayList = totalrst.get(i);
//					for (int j = 0;j < tempArrayList.size();j++) {
//						tempList[j] = tempArrayList.get(j);
//					}				
//					int temNum = 12;
//					if ((param.labels[tempList[0]] == 1.0d) && (param.labels[tempList[1]] == 1.0d)) {
//						int temNumHa = random.nextInt(temNum);
//				    	if (temNumHa >= 0 && (temNumHa <=12)) {
//				    		
//				    	} else {
//				    		continue;
//				    	}	
//					}
//					
//					if ((param.labels[tempList[0]] == 0.0d) && (param.labels[tempList[1]] == 1.0d)) {
//						int temNumHa = random.nextInt(temNum);
//				    	if (temNumHa >= 0 && (temNumHa <=10)) {
//				    		
//				    	} else {
//				    		continue;
//				    	}
//					}
//					
//					if ((param.labels[tempList[0]] == 1.0d) && (param.labels[tempList[1]] == 0.0d)) {
//						int temNumHa = random.nextInt(temNum);
//				    	if (temNumHa >= 0 && (temNumHa <=10)) {
//				    		
//				    	} else {
//				    		continue;
//				    	}
//					}
//					
//					if ((param.labels[tempList[0]] == 0.0d) && (param.labels[tempList[1]] == 0.0d)) {
//						int temNumHa = random.nextInt(temNum);
//				    	if (temNumHa >= 0 && (temNumHa <=8)) {
//				    		
//				    	} else {
//				    		continue;
//				    	}
//					}
								    				   
										
					//pre cauculate new d1 and d2.
					double[] tempd1Label = new double[d1.length];
					
					double[] tempd2Out = new double[d1.length];
					double[] tempd3OutMulXj = new double[d1.length];
					double tempOutExpPlus = 0.0d;
					double tempScoreExpPlus = 0.0d;
					double OutResultsMulXj = 0.0d;
					for(int p=0;p<outputs.size();p++)//outputs[i] ==> the output of the current neuron on the i-th document
					{
						tempScoreExpPlus += Math.exp(param.labels[p]);
						tempOutExpPlus += Math.exp(outputs.get(p));
						OutResultsMulXj += (s.getSource().getOutput(p) * Math.exp(outputs.get(p)));
					}
					for (int t = 0;t < numOfk;t++) {
						if (t == 0) {
							tempd1Label[t] = tempScoreExpPlus;
						} else {
							tempd1Label[t] = tempScoreExpPlus - Math.exp(param.labels[tempList[t - 1]]);
							tempScoreExpPlus = tempScoreExpPlus - Math.exp(param.labels[tempList[t - 1]]);
						}					
					}
					for (int t = 0;t < numOfk;t++) {
						if (t == 0) {
							tempd2Out[t] = tempOutExpPlus;
						} else {
							tempd2Out[t] = tempOutExpPlus - Math.exp(outputs.get(tempList[t - 1]));
							tempOutExpPlus = tempOutExpPlus - Math.exp(outputs.get(tempList[t - 1]));
						}					
					}
					for (int t = 0;t < numOfk;t++) {
						if (t == 0) {
							tempd3OutMulXj[t] = OutResultsMulXj;
						} else {
							tempd3OutMulXj[t] = OutResultsMulXj - s.getSource().getOutput(tempList[t - 1]) * Math.exp(outputs.get(tempList[t - 1]));
							OutResultsMulXj = OutResultsMulXj - s.getSource().getOutput(tempList[t - 1]) * Math.exp(outputs.get(tempList[t - 1]));
						}					
					}
					
					//Precalculate some media results					
					ArrayList<Double> tempOutResultsMulXj = new ArrayList<Double>();
										
					for (int t = 0;t < numOfk;t++) {
						//System.out.println(i);
						//System.out.println(tempList[t]);
						//System.out.println(outputs.get(tempList[t]));
						//for(int p=0;p<outputs.size();p++)//outputs[i] ==> the output of the current neuron on the i-th document
						//{							
						//	System.out.println(outputs.get(p));
						//}
						//System.out.println(s.getSource().getOutput(tempList[t]));
						//System.out.println(outputs.get(tempList[t]));
						tempOutResultsMulXj.add(tempd3OutMulXj[t] / tempd2Out[t]);		
					}	
															
					
					ArrayList<Double> tempScoreResults = new ArrayList<Double>();
					double totalScore = 1.0d;
					for (int t = 0;t < numOfk;t++) {
						totalScore *= (Math.exp(param.labels[tempList[t]])/(tempd1Label[t]));
					}
					
									
					double totalResult = 0.0d;
					for (int t = 0;t < numOfk;t++) {
						totalResult += (totalScore * (s.getSource().getOutput(tempList[t]) - tempOutResultsMulXj.get(t)) );
						//totalResult += (totalScore * (tempOutResultsMulXj.get(t) - s.getSource().getOutput(tempList[t])) );
					}
			
					dw += totalResult;
				}				
				//learningRate = 0.00000001d;
				dw *= learningRate;
				//System.out.println(learningRate);
				//System.out.println(dw);
				s.setWeightAdjustment(dw);
				s.updateWeight();
			}
		}
				
		if (ListNet.kNum == 1) {
			s = null;
			for(int k=0;k<inLinks.size();k++)
			{
				s = inLinks.get(k);
				double dw = 0;
				int tempL = (10<d1.length)?10:d1.length;
				for(int l=0;l<tempL;l++) {
				//for(int l=0;l<d1.length*0.6;l++) {
					int maxNum = d1.length - 1;
					Random random = new Random();
				    int tempK = random.nextInt(maxNum);
				    int temNum = 5;
				    if (param.labels[tempK] == 0) {				    	
				    	int temNumHa = random.nextInt(temNum);
				    	if (temNumHa >= 0 && (temNumHa <=2)) {
				    		
				    	} else {
				    		continue;
				    	}				    	
				    } else {
				    	int temNumHa = random.nextInt(temNum);
				    	if (temNumHa >= 0 && (temNumHa <=5)) {
				    		
				    	} else {
				    		continue;
				    	}
				    }
					//s = inLinks.get(tempK);
					dw += (d1[l] - d2[l]) * s.getSource().getOutput(l);
				}
				dw *= learningRate;
				s.setWeightAdjustment(dw);
				s.updateWeight();
			}
		}
		if (ListNet.kNum == 0) {
			s = null;
			for(int k=0;k<inLinks.size();k++)
			{
				s = inLinks.get(k);
				double dw = 0;
				for(int l=0;l<d1.length;l++) {
					//dw += (d1[l] - d2[l]) * s.getSource().getOutput(l);
					dw += (d1[l] - d2[l]) * s.getSource().getOutput(l);
				}
					
				
				dw *= learningRate;
				s.setWeightAdjustment(dw);
				s.updateWeight();
			}
		}
	}
}
