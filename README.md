# topKStoListNet
<p><a href="http://aclweb.org/anthology/D/D14/D14-1074.pdf">Chinese Poetry Generation with Recurrent Neural Networks</a></p>
This is implementation of Stochastic ListNet(A listwise learning to rank algorithm) top-k algorithm. We extend ListNet's fast computing algorithm - Top k probabilities algorithm to k = 2
and adopt three new sampling approaches to speed up training time. Results showed that our approach reduce the training complexity and get better p@1 performance.

The definitions of parameters are as following:
	-train               The location of your tarining set file. e.g. C:\MQ2008\Fold1\train.txt
	-validate      		 The location of your tarining set file. e.g. C:\MQ2008\Fold1\vali.txt
	-test                The location of your tarining set file. e.g. C:\MQ2008\Fold1\test.txt
	-metric2t            Metric to optimize on the training data. We surpport MAP, NDCG@k and P@k.
	-metric2T            Metric to evaluate on the test data. We surpport MAP, NDCG@k and P@k.
	-iterations          Number of iterations.
	-k                   Parameter of Top k probabilities algorithm.
	-learningRate        The learning rate e.g 1.0E-5. 
	-save          	     Saving the model file
	-isOrNotResampling   Whether we adopt re-sampling or not.
	-samplingMethods     Category of stochastic method. e.g. UDS, FDS and ADS.
	-numberOfsampling    The number of samplings.

The example is as following:
java stoListNet.jar -train C:\MQ2008\Fold1\train.txt -test C:\MQ2008\Fold1\test.txt -validate C:\MQ2008\Fold1\vali.txt -metric2t P@1 -metric2T P@1 -save mymodel.txt -iterations 20 -learningRate 1.0E-3 -k 1 -isOrNotResampling no -samplingMethods ADS -numberOfsampling 5
