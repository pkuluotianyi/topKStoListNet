package cslt.thu.edu.SGDListNet;

import java.util.ArrayList; 
import java.util.Iterator; 
import java.util.List;
import java.util.Random;

public class ListNeuron extends Neuron {
	
	protected double[] d1;
	protected double[] d2;	
	
	protected ArrayList<ArrayList<Integer>> totalrst = new ArrayList<ArrayList<Integer>>();
	protected ArrayList<ArrayList<Integer>> temprsttopk = new ArrayList<ArrayList<Integer>>();
	protected ArrayList<Integer> temprsttop1 = new ArrayList<Integer>();
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
		int samplingFlag = 0;
		if (!(ListNet.samplingMethods.equals("Uniform"))) {
			if (ListNet.samplingMethods.equals("fixedProba")) {
				samplingFlag = 1;
			}
			if (ListNet.samplingMethods.equals("adaptiveProba")) {
				samplingFlag = 2;
			}
		}
		
		
		////////////////////////////////////////////////
		////////////////////////////////////////////////
		////////////////////////////////////////////////
		//////(ListNet.kNum >= 2)
		////////////////////////////////////////////////
		////////////////////////////////////////////////
		////////////////////////////////////////////////
		if (ListNet.kNum >= 2) {
			for(int k=0;k<inLinks.size();k++) {
				s = inLinks.get(k);
				int numOfSampling = 20;
				numOfSampling = ListNet.numberOfsampling;
				int tempSampling = (numOfSampling >= ((d1.length * (d1.length - 1)) / 2))?((d1.length * (d1.length - 1)) / 2):numOfSampling;
//				System.out.println("d1:" + d1.length + "\n");
//				System.out.println("tempSampling:" + tempSampling + "\n");
				if (d1.length != lengthOfd1) {
					numOfk = ListNet.kNum;
					
					//Don't sampling the query which contains no relevant documents
					boolean tempflag = false;
					for (int h = 0;h < d1.length;h++) {
						if (param.labels[h] != 0.0d) {
							tempflag = true;
							break;
						}							
					}
					if (tempflag) {
						
					} else {
						break;
					}
					temprsttopk.clear();
					
					//Make probabilities interval
					double[] proInterval = new double[d2.length + 1];
					double tempResult = 0.0d;
					if (samplingFlag == 2) {
						for (int l = 0;l < d2.length + 1;l++) {
							if (l == 0) {
								proInterval[l] = 0.0d;
							} else {
								tempResult += d2[l - 1];
								proInterval[l] = tempResult;
							}
						}
					}
					
					if (samplingFlag == 1) {
						for (int l = 0;l < d1.length + 1;l++) {
							if (l == 0) {
								proInterval[l] = 0.0d;
							} else {
								tempResult += d1[l - 1];
								proInterval[l] = tempResult;
							}
						}
					}
					
					//Generate sampling list
					int numOfSelect = 0;
					for (int num = 0;num < tempSampling + 1;num++) {
						//samplingFlag == 2 || //samplingFlag == 1
						if ((samplingFlag == 2) || (samplingFlag == 1)) {
							if (num == temprsttopk.size()) {
								
							} else {
								num = num - 2;
								continue;
							}
							ArrayList<Integer> tempArrayList = new ArrayList<Integer>();
							for (int i = 0;i < numOfk;i++) {						
								Random random = new Random();
								double tempDouble = Math.random();
								int selectedItem = 0;
								
							    int start = 0;
						        int end = d1.length + 1 - 1;
						        int mid;

						        while (start + 1 < end) {
						            mid = start + (end - start) / 2;
						            if (proInterval[mid] == tempDouble) {
						                end = mid;
						            } else if (proInterval[mid] < tempDouble) {
						                start = mid;
						            } else if (proInterval[mid] > tempDouble) {
						                end = mid;
						            }
						        }
						        if (proInterval[start] == tempDouble) {
						        	selectedItem = start;
						        }
						        if (proInterval[end] == tempDouble) {
						        	selectedItem = end;
						        }
						        if ((proInterval[start] < tempDouble)&&(tempDouble < proInterval[end])) {
						        	selectedItem = start;
						        }
						        
						        if(tempArrayList.contains(selectedItem)) {
						        	i = i - 1;
						        	continue;
						        } else {
						        	tempArrayList.add(selectedItem);
						        }
								
							}
							if (temprsttopk.contains(tempArrayList)) {
								
							} else {
								int[] relevanceOfList = {0,0,0};
								int[] tempList = new int[numOfk];
								double totalScore = numOfk * 2.0d;
								double currentScore = 0.0d;
								for (k = 0;k < numOfk;k++) {
									tempList[k] = tempArrayList.get(k);
									if (param.labels[tempList[k]] == 0.0d) {
										relevanceOfList[0]++;
									}
									if (param.labels[tempList[k]] == 1.0d) {
										relevanceOfList[1]++;
									}
									if (param.labels[tempList[k]] == 2.0d) {
										relevanceOfList[2]++;
									}
								}
								currentScore = relevanceOfList[1] * 1.0d + relevanceOfList[2] * 2.0d;								
								double selectedProbability = currentScore / totalScore;
								double tempDoublePro = Math.random();
								if (currentScore == 0.0d) {
									selectedProbability = 0.1d;
								}
								if (tempDoublePro < selectedProbability) {
									temprsttopk.add(tempArrayList);
								}						
							}
						}
						//samplingFlag == 0
						if (samplingFlag == 0) {
							if (num == temprsttopk.size()) {
								
							} else {
								num = num - 2;
								continue;
							}
							ArrayList<Integer> tempArrayList = new ArrayList<Integer>();
							for (int i = 0;i < numOfk;i++) {
								Random random = new Random();
								double tempDouble = Math.random();
								int selectedItem = 0;
								int maxNum = d1.length - 1;
								selectedItem = random.nextInt(maxNum);
						        
						        if(tempArrayList.contains(selectedItem)) {
						        	i = i - 1;
						        	continue;
						        } else {
						        	tempArrayList.add(selectedItem);
						        }			        
							}
							if (temprsttopk.contains(tempArrayList)) {
								
							} else {
								temprsttopk.add(tempArrayList);						
							}
						}											
					}				
					lengthOfd1 = d1.length;
				}
				
				//Calculate gradient when k = 2
				double dw = 0;
				for(int i = 0;i < temprsttopk.size();i++) {		
					int[] tempListAgain = new int[numOfk];
					ArrayList<Integer> tempArrayList = new ArrayList<Integer>();
					tempArrayList = temprsttopk.get(i);
					for (int j = 0;j < tempArrayList.size();j++) {
						tempListAgain[j] = tempArrayList.get(j);
					}
																		    				   										
					//pre cauculate new d1 and d2.
					double[] tempd1Label = new double[d1.length];					
					double[] tempd2Out = new double[d1.length];
					double[] tempd3OutMulXj = new double[d1.length];
					double tempOutExpPlus = 0.0d;
					double tempScoreExpPlus = 0.0d;
					double OutResultsMulXj = 0.0d;
					for(int p=0;p<outputs.size();p++)
					{
						tempScoreExpPlus += Math.exp(param.labels[p]);
						tempOutExpPlus += Math.exp(outputs.get(p));
						OutResultsMulXj += (s.getSource().getOutput(p) * Math.exp(outputs.get(p)));
					}
					for (int t = 0;t < numOfk;t++) {
						if (t == 0) {
							tempd1Label[t] = tempScoreExpPlus;
						} else {						
							tempd1Label[t] = tempScoreExpPlus - Math.exp(param.labels[tempListAgain[t - 1]]);
							tempScoreExpPlus = tempScoreExpPlus - Math.exp(param.labels[tempListAgain[t - 1]]);
						}					
					}
					for (int t = 0;t < numOfk;t++) {
						if (t == 0) {
							tempd2Out[t] = tempOutExpPlus;
						} else {
							tempd2Out[t] = tempOutExpPlus - Math.exp(outputs.get(tempListAgain[t - 1]));
							tempOutExpPlus = tempOutExpPlus - Math.exp(outputs.get(tempListAgain[t - 1]));
						}					
					}
					for (int t = 0;t < numOfk;t++) {
						if (t == 0) {
							tempd3OutMulXj[t] = OutResultsMulXj;
						} else {
							tempd3OutMulXj[t] = OutResultsMulXj - s.getSource().getOutput(tempListAgain[t - 1]) * Math.exp(outputs.get(tempListAgain[t - 1]));
							OutResultsMulXj = OutResultsMulXj - s.getSource().getOutput(tempListAgain[t - 1]) * Math.exp(outputs.get(tempListAgain[t - 1]));
						}					
					}
					
					//Precalculate some media results					
					ArrayList<Double> tempOutResultsMulXj = new ArrayList<Double>();
										
					for (int t = 0;t < numOfk;t++) {
						tempOutResultsMulXj.add(tempd3OutMulXj[t] / tempd2Out[t]);		
					}	
															
					
					ArrayList<Double> tempScoreResults = new ArrayList<Double>();
					double totalScore = 1.0d;
					for (int t = 0;t < numOfk;t++) {
						totalScore *= (Math.exp(param.labels[tempListAgain[t]])/(tempd1Label[t]));
					}
					
									
					double totalResult = 0.0d;
					for (int t = 0;t < numOfk;t++) {
						totalResult += (totalScore * (s.getSource().getOutput(tempListAgain[t]) - tempOutResultsMulXj.get(t)) );
					}
			
					dw += totalResult;
				}
				dw *= learningRate;
				s.setWeightAdjustment(dw);
				s.updateWeight();
			}
		}
		
		
		////////////////////////////////////////////////
		////////////////////////////////////////////////
		////////////////////////////////////////////////
		//////(ListNet.kNum == 1)
		////////////////////////////////////////////////
		////////////////////////////////////////////////
		////////////////////////////////////////////////
		if (ListNet.kNum == 1) {
			for(int k=0;k<inLinks.size();k++)
			{				
				s = inLinks.get(k);
				int numOfSampling = 30;
				numOfSampling = ListNet.numberOfsampling;
				int tempSampling = (numOfSampling >= d1.length) ? d1.length - 1 : numOfSampling;
				if (d1.length != lengthOfd1) {
					numOfk = ListNet.kNum;
					
					//Don't sampling the query which contains no relevant documents
					boolean tempflag = false;
					//double[] templabels = 
					for (int h = 0;h < d1.length;h++) {
						if (param.labels[h] != 0.0d) {
							tempflag = true;
						}							
					}
					if (tempflag) {
						
					} else {
						break;
					}
					temprsttopk.clear();
					
					//Make probabilities interval
					double[] proInterval = new double[d2.length + 1];
					double tempResult = 0.0d;
					if (samplingFlag == 2) {
						for (int l = 0;l < d2.length + 1;l++) {
							if (l == 0) {
								proInterval[l] = 0.0d;
							} else {
								tempResult += d2[l - 1];
								proInterval[l] = tempResult;
							}
						}
					}
					if (samplingFlag == 1) {
						for (int l = 0;l < d1.length + 1;l++) {
							if (l == 0) {
								proInterval[l] = 0.0d;
							} else {
								tempResult += d1[l - 1];
								proInterval[l] = tempResult;
							}
						}
					}
					
					
					//Generate sampling list
					int numOfSelect = 0;
					for (int num = 0;num < tempSampling + 1;num++) {
						if ((samplingFlag == 2) || (samplingFlag == 1)) {
							if (num == temprsttopk.size()) {
								
							} else {
								num = num - 2;
								continue;
							}
							ArrayList<Integer> tempArrayList = new ArrayList<Integer>();
							for (int i = 0;i < numOfk;i++) {
								Random random = new Random();
								double tempDouble = Math.random();
								int selectedItem = 0;
								
							    int start = 0;
						        int end = d1.length + 1 - 1;
						        int mid;

						        while (start + 1 < end) {
						            mid = start + (end - start) / 2;
						            if (proInterval[mid] == tempDouble) {
						                end = mid;
						            } else if (proInterval[mid] < tempDouble) {
						                start = mid;
						            } else if (proInterval[mid] > tempDouble) {
						                end = mid;
						            }
						        }
						        if (proInterval[start] == tempDouble) {
						        	selectedItem = start;
						        }
						        if (proInterval[end] == tempDouble) {
						        	selectedItem = end;
						        }
						        if ((proInterval[start] < tempDouble)&&(tempDouble < proInterval[end])) {
						        	selectedItem = start;
						        }
						        
						        if(tempArrayList.contains(selectedItem)) {
						        	i = i - 1;
						        	continue;
						        } else {
						        	tempArrayList.add(selectedItem);
						        }			        
							}
							if (temprsttopk.contains(tempArrayList)) {
								
							} else {
								int[] relevanceOfList = {0,0,0};
								int[] tempList = new int[numOfk];
								double totalScore = numOfk * 2.0d;
								double currentScore = 0.0d;
								for (int ka = 0;ka < numOfk;ka++) {
									tempList[ka] = tempArrayList.get(ka);
									if (param.labels[tempList[ka]] == 0.0d) {
										relevanceOfList[0]++;
									}
									if (param.labels[tempList[ka]] == 1.0d) {
										relevanceOfList[1]++;
									}
									if (param.labels[tempList[ka]] == 2.0d) {
										relevanceOfList[2]++;
									}
								}
								currentScore = relevanceOfList[1] * 1.0d + relevanceOfList[2] * 2.0d;								
								double selectedProbability = currentScore / totalScore;
								double tempDoublePro = Math.random();
								if (currentScore == 0.0d) {
									selectedProbability = 0.1d;
								}
								if (tempDoublePro < selectedProbability) {
									temprsttopk.add(tempArrayList);
								}						
							}
						}
						
						if (samplingFlag == 0) {
							if (num == temprsttopk.size()) {
								
							} else {
								num = num - 2;
								continue;
							}
							ArrayList<Integer> tempArrayList = new ArrayList<Integer>();
							for (int i = 0;i < numOfk;i++) {
								Random random = new Random();
								double tempDouble = Math.random();
								int selectedItem = 0;
								int maxNum = d1.length - 1;
								selectedItem = random.nextInt(maxNum);
						        
						        if(tempArrayList.contains(selectedItem)) {
						        	i = i - 1;
						        	continue;
						        } else {
						        	tempArrayList.add(selectedItem);
						        }			        
							}
							if (temprsttopk.contains(tempArrayList)) {
								
							} else {
								temprsttopk.add(tempArrayList);						
							}
						}											
					}				
					lengthOfd1 = d1.length;
				}
				
				//Calculate the gradient when k = 1
				double dw = 0;
				for(int l = 0;l < temprsttopk.size();l++) {	
					ArrayList<Integer> tempArrayList = new ArrayList<Integer>();
					tempArrayList = temprsttopk.get(l);
				    int selectedItem = tempArrayList.get(0);		
					dw += (d1[selectedItem] - d2[selectedItem]) * s.getSource().getOutput(selectedItem);	
				}
				dw *= learningRate;
				s.setWeightAdjustment(dw);
				s.updateWeight();
			}
		}
		
		////////////////////////////////////////////////
		////////////////////////////////////////////////
		////////////////////////////////////////////////
		//////(ListNet.kNum == 0)
		////////////////////////////////////////////////
		////////////////////////////////////////////////
		////////////////////////////////////////////////
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
